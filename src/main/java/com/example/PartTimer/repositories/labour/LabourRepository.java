package com.example.PartTimer.repositories.labour;

import com.example.PartTimer.entities.labour.Labour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LabourRepository extends JpaRepository<Labour, Long> {
    Optional<Labour> findByPhoneNumber(String phoneNumber);

}
