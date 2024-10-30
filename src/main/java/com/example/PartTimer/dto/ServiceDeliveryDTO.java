package com.example.PartTimer.dto;

import lombok.Data;

import java.util.List;

@Data
public class ServiceDeliveryDTO {

    private Long id;
    private String customerName;
    private String status;
    private List<AssignedEmployeeDTO> assignedEmployees;
    private Double estimatedRevenue;
    private String area;
    private String address;
    private String date;
    private String time;
    private Integer progress;
}
