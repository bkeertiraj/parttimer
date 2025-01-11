package com.example.PartTimer.dto.labour;

import com.example.PartTimer.entities.labour.LabourFeedbackType;
import lombok.Data;

@Data
public class LabourReviewResponseDTO {
    private Long id;
    private Long bookingId;
    private Long labourId;
    private String labourName;
    private String userName;
    private Integer rating;
    private String review;
    private LabourFeedbackType feedbackType;
    private String feedbackDate;
}
