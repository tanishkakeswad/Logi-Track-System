package com.edutech.logisticsmanagementandtrackingsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.edutech.logisticsmanagementandtrackingsystem.entity.DriverLocation;


public interface DriverLocationRepository extends JpaRepository<DriverLocation, Long> {
}