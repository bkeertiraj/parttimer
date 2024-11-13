package com.example.PartTimer.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrganizationEmployeeDTO {
//    private Long bookingId;
//    private Long organizationId;
//    private String bookingStatus;
//    private String location;
//    private String serviceName;
//    private String description;
//    private String date;
//    private String time;


    private List<EmployeeDetails> employees;

    @Data
    public static class EmployeeDetails {
        private Long userId;
        private String fullName;
        private String role;
    }
}
