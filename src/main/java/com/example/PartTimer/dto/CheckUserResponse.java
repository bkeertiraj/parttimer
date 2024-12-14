package com.example.PartTimer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CheckUserResponse {
    private boolean exists;
    private boolean isProfileComplete;
    private String missingFields;

    private String userType;

    public CheckUserResponse(boolean exists, boolean isProfileComplete, String missingFields, String userType) {
        this.exists = exists;
        this.isProfileComplete = isProfileComplete;
        this.missingFields = missingFields;
        this.userType = userType;
    }
}
