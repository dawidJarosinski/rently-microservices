package com.example.propertyservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@Entity
@Table(name = "addresses")
@Data
@ToString(exclude = "property")
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @Id
    @Column(name = "property_id", nullable = false)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "property_id", nullable = false)
    @MapsId
    private Property property;

    @Column(name = "country", nullable = false)
    private String country;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "street", nullable = false)
    private String street;

    @Column(name = "house_number", nullable = false)
    private String houseNumber;

    @Column(name = "local_number")
    private String localNumber;

    @Column(name = "postal_code", nullable = false)
    private String postalCode;
}
