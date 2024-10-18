package com.example.PartTimer.dto;

import com.example.PartTimer.entities.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import com.example.PartTimer.entities.ServiceRequestStatus;

@Data
public class ServiceRequestDTO {
    private Long id;
    private Long userId; // ID of the user making the request
    private Long serviceId; // ID of the requested service
    //private LocalDateTime requestDate; // Date and time of the request
    private String status; // Status of the request
    private String address; // Address for the service
    private Long organizationId; // ID of the confirmed organization
    private String organizationName; // Name of the organization
    private Double agreedPrice; // Agreed price for the service
    private String comments; // Comments or instructions
    private Integer rating; // Rating given by the user
    private String feedback; // Feedback comments
    private LocalDate date;
    private LocalTime time;
    private PaymentStatus paymentStatus;

    private List<OrganizationDTO> availableOrganizations;

    private List<String> ownerNames;
    private List<String> coOwnerNames;

//    @JsonProperty("status")
//    private String frontendStatus;
}
