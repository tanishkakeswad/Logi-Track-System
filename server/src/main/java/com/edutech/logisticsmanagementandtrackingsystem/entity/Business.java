package com.edutech.logisticsmanagementandtrackingsystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

@Entity
public class Business {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;

    @OneToOne
    @JoinColumn(name="user_id",nullable = false)
    private User user;

    @OneToMany(mappedBy = "business", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Cargo> cargos;

    
    
    public Business(Long id, String name, String email, User user, List<Cargo> cargos) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.user = user;
        this.cargos = cargos;
    }

    public Business(String name, String email, List<Cargo> cargos) {
        this.name = name;
        this.email = email;
        this.cargos = cargos;
    }

    public Business() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Cargo> getCargos() {
        return cargos;
    }

    public void setCargos(List<Cargo> cargos) {
        this.cargos = cargos;
    }

   
}