package com.edutech.logisticsmanagementandtrackingsystem.entity;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

@Entity
public class Driver {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;

    @OneToOne
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    
    @OneToMany(mappedBy = "driver", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Cargo> assignedCargos;

    

    public Driver() {
    }

    
   
    public Driver(Long id, String name, String email, User user, List<Cargo> assignedCargos) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.user = user;
        this.assignedCargos = assignedCargos;
    }

    

    public Driver(String name, String email, User user, List<Cargo> assignedCargos) {
        this.name = name;
        this.email = email;
        this.user = user;
        this.assignedCargos = assignedCargos;
    }



    public Driver(String name, String email, List<Cargo> assignedCargos) {
        this.name = name;
        this.email = email;
        this.assignedCargos = assignedCargos;
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



    public List<Cargo> getAssignedCargos() {
        return assignedCargos;
    }



    public void setAssignedCargos(List<Cargo> assignedCargos) {
        this.assignedCargos = assignedCargos;
    }


    
    
}