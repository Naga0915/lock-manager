package jp.oecu.lockmng.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jp.oecu.lockmng.entity.NewRegister;

@Repository
public interface NewRegisterRepository extends JpaRepository<NewRegister, Integer>{
    public boolean existsByUuid(String uuid);
}
