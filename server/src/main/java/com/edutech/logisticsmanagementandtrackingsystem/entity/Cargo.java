package com.edutech.logisticsmanagementandtrackingsystem.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

@Entity
public class Cargo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Existing fields
    private String content;
    private String size;
    private String status;




    // ✅ NEW FIELD (to support cargo.getSource())
    private String source;

    @ManyToOne(fetch = FetchType.EAGER)
    private Business business;

    @ManyToOne(fetch = FetchType.EAGER)
    private Driver driver;

    @OneToMany(mappedBy = "cargo", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnoreProperties("cargo")
    private List<CargoDocument> documents;

    // ✅ Updated constructor (added source)
    public Cargo(String content, String size, String status, String source, Business business, Driver driver) {
        this.content = content;
        this.size = size;
        this.status = status;
        this.source = source;
        this.business = business;
        this.driver = driver;
    }

    public Cargo() {
    }

    // =========================
    // Getters & Setters
    // =========================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // Existing getter/setter
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    // Existing getter/setter
    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    // Existing getter/setter
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

    // ✅ You already had this – keeping it
    public Long getCargoId() {
        return this.id;
    }

    public List<CargoDocument> getDocuments() {
        return documents;
    }

    public void setDocuments(List<CargoDocument> documents) {
        this.documents = documents;
    }

    // =========================
    // ✅ NEW METHODS to fix Maven error
    // =========================

    // ✅ This fixes: cargo.getSource()
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    // ✅ Alias getter to support: cargo.getCargoContent()
    public String getCargoContent() {
        return this.content;
    }

    public void setCargoContent(String cargoContent) {
        this.content = cargoContent;
    }

    // ✅ Alias getter to support: cargo.getCargoSize()
    public String getCargoSize() {
        return this.size;
    }

    public void setCargoSize(String cargoSize) {
        this.size = cargoSize;
    }
}
