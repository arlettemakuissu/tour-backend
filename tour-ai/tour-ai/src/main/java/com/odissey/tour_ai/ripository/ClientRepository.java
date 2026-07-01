package com.odissey.tour_ai.ripository;

import com.odissey.tour_ai.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface ClientRepository  extends JpaRepository<Client, Integer> {

    boolean existsByApiKeyAndExpirationDateAfter(String apiKey, LocalDate now);
}
