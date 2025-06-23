package jp.oecu.lockmng.service;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.oecu.lockmng.config.TimeZoneConfig;
import jp.oecu.lockmng.config.properties.LockConfig;
import jp.oecu.lockmng.config.properties.ReservationConfig;
import jp.oecu.lockmng.entity.Reservation;
import jp.oecu.lockmng.entity.User;
import jp.oecu.lockmng.model.NewReserveModel;
import jp.oecu.lockmng.repository.jpa.ReservationRepository;
import jp.oecu.lockmng.util.CustomUserDetails;
import jp.oecu.lockmng.util.Result;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final TimeZoneConfig timeZoneConfig;
    private final LockConfig lockConfig;
    private final ReservationConfig reservationConfig;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository, TimeZoneConfig timeZoneConfig, LockConfig lockConfig, ReservationConfig reservationConfig) {
        this.reservationRepository = reservationRepository;
        this.timeZoneConfig = timeZoneConfig;
        this.lockConfig = lockConfig;
        this.reservationConfig = reservationConfig;
    }

    public Result<List<Reservation>> findAllUTC(){
        try{
            return Result.success(reservationRepository.findAll());
        }catch(Exception e){
            log.error("全予約情報取得時に予約サービスでエラー", e);
            return Result.error(e.getLocalizedMessage());
        }
    }
    
    public Result<List<Reservation>> findByPeriod(ZonedDateTime start, ZonedDateTime end, Integer lockId){
        try{
            return Result.success(reservationRepository.findByPeriodRead(start, end, lockId));
        }catch(Exception e){
            log.error("予約情報取得時に予約サービスでエラー", e);
            return Result.error(e.getLocalizedMessage());
        }
    }

    public Result<List<Reservation>> findByTimeAndUser(ZonedDateTime time, CustomUserDetails userDetails){
        try{
            return Result.success(reservationRepository.findByTimeAndUserRead(time, userDetails.getUser().getId()));
        }catch(Exception e){
            log.error("予約情報取得時に予約サービスでエラー", e);
            return Result.error(e.getLocalizedMessage());
        }
    }

    @Transactional
    public Result<Boolean> newReserve(NewReserveModel model, User user){
        if(user == null){
            return Result.error("無効なユーザ");
        }
        if(!user.isEnabled()){
            return Result.error("無効なユーザ");
        }
        try{
            ZonedDateTime startTime = model.getStartTime().atZone(timeZoneConfig.getZoneId());
            ZonedDateTime endTime = model.getEndTime().atZone(timeZoneConfig.getZoneId());
            if(reservationConfig.getDivided_time_minute() > 0){
                startTime = floorZonedDateTime(startTime, reservationConfig.getDivided_time_minute());
                endTime = floorZonedDateTime(endTime, reservationConfig.getDivided_time_minute());
            }
            if(startTime.isBefore(ZonedDateTime.now(timeZoneConfig.getZoneId()))){
                return Result.error("過去に予約することはできません");
            }
            if(startTime.isAfter(endTime)){
                return Result.error("開始時刻は終了時刻の前である必要があります");
            }
            if(model.getLockId() >= lockConfig.getNum() || model.getLockId() < 0){
                return Result.error("鍵IDが範囲外");
            }
            if(reservationRepository.countOverlapWrite(startTime, endTime, model.getLockId()) > 0){
                return Result.error("その時間帯はすでに予約されています");
            }
            if(endTime.minusMinutes(reservationConfig.getMin_duration_minute()).isBefore(startTime) || endTime.minusMinutes(reservationConfig.getMax_duration_minute()).isAfter(startTime)){
                return Result.error(String.format("予約は%d分間以上%d分間以内の期間である必要があります", reservationConfig.getMin_duration_minute(), reservationConfig.getMax_duration_minute()));
            }
            if(!reservationConfig.getAvailable_start().equals(reservationConfig.getAvailable_end())){
                if(startTime.toLocalTime().isBefore(reservationConfig.getAvailable_start())){
                    return Result.error(String.format("予約は %s 以降を指定してください", reservationConfig.getAvailable_start().toString()));
                }
                if(endTime.toLocalTime().isAfter(reservationConfig.getAvailable_end())){
                    return Result.error(String.format("予約は %s 以前を指定してください", reservationConfig.getAvailable_end().toString()));
                }
            }
            Reservation r = new Reservation();
            r.setLockId(model.getLockId());
            r.setStartTimeUtc(startTime);
            r.setEndTimeUtc(endTime);
            r.setUserId(user.getId());
            reservationRepository.save(r);
            return Result.success(true);
        }catch(Exception e){
            log.error("新規予約時に予約サービスでエラー", e);
            return Result.error(e.getLocalizedMessage());
        }
    }

    public static ZonedDateTime floorZonedDateTime(ZonedDateTime dateTime, int minutesUnit) {
        int minute = dateTime.getMinute();
        int flooredMinute = (minute / minutesUnit) * minutesUnit;

        return dateTime
            .withMinute(flooredMinute)
            .withSecond(0)
            .withNano(0);
    }
}