package com.example.PartTimer.services;

import com.example.PartTimer.entities.country.UsCity;
import com.example.PartTimer.repositories.UsCityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LocationService {

    @Autowired
    private UsCityRepository usCityRepository;

    public List<String> getCountries(String prefix) {
        return usCityRepository.findDistinctCountriesByPrefix(prefix != null ? prefix : "");
    }

    public List<String> getStates(String country, String prefix) {
        return usCityRepository.findDistinctStatesByCountryAndPrefix(country, prefix != null ? prefix : "");
    }

    public List<String> getCities(String country, String state, String prefix) {
        return usCityRepository.findDistinctCitiesByCountryStateAndPrefix(country, state, prefix != null ? prefix : "");
    }

    public List<String> getZipcodes(String country, String state, String city, String prefix) {
        return usCityRepository.findDistinctZipcodesByCountryStateAndCityAndPrefix(
                country, state, city, prefix != null ? prefix : "");
    }

    public List<String> getZipcodesByCity(String city) {
        return usCityRepository.findByCityIgnoreCase(city)
                .stream()
                .map(UsCity::getZipcode)
                .collect(Collectors.toList());
    }
}
