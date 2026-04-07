package com.edutech.logisticsmanagementandtrackingsystem.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.persistence.*;

@Entity
public class Cargo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ NEW: 11-digit AWB stored as String
    @Column(length = 11, unique = true)
    private String awb;

    private String content;
    private String size;
    private String status;

    private String source;

    @ManyToOne(fetch = FetchType.EAGER)
    private Business business;

    @ManyToOne(fetch = FetchType.EAGER)
    private Driver driver;

    @OneToMany(mappedBy = "cargo", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnoreProperties("cargo")
    private List<CargoDocument> documents;

    public Cargo(String content, String size, String status, String source, Business business, Driver driver) {
        this.content = content;
        this.size = size;
        this.status = status;
        this.source = source;
        this.business = business;
        this.driver = driver;
    }

    public Cargo() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    // ✅ AWB getter/setter
    public String getAwb() { return awb; }
    public void setAwb(String awb) { this.awb = awb; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Business getBusiness() { return business; }
    public void setBusiness(Business business) { this.business = business; }

    public Driver getDriver() { return driver; }
    public void setDriver(Driver driver) { this.driver = driver; }

    public Long getCargoId() { return this.id; }

    public List<CargoDocument> getDocuments() { return documents; }
    public void setDocuments(List<CargoDocument> documents) { this.documents = documents; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getCargoContent() { return this.content; }
    public void setCargoContent(String cargoContent) { this.content = cargoContent; }

    public String getCargoSize() { return this.size; }
    public void setCargoSize(String cargoSize) { this.size = cargoSize; }
}
