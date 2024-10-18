package com.example.PartTimer.dto;

import lombok.Data;

@Data
public class FeedbackDTO {
    private Integer rating;   // Rating out of 5, for example
    private String feedback;  // Additional comments from the user
}
