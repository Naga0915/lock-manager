package jp.oecu.lockmng.repository.jpa;

import org.springframework.stereotype.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import jp.oecu.lockmng.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>{
    Optional<User> findByIdStr(String idStr);
}
