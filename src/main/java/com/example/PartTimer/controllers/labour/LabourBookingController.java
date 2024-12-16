package com.example.PartTimer.controllers.labour;

import com.example.PartTimer.dto.labour.LabourBookingDTO;
import com.example.PartTimer.dto.labour.LabourBookingsByUserDTO;
import com.example.PartTimer.entities.labour.LabourBooking;
import com.example.PartTimer.services.labour.LabourBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/labour-bookings")
public class LabourBookingController {

    @Autowired
    private LabourBookingService labourBookingService;

    @PostMapping
    public ResponseEntity<LabourBooking> createLabourBooking(@RequestBody LabourBookingDTO bookingDTO) {
        LabourBooking createdBooking = labourBookingService.createLabourBooking(bookingDTO);
        return ResponseEntity.ok(createdBooking);
    }

    @GetMapping
    public ResponseEntity<List<LabourBookingsByUserDTO>> getLabourBookingsByUser() {
        List<LabourBookingsByUserDTO> labourBookings = labourBookingService.getLabourBookingsByUser();
        return ResponseEntity.ok(labourBookings);
    }
}
