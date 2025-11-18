package com.example.bookservice.mapper;

import com.example.bookservice.dto.request.BookingRequest;
import com.example.bookservice.dto.response.BookingResponse;
import com.example.bookservice.dto.response.PropertyResponse;
import com.example.bookservice.model.Booking;
import com.example.bookservice.model.Guest;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class BookingMapper {
    public BookingResponse toDto(Booking booking, PropertyResponse propertyResponse) {
        return new BookingResponse(
                booking.getId().toString(),
                booking.getPropertyId().toString(),
                booking.getCheckIn(),
                booking.getCheckOut(),
                booking.getCreatedAt(),
                countFinalPrice(booking, propertyResponse),
                booking.getGuests().stream().map(guest -> new BookingResponse.Guest(guest.getFirstName(), guest.getLastName())).toList()
        );
    }

    public Booking toEntity(BookingRequest bookingRequest, String userId) {
        Booking booking = new Booking(
            bookingRequest.checkOut(),
            bookingRequest.checkIn(),
            UUID.fromString(bookingRequest.propertyId()),
            UUID.fromString(userId),
                new ArrayList<>()
        );
        List<Guest> guests = bookingRequest.guests().stream().map(guest -> new Guest(guest.firstName(), guest.lastName(), booking)).toList();
        booking.setGuests(guests);
        return booking;
    }

    private BigDecimal countFinalPrice(Booking booking, PropertyResponse propertyResponse) {
        return propertyResponse.pricePerNight().multiply(new BigDecimal(ChronoUnit.DAYS.between(booking.getCheckIn(), booking.getCheckOut())));
    }
}
