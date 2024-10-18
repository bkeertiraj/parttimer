package com.example.PartTimer.repositories;

import com.example.PartTimer.entities.OrganizationService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrganizationServiceRepository extends JpaRepository<OrganizationService, Long> {

    @Query("SELECT os FROM OrganizationService os WHERE os.service.id = :serviceId")
    List<OrganizationService> findOrganizationsByServiceId(@Param("serviceId") Long serviceId);
}
