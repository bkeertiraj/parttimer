package com.example.PartTimer.dto;

import lombok.Data;

import java.util.List;

@Data
public class ServiceWithEmployeesDTO {

    private Long serviceId;
    private String serviceName;
    private String category;
    private String subCategory;
    private Double baseFee;
    private List<EmployeeWithPriceDTO> employees;

    @Data
    public static class EmployeeWithPriceDTO {
        private Long employeeId;
        private String employeeName;
        private double quotedPrice;
    }
}
