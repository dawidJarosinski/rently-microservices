package com.example.bookservice.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record BookingRequest(
        @NotBlank(message = "property id cant be null") String propertyId,
        @NotNull(message = "check in cant be null") LocalDate checkIn,
        @NotNull(message = "check out cant be null") LocalDate checkOut,
        @NotNull(message = "guests cant be Null") @Valid List<Guest> guests
        ) {
    public record Guest(
            @NotBlank(message = "first name cant be blank") String firstName,
            @NotBlank(message = "last name cant be blank") String lastName
    ) {}
}
