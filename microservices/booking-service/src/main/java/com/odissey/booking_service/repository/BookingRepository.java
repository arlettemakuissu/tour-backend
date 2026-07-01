package com.odissey.booking_service.repository;

import com.odissey.booking_service.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    boolean existsByCustomerIdAndTourId(int customerId, int tourId);
    long countByCustomerIdAndTourIdAndCanceledFalse(int customerId, int tourId);
    Optional<Booking> findByCode(String code);

    long countByTourIdAndCanceledFalse(int tourId);
}
