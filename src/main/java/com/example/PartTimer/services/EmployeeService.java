package com.example.PartTimer.services;

import com.example.PartTimer.repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

//    public List<Employee> findEmployeesForService(Long serviceId) {
//        return employeeRepository.findEmployeesForService(serviceId);
//    }
//
//    public Employee saveEmployee(Employee employee) {
//        return employeeRepository.save(employee);
//    }
//
//    public List<Employee> getAllEmployees() {
//        return employeeRepository.findAll();
//    }
}
