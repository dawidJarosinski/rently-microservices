package com.example.propertyservice.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record PropertyResponse(
        String id,
        String userId,
        String propertyType,
        String name,
        String description,
        Integer maxNumberOfGuests,
        BigDecimal pricePerNight,
        boolean approved,
        Address address,
        List<String> propertyImages
) {
    public record Address(
            String country,
            String city,
            String street,
            String houseNumber,
            String localNumber,
            String postalCode
    ){}
}
