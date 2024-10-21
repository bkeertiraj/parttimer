package com.example.PartTimer.dto;

import com.example.PartTimer.entities.UserRole;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
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

    public OwnerDTO(Long id, String name, UserRole role) {
        super(id, name, role);
    }
}
