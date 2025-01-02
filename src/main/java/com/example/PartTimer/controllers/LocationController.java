package com.example.PartTimer.controllers;

import com.example.PartTimer.services.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    @Autowired
    private LocationService locationService;
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

    @GetMapping("/{city}/zipcodes")
    public ResponseEntity<List<String>> getCityZipcodes(@PathVariable String city) {
        List<String> zipcodes = locationService.getZipcodesByCity(city);
        return zipcodes.isEmpty()
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(zipcodes);
    }
}
