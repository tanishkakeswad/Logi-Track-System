package com.edutech.logisticsmanagementandtrackingsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.edutech.logisticsmanagementandtrackingsystem.entity.Cargo;

import java.util.List;
import java.util.Optional;

@Repository
public interface CargoRepository extends JpaRepository<Cargo, Long> {

    // existing methods (kept as-is)
    List<Cargo> findByBusinessId(Long businessId);
    List<Cargo> findByDriverId(Long driverId);

    // ✅ NEW: lookup by AWB (required for AWB tracking)
    Optional<Cargo> findByAwb(String awb);
}