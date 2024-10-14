package com.example.PartTimer.dto;

import lombok.Data;

@Data
public class BookingRequestDTO {
    private Long serviceId;
    private String customerName;
    private String email;
    private String location;
    private String date;
    private String time;
    private String description;

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }
}
