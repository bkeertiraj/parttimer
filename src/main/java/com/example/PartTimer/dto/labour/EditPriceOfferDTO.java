package com.example.PartTimer.dto.labour;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditPriceOfferDTO {
    private Long priceOfferId;
    private BigDecimal newPrice;
    private String notes;
}
