package com.edutech.logisticsmanagementandtrackingsystem.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    public void sendOtp(String toEmail, String otp) {

        // ✅ Never log OTP in production
        logger.info("EMAIL-SERVICE: OTP email send request | toEmail={}", toEmail);

        if (toEmail == null || toEmail.trim().isEmpty()) {
            logger.warn("EMAIL-SERVICE: OTP email blocked - toEmail is null/empty");
            return;
        }

        if (otp == null || otp.trim().isEmpty()) {
            logger.warn("EMAIL-SERVICE: OTP email blocked - otp is null/empty | toEmail={}", toEmail);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("OTP Verification for CARGOWALA registration");
            message.setText("Your OTP is: " + otp);

            mailSender.send(message);

            logger.info("EMAIL-SERVICE: OTP email sent successfully | toEmail={}", toEmail);

        } catch (Exception ex) {
            logger.error("EMAIL-SERVICE: Failed to send OTP email | toEmail={} | Reason={}",
                    toEmail, ex.getMessage(), ex);
            throw ex; // so controller/service can handle and show proper message
        }
    }
}
