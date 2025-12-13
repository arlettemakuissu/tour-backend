package com.odissay.tour.repository;

import com.odissay.tour.model.entity.Customer;
import com.odissay.tour.model.entity.Vaucher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface VoucherRepository extends JpaRepository<Vaucher,Integer> {


    Optional<Vaucher> findByIdAndCustomerIsAndUsedFalse(int id, Customer customer);
}
