package com.example.PartTimer.repositories.labour;

import com.example.PartTimer.entities.labour.Labour;
import com.example.PartTimer.entities.labour.LabourAssignment;
import com.example.PartTimer.entities.labour.LabourBooking;
import com.example.PartTimer.entities.labour.LabourPriceOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LabourPriceOfferRepository extends JpaRepository<LabourPriceOffer, Long> {
    boolean existsByLabourAssignmentAndLabour(LabourAssignment labourAssignment, Labour labour);

    List<LabourPriceOffer> findByLabourAssignment(LabourAssignment labourAssignment);
}
