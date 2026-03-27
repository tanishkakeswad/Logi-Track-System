package com.edutech.logisticsmanagementandtrackingsystem.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.edutech.logisticsmanagementandtrackingsystem.entity.Cargo;

import java.util.List;

@Repository
public interface CargoRepository extends JpaRepository<Cargo,Long>{
    // extend jpa repository to add custom query methods if needed
    public List<Cargo> findByBusinessId(Long id);
    public List<Cargo> findByDriverId(Long id);

}
