package com.example.PartTimer.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    private String namePrefix;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String password;
    private String phoneNumber;
//    private String location;

    private String country;
    private String state;
    private String city;
    private String zipcode;

    private boolean docsVerified; // New field
    private String typeOfVerificationFile; // New field
}
