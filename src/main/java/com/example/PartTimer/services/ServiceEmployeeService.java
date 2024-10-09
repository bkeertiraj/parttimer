package com.example.PartTimer.services;

import com.example.PartTimer.entities.Employee;
import com.example.PartTimer.entities.ServiceEmployee;
import com.example.PartTimer.repositories.EmployeeRepository;
import com.example.PartTimer.repositories.ServiceEmployeeRepository;
import com.example.PartTimer.repositories.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServiceEmployeeService {

    @Autowired
    private ServiceEmployeeRepository serviceEmployeeRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public ServiceEmployee assignEmployeeToService(Long serviceId, Long employeeId, Double baseFee) {
        com.example.PartTimer.entities.Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service not found"));

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        ServiceEmployee serviceEmployee = new ServiceEmployee();
        serviceEmployee.setService(service);
        serviceEmployee.setEmployee(employee);
        serviceEmployee.setBaseFee(baseFee);

        return serviceEmployeeRepository.save(serviceEmployee);
    }
}
