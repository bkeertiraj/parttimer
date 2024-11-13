package com.example.PartTimer.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class BookingAssignmentDTO {

    private Long bookingId;
    private Long organizationId;
    private List<Long> assignedEmployeeIds;

    private String status;
    private String address;
    private String serviceName;

    private String description;

    private LocalDate date;
    private LocalTime time;
    private String feedback;
    private Integer rating;

    private String paymentStatus;
    private String location;
    private String zip;
    private Double agreedPrice;
    private String clientEmail;
}
