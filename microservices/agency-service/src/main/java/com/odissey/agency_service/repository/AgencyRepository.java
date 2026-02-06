package com.odissey.agency_service.repository;

import com.odissey.agency_service.dto.response.AgencyActiveListResponse;
import com.odissey.agency_service.dto.response.AgencyListResponse;
import com.odissey.agency_service.entity.Agency;
import com.odissey.agency_service.entity.Country;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AgencyRepository extends JpaRepository<@NonNull Agency, @NonNull Integer> {

    boolean existsByVatAndCountryActiveTrue(String vat);
    boolean existsByVatAndIdNotAndCountryActiveTrue(String vat, int id);

    @Query("SELECT new com.odissey.agency_service.dto.response.AgencyListResponse(" +
            "a.id, " +
            "a.name, " +
            "a.address, " +
            "a.city, " +
            "a.country.id, " +
            "a.active) " +
            "FROM Agency a " +
            "ORDER BY a.country.id, a.name")
    List<AgencyListResponse> getAll();

    @Query("SELECT new com.odissey.agency_service.dto.response.AgencyActiveListResponse(" +
            "a.id, " +
            "a.name, " +
            "a.address, " +
            "a.city, " +
            "a.country.id) " +
            "FROM Agency a " +
            "WHERE a.active = true AND a.country.active = true " +
            "ORDER BY a.country.id, a.name")
    List<AgencyActiveListResponse> getActive();


    // -- CONSUMER
    @Modifying
    @Query("UPDATE Agency a " +
            "SET a.active = false " +
            "WHERE a.country = :country")
    void massiveAgenciesDeactivation(Country country);

    @Modifying
    @Query("UPDATE Agency a " +
            "SET a.active = true " +
            "WHERE a.country = :country")
    void massiveAgenciesActivation(Country country);
}
