package com.example.PartTimer.repositories.labour;

import com.example.PartTimer.entities.User;
import com.example.PartTimer.entities.labour.LabourBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LabourBookingRepository extends JpaRepository<LabourBooking, Long> {

    List<LabourBooking> findByUser(User user);
}
