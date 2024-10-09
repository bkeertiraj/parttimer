package com.example.PartTimer.controllers;

import com.example.PartTimer.entities.Employee;
import com.example.PartTimer.services.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/service/{serviceId}")
    public List<Employee> getEmployeesForService(@PathVariable Long serviceId) {
        return employeeService.findEmployeesForService(serviceId);
    }

    @PostMapping("/add")
    public Employee addEmployee(@RequestBody Employee employee) {
        return employeeService.saveEmployee(employee);
    }

    @GetMapping("/get")
    public List<Employee> getAllEmployees() {
        return employeeService.getAllEmployees();
    }
}
