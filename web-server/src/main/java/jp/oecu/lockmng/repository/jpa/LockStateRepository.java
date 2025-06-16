package jp.oecu.lockmng.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jp.oecu.lockmng.entity.LockState;

@Repository
public interface LockStateRepository extends JpaRepository<LockState, Integer> {

}
