package com.edutech.logisticsmanagementandtrackingsystem.repository;

import com.edutech.logisticsmanagementandtrackingsystem.entity.CargoDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CargoDocumentRepository extends JpaRepository<CargoDocument, Long> {

    List<CargoDocument> findByCargoId(Long cargoId);
}