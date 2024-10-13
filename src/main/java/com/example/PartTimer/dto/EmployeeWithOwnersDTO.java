package com.example.PartTimer.dto;

import lombok.Data;

import java.util.List;

@Data
public class EmployeeWithOwnersDTO {
    private Long employeeId;
    private String name;
    private String email;
    private String designation;
    private String status;
    private Boolean isAdmin;
    private String roleType;
    private List<OwnerDTO> owners;
}
