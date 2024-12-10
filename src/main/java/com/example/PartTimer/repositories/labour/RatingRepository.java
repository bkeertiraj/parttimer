package com.example.PartTimer.repositories.labour;

import com.example.PartTimer.entities.labour.Labour;
import com.example.PartTimer.entities.labour.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    // Find ratings for a specific labour
    List<Rating> findByLabour(Labour labour);
}
