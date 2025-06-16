package jp.oecu.lockmng.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "lock_state")
@Data
public class LockState {
    @Id
    private Integer id;

    /*
     * l: 施錠されている
     * u: 解錠されている
     */
    @Column(name = "state", nullable = false, length = 1)
    private String state;
}