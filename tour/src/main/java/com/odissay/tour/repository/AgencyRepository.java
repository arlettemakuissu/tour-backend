package com.odissay.tour.repository;

import com.odissay.tour.model.dto.reponse.AgencyResponse;
import com.odissay.tour.model.entity.Agency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface AgencyRepository extends JpaRepository<Agency, Integer> {

    boolean existsByVatAndIdNot(String vat, int id);

    @Query("SELECT new com.odissay.tour.model.dto.reponse.AgencyResponse(" +
            "a.id, a.name, a.vat, a.city || ' - ' || a.address, c.name) " +  // il doppio pipe || serve a concatenare
            "FROM Agency a " +
            "INNER JOIN Country c ON c.id = a.country.id ")
    List<AgencyResponse> findAllAgencies();

    @Query("SELECT new com.odissay.tour.model.dto.reponse.AgencyResponse(" +
            "a.id, a.name, a.vat, a.city || ' - ' || a.address, c.name) " +  // il doppio pipe || serve a concatenare
            "FROM Agency a " +
            "INNER JOIN Country c ON c.id = a.country.id " +
            "WHERE a.id = :id")
    Optional<AgencyResponse> findAgency(int id);

    @Query("SELECT new com.odissay.tour.model.dto.reponse.AgencyResponse(" +
            "a.id, a.name, a.vat, a.city || ' - ' || a.address, c.name) " +  // il doppio pipe || serve a concatenare
            "FROM Agency a " +
            "INNER JOIN Country c ON c.id = a.country.id " +
            "WHERE a.country.id = :countryId")
    List<AgencyResponse> findAgenciesByCountry(short countryId);
    @Query("SELECT a from Agency a JOIN FETCH a.country WHERE a.id= :id")
    Optional<Agency>findAgencyByIdWithCountry(int id);
}
