package com.example.propertyservice.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "property_images")
public class PropertyImage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "url")
    private String url;

    @JoinColumn(name = "property_id")
    @ManyToOne
    private Property property;

    public PropertyImage(String url, Property property) {
        this.url = url;
        this.property = property;
    }
}
