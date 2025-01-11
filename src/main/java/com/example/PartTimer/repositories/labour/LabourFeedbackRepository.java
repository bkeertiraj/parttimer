package com.example.PartTimer.repositories.labour;

import com.example.PartTimer.entities.labour.LabourFeedback;
import com.example.PartTimer.entities.labour.LabourFeedbackType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LabourFeedbackRepository extends JpaRepository<LabourFeedback, Long> {
    // Find reviews for a specific labour with a given feedback type (e.g., USER_TO_LABOUR)
    List<LabourFeedback> findByLabourIdAndFeedbackType(Long labourId, LabourFeedbackType feedbackType);

    // Find reviews given by a specific user with a given feedback type (e.g., LABOUR_TO_USER)
    List<LabourFeedback> findByUser_UserIdAndFeedbackType(Long userId, LabourFeedbackType feedbackType);

    // Check if a specific user has already reviewed a booking
    boolean existsByUser_UserIdAndBookingId(Long userId, Long bookingId);

    // Find all feedback for a specific booking
    List<LabourFeedback> findByBookingId(Long bookingId);

    boolean existsByLabourIdAndBookingId(Long labourId, Long bookingId);

    boolean existsByLabourIdAndBookingIdAndFeedbackType(
            Long labourId,
            Long bookingId,
            LabourFeedbackType feedbackType
    );
}
