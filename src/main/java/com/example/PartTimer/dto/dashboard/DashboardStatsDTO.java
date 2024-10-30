package com.example.PartTimer.dto.dashboard;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardStatsDTO {
    private long totalEmployees;
    private long totalServices;
    private long totalBookings;
    private long completedBookings;
    private long pendingBookings;
}
