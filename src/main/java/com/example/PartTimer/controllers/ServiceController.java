package com.example.PartTimer.controllers;

import com.example.PartTimer.dto.ServiceWithEmployeesDTO;
import com.example.PartTimer.entities.Service;
import com.example.PartTimer.services.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
public class ServiceController {

    @Autowired
    private ServiceService serviceService;

    @GetMapping
    public List<Service> getAllService() {
        return serviceService.getAllServices();
    }

    @GetMapping("/with-employees")
    public List<ServiceWithEmployeesDTO> getAllServicesWithEmployees() {
        return serviceService.getAllServicesWithEmployees();
    }

    @PostMapping
    public ResponseEntity<Service> createService(@RequestBody Service service) {
        Service newService = serviceService.createService(service);
        return ResponseEntity.ok(newService);
    }
}
