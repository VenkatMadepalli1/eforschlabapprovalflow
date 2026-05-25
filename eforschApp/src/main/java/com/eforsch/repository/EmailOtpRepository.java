package com.eforsch.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eforsch.entity.EmailOtpEntity;

@Repository
public interface EmailOtpRepository extends JpaRepository<EmailOtpEntity, Long> {
    Optional<EmailOtpEntity> findByEmail(String email);
    void deleteByEmail(String email);
}
