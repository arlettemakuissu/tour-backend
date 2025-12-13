package com.odissey.tour.repository;

import com.odissey.tour.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

    boolean existsByUsernameOrEmail(String username, String email);
}
