package com.edutech.logisticsmanagementandtrackingsystem.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.edutech.logisticsmanagementandtrackingsystem.dto.CargoStatusResponse;
import com.edutech.logisticsmanagementandtrackingsystem.entity.Cargo;
import com.edutech.logisticsmanagementandtrackingsystem.entity.Customer;
import com.edutech.logisticsmanagementandtrackingsystem.repository.CargoRepository;
import com.edutech.logisticsmanagementandtrackingsystem.repository.CustomerRepository;

@Service
public class CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    @Autowired
    private CargoRepository cargoRepository;

    @Autowired
    private CustomerRepository customerRepository;

    // =========================
    // CREATE CUSTOMER PROFILE
    // =========================
    public Customer createCustomer(Customer customer) {

        if (customer == null) {
            logger.warn("CUSTOMER-SERVICE: createCustomer called with null customer");
            return null;
        }

        logger.info("CUSTOMER-SERVICE: Creating customer profile | name={} | email={}",
                customer.getName(), customer.getEmail());

        try {
            Customer saved = customerRepository.save(customer);

            logger.info("CUSTOMER-SERVICE: Customer profile created successfully | customerId={}",
                    saved != null ? saved.getId() : null);

            return saved;

        } catch (Exception ex) {
            logger.error("CUSTOMER-SERVICE: Failed to create customer profile | email={} | Reason={}",
                    customer.getEmail(), ex.getMessage(), ex);
            throw ex;
        }
    }

    // =========================
    // VIEW CARGO STATUS (FULL DETAILS)
    // =========================
    public CargoStatusResponse viewCargoStatus(Long cargoId) {

        logger.info("CUSTOMER-SERVICE: View cargo status request | cargoId={}", cargoId);

        if (cargoId == null) {
            logger.warn("CUSTOMER-SERVICE: cargoId is null in viewCargoStatus");
            return null;
        }

        try {
            Cargo cargo = cargoRepository.findById(cargoId).orElse(null);

            if (cargo == null) {
                logger.warn("CUSTOMER-SERVICE: Cargo not found for status request | cargoId={}", cargoId);
                return null;
            }

            CargoStatusResponse response = new CargoStatusResponse();
            response.setCargoId(cargo.getId());
            response.setStatus(cargo.getStatus());
            response.setSource(cargo.getSource());
            response.setCargoContent(cargo.getCargoContent());
            response.setCargoSize(cargo.getCargoSize());

            // ✅ DRIVER NULL SAFE
            if (cargo.getDriver() != null) {
                response.setDriverId(cargo.getDriver().getId());
            } else {
                response.setDriverId(null);
            }

            logger.info("CUSTOMER-SERVICE: Cargo status returned successfully | cargoId={} | status={}",
                    cargo.getId(), cargo.getStatus());

            return response;

        } catch (Exception ex) {
            logger.error("CUSTOMER-SERVICE: Error while fetching cargo status | cargoId={} | Reason={}",
                    cargoId, ex.getMessage(), ex);
            throw ex;
        }
    }
}
