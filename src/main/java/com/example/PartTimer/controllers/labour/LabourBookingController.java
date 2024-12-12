package com.example.PartTimer.controllers.labour;

import com.example.PartTimer.dto.labour.LabourBookingDTO;
import com.example.PartTimer.entities.labour.LabourBooking;
import com.example.PartTimer.services.labour.LabourBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/labour-bookings")
public class LabourBookingController {

    @Autowired
    private LabourBookingService labourBookingService;

    @PostMapping
    public ResponseEntity<LabourBooking> createLabourBooking(@RequestBody LabourBookingDTO bookingDTO) {
        LabourBooking createdBooking = labourBookingService.createLabourBooking(bookingDTO);
        return ResponseEntity.ok(createdBooking);
    }
}
