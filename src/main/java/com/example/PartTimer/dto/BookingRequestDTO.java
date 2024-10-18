package com.example.PartTimer.dto;

import lombok.Data;

@Data
public class BookingRequestDTO {
    private Long serviceId;
    private Long userId;  // Add userId to identify the user making the booking
    private String name;  // Add name for the person the booking is for
    private String email;  // Add email for the person the booking is for
    private String location;
    private String date;
    private String time;
    private String description;

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }
}
