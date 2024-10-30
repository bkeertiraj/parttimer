package com.example.PartTimer.dto.dashboard;

import com.example.PartTimer.entities.UserRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardUserDTO {
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private UserRole userRole;
}
