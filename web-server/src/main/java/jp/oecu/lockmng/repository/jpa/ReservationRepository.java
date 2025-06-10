package jp.oecu.lockmng.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import jp.oecu.lockmng.entity.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
    
}
