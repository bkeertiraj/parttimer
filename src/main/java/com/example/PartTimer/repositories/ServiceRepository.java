package com.example.PartTimer.repositories;

import com.example.PartTimer.entities.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {

    List<Service> findByCategory(String category);

    List<Service> findAll();

}
