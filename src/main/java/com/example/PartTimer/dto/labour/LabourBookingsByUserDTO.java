package com.example.PartTimer.dto.labour;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LabourBookingsByUserDTO {
    private Long bookingId;
//    private String address;
//    private String phoneNumber;
//    private String email;
//    private LocalDateTime createdAt;
//    private LocalDateTime updatedAt;

    private List<LabourAssignmentDTO> labourAssignments;


    @Data
    public static class LabourAssignmentDTO {
        private Long assignmentId;
//        private Long labourId;
        private LocalDate bookingDate;
        private String timeSlot;
        private String bookingNote;
        private String bookingStatus;
//        private BigDecimal proposedPrice;

        private Integer numberOfPriceOffers;
    }
}
