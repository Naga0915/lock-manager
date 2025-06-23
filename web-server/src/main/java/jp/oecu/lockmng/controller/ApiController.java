package jp.oecu.lockmng.controller;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jp.oecu.lockmng.config.TimeZoneConfig;
import jp.oecu.lockmng.entity.LockState;
import jp.oecu.lockmng.entity.Reservation;
import jp.oecu.lockmng.model.ReservationInfoModel;
import jp.oecu.lockmng.model.ReservationInfoRequestModel;
import jp.oecu.lockmng.service.LockStateService;
import jp.oecu.lockmng.service.ReservationService;
import jp.oecu.lockmng.util.Result;

import org.springframework.web.bind.annotation.PostMapping;

@RestController
public class ApiController {

    private final LockStateService lockStateService;
    private final ReservationService reservationService;
    private final TimeZoneConfig timeZoneConfig;

    @Autowired
    public ApiController(ReservationService reservationService, TimeZoneConfig timeZoneConfig, LockStateService lockStateService) {
        this.reservationService = reservationService;
        this.timeZoneConfig = timeZoneConfig;
        this.lockStateService = lockStateService;
    }

    @PostMapping("/api/lock/get")
    public List<LockState> getLockState() {
        Result<List<LockState>> result = lockStateService.findAll();
        if(result.isSuccess()){
            return result.getValue();
        }else{
            return null;
        }
    }
    

    @PostMapping("/api/resv/get")
    public ReservationInfoModel getResvInfo(@RequestBody @Valid ReservationInfoRequestModel model, BindingResult bindingResult) {
        ReservationInfoModel result = new ReservationInfoModel();
        if(bindingResult.hasErrors()){
            StringBuilder sb = new StringBuilder();
            for (FieldError e : bindingResult.getFieldErrors()) {
                sb.append(e.getDefaultMessage() + "\n");
            }
            result.setSuccess(false);
            result.setMessage(sb.toString());
            return result;
        }
        ZonedDateTime start;
        try{
            start = ZonedDateTime.of(model.getYear(), model.getMonth(), model.getDay(), 0, 0, 0, 0, timeZoneConfig.getZoneId());
            start = start.withZoneSameInstant(ZoneId.of("UTC"));
        }catch(DateTimeException e){
            result.setSuccess(false);
            result.setMessage(e.getLocalizedMessage());
            return result;
        }
        ZonedDateTime end = start.plusDays(1);
        Result<List<Reservation>> listResult = reservationService.findByPeriod(start, end, model.getLockId());
        if(listResult.isSuccess()){
            result.setSuccess(true);
            result.setMessage("ok");
            long day = end.toEpochSecond() - start.toEpochSecond();
            List<Double[]> list = new ArrayList<Double[]>();
            for(Reservation r : listResult.getValue()){
                Double ratioStart, ratioEnd;
                if(r.getStartTimeUtc().isBefore(start)){
                    ratioStart = 0.0;
                }else{
                    ratioStart = (r.getStartTimeUtc().toEpochSecond() - start.toEpochSecond()) / (double)day;
                }
                if(r.getEndTimeUtc().isAfter(end)){
                    ratioEnd = 1.0;
                }else{
                    ratioEnd = (r.getEndTimeUtc().toEpochSecond() - start.toEpochSecond()) / (double)day;
                }
                list.add(new Double[]{ratioStart, ratioEnd});
            }
            result.setList(list);
        }else{
            result.setSuccess(false);
            result.setMessage(listResult.getError());
        }
        return result;
    }
}