package com.example.PartTimer.dto.dashboard;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardServiceAssignmentDTO {
    private Long id;
    private Long bookingId;
    private String serviceName;
    private Double agreedPrice;
}
