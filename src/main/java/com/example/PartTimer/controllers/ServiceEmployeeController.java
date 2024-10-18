package com.example.PartTimer.controllers;

import com.example.PartTimer.dto.ServiceEmployeeDTO;
import com.example.PartTimer.entities.ServiceEmployee;
import com.example.PartTimer.services.ServiceEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/service-employee")
public class ServiceEmployeeController {
//
//    @Autowired
//    private ServiceEmployeeService serviceEmployeeService;
//
//    @PostMapping("/assign")
//    public ResponseEntity<ServiceEmployee> assignEmployeeToService(@RequestBody ServiceEmployeeDTO serviceEmployeeDTO) {
//        ServiceEmployee assignedServiceEmployee = serviceEmployeeService.assignEmployeeToService(
//
//                serviceEmployeeDTO.getServiceId(),
//                serviceEmployeeDTO.getEmployeeId(),
//                serviceEmployeeDTO.getBaseFee()
//        );
//        return new ResponseEntity<>(assignedServiceEmployee, HttpStatus.CREATED);
//    }
}
