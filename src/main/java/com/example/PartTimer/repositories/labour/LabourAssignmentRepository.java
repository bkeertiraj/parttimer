package com.example.PartTimer.repositories.labour;

import com.example.PartTimer.entities.labour.Labour;
import com.example.PartTimer.entities.labour.LabourAssignment;
import com.example.PartTimer.entities.labour.LabourBookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LabourAssignmentRepository extends JpaRepository<LabourAssignment, Long> {

    // Find labour assignments by booking status
    List<LabourAssignment> findByBookingStatus(LabourBookingStatus status);

    // Find labour assignments for a specific labour
    List<LabourAssignment> findByLabour(Labour labour);

    @Query("SELECT COUNT(la) FROM LabourAssignment la WHERE la.id = :assignmentId AND la.proposedPrice IS NOT NULL")
    long countPriceOffersForAssignment(@Param("assignmentId") Long assignmentId);

    //16-12-2024, and then updated in 21-12-2024 and in 23-12-2024
    @Query("""
    SELECT la 
    FROM LabourAssignment la 
    JOIN FETCH la.booking b 
    LEFT JOIN LabourPriceOfferCount lpc ON lpc.labourBooking = b 
    WHERE la.bookingStatus = 'OPEN' 
      AND (lpc.offerCount IS NULL OR lpc.offerCount < 10)
      AND NOT EXISTS (
          SELECT lpo 
          FROM LabourPriceOffer lpo 
          WHERE lpo.labourAssignment = la 
            AND lpo.labour.id = :labourId
      )
    ORDER BY la.booking.createdAt DESC
""")
    List<LabourAssignment> findOpenBookingsForLabour(@Param("labourId") Long labourId);

    List<LabourAssignment> findByBookingDateBeforeAndBookingStatusNotIn(LocalDate date, List<LabourBookingStatus> statuses);
}
