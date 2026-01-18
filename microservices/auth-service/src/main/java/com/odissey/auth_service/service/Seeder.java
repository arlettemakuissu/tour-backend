package com.odissey.auth_service.service;

import com.odissey.auth_service.entity.Role;
import com.odissey.auth_service.entity.User;
import com.odissey.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Seeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    @Override
    public void run(String... args) throws Exception {
        if(userRepository.existsByUsername("admin")) return;
        User user = new User(
                "admin",
                "admin@odissey.abc",
                encoder.encode("Password123!"),
                Role.ADMIN.name(),
                1, null,null
        );
        userRepository.save(user);
    }
}
