package com.example.PartTimer.repositories;

import com.example.PartTimer.entities.Booking;
import com.example.PartTimer.entities.ServiceAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ServiceAssignmentRepository extends JpaRepository<ServiceAssignment, Long> {

    // Method to find the assignment by booking
    Optional<ServiceAssignment> findByBooking(Booking booking); //Optional<ServiceAssignment>

    List<ServiceAssignment> findByOrganizationId(Long orgId);

    Optional<ServiceAssignment> findByBooking_BookingIdAndOrganization_Id(Long bookingId, Long organizationId);

    //15-11-2024
    // Add new method to find all assignments for a booking
    List<ServiceAssignment> findAllByBooking(Booking booking);

    // Add method to find the accepted/final assignment
    Optional<ServiceAssignment> findByBookingAndAgreedPriceGreaterThan(Booking booking, Double price);

    // for user profile dashboard
    Optional<ServiceAssignment> findTopByBookingOrderByIdAsc(Booking booking);

}
