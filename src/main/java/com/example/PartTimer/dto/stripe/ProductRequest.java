package com.example.PartTimer.dto.stripe;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

//    private Long amount;
//    private Long quantity;
//    private String name;
//    private String currency;

    private int gems; // Number of gems to purchase
    private long price; // Price in cents
}
