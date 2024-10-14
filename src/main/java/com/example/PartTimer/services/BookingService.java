package com.example.PartTimer.services;

import com.example.PartTimer.dto.BookingRequestDTO;
import com.example.PartTimer.entities.Booking;
import com.example.PartTimer.repositories.BookingRepository;
import com.example.PartTimer.repositories.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    public Booking createBooking(BookingRequestDTO bookingRequest) {

        Long serviceId = bookingRequest.getServiceId();

        com.example.PartTimer.entities.Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service Not Found Exception"));

        Booking booking = new Booking();
        booking.setService(service);
        booking.setCustomerName(bookingRequest.getCustomerName());
        booking.setEmail(bookingRequest.getEmail());
        booking.setLocation(bookingRequest.getLocation());
        booking.setDate(bookingRequest.getDate());
        booking.setTime(bookingRequest.getTime());
        booking.setDescription(bookingRequest.getDescription());

        return bookingRepository.save(booking);
    }
}
