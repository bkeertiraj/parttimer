package com.example.PartTimer.dto.labour;

import com.example.PartTimer.entities.labour.LabourSubscriptionStatus;
import lombok.Data;

import java.util.List;

@Data
public class LabourSignUpRequestDTO {
    private String phoneNumber;
    private String title;
    private String firstName;
    private String middleName;
    private String lastName;
    private String password;
    private List<String> serviceZipCodes;
    private Boolean isRideNeeded;
    private LabourSubscriptionStatus subscriptionStatus;
}
