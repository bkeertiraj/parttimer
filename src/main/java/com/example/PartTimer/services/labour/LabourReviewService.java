package com.example.PartTimer.services.labour;

import com.example.PartTimer.dto.labour.LabourReviewDTO;
import com.example.PartTimer.dto.labour.LabourReviewResponseDTO;
import com.example.PartTimer.entities.labour.*;
import com.example.PartTimer.repositories.UserRepository;
import com.example.PartTimer.repositories.labour.LabourBookingRepository;
import com.example.PartTimer.repositories.labour.LabourFeedbackRepository;
import com.example.PartTimer.repositories.labour.LabourPriceOfferRepository;
import com.example.PartTimer.repositories.labour.LabourRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LabourReviewService {

    private final LabourFeedbackRepository feedbackRepository;
    private final LabourBookingRepository bookingRepository;
    private final LabourRepository labourRepository;
    private final UserRepository userRepository;
    private final LabourPriceOfferRepository labourPriceOfferRepository;

    public LabourReviewService(LabourFeedbackRepository feedbackRepository, LabourBookingRepository bookingRepository, LabourRepository labourRepository, UserRepository userRepository, LabourPriceOfferRepository labourPriceOfferRepository) {
        this.feedbackRepository = feedbackRepository;
        this.bookingRepository = bookingRepository;
        this.labourRepository = labourRepository;
        this.userRepository = userRepository;
        this.labourPriceOfferRepository = labourPriceOfferRepository;
    }

    @Transactional
    public LabourReviewResponseDTO submitReview(Long userId, LabourReviewDTO reviewDTO) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        var labour = labourRepository.findById(reviewDTO.getLabourId())
                .orElseThrow(() -> new RuntimeException("Labour not found"));

        LabourBooking booking = bookingRepository.findById(reviewDTO.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Validate if the booking is completed
        if (!isBookingAccepted(booking)) {

            throw new RuntimeException("Cannot review an unaccepted booking");
        }

        // Check if user has already reviewed this booking
        if (hasUserReviewedBooking(userId, reviewDTO.getBookingId())) {
            throw new RuntimeException("User has already reviewed this booking");
        }

        var feedback = new LabourFeedback();
        feedback.setUser(user);
        feedback.setLabour(labour);
        feedback.setBooking(booking);
        feedback.setRatingValue(reviewDTO.getRating());
        feedback.setReview(reviewDTO.getReview());
        feedback.setFeedbackType(reviewDTO.getFeedbackType());
        feedback.setFeedbackDate(LocalDateTime.now());

        var savedFeedback = feedbackRepository.save(feedback);
        return convertToResponseDTO(savedFeedback);
    }

    public List<LabourReviewResponseDTO> getLabourReviews(Long labourId) {
        return feedbackRepository.findByLabourIdAndFeedbackType(
                        labourId, LabourFeedbackType.USER_TO_LABOUR)
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }


    public List<LabourReviewResponseDTO> getUserReviews(Long userId) {
        return feedbackRepository.findByUser_UserIdAndFeedbackType(
                        userId, LabourFeedbackType.LABOUR_TO_USER)
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public Double getLabourAverageRating(Long labourId) {
        return feedbackRepository.findByLabourIdAndFeedbackType(
                        labourId, LabourFeedbackType.USER_TO_LABOUR)
                .stream()
                .mapToInt(LabourFeedback::getRatingValue)
                .average()
                .orElse(0.0);
    }

    public boolean hasUserReviewedBooking(Long userId, Long bookingId) {
        return feedbackRepository.existsByUser_UserIdAndBookingId(userId, bookingId);
    }

    private boolean isBookingAccepted(LabourBooking booking) {
        return booking.getLabourAssignments().stream()
                .anyMatch(assignment -> assignment.getBookingStatus() == LabourBookingStatus.ACCEPTED);
    }

    private LabourReviewResponseDTO convertToResponseDTO(LabourFeedback feedback) {
        var dto = new LabourReviewResponseDTO();
        dto.setId(feedback.getId());
        dto.setBookingId(feedback.getBooking().getId());
        dto.setLabourId(feedback.getLabour().getId());
        dto.setLabourName(feedback.getLabour().getFirstName() + " " + feedback.getLabour().getLastName());
        dto.setUserName(feedback.getUser().getFirstName() + " " + feedback.getUser().getLastName());
        dto.setRating(feedback.getRatingValue());
        dto.setReview(feedback.getReview());
        dto.setFeedbackType(feedback.getFeedbackType());
        dto.setFeedbackDate(feedback.getFeedbackDate().toString());
        return dto;
    }


    @Transactional
    public LabourReviewResponseDTO submitLabourToUserReview(Long labourId, LabourReviewDTO reviewDTO) {
        // Validate labour exists
        Labour labour = labourRepository.findById(labourId)
                .orElseThrow(() -> new RuntimeException("Labour not found"));

        System.out.println("Logged in Labour ID: " + labourId);
        System.out.println("Logged in Labour Phone: " + labour.getPhoneNumber());

        // Get the booking
        LabourBooking booking = bookingRepository.findById(reviewDTO.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Get the LabourAssignment for this booking
        LabourAssignment labourAssignment = booking.getLabourAssignments().stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No assignment found for this booking"));

        // Find the accepted price offer for this assignment and labour
        LabourPriceOffer acceptedOffer = labourPriceOfferRepository.findByLabourAssignmentAndLabourAndStatus(
                labourAssignment,
                labour,
                LabourPriceOfferStatus.ACCEPTED
        ).orElseThrow(() -> new RuntimeException("No accepted price offer found for this labour and booking"));

        // If we found an accepted offer, this labour is authorized to review
        System.out.println("Found accepted offer with ID: " + acceptedOffer.getId());

        // Check if the booking status is ACCEPTED
        if (labourAssignment.getBookingStatus() != LabourBookingStatus.ACCEPTED) {
            throw new RuntimeException("Cannot review user if booking is not accepted");
        }

//        // Check if labour has already reviewed this booking
//        if (feedbackRepository.existsByLabourIdAndBookingId(labourId, reviewDTO.getBookingId())) {
//            throw new RuntimeException("You have already reviewed this booking");
//        }

        // Create the feedback
        LabourFeedback feedback = new LabourFeedback();
        feedback.setLabour(labour);
        feedback.setUser(booking.getUser());
        feedback.setBooking(booking);
        feedback.setRatingValue(reviewDTO.getRating());
        feedback.setReview(reviewDTO.getReview());
        feedback.setFeedbackType(LabourFeedbackType.LABOUR_TO_USER);
        feedback.setFeedbackDate(LocalDateTime.now());

        LabourFeedback savedFeedback = feedbackRepository.save(feedback);
        return convertToResponseDTO(savedFeedback);
    }
}
