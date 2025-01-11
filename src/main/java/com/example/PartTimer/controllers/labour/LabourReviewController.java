package com.example.PartTimer.controllers.labour;

import com.example.PartTimer.dto.labour.LabourReviewDTO;
import com.example.PartTimer.dto.labour.LabourReviewResponseDTO;
import com.example.PartTimer.entities.User;
import com.example.PartTimer.entities.labour.Labour;
import com.example.PartTimer.repositories.UserRepository;
import com.example.PartTimer.repositories.labour.LabourRepository;
import com.example.PartTimer.services.labour.LabourReviewService;
import com.example.PartTimer.utils.EncryptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reviews")
public class LabourReviewController {

    private final LabourReviewService reviewService;
    private final UserRepository userRepository;
    private final EncryptionUtil encryptionUtil;
    private final LabourRepository labourRepository;

    public LabourReviewController(LabourReviewService reviewService, UserRepository userRepository, EncryptionUtil encryptionUtil, LabourRepository labourRepository) {
        this.reviewService = reviewService;
        this.userRepository = userRepository;
        this.encryptionUtil = encryptionUtil;
        this.labourRepository = labourRepository;
    }

    @PostMapping("/user-review")
    public ResponseEntity<LabourReviewResponseDTO> submitReview(@RequestBody LabourReviewDTO reviewDTO) {
        // Get current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        System.out.println("user email in review endpoint" + userEmail);

        // Validate authentication
        if (userEmail == null || "anonymousUser".equals(userEmail)) {
            throw new IllegalStateException("User is not authenticated");
        }

        String encryptedEmail = encryptionUtil.encrypt(userEmail);
//        User userEntity = userRepository.findByEmail(encryptedEmail)
//                .orElseThrow(() -> new IllegalStateException("User not found"));

        // Get user id from email
        User user = userRepository.findByEmail(encryptedEmail)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        return ResponseEntity.ok(reviewService.submitReview(user.getUserId(), reviewDTO));
    }

    @GetMapping("/labour/{labourId}")
    public ResponseEntity<List<LabourReviewResponseDTO>> getLabourReviews(
            @PathVariable Long labourId) {
        return ResponseEntity.ok(reviewService.getLabourReviews(labourId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<LabourReviewResponseDTO>> getUserReviews(
            @PathVariable Long userId) {
        return ResponseEntity.ok(reviewService.getUserReviews(userId));
    }

    @GetMapping("/labour/{labourId}/rating")
    public ResponseEntity<Double> getLabourAverageRating(
            @PathVariable Long labourId) {
        return ResponseEntity.ok(reviewService.getLabourAverageRating(labourId));
    }

    @GetMapping("/check-user-review") //check user review
    public ResponseEntity<Boolean> checkUserReview(@RequestParam Long bookingId) {
        // Get current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        // Validate authentication
        if (userEmail == null || "anonymousUser".equals(userEmail)) {
            throw new IllegalStateException("User is not authenticated");
        }
        String encryptedEmail = encryptionUtil.encrypt(userEmail);
//        User userEntity = userRepository.findByEmail(encryptedEmail)
//                .orElseThrow(() -> new IllegalStateException("User not found"));

        // Get user id from email
        User user = userRepository.findByEmail(encryptedEmail)
                .orElseThrow(() -> new IllegalStateException("User not found"));
        return ResponseEntity.ok(reviewService.hasUserReviewedBooking(user.getUserId(), bookingId));
    }

    @GetMapping("/check-labour-review")
    public ResponseEntity<Boolean> checkLabourReview(@RequestParam Long bookingId) {
        try {
            // Get current authenticated labour
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String labourEmail = authentication.getName();

            if (labourEmail == null || "anonymousUser".equals(labourEmail)) {
                throw new IllegalStateException("Labour is not authenticated");
            }

            // Get labour from email (phone number)
            Labour labour = labourRepository.findByPhoneNumber(labourEmail)
                    .orElseThrow(() -> new IllegalStateException("Labour not found"));

            boolean hasReviewed = reviewService.hasLabourReviewedBooking(labour.getId(), bookingId);
            return ResponseEntity.ok(hasReviewed);

        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }

    @PostMapping("/labour-review")
    public ResponseEntity<?> submitUserReview(@RequestBody LabourReviewDTO reviewDTO) {
        try {
            // Get current authenticated labour
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String labourEmail = authentication.getName();

            if (labourEmail == null || "anonymousUser".equals(labourEmail)) {
                throw new IllegalStateException("Labour is not authenticated");
            }

            // Get labour ID from email
            System.out.println("Labour ph no: " + labourEmail);
            Optional<Labour> labourOptional = labourRepository.findByPhoneNumber(labourEmail);
            Labour labour = labourOptional.get();
            Long labourId = labour.getId();
//            Labour labour = labourRepository.findByPhoneNumber(labourEmail)  // Assuming phone number is used as username
//                    .orElseThrow(() -> new IllegalStateException("Labour not found"));

            // Validate rating value
            if (reviewDTO.getRating() < 1 || reviewDTO.getRating() > 5) {
                return ResponseEntity.badRequest().body("Rating must be between 1 and 5");
            }

            LabourReviewResponseDTO response = reviewService.submitLabourToUserReview(Long.valueOf(labourId), reviewDTO);
            return ResponseEntity.ok(response);

        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
