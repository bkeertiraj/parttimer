package com.example.PartTimer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckUserResponse {
    private boolean exists;
    private boolean isProfileComplete;
    private String missingFields;
}
