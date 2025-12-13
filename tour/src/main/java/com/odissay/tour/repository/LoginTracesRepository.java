package com.odissay.tour.repository;

import com.odissay.tour.model.entity.LoginTraces;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface LoginTracesRepository extends JpaRepository<LoginTraces, Integer> {

    @Query("SELECT " +
            "CAST (MAX(lt.loginDate) AS STRING) FROM LoginTraces lt " + // casting di DATETIME in String
            "WHERE lt.user.id = :userId")
     Optional<String> getLastLoginByUser(int userId);


}
