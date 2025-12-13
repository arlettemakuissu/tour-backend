package com.odissay.tour.repository;

import com.odissay.tour.model.entity.Country;
import com.odissay.tour.model.dto.reponse.CountryResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CountryRepository extends JpaRepository<Country,Short> {
      boolean existsByCodeOrName(String code,String name);
      boolean existsByCodeAndIdNot(String code, short id);
     boolean existsByNameAndIdNot(String name, short id);



     // JPQL ---> java persistente language (nix tra java e sql)
     // permette di avere la lista di quelle ci serve
    // in sql nativo è --->select id,name from countries orderby name
   Optional<Country> findByIdAndActiveTrue(short id);
    @Query("SELECT new com.odissay.tour.model.dto.reponse.CountryResponse(c.id, c.name) FROM Country c WHERE c.active = true ORDER BY c.name")
    List<CountryResponse> findAllActiveCountries();

    @Query("SELECT new com.odissay.tour.model.dto.reponse.CountryResponse(c.id, c.name) FROM Country c ORDER BY c.name")
    List<CountryResponse> findAllCountries();

    @Query("SELECT new com.odissay.tour.model.dto.reponse.CountryResponse(c.id,c.code, c.name,c.currency )" +
            " FROM Country c WHERE c.id = :countryId ")// /countryId è il nome della variabile che deve uguale al nome settato
    Optional<CountryResponse> findCountry(@Param("countryId")short countryId);


    }