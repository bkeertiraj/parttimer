package com.example.PartTimer.dto;

import com.example.PartTimer.entities.User;
import com.example.PartTimer.entities.UserRole;
import lombok.Data;

import java.util.List;

@Data
public class EmployeeDTO {

    private Long id;
    private String name;
    private UserRole role;

    public EmployeeDTO(Long id, String name, UserRole role) {
        this.id = id;
        this.name = name;
        this.role = role;

    }

    public EmployeeDTO() {
    }
}
