package com.example.bookservice.mapper;

import com.example.bookservice.dto.request.BookingRequest;
import com.example.bookservice.dto.response.BookingResponse;
import com.example.bookservice.model.Booking;
import com.example.bookservice.model.Guest;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class BookingMapper {
    public BookingResponse toDto(Booking booking) {
        return new BookingResponse(
                booking.getId().toString(),
                booking.getPropertyId().toString(),
                booking.getCheckIn(),
                booking.getCheckOut(),
                booking.getCreatedAt(),
                booking.getFinalPrice(),
                booking.getGuests().stream().map(guest -> new BookingResponse.Guest(guest.getFirstName(), guest.getLastName())).toList()
        );
    }

    public Booking toEntity(BookingRequest bookingRequest, String userId, BigDecimal finalPrice) {
        Booking booking = new Booking(
            bookingRequest.checkOut(),
            bookingRequest.checkIn(),
            UUID.fromString(bookingRequest.propertyId()),
            UUID.fromString(userId),
            new ArrayList<>(),
            finalPrice
        );
        List<Guest> guests = bookingRequest.guests().stream().map(guest -> new Guest(guest.firstName(), guest.lastName(), booking)).toList();
        booking.setGuests(guests);
        return booking;
    }
}
