package com.example.PartTimer.dto.labour;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PriceOfferDetailsDTO {
    private Long priceOfferId;
    private String labourFirstName;
    private String labourMiddleName;
    private String labourLastName;
    private Double labourRating;
    private BigDecimal proposedPrice;
}
