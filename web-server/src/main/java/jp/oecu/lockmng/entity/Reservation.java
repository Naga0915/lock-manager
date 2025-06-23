package jp.oecu.lockmng.entity;

import java.time.ZonedDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "res")
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "start_time_utc", nullable = false)
    private ZonedDateTime startTimeUtc;

    @Column(name = "end_time_utc", nullable = false)
    private ZonedDateTime endTimeUtc;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "lock_id", nullable = false)
    private Integer lockId;
}