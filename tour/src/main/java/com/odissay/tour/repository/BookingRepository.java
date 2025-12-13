package com.odissay.tour.repository;

import com.odissay.tour.model.entity.Tour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookingRepository extends JpaRepository<Tour,Integer> {

    @Query("SELECT COUNT(t) > 0 " +
            "FROM Tour t " +
            "JOIN t.customers c " +
            "WHERE t.id = :tourId " +
            "AND c.id = :customerId")
    boolean isCustomerBookedTour( int tourId, int  customerId);


}
