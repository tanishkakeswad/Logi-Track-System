package com.edutech.logisticsmanagementandtrackingsystem.dto;

public class ChatRequest {

    private String message;
    private String context; // optional (page name, cargoId, etc.)

    public ChatRequest() {}

    public ChatRequest(String message, String context) {
        this.message = message;
        this.context = context;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }
}