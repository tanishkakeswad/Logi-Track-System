package com.edutech.logisticsmanagementandtrackingsystem.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class DriverLocation {

@Id
private Long driverId;

private double lat;
private double lng;

public Long getDriverId() { return driverId; }
public void setDriverId(Long driverId) { this.driverId = driverId; }

public double getLat() { return lat; }
public void setLat(double lat) { this.lat = lat; }

public double getLng() { return lng; }
public void setLng(double lng) { this.lng = lng; }
}