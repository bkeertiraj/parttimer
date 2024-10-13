package com.example.PartTimer.dto;

import lombok.Data;

import java.util.List;

@Data
public class OwnerWithEmployeesDTO {

    private Long employeeId;
    private String name;
    private String email;
    private String designation;
    private String status;
    private Boolean isAdmin;
    private String roleType;
    private String phoneNumber;
    private List<EmployeeDTO> employees;
}
