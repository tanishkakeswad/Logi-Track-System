package com.edutech.logisticsmanagementandtrackingsystem.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.edutech.logisticsmanagementandtrackingsystem.entity.OtpEntity;

@Repository
public interface OtpRepository extends JpaRepository<OtpEntity, Long> {
  Optional<OtpEntity> findByEmail(String email);
}
