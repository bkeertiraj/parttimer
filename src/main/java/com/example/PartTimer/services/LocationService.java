package com.example.PartTimer.services;

import com.example.PartTimer.repositories.UsCityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationService {

    @Autowired
    private UsCityRepository usCityRepository;

    public List<String> getStates(String prefix) {
        return usCityRepository.findDistinctStatesByPrefix(prefix != null ? prefix : "");
    }

    public List<String> getCities(String state, String prefix) {
        return usCityRepository.findDistinctCitiesByStateAndPrefix(state, prefix != null ? prefix : "");
    }

    public List<String> getZipcodes(String state, String city, String prefix) {
        return usCityRepository.findDistinctZipcodesByStateAndCityAndPrefix(state, city, prefix != null ? prefix : "");
    }
}
