package com.example.PartTimer.controllers;

import com.example.PartTimer.services.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    @Autowired
    private LocationService locationService;

    @GetMapping("/states")
    public ResponseEntity<List<String>> getStates(@RequestParam(required = false) String prefix) {
        return ResponseEntity.ok(locationService.getStates(prefix));
    }

    @GetMapping("/cities")
    public ResponseEntity<List<String>> getCities(
            @RequestParam String state,
            @RequestParam(required = false) String prefix) {
        return ResponseEntity.ok(locationService.getCities(state, prefix));
    }

    @GetMapping("/zipcodes")
    public ResponseEntity<List<String>> getZipcodes(
            @RequestParam String state,
            @RequestParam String city,
            @RequestParam(required = false) String prefix) {
        return ResponseEntity.ok(locationService.getZipcodes(state, city, prefix));
    }
}
