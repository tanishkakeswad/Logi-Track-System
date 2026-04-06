package com.edutech.logisticsmanagementandtrackingsystem.dto;

public class CargoStatusResponse {

    private Long cargoId;
    private String status;
    private String source;
    private Long driverId;
    private String cargoContent;
    private String cargoSize;

    public Long getCargoId() {
        return cargoId;
    }

    public void setCargoId(Long cargoId) {
        this.cargoId = cargoId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public String getCargoContent() {
        return cargoContent;
    }

    public void setCargoContent(String cargoContent) {
        this.cargoContent = cargoContent;
    }

    public String getCargoSize() {
        return cargoSize;
    }

    public void setCargoSize(String cargoSize) {
        this.cargoSize = cargoSize;
    }
}