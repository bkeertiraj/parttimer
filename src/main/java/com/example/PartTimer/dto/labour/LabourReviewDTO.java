package com.example.PartTimer.dto.labour;

import com.example.PartTimer.entities.labour.LabourFeedbackType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LabourReviewDTO {

    private Long bookingId;
    private Long labourId;
    private Integer rating;
    private String review;
    private LabourFeedbackType feedbackType;
}
