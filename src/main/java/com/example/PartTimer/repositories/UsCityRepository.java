package com.example.PartTimer.repositories;

import com.example.PartTimer.entities.country.UsCity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsCityRepository extends JpaRepository<UsCity, Integer> {

    @Query("SELECT DISTINCT c.country FROM UsCity c WHERE LOWER(c.country) LIKE LOWER(CONCAT(:prefix, '%')) ORDER BY c.country")
    List<String> findDistinctCountriesByPrefix(@Param("prefix") String prefix);

    @Query("SELECT DISTINCT c.state FROM UsCity c WHERE c.country = :country AND LOWER(c.state) LIKE LOWER(CONCAT(:prefix, '%')) ORDER BY c.state")
    List<String> findDistinctStatesByCountryAndPrefix(@Param("country") String country, @Param("prefix") String prefix);

    @Query("SELECT DISTINCT c.city FROM UsCity c WHERE c.country = :country AND c.state = :state AND LOWER(c.city) LIKE LOWER(CONCAT(:prefix, '%')) ORDER BY c.city")
    List<String> findDistinctCitiesByCountryStateAndPrefix(@Param("country") String country, @Param("state") String state, @Param("prefix") String prefix);

    @Query("SELECT DISTINCT c.zipcode FROM UsCity c WHERE c.country = :country AND c.state = :state AND c.city = :city AND c.zipcode LIKE CONCAT(:prefix, '%') ORDER BY c.zipcode")
    List<String> findDistinctZipcodesByCountryStateAndCityAndPrefix(
            @Param("country") String country,
            @Param("state") String state,
            @Param("city") String city,
            @Param("prefix") String prefix);

    List<UsCity> findByCityIgnoreCase(String city);

    List<UsCity> findByCityAndStateIgnoreCase(String city, String state);
}
