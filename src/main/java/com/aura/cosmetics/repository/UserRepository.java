package com.aura.cosmetics.repository;

import com.aura.cosmetics.entity.User;
import com.aura.cosmetics.enums.WholesaleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);
    boolean existsByEmail(String email);
    List<User> findByWholesaleStatus(WholesaleStatus status);
    List<User> findByWholesaleStatusNot(WholesaleStatus status);
}
