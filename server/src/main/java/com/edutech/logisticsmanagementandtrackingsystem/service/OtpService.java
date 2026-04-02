package com.edutech.logisticsmanagementandtrackingsystem.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.edutech.logisticsmanagementandtrackingsystem.entity.OtpEntity;
import com.edutech.logisticsmanagementandtrackingsystem.repository.OtpRepository;

@Service
public class OtpService {

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private EmailService emailService;

    public void generateAndSendOtp(String email) {
        String otp = String.valueOf((int)(Math.random() * 900000) + 100000);

        OtpEntity entity = otpRepository.findByEmail(email)
                .orElse(new OtpEntity());

        entity.setEmail(email);
        entity.setOtp(otp);
        entity.setExpiryTime(LocalDateTime.now().plusMinutes(5));

        otpRepository.save(entity);

        emailService.sendOtp(email, otp);
    }

    public boolean verifyOtp(String email, String otp) {
        Optional<OtpEntity> optional = otpRepository.findByEmail(email);

        if (optional.isPresent()) {
            OtpEntity entity = optional.get();

            return entity.getOtp().equals(otp) &&
                   entity.getExpiryTime().isAfter(LocalDateTime.now());
        }
        return false;
    }
}

