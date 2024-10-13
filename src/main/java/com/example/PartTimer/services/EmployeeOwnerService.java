package com.example.PartTimer.services;

import com.example.PartTimer.dto.EmployeeDTO;
import com.example.PartTimer.dto.EmployeeWithOwnersDTO;
import com.example.PartTimer.dto.OwnerDTO;
import com.example.PartTimer.dto.OwnerWithEmployeesDTO;
import com.example.PartTimer.entities.Employee;
import com.example.PartTimer.entities.Owner;
import com.example.PartTimer.repositories.EmployeeRepository;
import com.example.PartTimer.repositories.OwnerRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmployeeOwnerService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private OwnerRepository ownerRepository;
    
    @Autowired
    private OwnerService ownerService;

    @Transactional
    public EmployeeDTO assignEmployeeToOwner(Long employeeId, Long ownerId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        Owner owner = (Owner) employeeRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        employee.getOwners().add(owner);
        owner.getEmployees().add(employee);

        employeeRepository.save(employee);
        employeeRepository.save(owner);

        return convertToEmployeeDTO(employee);
    }

    private EmployeeDTO convertToEmployeeDTO(Employee employee) {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setEmployeeId(employee.getEmployeeId());
        dto.setName(employee.getName());
        dto.setEmail(employee.getEmail());
        dto.setDesignation(employee.getDesignation());
        dto.setStatus(employee.getStatus());
        return dto;
    }

    public List<OwnerWithEmployeesDTO> getAllOwnersWithEmployees() {
        List<Owner> owners = ownerRepository.findAllOwners();
        return owners.stream()
                .map(this::convertToOwnerWithEmployeesDTO)
                .collect(Collectors.toList());
    }

    private OwnerWithEmployeesDTO convertToOwnerWithEmployeesDTO(Owner owner) {
        OwnerWithEmployeesDTO dto = new OwnerWithEmployeesDTO();
        dto.setEmployeeId(owner.getEmployeeId());
        dto.setName(owner.getName());
        dto.setEmail(owner.getEmail());
        dto.setDesignation(owner.getDesignation());
        dto.setStatus(owner.getStatus());
        dto.setIsAdmin(owner.getIsAdmin());
        dto.setRoleType(owner.getRoleType());
        dto.setPhoneNumber(owner.getPhoneNumber());
        dto.setEmployees(owner.getEmployees().stream()
                .map(this::convertToEmployeeDTO)
                .collect(Collectors.toList()));
        return dto;
    }

    public List<EmployeeWithOwnersDTO> getAllEmployeesWithOwners() {
        List<Employee> employees = employeeRepository.findAll();
        return employees.stream()
                .map(this::convertToEmployeeWithOwnersDTO)
                .collect(Collectors.toList());
    }

    private EmployeeWithOwnersDTO convertToEmployeeWithOwnersDTO(Employee employee) {
        EmployeeWithOwnersDTO dto = new EmployeeWithOwnersDTO();
        dto.setEmployeeId(employee.getEmployeeId());
        dto.setName(employee.getName());
        dto.setEmail(employee.getEmail());
        dto.setDesignation(employee.getDesignation());
        dto.setStatus(employee.getStatus());
        dto.setIsAdmin(employee.getIsAdmin());
        dto.setRoleType(employee.getRoleType());
        dto.setOwners(employee.getOwners().stream()
                .map(this::convertToOwnerDTO)
                .collect(Collectors.toList()));
        return dto;
    }

    private OwnerDTO convertToOwnerDTO(Owner owner) {
        OwnerDTO dto = new OwnerDTO();
        dto.setEmployeeId(owner.getEmployeeId());  // Inherited from Employee
        dto.setName(owner.getName());
        dto.setEmail(owner.getEmail());
        dto.setDesignation(owner.getDesignation());
        dto.setStatus(owner.getStatus());
        dto.setIsAdmin(owner.getIsAdmin());
        dto.setRoleType(owner.getRoleType());

        // Owner-specific fields
        dto.setPhoneNumber(owner.getPhoneNumber());
        dto.setPassword(owner.getPassword());

        return dto;
    }

}
