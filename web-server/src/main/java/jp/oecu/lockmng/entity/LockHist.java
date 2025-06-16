package jp.oecu.lockmng.entity;

import java.time.ZonedDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "lock_hist")
@Data
public class LockHist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "datetime_utc", nullable = false)
    private ZonedDateTime datetimeUtc;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "lock_id", nullable = false)
    private Integer lockId;

    @Column(name = "ops", nullable = false)
    private String ops;
}