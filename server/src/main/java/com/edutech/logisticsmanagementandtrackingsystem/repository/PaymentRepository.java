package com.edutech.logisticsmanagementandtrackingsystem.repository;

import com.edutech.logisticsmanagementandtrackingsystem.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;   // ✅ ADD THIS LINE

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByCargoId(Long cargoId);
}
