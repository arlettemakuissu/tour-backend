package com.odissey.tour_service.repository;

import com.odissey.tour_service.dto.response.HeroImageResponse;
import com.odissey.tour_service.entity.HeroImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface HeroImageRepository extends JpaRepository<HeroImage, String> {

    boolean existsByCheckSumAlgorithmAndChecksumAndTourId(String checkSumAlgorithm, String checksum, int tourId);

    @Query(value = "SELECT MAX(h.prg) FROM hero_image h WHERE h.tour_id = :tourId", nativeQuery = true)
    Optional<Integer> maxPrg(int tourId);

    @Query("SELECT new com.odissey.tour_service.dto.response.HeroImageResponse(" +
            "h.id, " +
            "h.filename, " +
            "h.mimeType, " +
            "h.data, " +
            "h.prg" +
            ") FROM HeroImage h " +
            "WHERE h.tour.id = :tourId " +
            "ORDER BY h.prg")
    List<HeroImageResponse> getImages(int tourId);

    @Modifying
    @Transactional
    @Query("UPDATE HeroImage h " +
            "SET " +
            "h.prg = :prg, " +
            "h.updatedAt = :now," +
            "h.updatedBy = :updatedBy " +
            "WHERE h.id = :id")
    void updatePrg(String id, int prg, LocalDateTime now, int updatedBy);

    @Query("SELECT h.id FROM HeroImage h WHERE h.tour.id = :tourId")
    List<String> getHeroImageIds(int tourId);

    @Query("SELECT new com.odissey.tour_service.dto.response.HeroImageResponse(" +
            "h.id, " +
            "h.filename, " +
            "h.mimeType, " +
            "h.data, " +
            "h.prg" +
            ") FROM HeroImage h " +
            "WHERE h.id = :id")
    HeroImageResponse getHeroImageById(String id);
}
