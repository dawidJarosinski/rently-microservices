package com.example.bookservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "guests")
public class Guest {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @JoinColumn(name = "booking_id", nullable = false)
    @ManyToOne(cascade = CascadeType.ALL)
    private Booking booking;

    public Guest(String firstName, String lastName, Booking booking) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.booking = booking;
    }
}
