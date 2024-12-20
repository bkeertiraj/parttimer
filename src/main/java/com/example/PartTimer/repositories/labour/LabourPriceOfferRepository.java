package com.example.PartTimer.repositories.labour;

import com.example.PartTimer.entities.labour.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

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
}
