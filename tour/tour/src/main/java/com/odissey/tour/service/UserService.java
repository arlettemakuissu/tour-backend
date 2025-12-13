package com.odissey.tour.service;

import com.odissey.tour.exception.Exception400;
import com.odissey.tour.exception.Exception409;
import com.odissey.tour.exception.Exception500;
import com.odissey.tour.model.GenericMail;
import com.odissey.tour.model.dto.request.UserRoleRequest;
import com.odissey.tour.model.dto.response.UserDetailResponse;
import com.odissey.tour.model.entity.Role;
import com.odissey.tour.model.entity.User;
import com.odissey.tour.repository.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public UserDetailResponse register(UserRoleRequest req){
        String username = req.getUsername().trim();
        String email = req.getEmail().trim();
        String role = req.getRole().trim().toUpperCase();

        // verificare che non esista altro utente con stessa username o email
        if(userRepository.existsByUsernameOrEmail(username, email))
            throw new Exception409("Un utente con email "+email+" o username "+username+" esiste già a sistema.");

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setFirstname(req.getFirstname().trim());
        user.setLastname((req.getLastname().trim()));
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setOtpCode(generateOtpCode(6));
        try {
            user.setRole(Role.valueOf(role));
        } catch (Exception ex){
            log.error(">>> {}", ex.getMessage());
            throw new Exception400("Ruolo non valido");
        }
        try {
            userRepository.save(user);
            emailService.sendMail(sendOtp(user));
        } catch(MessagingException e){
            log.error(">>> "+e.getMessage());
            throw new Exception500("Si è verificato un errore nell'invio dell'email");
        }
        return UserDetailResponse.fromEntityToDto(user);
    }

    private static String generateOtpCode(int lunghezza) {
        String numeri = "0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(lunghezza);

        for (int i = 0; i < lunghezza; i++) {
            sb.append(numeri.charAt(random.nextInt(numeri.length())));
        }
        return sb.toString();
    }

    private GenericMail sendOtp(User user){
        GenericMail mail = new GenericMail();
        mail.setTo(user.getEmail());
        mail.setSubject("Tour Odissey: conferma di registrazione");
        mail.setBody("Gentile "+user.getFirstname()+" "+user.getLastname()+",\nal fine di confermare la registrazione, clicca sul seguente <a href=''>link</a> ed inserisci il codice otp "+user.getOtpCode());
        return mail;
    }
}
