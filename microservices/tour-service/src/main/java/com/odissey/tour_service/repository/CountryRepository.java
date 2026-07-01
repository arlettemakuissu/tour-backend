package com.odissey.tour_service.repository;

import com.odissey.tour_service.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CountryRepository extends JpaRepository<Country, String> {

    Optional<Country> findByIdAndActiveTrue(String id);

    @Query("SELECT c.active FROM Country c WHERE c.id = :id")
    Boolean isActive(String id);
}
