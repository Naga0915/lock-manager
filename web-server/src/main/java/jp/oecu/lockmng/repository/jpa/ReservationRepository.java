package jp.oecu.lockmng.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import jakarta.persistence.LockModeType;
import jp.oecu.lockmng.entity.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
    @Query("""
    SELECT r
    FROM Reservation r
    WHERE r.startTimeUtc > :end
    AND r.endTimeUtc < :start
    AND r.lockId = :lockId
    """)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public List<Reservation> findByPeriod();
}
