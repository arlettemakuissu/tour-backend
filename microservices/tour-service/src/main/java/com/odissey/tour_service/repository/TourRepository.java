package com.odissey.tour_service.repository;

import com.odissey.tour_service.dto.response.TourFindResponse;
import com.odissey.tour_service.dto.response.TourHomePageResponse;
import com.odissey.tour_service.entity.Agency;
import com.odissey.tour_service.entity.Country;
import com.odissey.tour_service.entity.Tour;
import com.odissey.tour_service.entity.TourStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TourRepository extends JpaRepository<Tour, Integer> {

    @Modifying
    @Query("UPDATE Tour t " +
            "SET t.status = :status, " +
            "t.updatedAt = :updatedAt, " +
            "t.updatedBy = :updatedBy " +
            "WHERE t.agency = :agency " +
            "AND t.status IN(:statuses)")
    void massiveTourChangeStatusByAgency(TourStatus status, LocalDateTime updatedAt, int updatedBy, Set<TourStatus> statuses, Agency agency);

    @Modifying
    @Query("UPDATE Tour t " +
            "SET t.status = :status, " +
            "t.updatedAt = :updatedAt, " +
            "t.updatedBy = :updatedBy " +
            "WHERE t.agency.country = :country " +
            "AND t.status IN(:statuses)")
    void massiveChangeStatusByAgencyInCountry(TourStatus status, LocalDateTime updatedAt, int updatedBy, Set<TourStatus> statuses, Country country);


    @Modifying
    @Query("UPDATE Tour t " +
            "SET t.status = :status, " +
            "t.updatedAt = :updatedAt, " +
            "t.updatedBy = :updatedBy " +
            "WHERE t.country = :country " +
            "AND t.status IN(:statuses)")
    void massiveChangeStatusByCountry(TourStatus status, LocalDateTime updatedAt, int updatedBy, Set<TourStatus> statuses, Country country);


    @Modifying
    @Query("UPDATE Agency a " +
            "SET a.active = false " +
            "WHERE a.country = :country")
    void massiveAgencyChangeStatusByCountry(Country country);

    @Query("SELECT new com.odissey.tour_service.dto.response.TourHomePageResponse(" +
            "t.id, " +
            "t.name, " +
            "t.price, " +
            "t.startDate, " +
            "t.endDate," +
            "t.country.name," +
            "t.agency.country.currency" +
            ") FROM Tour t " +
            "WHERE t.inHomePage = true " +
            "AND t.status = :status " +
            "ORDER BY t.startDate ASC"
    )
    Page<TourHomePageResponse> toursInHomePage(Pageable pageable, TourStatus status);

    @Query("SELECT new com.odissey.tour_service.dto.response.TourFindResponse(" +
            "t.id, " +
            "t.name, " +
            "t.country.name," +
            "t.startDate, " +
            "t.endDate," +
            "t.price, " +
            "t.agency.country.currency" +
            ") FROM Tour t " +
            "WHERE t.status = :status " +
            "AND (" +
                "( :start IS NULL AND :end IS NULL ) OR "+
                "( :end IS NULL AND :start IS NOT NULL AND t.startDate >= :start ) OR" +
                "( :end IS NOT NULL AND :start IS NOT NULL AND t.startDate BETWEEN :start AND :end )" +
            ")" +
            "AND ( :keyword IS NULL OR (t.name LIKE :keyword OR t.description LIKE :keyword OR t.country.name LIKE :keyword))")
    List<TourFindResponse> findTours(String keyword, LocalDate start, LocalDate end, TourStatus status);

    Optional<Tour> findByIdAndStatus(int tourId, TourStatus status);

    List<Tour> findByStatusIn(Set<TourStatus> statuses);
}
