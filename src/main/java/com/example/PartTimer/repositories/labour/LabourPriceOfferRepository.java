package com.example.PartTimer.repositories.labour;

import com.example.PartTimer.entities.labour.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LabourPriceOfferRepository extends JpaRepository<LabourPriceOffer, Long> {
    boolean existsByLabourAssignmentAndLabour(LabourAssignment labourAssignment, Labour labour);

    List<LabourPriceOffer> findByLabourAssignment(LabourAssignment labourAssignment);

    List<LabourPriceOffer> findByLabourAssignmentAndStatus(
            LabourAssignment labourAssignment,
            LabourPriceOfferStatus status
    );

    List<LabourPriceOffer> findByLabourAndLabourAssignmentBookingDateAndStatus(
            Labour labour,
            LocalDate bookingDate,
            LabourPriceOfferStatus status
    );

    List<LabourPriceOffer> findByLabourIdOrderByCreatedAtDesc(Long labourId);

    List<LabourPriceOffer> findByLabourAssignmentBookingAndStatus(
            LabourBooking booking,
            LabourPriceOfferStatus status
    );

    Optional<LabourPriceOffer> findFirstByLabourAssignmentAndStatus(LabourAssignment assignment, LabourPriceOfferStatus labourPriceOfferStatus);

    int countByLabourAssignment_BookingId(Long bookingId);

    List<LabourPriceOffer> findByLabourIdAndStatus(Long labourId, LabourPriceOfferStatus status);

    // In LabourPriceOfferRepository
    @Query("SELECT po FROM LabourPriceOffer po " +
            "WHERE po.labour.id = :labourId " +
            "AND po.status != :priceOfferStatus " +
            "AND po.labourAssignment.bookingStatus != :bookingStatus " +
            "ORDER BY po.createdAt DESC")
    List<LabourPriceOffer> findLabourPriceHistory(
            @Param("labourId") Long labourId,
            @Param("priceOfferStatus") LabourPriceOfferStatus priceOfferStatus,
            @Param("bookingStatus") LabourBookingStatus bookingStatus
    );

    Optional<LabourPriceOffer> findByLabourAssignmentAndLabourAndStatus(
            LabourAssignment labourAssignment,
            Labour labour,
            LabourPriceOfferStatus status
    );
}
