package com.example.PartTimer.dto;

import com.example.PartTimer.entities.PaymentStatus;
import com.example.PartTimer.entities.UserRole;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

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
    private String fullName;
    private UserRole role;

    private List<OrganizationDTO> availableOrganizations;
////
    private String organizationOwnerName = "";
    private List<String> organizationCoOwnerNames = new ArrayList<>();;

//    @JsonProperty("status")
//    private String frontendStatus;

    private List<EmployeeDTO> employeesInvolved; // New field for service providers

    // Add setter method for employees
//    public void setEmployeesInvolved(List<String> employees) {
//        this.employeesInvolved = employees;
//    }

//    public void setAvailableOrganizations(List<OrganizationDTO> availableOrganizations) {
//    }

    // Add getters and setters
    public String getOrganizationOwnerName() {
        return organizationOwnerName;
    }

    public void setOrganizationOwnerName(String organizationOwnerName) {
        this.organizationOwnerName = organizationOwnerName;
    }

    public List<String> getOrganizationCoOwnerNames() {
        return organizationCoOwnerNames;
    }

    public void setOrganizationCoOwnerNames(List<String> organizationCoOwnerNames) {
        this.organizationCoOwnerNames = organizationCoOwnerNames;
    }


    public List<EmployeeDTO> getEmployeesInvolved() {
        return employeesInvolved;
    }

    public void setEmployeesInvolved(List<EmployeeDTO> employeesInvolved) {
        this.employeesInvolved = employeesInvolved;
    }
}
