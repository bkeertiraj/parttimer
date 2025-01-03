package com.example.PartTimer.controllers;

import com.example.PartTimer.entities.country.UsCity;
import com.example.PartTimer.repositories.UsCityRepository;
import com.example.PartTimer.services.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    @Autowired
    private LocationService locationService;

    @Autowired
    private UsCityRepository usCityRepository;

    @GetMapping("/countries")
    public ResponseEntity<List<String>> getCountries(
            @RequestParam(required = false) String prefix) {
        return ResponseEntity.ok(locationService.getCountries(prefix));
    }

    @GetMapping("/states")
    public ResponseEntity<List<String>> getStates(
            @RequestParam String country,
            @RequestParam(required = false) String prefix) {
        return ResponseEntity.ok(locationService.getStates(country, prefix));
    }

    @GetMapping("/cities")
    public ResponseEntity<List<String>> getCities(
            @RequestParam String country,
            @RequestParam String state,
            @RequestParam(required = false) String prefix) {
        return ResponseEntity.ok(locationService.getCities(country, state, prefix));
    }

    @GetMapping("/zipcodes")
    public ResponseEntity<List<String>> getZipcodes(
            @RequestParam String country,
            @RequestParam String state,
            @RequestParam String city,
            @RequestParam(required = false) String prefix) {
        return ResponseEntity.ok(locationService.getZipcodes(country, state, city, prefix));
    }

    @GetMapping("/city-zipcode")
    public ResponseEntity<?> getCityZipcodes(
            @RequestParam String city,
            @RequestParam String state) {

        List<String> zipcodes = usCityRepository.findByCityAndStateIgnoreCase(city, state)
                .stream()
                .map(UsCity::getZipcode)
                .collect(Collectors.toList());

        if (zipcodes.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(zipcodes);
    }
}
