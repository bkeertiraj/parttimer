package com.example.PartTimer.dto;

import com.example.PartTimer.entities.Booking;
import com.example.PartTimer.entities.Organization;
import lombok.Data;

@Data
public class ServiceAssignmentDTO {
    private Long bookingId;
    private Long organizationId;
    private Double agreedPrice;
    private Double prevPrice;

    private String serviceName;
    private String description;

    private String bookingStatus;
    private String paymentStatus;

    private String date;
    private String time;
    private String address;
    private String location;
    private String zip;
}
