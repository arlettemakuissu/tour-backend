package com.odissey.country_service.repository;

import com.odissey.country_service.dto.response.CountryDetailResponse;
import com.odissey.country_service.dto.response.CountryResponse;
import com.odissey.country_service.entity.Country;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CountryRepository extends JpaRepository<Country, String> {

    boolean existsByIdOrName(String id, String name);
    boolean existsByNameAndIdNot(String name, String id);

    @Query("SELECT new com.odissey.country_service.dto.response.CountryDetailResponse(" +
            "c.id, " +
            "c.name, " +
            "c.currency," +
            "c.active) " +
            "FROM Country c " +
            "ORDER BY c.name")
    List<CountryDetailResponse> getAll();

    @Query("SELECT new com.odissey.country_service.dto.response.CountryResponse(" +
            "c.id, " +
            "c.name, " +
            "c.currency) " +
            "FROM Country c " +
            "WHERE c.active = true " +
            "ORDER BY c.name")
    List<CountryResponse> getActiveCountries();
}
