package com.example.PartTimer.repositories;

import com.example.PartTimer.entities.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    // Query method to find organizations that offer a specific service
    @Query("SELECT o FROM Organization o JOIN o.offeredServices s WHERE s.id = :serviceId")
    List<Organization> findByOfferedServices_ServiceId(@Param("serviceId") Long serviceId);
}
