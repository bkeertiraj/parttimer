package com.example.PartTimer.dto;

import lombok.Data;

import java.util.List;

@Data
public class EmployeeAssignmentRequest {

    private List<Long> employeeIds;
}
