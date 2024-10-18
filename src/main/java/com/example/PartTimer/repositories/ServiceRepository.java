package com.example.PartTimer.repositories;

import com.example.PartTimer.entities.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {

    List<Service> findByCategory(String category);

    List<Service> findAll();

//    @Query("SELECT s FROM Service s JOIN FETCH s.employees WHERE s.serviceId = :serviceId")
//    Optional<Service> findByIdWithEmployees(@Param("serviceId") Long serviceId);


}
