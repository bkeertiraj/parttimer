package com.example.PartTimer.repositories;

import com.example.PartTimer.entities.Organization;
import com.example.PartTimer.entities.Service;
import org.springframework.data.annotation.Id;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    // Query method to find organizations that offer a specific service
    @Query("SELECT os FROM OrganizationService os WHERE os.service.serviceId = :serviceId")
    List<Organization> findByOfferedServices_ServiceId(@Param("serviceId") Long serviceId);

    @Query("SELECT o FROM Organization o " +
            "LEFT JOIN FETCH o.owner " +
            "LEFT JOIN FETCH o.coOwners " +
            "WHERE o.id = :id")
    Optional<Organization> findByIdWithOwnersAndCoOwners(Long id);
}
