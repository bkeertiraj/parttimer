package com.example.PartTimer.services;

import com.example.PartTimer.dto.BookingRequestDTO;
import com.example.PartTimer.dto.ServiceRequestDTO;
import com.example.PartTimer.entities.Booking;
import com.example.PartTimer.entities.ServiceAssignment;
import com.example.PartTimer.entities.User;
import com.example.PartTimer.repositories.BookingRepository;
import com.example.PartTimer.repositories.ServiceAssignmentRepository;
import com.example.PartTimer.repositories.ServiceRepository;
import com.example.PartTimer.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private UserRepository userRepository;

    private final ServiceAssignmentRepository serviceAssignmentRepository;

    public BookingService(ServiceAssignmentRepository serviceAssignmentRepository) {
        this.serviceAssignmentRepository = serviceAssignmentRepository;
    }

    public Booking createBooking(BookingRequestDTO bookingRequest) {

        Long serviceId = bookingRequest.getServiceId();
        //Long userId = bookingRequest.getUserId();
        com.example.PartTimer.entities.Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service Not Found Exception"));

        User user = userRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("User Not Found Exception"));

        // Create a new booking and set the values from DTO
        Booking booking = new Booking();
        booking.setUser(user);  // Set the user who made the booking
        booking.setService(service);
        booking.setName(bookingRequest.getName());  // Set the name of the person for whom the service is booked
        booking.setEmail(bookingRequest.getEmail());  // Set the email of the person for whom the service is booked
        booking.setLocation(bookingRequest.getLocation());
        booking.setDate(bookingRequest.getDate());
        booking.setTime(bookingRequest.getTime());
        booking.setDescription(bookingRequest.getDescription());


        return bookingRepository.save(booking);
    }

    public ServiceRequestDTO getServiceRequestById(Long requestId) {
        Booking booking = bookingRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        return mapToServiceRequestDTO(booking);
    }

    private ServiceRequestDTO mapToServiceRequestDTO(Booking booking) {
        ServiceRequestDTO serviceRequestDTO = new ServiceRequestDTO();
        serviceRequestDTO.setId(booking.getBookingId());
        serviceRequestDTO.setUserId(booking.getUser().getUserId()); // Assuming User entity has an ID field
        serviceRequestDTO.setServiceId(booking.getService().getServiceId()); // Assuming Service entity has an ID field
        //serviceRequestDTO.setRequestDate(LocalDateTime.parse(booking.getDate()));
        serviceRequestDTO.setStatus(String.valueOf(booking.getStatus()));
        serviceRequestDTO.setAddress(booking.getLocation());

        // Fetch the ServiceAssignment for this booking, if available
        Optional<ServiceAssignment> assignment = serviceAssignmentRepository.findByBooking(booking);

        if (assignment.isPresent()) {
            ServiceAssignment serviceAssignment = assignment.get();
            serviceRequestDTO.setOrganizationId(serviceAssignment.getOrganization().getId());
            serviceRequestDTO.setOrganizationName(serviceAssignment.getOrganization().getName());
            serviceRequestDTO.setAgreedPrice(serviceAssignment.getAgreedPrice());
        } else {
            // Handle cases where no organization has picked up the service yet
            serviceRequestDTO.setOrganizationId(null);
            serviceRequestDTO.setOrganizationName("Not yet picked");
            serviceRequestDTO.setAgreedPrice(null);
        }

        return serviceRequestDTO;
    }
}