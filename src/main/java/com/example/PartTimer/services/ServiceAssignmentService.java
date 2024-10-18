package com.example.PartTimer.services;

import com.example.PartTimer.dto.EmployeeDTO;
import com.example.PartTimer.dto.OwnerDTO;
import com.example.PartTimer.dto.ServiceDTO;
//import com.example.PartTimer.entities.Employee;
import com.example.PartTimer.entities.Owner;
import com.example.PartTimer.repositories.EmployeeRepository;
import com.example.PartTimer.repositories.OwnerRepository;
import com.example.PartTimer.repositories.ServiceRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.PartTimer.entities.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class ServiceAssignmentService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private ServiceRepository serviceRepository;

//    @Transactional
//    public EmployeeDTO assignServiceToEmployee(Long ownerId, Long employeeId, Long serviceId) {
//        Owner owner = ownerRepository.findById(ownerId)
//                .orElseThrow(() -> new RuntimeException("Owner not found"));
//        Employee employee = employeeRepository.findById(employeeId)
//                .orElseThrow(() -> new RuntimeException("Employee not found"));
//        Service service = serviceRepository.findById(serviceId)
//                .orElseThrow(() -> new RuntimeException("Service not found"));
//
//        if (!owner.getEmployees().contains(employee)) {
//            throw new RuntimeException("This owner is not associated with the employee");
//        }
//
//        employee.getServices().add(service);
//        employeeRepository.save(employee);
//
//        return convertToEmployeeDTO(employee);
//    }

//    private EmployeeDTO convertToEmployeeDTO(Employee employee) {
//        EmployeeDTO dto = new EmployeeDTO();
//        dto.setEmployeeId(employee.getEmployeeId());
//        dto.setName(employee.getName());
//        dto.setEmail(employee.getEmail());
//        dto.setDesignation(employee.getDesignation());
//        dto.setStatus(employee.getStatus());
//        dto.setIsAdmin(employee.getIsAdmin());
//        dto.setRoleType(employee.getRoleType());
//
//        // If employee has owners, map them
//        if (employee.getOwners() != null && !employee.getOwners().isEmpty()) {
//            dto.setOwners(employee.getOwners().stream()
//                    .map(this::convertToOwnerDTO)  // Assuming you have a similar convertToOwnerDTO method
//                    .collect(Collectors.toList()));
//        }
//        dto.setServices(employee.getServices().stream()
//                .map(this::convertToServiceDTO)
//                .collect(Collectors.toList()));
//
//        return dto;
//    }
//
//    private ServiceDTO convertToServiceDTO(Service service) {
//        ServiceDTO dto = new ServiceDTO();
//        dto.setServiceId(service.getServiceId());
//        dto.setName(service.getName());
//        dto.setDescription(service.getDescription());
//        return dto;
//    }
//
//    private OwnerDTO convertToOwnerDTO(Owner owner) {
//
//        OwnerDTO dto = new OwnerDTO();
//        dto.setEmployeeId(owner.getEmployeeId());
//        dto.setName(owner.getName());
//        dto.setEmail(owner.getEmail());
//        dto.setDesignation(owner.getDesignation());
//        dto.setStatus(owner.getStatus());
//        dto.setPhoneNumber(owner.getPhoneNumber());
//        dto.setPassword(owner.getPassword());
//        return dto;
//    }
//    @Transactional
//    public List<EmployeeDTO> getEmployeesByService(Long serviceId) {
//        List<Employee> employees = employeeRepository.findEmployeesByServiceId(serviceId);
//        return convertToEmployeeDTOs(employees);
//    }
//
//    private List<EmployeeDTO> convertToEmployeeDTOs(List<Employee> employees) {
//        return employees.stream()
//                .map(this::convertToEmployeeDTO)
//                .collect(Collectors.toList());
//    }
}
