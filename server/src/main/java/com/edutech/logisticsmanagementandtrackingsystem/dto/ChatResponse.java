package com.edutech.logisticsmanagementandtrackingsystem.dto;

import java.time.LocalDateTime;

public class ChatResponse {

    private String reply;
    private String matchedIntent;
    private LocalDateTime timestamp;

    public ChatResponse() {}

    public ChatResponse(String reply, String matchedIntent) {
        this.reply = reply;
        this.matchedIntent = matchedIntent;
        this.timestamp = LocalDateTime.now();
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getMatchedIntent() {
        return matchedIntent;
    }

    public void setMatchedIntent(String matchedIntent) {
        this.matchedIntent = matchedIntent;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}