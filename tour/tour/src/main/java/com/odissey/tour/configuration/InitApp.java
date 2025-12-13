package com.odissey.tour.configuration;

import com.odissey.tour.model.entity.Role;
import com.odissey.tour.model.entity.User;
import com.odissey.tour.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration
@RequiredArgsConstructor
@Slf4j
public class InitApp {

    private final UserRepository userRepository;

    @Bean
    public boolean insertFirstAdmin(){
        log.info(">>> Verifico le condizioni per inserire il primo admin a sistema.");
        if(!userRepository.existsById(1)) {
            User user = new User();
            user.setEmail("admin@tour-odissey.abc");
            user.setEnabled(true);
            user.setFirstname("Deus");
            user.setLastname("Ex machina");
            user.setPassword("$2a$10$TVs/s0gtBhTpJkWQatnKl.gqzb0S9iy2P57gBBHMJ7xXFz0JHYC1e"); // Password123!|
            user.setRole(Role.ADMIN);
            user.setUsername("admin");
            user.setVerified(true);
            userRepository.save(user);
            log.info(">>> L'utente "+user.getFirstname()+ " "+user.getLastname()+" è stato inserito con successo");
            return true;
        }
        log.info(">>> Nessun utente di tipo ADMIN aggiunto in quanto già presente");
        return false;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
