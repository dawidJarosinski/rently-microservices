package com.example.bookservice.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "property_id")
    private UUID propertyId;

    @Column(name = "check_in", nullable = false)
    private LocalDate checkIn;

    @Column(name = "check_out", nullable = false)
    private LocalDate checkOut;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "final_price", nullable = false)
    private BigDecimal finalPrice;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
    private List<Guest> guests;

    public Booking(LocalDate checkOut, LocalDate checkIn, UUID propertyId, UUID userId, List<Guest> guests, BigDecimal finalPrice) {
        this.checkOut = checkOut;
        this.checkIn = checkIn;
        this.propertyId = propertyId;
        this.userId = userId;
        this.guests = guests;
        this.createdAt = LocalDateTime.now();
        this.finalPrice = finalPrice;
    }
}
