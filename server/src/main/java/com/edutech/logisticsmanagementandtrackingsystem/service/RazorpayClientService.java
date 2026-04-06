package com.edutech.logisticsmanagementandtrackingsystem.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class RazorpayClientService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String secret;

    @Value("${razorpay.currency:INR}")
    private String currency;

    public Map<String, Object> createOrder(long amountPaise, String receipt) {

        String url = "https://api.razorpay.com/v1/orders";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(keyId, secret);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("amount", amountPaise);
        body.put("currency", currency);
        body.put("receipt", receipt);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new IllegalArgumentException("Failed to create Razorpay order.");
        }

        return response.getBody();
    }

    public String getPublicKey() {
        return keyId;
    }

    public String getSecret() {
        return secret;
    }

    public String getCurrency() {
        return currency;
    }
}