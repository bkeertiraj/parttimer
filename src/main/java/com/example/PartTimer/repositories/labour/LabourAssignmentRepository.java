package com.example.PartTimer.repositories.labour;

import com.example.PartTimer.entities.labour.Labour;
import com.example.PartTimer.entities.labour.LabourAssignment;
import com.example.PartTimer.entities.labour.LabourBookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LabourAssignmentRepository extends JpaRepository<LabourAssignment, Long> {

    // Find labour assignments by booking status
    List<LabourAssignment> findByBookingStatus(LabourBookingStatus status);

    // Find labour assignments for a specific labour
    List<LabourAssignment> findByLabour(Labour labour);
}
