package jp.oecu.lockmng.service;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.oecu.lockmng.config.TimeZoneConfig;
import jp.oecu.lockmng.config.properties.LockConfig;
import jp.oecu.lockmng.entity.Reservation;
import jp.oecu.lockmng.entity.User;
import jp.oecu.lockmng.model.NewReserveModel;
import jp.oecu.lockmng.repository.jpa.ReservationRepository;
import jp.oecu.lockmng.util.Result;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final TimeZoneConfig timeZoneConfig;
    private final LockConfig lockConfig;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository, TimeZoneConfig timeZoneConfig, LockConfig lockConfig) {
        this.reservationRepository = reservationRepository;
        this.timeZoneConfig = timeZoneConfig;
        this.lockConfig = lockConfig;
    }
    
    public Result<List<Reservation>> findByPeriod(ZonedDateTime start, ZonedDateTime end, Integer lockId){
        try{
            return Result.success(reservationRepository.findByPeriodRead(start, end, lockId));
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
            ZonedDateTime starTime = model.getStartTime().atZone(timeZoneConfig.getZoneId());
            ZonedDateTime endTime = model.getEndTime().atZone(timeZoneConfig.getZoneId());
            if(starTime.isBefore(ZonedDateTime.now(timeZoneConfig.getZoneId()))){
                return Result.error("過去に予約することはできません");
            }
            if(starTime.isAfter(endTime)){
                return Result.error("開始時刻は終了時刻の前である必要があります");
            }
            if(model.getLockerId() >= lockConfig.getNum() || model.getLockerId() < 0){
                return Result.error("鍵IDが範囲外");
            }
            if(reservationRepository.countOverlapWrite(starTime, endTime, model.getLockerId()) > 0){
                return Result.error("その時間帯はすでに予約されています");
            }
            Reservation r = new Reservation();
            r.setLockId(model.getLockerId());
            r.setStartTimeUtc(starTime);
            r.setEndTimeUtc(endTime);
            r.setUserId(user.getId());
            reservationRepository.save(r);
            return Result.success(true);
        }catch(Exception e){
            log.error("新規予約時に予約サービスでエラー", e);
            return Result.error(e.getLocalizedMessage());
        }
    }
}