package com.odissay.tour.repository;

import com.odissay.tour.model.entity.Customer;
import com.odissay.tour.model.entity.Payment;
import com.odissay.tour.model.entity.Tour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PayementRepository extends JpaRepository<Payment,Integer> {



    @Query(value = "SELECT SUM(p.amount) FROM payments p " +
            "WHERE p.tour_id = :tourId AND p.customer_id = :customerId", nativeQuery = true)
    Float sumPaymentByTourAndCustomer(int tourId, int customerId);




}
