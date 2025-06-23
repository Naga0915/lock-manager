package jp.oecu.lockmng.service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jp.oecu.lockmng.component.DeviceManager;
import jp.oecu.lockmng.entity.Reservation;
import jp.oecu.lockmng.util.CustomUserDetails;
import jp.oecu.lockmng.util.Result;

@Service
public class DeviceService {
    private final DeviceManager deviceManager;
    private final ReservationService reservationService;

    @Autowired
    public DeviceService(DeviceManager deviceManager, ReservationService reservationService){
        this.deviceManager = deviceManager;
        this.reservationService = reservationService;
    }

    public Optional<String> lock(CustomUserDetails userDetails, Integer lockId){
        Result<List<Reservation>> result = reservationService.findByTimeAndUser(ZonedDateTime.now(), userDetails);
        if(result.isSuccess()){
            List<Reservation> list = result.getValue();
            for (Reservation reservation : list) {
                if(reservation.getLockId().equals(lockId)){
                    return deviceManager.tryLock(lockId);
                }
            }
            return Optional.of("施錠失敗: その鍵IDは予約されていません。");
        }else{
            return Optional.of("施錠失敗: " +   result.getError());
        }
    }

    public Optional<String> unlock(CustomUserDetails userDetails, Integer lockId){
        Result<List<Reservation>> result = reservationService.findByTimeAndUser(ZonedDateTime.now(), userDetails);
        if(result.isSuccess()){
            List<Reservation> list = result.getValue();
            for (Reservation reservation : list) {
                if(reservation.getLockId().equals(lockId)){
                    return deviceManager.tryUnlock(lockId);
                }
            }
            return Optional.of("解錠失敗: その鍵IDは予約されていません。");
        }else{
            return Optional.of("解錠失敗: " +   result.getError());
        }
    }
}
