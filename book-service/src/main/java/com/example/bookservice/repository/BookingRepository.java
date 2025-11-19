package com.example.bookservice.repository;

import com.example.bookservice.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {
    @Query("SELECT EXISTS " +
            "(SELECT b FROM Booking b " +
            "WHERE b.checkIn <= :checkOut AND b.checkOut >= :checkIn AND b.propertyId = :propertyId)")
    boolean existsBookingCollisionInDatesAndPropertyId(
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut")LocalDate checkOut,
            @Param("propertyId")UUID propertyId);

    List<Booking> findAllByUserId(UUID userId);

    List<Booking> findAllByPropertyIdIn(Collection<UUID> propertyIds);
}
