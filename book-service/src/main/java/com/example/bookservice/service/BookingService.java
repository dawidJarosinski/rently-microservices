package com.example.bookservice.service;

import com.example.bookservice.dto.request.BookingRequest;
import com.example.bookservice.dto.response.BookingResponse;
import com.example.bookservice.dto.response.PropertyResponse;
import com.example.bookservice.dto.response.UserResponse;
import com.example.bookservice.exception.ResourceNotFoundException;
import com.example.bookservice.exception.BadRequestException;
import com.example.bookservice.mapper.BookingMapper;
import com.example.bookservice.model.Booking;
import com.example.bookservice.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final WebClient userServiceWebClient;
    private final WebClient propertyServiceWebClient;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    @Transactional
    public BookingResponse save(BookingRequest request, Jwt jwt) {
        UserResponse user = userServiceWebClient
                .post()
                .uri("/api/users/get-or-create")
                .header("Authorization", "Bearer " + jwt.getTokenValue())
                .retrieve()
                .bodyToMono(UserResponse.class)
                .block();
        if (user == null) {
            throw new ResourceNotFoundException("user not found");
        }

        PropertyResponse propertyResponse = propertyServiceWebClient
                .get()
                .uri("/api/properties/" + request.propertyId())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, res -> {
                    if (res.statusCode() == HttpStatus.NOT_FOUND) {
                        return Mono.error(() -> new ResourceNotFoundException("property not found"));
                    }
                    return Mono.error(() -> new RuntimeException("web client error: "+ res.statusCode()));
                })
                .bodyToMono(PropertyResponse.class)
                .block();
        if (propertyResponse == null) {
            throw new ResourceNotFoundException("property not found");
        }
        if (!propertyResponse.isApproved()) {
            throw new BadRequestException("this property is not approved");
        }

        if (request.checkIn().isBefore(LocalDate.now()) || request.checkIn().isAfter(request.checkOut())) {
            throw new BadRequestException("wrong check in or check out date");
        }

        if (checkAvailability(propertyResponse, request.checkIn(), request.checkOut())) {
            throw new BadRequestException("these dates are not available");
        }

        if (request.guests().size() > propertyResponse.maxNumberOfGuests()) {
            throw new BadRequestException("too many guests");
        }

        Booking booking = bookingMapper.toEntity(request, user.id());
        bookingRepository.save(booking);

////        tutaj wrzucic rabbita do powiadomien
        return bookingMapper.toDto(booking, propertyResponse);
    }

    @Transactional
    public void delete(Jwt jwt, String bookingId) {
        UserResponse user = userServiceWebClient
                .post()
                .uri("/api/users/get-or-create")
                .header("Authorization", "Bearer " + jwt.getTokenValue())
                .retrieve()
                .bodyToMono(UserResponse.class)
                .block();
        if (user == null) {
            throw new ResourceNotFoundException("user not found");
        }

        Booking booking = bookingRepository.findById(UUID.fromString(bookingId))
                .orElseThrow(() -> new ResourceNotFoundException("booking not found"));

        if(!booking.getUserId().toString().equals(user.id())) {
            throw new AccessDeniedException("unauthorized");
        }

        bookingRepository.delete(booking);
    }

    @Transactional
    public void deleteByHost(Jwt jwt, String bookingId) {
        UserResponse user = userServiceWebClient
                .post()
                .uri("/api/users/get-or-create")
                .header("Authorization", "Bearer " + jwt.getTokenValue())
                .retrieve()
                .bodyToMono(UserResponse.class)
                .block();

        Booking booking = bookingRepository.findById(UUID.fromString(bookingId))
                .orElseThrow(() -> new ResourceNotFoundException("booking not found"));

        PropertyResponse propertyResponse = propertyServiceWebClient
                .get()
                .uri("/api/properties/" + booking.getPropertyId())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, res -> {
                    if (res.statusCode() == HttpStatus.NOT_FOUND) {
                        return Mono.error(() -> new ResourceNotFoundException("property not found"));
                    }
                    return Mono.error(() -> new RuntimeException("web client error: "+ res.statusCode()));
                })
                .bodyToMono(PropertyResponse.class)
                .block();
        if (propertyResponse == null) {
            throw new ResourceNotFoundException("property not found");
        }
        if (propertyResponse.userId() != user.id()) {
            throw new AccessDeniedException("you are not able to manage other hosts bookings");
        }

        bookingRepository.delete(booking);
    }

    private boolean checkAvailability(PropertyResponse property, LocalDate checkIn, LocalDate checkOut) {
        return bookingRepository.existsBookingCollisionInDatesAndPropertyId(checkIn, checkOut, UUID.fromString(property.id()));
    }
}
