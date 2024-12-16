package com.example.PartTimer.dto.user_dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserBookingsDTO {
    private Long bookingId;
    private LocalDate date;
    private LocalTime time;
    private String city;
    private String zipcode;
    private String description; // using description as note
    private String status;
    private Double offeredPrice;
    private String serviceName;
}
