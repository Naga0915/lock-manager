package jp.oecu.lockmng.service;

import java.lang.StackWalker.Option;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.hibernate.sql.ast.tree.predicate.BooleanExpressionPredicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import jp.oecu.lockmng.entity.Reservation;
import jp.oecu.lockmng.entity.User;
import jp.oecu.lockmng.model.NewReserveModel;
import jp.oecu.lockmng.repository.jpa.ReservationRepository;
import jp.oecu.lockmng.util.Result;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public Result<List<Reservation>> findAll(){
        try{
            return Result.success(reservationRepository.findAll());
        }catch(Exception e){
            log.error("予約サービスでエラー", e);
            return Result.error(e.getLocalizedMessage());
        }
    }

    public Result<Boolean> isReserved(ZonedDateTime start, ZonedDateTime end, Integer lockId){
        try{
            return Result.success(!reservationRepository.findByPeriod(start, end, lockId).isEmpty());
        }catch(Exception e){
            log.error("予約サービスでエラー", e);
            return Result.error(e.getLocalizedMessage());
        }
    }

    public Result<List<Reservation>> findByPeriod(ZonedDateTime start, ZonedDateTime end, int lockId){
        try{
            return Result.success(reservationRepository.findByPeriod(start, end, lockId));
        }catch(Exception e){
            log.error("予約サービスでエラー", e);
            return Result.error(e.getLocalizedMessage());
        }
    }

    public Optional<String> newReservation(NewReserveModel model, User user){
        try{
            if(!reservationRepository.hasOverlap(model.getStartTime(), model.getEndTime(), model.getLockerId())){
                Reservation reservation = new Reservation();
                reservation.setUserId(user.getId());
                reservation.setStartTimeUtc(model.getStartTime());
                reservation.setEndTimeUtc(model.getEndTime());
                reservation.setLockId(model.getLockerId());
                reservationRepository.save(reservation);
                return Optional.empty();
            }else{
                return Optional.of("その期間はすでに予約されています。");
            }
        }catch(Exception e){
            log.error("予約サービスでエラー", e);
            return Optional.of(e.getLocalizedMessage());
        }
    }
}
