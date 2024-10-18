package com.example.PartTimer.repositories;

import com.example.PartTimer.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<User, Long> {
//
//    @Query("SELECT e FROM Employee e JOIN ServiceEmployee se ON e = se.employee WHERE se.service.id = :serviceId")
//    List<Employee> findEmployeesForService(@Param("serviceId") Long serviceId);
//
//
//    @Query("SELECT e FROM Employee e JOIN e.services s WHERE s.serviceId = :serviceId")
//    List<Employee> findEmployeesByServiceId(@Param("serviceId") Long serviceId);

}
