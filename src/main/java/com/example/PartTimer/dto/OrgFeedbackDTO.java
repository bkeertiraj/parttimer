package com.example.PartTimer.dto;

import lombok.Data;

@Data
public class OrgFeedbackDTO {
    private Integer userRating;   // Rating out of 5, for example
    private String userFeedback;  // Additional comments from the organization
}
