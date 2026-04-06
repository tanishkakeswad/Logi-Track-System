package com.edutech.logisticsmanagementandtrackingsystem.service;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PricingService {

    private static final double BASE_RATE = 8.0;     // per km
    private static final double WEIGHT_RATE = 2.0;   // per kg
    private static final double MAX_WEIGHT = 10000.0;

    private static final Set<String> SUPPORTED_CITIES = new HashSet<>(Arrays.asList(
            "New Delhi", "Mumbai", "Kolkata", "Chennai", "Bengaluru", "Hyderabad", "Pune"
    ));

    private static final Map<String, Integer> DISTANCES_KM = new HashMap<>();

    static {
        // ✅ Replace these distances with your official matrix if needed.
        put("New Delhi", "Mumbai", 1415);
        put("New Delhi", "Kolkata", 1520);
        put("New Delhi", "Chennai", 2190);
        put("New Delhi", "Bengaluru", 2150);
        put("New Delhi", "Hyderabad", 1580);
        put("New Delhi", "Pune", 1450);

        put("Mumbai", "Kolkata", 1960);
        put("Mumbai", "Chennai", 1330);
        put("Mumbai", "Bengaluru", 985);
        put("Mumbai", "Hyderabad", 710);
        put("Mumbai", "Pune", 150);

        put("Kolkata", "Chennai", 1660);
        put("Kolkata", "Bengaluru", 1870);
        put("Kolkata", "Hyderabad", 1500);
        put("Kolkata", "Pune", 2050);

        put("Chennai", "Bengaluru", 350);
        put("Chennai", "Hyderabad", 625);
        put("Chennai", "Pune", 1160);

        put("Bengaluru", "Hyderabad", 570);
        put("Bengaluru", "Pune", 840);

        put("Hyderabad", "Pune", 560);
    }

    private static void put(String a, String b, int km) {
        DISTANCES_KM.put(key(a, b), km);
    }

    private static String key(String a, String b) {
        String x = a.trim();
        String y = b.trim();
        return (x.compareToIgnoreCase(y) <= 0) ? x + "|" + y : y + "|" + x;
    }

    // ✅ size = weightKg
    public double calculateAmount(String source, String destination, double size) {
        validateCity(source);
        validateCity(destination);
        validateWeight(size);

        if (source.equalsIgnoreCase(destination)) {
            throw new IllegalArgumentException("Source and destination cannot be the same.");
        }

        Integer distance = DISTANCES_KM.get(key(source, destination));
        if (distance == null) {
            throw new IllegalArgumentException("Distance not found for route: " + source + " -> " + destination);
        }

        return (distance * BASE_RATE) + (size * WEIGHT_RATE);
    }

    private void validateWeight(double size) {
        if (size <= 0) throw new IllegalArgumentException("Weight must be greater than 0 KG.");
        if (size > MAX_WEIGHT) throw new IllegalArgumentException("Weight must be ≤ 10000 KG.");
    }

    private void validateCity(String city) {
        if (city == null || city.trim().isEmpty()) {
            throw new IllegalArgumentException("City is required.");
        }
        if (!SUPPORTED_CITIES.contains(city.trim())) {
            throw new IllegalArgumentException("Unsupported city: " + city + ". Supported: " + SUPPORTED_CITIES);
        }
    }
}