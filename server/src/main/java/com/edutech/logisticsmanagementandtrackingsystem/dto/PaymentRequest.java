package com.edutech.logisticsmanagementandtrackingsystem.dto;

public class PaymentRequest {

    private String sourceCity;
    private String destinationCity;

    // ✅ keep name "size" to avoid conflicts
    // ✅ meaning: weight in KG
    private double size;

    private Long driverId;
    private Long cargoId;

    public PaymentRequest() {}

    public String getSourceCity() { return sourceCity; }
    public void setSourceCity(String sourceCity) { this.sourceCity = sourceCity; }

    public String getDestinationCity() { return destinationCity; }
    public void setDestinationCity(String destinationCity) { this.destinationCity = destinationCity; }

    public double getSize() { return size; }
    public void setSize(double size) { this.size = size; }

    public Long getDriverId() { return driverId; }
    public void setDriverId(Long driverId) { this.driverId = driverId; }

    public Long getCargoId() { return cargoId; }
    public void setCargoId(Long cargoId) { this.cargoId = cargoId; }
}