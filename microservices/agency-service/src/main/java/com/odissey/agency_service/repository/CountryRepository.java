package com.odissey.agency_service.repository;

import com.odissey.agency_service.entity.Country;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CountryRepository extends JpaRepository<@NonNull Country, @NonNull String> {

    Optional<Country> findByIdAndActiveTrue(String countryCode);

    @Query("SELECT c.active FROM Country c WHERE c.id = :countryCode")
    Boolean isActive(String countryCode);
}
