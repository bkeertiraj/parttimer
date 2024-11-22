package com.example.PartTimer.dto;

import lombok.Data;

@Data
public class AuthHeaderRequest {
    private String authHeader;

    // Getters and setters
    public String getAuthHeader() {
        return authHeader;
    }

    public void setAuthHeader(String authHeader) {
        this.authHeader = authHeader;
    }

}
