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
        SELECT r
        FROM Reservation r
        WHERE r.startTimeUtc < :end
        AND r.endTimeUtc > :start
        AND r.lockId = :lockId
    """)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public List<Reservation> findByPeriodWrite(
        @Param("start") ZonedDateTime start, 
        @Param("end") ZonedDateTime end, 
        @Param("lockId") Integer lockId
    );

    @Query("""
        SELECT r
        FROM Reservation r
        WHERE r.startTimeUtc < :end
        AND r.endTimeUtc > :start
        AND r.lockId = :lockId
    """)
    public List<Reservation> findByPeriodRead(
        @Param("start") ZonedDateTime start, 
        @Param("end") ZonedDateTime end, 
        @Param("lockId") Integer lockId
    );
    
    @Query("""
        SELECT COUNT(r)
        FROM Reservation r
        WHERE r.startTimeUtc < :end
        AND r.endTimeUtc > :start
        AND r.lockId = :lockId
    """)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public long countOverlapWrite(
        @Param("start") ZonedDateTime start,
        @Param("end") ZonedDateTime end,
        @Param("lockId") Integer lockId
    );

    @Query("""
        SELECT COUNT(r)
        FROM Reservation r
        WHERE r.startTimeUtc < :end
        AND r.endTimeUtc > :start
        AND r.lockId = :lockId
    """)
    public boolean countOverlapRead(
        @Param("start") ZonedDateTime start,
        @Param("end") ZonedDateTime end,
        @Param("lockId") Integer lockId
    );

    @Query("""
        SELECT r
        FROM Reservation r
        WHERE r.startTimeUtc < :time
        AND r.endTimeUtc > :time
        AND r.userId = :userId
    """)
    public List<Reservation> findByTimeAndUserRead(
        @Param("time") ZonedDateTime time, 
        @Param("userId") Integer userId
    );
}
