package com.example.PartTimer.repositories;

import com.example.PartTimer.entities.BookingAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookingAssignmentRepository extends JpaRepository<BookingAssignment, Long> {

    Optional<BookingAssignment> findByBookingBookingId(Long bookingId);

    BookingAssignment save(BookingAssignment bookingAssignment);

    @Query("SELECT ba FROM BookingAssignment ba WHERE ba.booking.bookingId = :bookingId")
    Optional<BookingAssignment> findByBookingId(@Param("bookingId") Long bookingId);

}
