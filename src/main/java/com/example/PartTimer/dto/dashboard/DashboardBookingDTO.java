package com.example.PartTimer.dto.dashboard;

import com.example.PartTimer.entities.BookingStatus;
import com.example.PartTimer.entities.PaymentStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardBookingDTO {
    private Long bookingId;
    private String serviceName;
    private String customerName;
    private String date;
    private String time;
    private BookingStatus status;
    private PaymentStatus paymentStatus;
}
