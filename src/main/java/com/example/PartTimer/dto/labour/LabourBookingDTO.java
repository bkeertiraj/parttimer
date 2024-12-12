package com.example.PartTimer.dto.labour;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LabourBookingDTO {
    private String address;
    private String phoneNumber;
    private String email;
    private int numberOfLabors;
    private boolean sameDateForAll;
    private boolean sameTimeSlotForAll;
    private boolean sameNoteForAll;
    private String sharedNote;
    private List<LaborDetailDTO> laborDetails;

    // Nested DTO for labor details
    public static class LaborDetailDTO {
        private Date date;
        private String timeSlot;
        private String note;

        // Constructors
        public LaborDetailDTO() {
        }

        public LaborDetailDTO(Date date, String timeSlot, String note) {
            this.date = date;
            this.timeSlot = timeSlot;
            this.note = note;
        }

        // Getters and setters
        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public String getTimeSlot() {
            return timeSlot;
        }

        public void setTimeSlot(String timeSlot) {
            this.timeSlot = timeSlot;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }
    }
}
