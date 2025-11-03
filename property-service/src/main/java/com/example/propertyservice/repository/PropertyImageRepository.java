package com.example.propertyservice.repository;

import com.example.propertyservice.model.PropertyImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PropertyImageRepository extends JpaRepository<PropertyImage, UUID> {
}
