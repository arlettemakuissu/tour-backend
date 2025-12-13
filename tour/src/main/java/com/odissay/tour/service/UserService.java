package com.odissay.tour.service;

import com.odissay.tour.exception.Exception400;
import com.odissay.tour.exception.Exception404;
import com.odissay.tour.exception.Exception409;
import com.odissay.tour.exception.Exception500;
import com.odissay.tour.model.GenericMail;
import com.odissay.tour.model.dto.reponse.UserDetailResponse;
import com.odissay.tour.model.dto.request.ChangePasswordRequest;
import com.odissay.tour.model.dto.request.UserRoleRequest;
import com.odissay.tour.model.dto.request.UserUpdateRequest;
import com.odissay.tour.model.entity.OldPasswords;
import com.odissay.tour.model.entity.emurator.Role;
import com.odissay.tour.model.entity.User;
import com.odissay.tour.repository.LoginTracesRepository;
import com.odissay.tour.repository.OldPasswordRepository;
import com.odissay.tour.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final LoginTracesRepository loginTracesRepository;
    private final OldPasswordRepository oldPasswordRepository;

    public UserDetailResponse register (UserRoleRequest req){

         String username = req.getUsername().trim();
         String email = req.getEmail().trim();
         String role = req.getRole().trim().toUpperCase();

          if(userRepository.existsByUsernameOrEmail(username,email))
              throw new Exception409("Un utente con email "+email+" o username "+username+" esiste già a sistema.");
          User user = new User();
          user.setUsername(username);
          user.setEmail(email);
          user.setFirstname(req.getFirstname());
          user.setLastname(req.getLastname());
          user.setPassword(passwordEncoder.encode(req.getPassword()));
          user.setOtpCode(generateOtpCode(6));
          try{
             user.setRole(Role.valueOf(role));
          } catch (Exception ex){
             log.error(">>> {}",ex.getMessage());
            throw new Exception400("Ruolo non valido");
          }
          try{
              userRepository.save(user);
              emailService.sendMail(sendOtp(user));

           } catch(MessagingException e){
              log.error(">>> "+e.getMessage());
              throw new Exception500("Si è verificato un errore nell'invio dell'email");

          }
          return  UserDetailResponse.fromEntityToDto(user);
    }
    public String lastLoginByUser(int userId){


        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception404("utente non trovato con l'id " + userId));
        System.out.println("éééééééééééééééééééééééééééé");

        return loginTracesRepository.getLastLoginByUser(userId)
                .orElseThrow(() -> new Exception404("\"L'utente con id \"+userId+\" non s è mai loggato\""));

    }

    public String resendOtpCode(@Min(value = 1, message = "L'id dell'utente deve essere un numero intero maggiore di zero") int id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new Exception404("urente non trovato con l'id " + id));
        user.setOtpCode(generateOtpCode(6));
        user.setEnabled(false);
        user.setVerified(false);
        try{
            userRepository.save(user);
            emailService.sendMail((sendOtp(user)));

        } catch (MessagingException e){
            log.error(">>> "+e.getMessage());
            throw new Exception500("Si è verificato un errore nell'invio dell'email");



        }

        return "Codice OTP inviato correttamnte a "+user.getEmail();
    }

    public String enableDisableUser(@Min(value = 1, message = "L'id dell'utente deve essere un numero intero maggiore di zero") int id, UserDetails userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(()-> new Exception404("Utente non trovato con id " + id));
        if(userDetails.getUsername().equals(user.getUsername()))
            throw new Exception409("non puoi disabilitarre te stesso");
        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
        String status = user.isEnabled()? "abilitato" : "disabilitato";
        return "L'utente è stato " +status+"con successo";

    }

    public UserDetailResponse update(UserDetails userDetails, @Valid UserUpdateRequest req) {


        System.out.println("11111111111");
        User user = (User) userDetails;
        System.out.println("22222222222222");
        String username =  req.getUsername().trim();
        System.out.println("3333333333");
        String email = req.getEmail().trim();

        if(userRepository.existsByIdNotAndUsernameOrEmail(user.getId(),username,email)>0)
            throw  new Exception409("Un utente con email "+email+" o username "+username+" esiste già a sistema.");
        user.setEmail(req.getEmail());
        user.setUsername(req.getUsername());
        user.setFirstname(req.getFirstname());
        user.setLastname(req.getLastname());

        System.out.println("4444444444444444");


        userRepository.save(user);
        return UserDetailResponse.fromEntityToDto(user);

    }

    public String changePassword(UserDetails userDetails, @Valid ChangePasswordRequest req) {
        User user = (User) userDetails;

        if(!passwordEncoder.matches(req.getOldPassword(),user.getPassword()))
            throw new Exception400("la vechia password errata.");
        if(!req.getPassword1().trim().equals(req.getPassword2().trim()))
            throw new Exception400("la nuova password e la sua repetizione non coincidono");
        List<OldPasswords> oldPasswords = oldPasswordRepository.findTop3ByUserOrderByLastChangePasswordDesc(user);
        for(OldPasswords o : oldPasswords){

            if(passwordEncoder.matches(req.getPassword1(),o.getOldPassword()))
                throw new Exception500("la password scelta è gia stata utilizzata i uno dei tre precedenti cxambi password");

        }
        String newPassword = passwordEncoder.encode(req.getPassword1().trim());
        LocalDateTime now = LocalDateTime.now();
        user.setPassword(newPassword);
        user.setLastChangePassword(now);
        userRepository.save(user);
        oldPasswordRepository.save(new OldPasswords(user,newPassword,now) );
        return "la password è stata aggiornata con successo . Procedi ad un nuova login";

    }

    //---------------------------------------------------------------------------------
    public static String generateOtpCode(int lunghezza){
        String numeri = "0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(lunghezza);
        for(int i=0;i<lunghezza;i++){
            sb.append(numeri.charAt(random.nextInt(numeri.length())));
        }
        return sb.toString();
    }

    public static  GenericMail sendOtp(User user){
        GenericMail mail = new GenericMail();
        mail.setTo(user.getEmail());
        mail.setSubject("Tour Odissey: conferma di registrazione");
        mail.setBody("Gentile "+user.getFirstname()+" "+user.getLastname()+",\nal fine di confermare la registrazione, clicca sul seguente <a href=''>link</a> ed inserisci il codice otp "+user.getOtpCode());
        return mail;
    }



}
