package com.odissey.tour_service.repository;

import com.odissey.tour_service.entity.Agency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AgencyRepository extends JpaRepository<Agency, Integer> {

    Optional<Agency> findByIdAndActiveTrueAndCountryActiveTrue(int id);

    @Query("SELECT a.active FROM Agency a WHERE a.id = :id")
    Boolean isActive(int id);

    @Modifying
    @Query("UPDATE Agency a " +
            "SET a.active = true " +
            "WHERE a.country.id = :countryCode")
    void massiveAgencyActivationByCountry(String countryCode);
}
