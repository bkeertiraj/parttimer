package com.example.PartTimer.repositories;

import com.example.PartTimer.entities.OrganizationService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrganizationServiceRepository extends JpaRepository<OrganizationService, Long> {

    @Query("SELECT os FROM OrganizationService os WHERE os.service.serviceId = :serviceId") //os.service.id
    List<OrganizationService> findOrganizationsByServiceId(@Param("serviceId") Long serviceId);

    long countByOrganizationId(Long orgId);

    List<OrganizationService> findByOrganizationId(Long orgId);

    @Query("SELECT os FROM OrganizationService os WHERE os.organization.id = :orgId AND os.service.id = :serviceId")
    Optional<OrganizationService> findByOrganizationIdAndServiceId(@Param("orgId") Long orgId, @Param("serviceId") Long serviceId);
}
