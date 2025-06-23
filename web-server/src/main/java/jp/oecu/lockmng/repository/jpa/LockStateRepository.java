package jp.oecu.lockmng.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;
import jp.oecu.lockmng.entity.LockState;

@Repository
public interface LockStateRepository extends JpaRepository<LockState, Integer> {
    @Modifying
    @Transactional
    @Query("DELETE FROM LockState")
    public void deleteAllLockStates();
}