package com.eforsch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eforsch.entity.UserTokenEntity;

@Repository
public interface UserTokenRepository extends JpaRepository<UserTokenEntity, Long> {
    // You can define custom queries here if necessary, for example:
    UserTokenEntity findByToken(String token);
    void deleteByToken(String token);
}

