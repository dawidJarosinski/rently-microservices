package com.example.propertyservice.repository;

import com.example.propertyservice.model.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PropertyRepository extends JpaRepository<Property, UUID> {

    @Query("SELECT p FROM Property p LEFT JOIN FETCH p.address LEFT JOIN FETCH p.propertyImages WHERE p.id = :id")
    Optional<Property> findByIdWithAddressAndImages(@Param("id") UUID id);

    @Query("SELECT p FROM Property p LEFT JOIN FETCH p.address LEFT JOIN FETCH p.propertyImages")
    List<Property> findAll();

    @Query("SELECT p FROM Property p LEFT JOIN FETCH p.address LEFT JOIN FETCH p.propertyImages WHERE p.isApproved = true")
    List<Property> findAllApproved();

    @Query("SELECT p FROM Property p LEFT JOIN FETCH p.address LEFT JOIN FETCH p.propertyImages WHERE p.isApproved = :isApproved")
    List<Property> findAllByApproved(@Param("isApproved")boolean isApproved);
}
