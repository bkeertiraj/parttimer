package com.example.PartTimer.repositories;

import com.example.PartTimer.entities.Booking;
import com.example.PartTimer.entities.ServiceAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServiceAssignmentRepository extends JpaRepository<ServiceAssignment, Long> {

    // Method to find the assignment by booking
    Optional<ServiceAssignment> findByBooking(Booking booking);

}
