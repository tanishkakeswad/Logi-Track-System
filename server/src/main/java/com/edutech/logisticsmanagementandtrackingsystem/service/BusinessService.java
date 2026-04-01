package com.edutech.logisticsmanagementandtrackingsystem.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.edutech.logisticsmanagementandtrackingsystem.entity.Business;
import com.edutech.logisticsmanagementandtrackingsystem.repository.BusinessRepository;

@Service
public class BusinessService {
    // implement service logic here
    
   @Autowired
private BusinessRepository businessRepository;

public Business createBusiness(Business business){
    return businessRepository.save(business);
}
}
