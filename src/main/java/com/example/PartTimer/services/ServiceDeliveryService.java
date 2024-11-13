package com.example.PartTimer.services;

import com.example.PartTimer.dto.AssignedEmployeeDTO;
import com.example.PartTimer.dto.ServiceDeliveryDTO;
import com.example.PartTimer.entities.Booking;
import com.example.PartTimer.entities.BookingStatus;
import com.example.PartTimer.repositories.BookingRepository;
import com.example.PartTimer.repositories.ServiceRepository;
import com.example.PartTimer.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import com.example.PartTimer.entities.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServiceDeliveryService {

    private final BookingRepository bookingRepository;
    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;

    public List<ServiceDeliveryDTO> getServiceRequests(Long serviceId) {
        com.example.PartTimer.entities.Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service not found"));

        List<Booking> bookings = bookingRepository.findByService(service);

        return bookings.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private ServiceDeliveryDTO convertToDTO(Booking booking) {
        ServiceDeliveryDTO dto = new ServiceDeliveryDTO();

        dto.setId(booking.getBookingId());
        dto.setCustomerName(booking.getName() != null ? booking.getName() : booking.getUser().getFullName());
        dto.setStatus(booking.getStatus().toFrontendStatus());

        // Get the first service provider's name if any are assigned
        List<AssignedEmployeeDTO> assignedEmployees = booking.getServiceProviders().stream()
                .map(user -> {
                    AssignedEmployeeDTO employeeDto = new AssignedEmployeeDTO();
                    employeeDto.setUserId(user.getUserId());
                    employeeDto.setName(user.getFullName());
                    return employeeDto;
                })
                .collect(Collectors.toList());
        dto.setAssignedEmployees(assignedEmployees);

        // You might want to calculate estimated revenue based on your business logic
        dto.setEstimatedRevenue(booking.getService().getBaseFee());
        dto.setArea(booking.getLocation());
        dto.setAddress(booking.getAddress());
        dto.setDate(booking.getDate());
        dto.setTime(booking.getTime());

        //progress
        int progress = switch (booking.getStatus()) {
            case OPEN -> 0;
            case SELLER_SELECTED -> 20;
            case SELLER_ACCEPTED -> 40;
            case INITIATED -> 60;
            case PAYMENT_PENDING -> 80;
            case PAYMENT_SUBMITTED -> 90;
            case COMPLETED -> 100;
        };
        dto.setProgress(progress);

        return dto;
    }

    public ServiceDeliveryDTO updateServiceRequest(Long requestId, ServiceDeliveryDTO updateRequest) {
        Booking booking = bookingRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Update booking status based on frontend status
        if (updateRequest.getStatus() != null) {
            booking.setStatus(BookingStatus.fromFrontendStatus(updateRequest.getStatus()));
        }

        if (updateRequest.getAssignedEmployees() != null) {
            // Clear the existing service providers
            booking.getServiceProviders().clear();

            // Add the new assigned employees
            for (AssignedEmployeeDTO employeeDto : updateRequest.getAssignedEmployees()) {
                User employee = userRepository.findById(employeeDto.getUserId())
                        .orElseThrow(() -> new RuntimeException("Employee not found"));
                booking.getServiceProviders().add(employee);
            }
        }

        // Save the updated booking
        Booking updatedBooking = bookingRepository.save(booking);
        return convertToDTO(updatedBooking);
    }
}
