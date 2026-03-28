package com.edutech.logisticsmanagementandtrackingsystem.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginResponse {
   //Implement required code here!!!
   private String token;
   private String username;
   private String email;
   private String role;

   @JsonCreator
   public LoginResponse(@JsonProperty String token,@JsonProperty String username,@JsonProperty String email,@JsonProperty String role) {
      this.token = token;
      this.username = username;
      this.email = email;
      this.role = role;
   }
   
}

