package com.example.PartTimer.controllers;

import com.example.PartTimer.dto.EmployeeDTO;
import com.example.PartTimer.services.ServiceAssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ServiceAssignmentController {

    @Autowired
    private ServiceAssignmentService serviceAssignmentService;

    @PostMapping("/owners/{ownerId}/employees/{employeeId}/services/{serviceId}/assign")
    public ResponseEntity<EmployeeDTO> assignServiceToEmployee(
            @PathVariable Long ownerId,
            @PathVariable Long employeeId,
            @PathVariable Long serviceId) {
        EmployeeDTO updatedEmployee = serviceAssignmentService.assignServiceToEmployee(ownerId, employeeId, serviceId);
        return ResponseEntity.ok(updatedEmployee);
    }

    @GetMapping("/services/{serviceId}/employees")
    public ResponseEntity<List<EmployeeDTO>> getEmployeesByService(@PathVariable Long serviceId) {
        List<EmployeeDTO> employees = serviceAssignmentService.getEmployeesByService(serviceId);
        return ResponseEntity.ok(employees);
    }

}
