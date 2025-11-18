package com.example.bookservice.rest;

import com.example.bookservice.dto.request.BookingRequest;
import com.example.bookservice.dto.response.BookingResponse;
import com.example.bookservice.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping()
    public ResponseEntity<BookingResponse> save(@RequestBody BookingRequest request, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.save(request, jwt));
    }

    @DeleteMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String bookingId, @AuthenticationPrincipal Jwt jwt) {
        bookingService.delete(jwt, bookingId);
    }
}
