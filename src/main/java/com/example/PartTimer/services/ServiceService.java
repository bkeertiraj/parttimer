package com.example.PartTimer.services;

import com.example.PartTimer.dto.ServiceWithEmployeesDTO;
import com.example.PartTimer.entities.Employee;
import com.example.PartTimer.entities.ServiceEmployee;
import com.example.PartTimer.repositories.EmployeeRepository;
import com.example.PartTimer.repositories.ServiceEmployeeRepository;
import com.example.PartTimer.repositories.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ServiceService {

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ServiceEmployeeRepository serviceEmployeeRepository;

    public List<com.example.PartTimer.entities.Service> getAllServices() {
        return serviceRepository.findAll();
    }

    public List<com.example.PartTimer.entities.Service> getServicesByCategory(String category) {
        return serviceRepository.findByCategory(category);
    }

    public List<ServiceWithEmployeesDTO> getAllServicesWithEmployees() {
        List<ServiceWithEmployeesDTO> serviceWithEmployeesList = new ArrayList<>();
        List<com.example.PartTimer.entities.Service> services = serviceRepository.findAll();

        for(com.example.PartTimer.entities.Service service: services) {
            ServiceWithEmployeesDTO dto = new ServiceWithEmployeesDTO();
            dto.setServiceId(service.getServiceId());
            dto.setServiceName(service.getName());
            dto.setCategory(service.getCategory());
            dto.setSubCategory(service.getSubcategory());
            dto.setBaseFee(service.getBaseFee());

            //fetch employees for the service
            List<ServiceEmployee> serviceEmployees = serviceEmployeeRepository.findByService(service);
            List<ServiceWithEmployeesDTO.EmployeeWithPriceDTO> employeeWithPrices = new ArrayList<>();

            for(ServiceEmployee  serviceEmployee : serviceEmployees) {
                Employee employee = serviceEmployee.getEmployee();
                ServiceWithEmployeesDTO.EmployeeWithPriceDTO empDto = new ServiceWithEmployeesDTO.EmployeeWithPriceDTO();
                empDto.setEmployeeId(employee.getEmployeeId());
                empDto.setEmployeeName(employee.getName());
                empDto.setQuotedPrice(serviceEmployee.getBaseFee());

                employeeWithPrices.add(empDto);
            }

            dto.setEmployees(employeeWithPrices);
            serviceWithEmployeesList.add(dto);
        }
        return serviceWithEmployeesList;
    }

    public com.example.PartTimer.entities.Service createService(com.example.PartTimer.entities.Service service) {
        return serviceRepository.save(service);
    }
}
