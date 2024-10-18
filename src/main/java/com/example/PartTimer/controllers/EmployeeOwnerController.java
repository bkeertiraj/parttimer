package com.example.PartTimer.controllers;

import com.example.PartTimer.dto.EmployeeWithOwnersDTO;
import com.example.PartTimer.dto.OwnerWithEmployeesDTO;
import com.example.PartTimer.services.EmployeeOwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class EmployeeOwnerController {

//    @Autowired
//    private EmployeeOwnerService employeeOwnerService;
//
//    @PostMapping("/employees/{employeeId}/owners/{ownerId}/assign")
//    public String assignEmployeeToOwner(@PathVariable Long employeeId, @PathVariable Long ownerId) {
//        employeeOwnerService.assignEmployeeToOwner(employeeId, ownerId);
//        return "Employee assigned to Owner successfully!";
//    }
//
//    @GetMapping("/owners-with-employees")
//    public ResponseEntity<List<OwnerWithEmployeesDTO>> getAllOwnersWithEmployees() {
//        List<OwnerWithEmployeesDTO> ownersWithEmployees = employeeOwnerService.getAllOwnersWithEmployees();
//        return ResponseEntity.ok(ownersWithEmployees);
//    }
//
//    @GetMapping("/employees-with-owners")
//    public ResponseEntity<List<EmployeeWithOwnersDTO>> getAllEmployeesWithOwners() {
//        List<EmployeeWithOwnersDTO> employeesWithOwners = employeeOwnerService.getAllEmployeesWithOwners();
//        return ResponseEntity.ok(employeesWithOwners);
//    }
}
