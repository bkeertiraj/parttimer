package com.example.PartTimer.repositories;

import com.example.PartTimer.entities.Booking;
import com.example.PartTimer.entities.BookingStatus;
import com.example.PartTimer.entities.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT COUNT(b) FROM Booking b JOIN ServiceAssignment sa ON sa.booking = b WHERE sa.organization.id = :orgId")
    long countByServiceAssignmentOrganizationId(@Param("orgId") Long orgId);

    @Query("SELECT COUNT(b) FROM Booking b JOIN ServiceAssignment sa ON sa.booking = b WHERE sa.organization.id = :orgId AND b.status = :status")
    long countByServiceAssignmentOrganizationIdAndStatus(@Param("orgId") Long orgId, @Param("status") BookingStatus bookingStatus);

    @Query("SELECT b FROM Booking b JOIN ServiceAssignment sa ON sa.booking = b WHERE sa.organization.id = :orgId") //i think ORDER BY b.bookingId DESC can also be done here
    List<Booking> findByServiceAssignmentOrganizationId(@Param("orgId") Long orgId);

    @Query("SELECT b FROM Booking b JOIN ServiceAssignment sa ON sa.booking = b WHERE sa.organization.id = :orgId AND b.status = :status")
    List<Booking> findByServiceAssignmentOrganizationIdAndStatus(@Param("orgId") Long orgId, @Param("status") BookingStatus status);

    @Query("SELECT b FROM Booking b JOIN ServiceAssignment sa ON sa.booking = b " +
            "WHERE sa.organization.id = :orgId ORDER BY b.bookingId DESC")
    List<Booking> findTop5ByOrganizationId(@Param("orgId") Long orgId, Pageable pageable);


    @Query("SELECT DISTINCT b FROM Booking b " +
            "LEFT JOIN ServiceAssignment sa ON sa.booking = b " +
            "JOIN OrganizationService os ON os.service = b.service " +
            "WHERE os.organization.id = :orgId " +
            "AND (sa.organization.id = :orgId OR " +
            "(b.status = com.example.PartTimer.entities.BookingStatus.OPEN " +
            "AND sa.organization IS NULL)) " +
            "ORDER BY b.bookingId DESC")
    List<Booking> findLatestAndPostedBookings(@Param("orgId") Long orgId, Pageable pageable);


    @Query("SELECT DISTINCT b FROM Booking b " +
            "JOIN OrganizationService os ON os.service = b.service " +
            "WHERE os.organization.id = :orgId " +
            "AND b.status = com.example.PartTimer.entities.BookingStatus.OPEN " +
            "AND NOT EXISTS (SELECT 1 FROM ServiceAssignment sa WHERE sa.booking = b) " +
            "ORDER BY b.bookingId DESC")
    List<Booking> findNewlyPostedBookings(@Param("orgId") Long orgId);


    @Query("SELECT DISTINCT b FROM Booking b " +
            "LEFT JOIN FETCH b.bookingAssignment ba " +  // Add this to fetch BookingAssignment
            "JOIN BookingAssignment assignmt ON b.bookingId = assignmt.booking.bookingId " +
            "WHERE b.service.serviceId = :serviceId " +
            "AND assignmt.organization.id = :organizationId")
    List<Booking> findByServiceAndOrganization(
            @Param("serviceId") Long serviceId,
            @Param("organizationId") Long organizationId);


    List<Booking> findByService(Service service);
}
