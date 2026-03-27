package com.edutech.logisticsmanagementandtrackingsystem.entity;

import javax.persistence.*;
 
@Entity
public class Cargo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;
    private String size;
    private String status; 

    @ManyToOne(fetch = FetchType.EAGER)
    private Business business;

    @ManyToOne(fetch = FetchType.EAGER)
    private Driver driver;

    
    
    public Cargo(String content, String size, String status, Business business, Driver driver) {
        this.content = content;
        this.size = size;
        this.status = status;
        this.business = business;
        this.driver = driver;
    }

    public Cargo() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Business getBusiness() {
        return business;
    }

    public void setBusiness(Business business) {
        this.business = business;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }
}