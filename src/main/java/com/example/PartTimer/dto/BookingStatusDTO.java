package com.example.PartTimer.dto;

import lombok.Data;

@Data
public class BookingStatusDTO {
    private String status;

    public BookingStatusDTO(String status) {
        this.status = status;
    }
}
