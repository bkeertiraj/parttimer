package com.example.PartTimer.services;

import com.example.PartTimer.dto.EmployeeDTO;
import com.example.PartTimer.dto.OwnerDTO;
import com.example.PartTimer.entities.Employee;
import com.example.PartTimer.entities.Owner;
import com.example.PartTimer.repositories.OwnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OwnerService {

    @Autowired
    private OwnerRepository ownerRepository;

    public Owner saveOwner(Owner owner) {
        return ownerRepository.save(owner);
    }

    public List<OwnerDTO> getOwners() {
        List<Owner> owners = ownerRepository.findAllOwners();
        return owners.stream()
                .map(this::convertToOwnerDTO)
                .collect(Collectors.toList());
    }

    private OwnerDTO convertToOwnerDTO(Owner owner) {

        OwnerDTO dto = new OwnerDTO();
        dto.setEmployeeId(owner.getEmployeeId());
        dto.setName(owner.getName());
        dto.setEmail(owner.getEmail());
        dto.setDesignation(owner.getDesignation());
        dto.setStatus(owner.getStatus());
        dto.setPhoneNumber(owner.getPhoneNumber());
        dto.setPassword(owner.getPassword());
        return dto;
    }

    // Convert Employee entity to EmployeeDTO
    private EmployeeDTO convertToEmployeeDTO(Employee employee) {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setEmployeeId(employee.getEmployeeId());
        dto.setName(employee.getName());
        dto.setEmail(employee.getEmail());
        dto.setDesignation(employee.getDesignation());
        dto.setStatus(employee.getStatus());
        return dto;
    }
}
