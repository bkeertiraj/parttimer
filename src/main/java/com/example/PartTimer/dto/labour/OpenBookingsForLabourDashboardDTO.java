package com.example.PartTimer.dto.labour;

import com.example.PartTimer.entities.labour.LabourBookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpenBookingsForLabourDashboardDTO {
    private Long id;
    private LocalDate bookingDate;
    private String timeSlot;
    private String bookingNote;
    private LabourBookingStatus bookingStatus;
    private Long bookingId;
    private String city;
    private String zipcode;
}
