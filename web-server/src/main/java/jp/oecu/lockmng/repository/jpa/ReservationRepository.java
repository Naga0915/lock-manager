package jp.oecu.lockmng.repository.jpa;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import jp.oecu.lockmng.entity.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
    @Query("""
    SELECT CASE WHEN COUNT(r) > 0 THEN TRUE ELSE FALSE END
    FROM Reservation r
    WHERE r.startTimeUtc < :end
      AND r.endTimeUtc > :start
      AND r.lockId = :lockId
    """)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public boolean hasOverlap(@Param("start") ZonedDateTime start, @Param("end") ZonedDateTime end, @Param("lockId") Integer lockId);

    @Query("""
    SELECT r
    FROM Reservation r
    WHERE r.startTimeUtc < :end
      AND r.endTimeUtc > :start
      AND r.lockId = :lockId
    """)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public List<Reservation> findByPeriod(@Param("start") ZonedDateTime start, @Param("end") ZonedDateTime end, @Param("lockId") Integer lockId);

    @Lock(LockModeType.PESSIMISTIC_READ)
    public List<Reservation> findAll();

    //public void save(Reservation reservation);
}
