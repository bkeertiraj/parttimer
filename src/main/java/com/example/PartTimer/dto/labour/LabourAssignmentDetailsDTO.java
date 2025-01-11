package com.example.PartTimer.dto.labour;

import lombok.Data;

import java.time.LocalDate;

@Data
public class LabourAssignmentDetailsDTO {
    private LocalDate date;
    private String timeSlot;
    private String status;
    private String description;
    private String location;
    private String zipcode;
    private String city;

    private SelectedLabourDTO selectedLabour;
}
