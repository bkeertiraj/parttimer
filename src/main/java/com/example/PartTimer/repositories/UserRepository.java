package com.example.PartTimer.repositories;

import com.example.PartTimer.entities.Organization;
import com.example.PartTimer.entities.User;
import com.example.PartTimer.entities.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);  // To find user by email

    User findByOrganizationAndUserRole(Organization organization, UserRole userRole);

    List<User> findAllByOrganizationAndUserRole(Organization organization, UserRole userRole);

    @Query("SELECT u FROM User u WHERE u.organization.id = :orgId AND u.userRole = 'CO_OWNER'")
    List<User> findCoOwnersByOrganizationId(@Param("orgId") Long organizationId);

    @Query("SELECT u FROM User u WHERE u.organization.id = :orgId AND u.userRole = 'OWNER'")
    User findOwnerByOrganizationId(@Param("orgId") Long organizationId);

    long countByOrganizationId(Long orgId);

    List<User> findByOrganizationId(Long orgId);

}
