package com.edutech.logisticsmanagementandtrackingsystem.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.edutech.logisticsmanagementandtrackingsystem.entity.Business;
import com.edutech.logisticsmanagementandtrackingsystem.repository.BusinessRepository;

@Service
public class BusinessService {

    private static final Logger logger =
            LoggerFactory.getLogger(BusinessService.class);

    @Autowired
    private BusinessRepository businessRepository;

    // =========================
    // CREATE BUSINESS PROFILE
    // =========================
    public Business createBusiness(Business business) {

        if (business == null) {
            logger.warn("BUSINESS-SERVICE: Attempted to create null Business entity");
            return null;
        }

        logger.info(
            "BUSINESS-SERVICE: Creating business profile | name={} | email={}",
            business.getName(),
            business.getEmail()
        );

        try {
            Business savedBusiness = businessRepository.save(business);

            logger.info(
                "BUSINESS-SERVICE: Business profile created successfully | businessId={}",
                savedBusiness.getId()
            );

            return savedBusiness;

        } catch (Exception ex) {
            logger.error(
                "BUSINESS-SERVICE: Failed to create business profile | name={} | Reason={}",
                business.getName(),
                ex.getMessage(),
                ex
            );
            throw ex; // important: don't swallow exception
        }
    }
}