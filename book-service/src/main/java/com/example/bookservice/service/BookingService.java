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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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

        Booking booking = bookingMapper.toEntity(request, user.id(), countFinalPrice(request.checkIn(), request.checkOut(), propertyResponse));
        bookingRepository.save(booking);

////        tutaj wrzucic rabbita do powiadomien
        return bookingMapper.toDto(booking);
    }

    @Transactional
    public void cancelBooking(Jwt jwt, String bookingId) {
        UUID currentUserId = UUID.fromString(jwt.getSubject());

        Booking booking = bookingRepository.findById(UUID.fromString(bookingId))
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        boolean isGuest = booking.getUserId().equals(currentUserId);

        boolean isHost = false;
        if (!isGuest) {
            PropertyResponse property = propertyServiceWebClient
                    .get()
                    .uri("/api/properties/" + booking.getPropertyId())
                    .retrieve()
                    .bodyToMono(PropertyResponse.class)
                    .block();

            if (property != null && property.userId().equals(currentUserId.toString())) {
                isHost = true;
            }
        }

        if (!isGuest && !isHost) {
            throw new AccessDeniedException("you dont have permission to delete this booking");
        }

        bookingRepository.delete(booking);
    }

    public List<BookingResponse> findAll(Jwt jwt) {
        UUID currentUserId = UUID.fromString(jwt.getSubject());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        List<Booking> bookings;

        boolean isHost = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_HOST"));

        if (isHost) {
            List<PropertyResponse> propertyResponsesList = propertyServiceWebClient
                    .get()
                    .uri("/api/properties/host/" + jwt.getSubject())
                    .header("Authorization", "Bearer " + jwt.getTokenValue())
                    .retrieve()
                    .bodyToFlux(PropertyResponse.class)
                    .collectList()
                    .block();
            if (propertyResponsesList == null) {
                throw new ResourceNotFoundException("property response list is null");
            }
            bookings = bookingRepository.findAllByPropertyIdIn(propertyResponsesList
                            .stream()
                            .map(propertyResponse -> UUID.fromString(propertyResponse.id()))
                            .toList());
        } else {
            bookings = bookingRepository.findAllByUserId(currentUserId);
        }

        return bookings.stream()
                .map(bookingMapper::toDto)
                .toList();
    }

    private boolean checkAvailability(PropertyResponse property, LocalDate checkIn, LocalDate checkOut) {
        return bookingRepository.existsBookingCollisionInDatesAndPropertyId(checkIn, checkOut, UUID.fromString(property.id()));
    }

    private BigDecimal countFinalPrice(LocalDate checkIn, LocalDate checkOut, PropertyResponse propertyResponse) {
        return propertyResponse.pricePerNight().multiply(new BigDecimal(ChronoUnit.DAYS.between(checkIn, checkOut)));
    }
}
