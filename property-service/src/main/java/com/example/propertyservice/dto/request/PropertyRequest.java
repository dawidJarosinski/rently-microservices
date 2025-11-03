package com.example.propertyservice.dto.request;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PropertyRequest(
        @NotBlank(message = "Property type cant be null or blank") String propertyType,
        @NotBlank(message = "Name cant be null or blank") String name,
        @NotBlank(message = "Description cant be null or blank") String description,
        @NotNull(message = "Number of guests cant be null") Integer maxNumberOfGuests,
        @NotNull(message = "Price per night cant be null") BigDecimal pricePerNight,
        @NotNull(message = "Address cant be null") @Valid Address address
) {
    public record Address(
            @NotBlank(message = "Country cant be null or blank") String country,
            @NotBlank(message = "City type cant be null or blank") String city,
            @NotBlank(message = "Street type cant be null or blank") String street,
            @NotBlank(message = "House number type cant be null or blank") String houseNumber,
            String localNumber,
            @NotBlank(message = "Postal code cant be null or blank") String postalCode
    ) {}
}
