package jp.oecu.lockmng.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jp.oecu.lockmng.entity.LockHist;
import java.util.List;



@Repository
public interface LockHistRepository extends JpaRepository<LockHist, Integer> {
    public List<LockHist> findByUserId(Integer userId);
    public List<LockHist> findByLockId(Integer lockId);
}
