package com.example.PartTimer.dto;

import lombok.Data;

@Data
public class OwnerDTO extends EmployeeDTO {


    private Long employeeId;
    private String name;
    private String email;
    private String designation;
    private String status;
    private Boolean isAdmin;
    private String roleType;

    private String phoneNumber;
    private String password;
}
