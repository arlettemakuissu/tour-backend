package com.odissay.tour.service;

import com.odissay.tour.exception.*;
import com.odissay.tour.model.GenericMail;
import com.odissay.tour.model.dto.reponse.LoginResponse;
import com.odissay.tour.model.dto.request.CustomerRequest;
import com.odissay.tour.model.dto.request.LoginRequest;
import com.odissay.tour.model.dto.request.ResetPasswordRequest;
import com.odissay.tour.model.entity.*;
import com.odissay.tour.model.entity.emurator.Role;
import com.odissay.tour.repository.CountryRepository;
import com.odissay.tour.repository.LoginTracesRepository;
import com.odissay.tour.repository.OldPasswordRepository;
import com.odissay.tour.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final LoginTracesRepository loginTracesRepository;
    private final OldPasswordRepository oldPasswordRepository;
    private final EmailService emailService;
    private final CountryRepository countryRepository;

    @Value("${passwordExpireIDays}")
    private int passwordExpireIDays;

    public String customersignup( CustomerRequest req) {

        // Verificare che non esistano già utenti con quella username o email

        String email = req.getEmail().trim();
        String username= req.getUsername().trim();
        LocalDateTime now = LocalDateTime.now();


        if(userRepository.existsByUsernameOrEmail(username,email))
            throw new Exception409("Un utente con email "+email+" o username "+username+" esiste già a sistema.");
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setFirstname(req.getFirstname().trim());
        user.setLastname(req.getLastname().trim());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setOtpCode(UserService.generateOtpCode(6));
        user.setRole(Role.CUSTOMER);
        user.setLastChangePassword(now);

        // verificare existenza country

        Country country = countryRepository.findByIdAndActiveTrue(req.getCountryId())
                .orElseThrow(() ->new Exception404("nazione non trovato con id"));


        // instanzio oggetto user Customer

        Customer customer = new Customer();
        customer.setAddress(req.getAddress());
        customer.setCity(req.getCity());
        customer.setCountry(country);

            // Sincronizzazione

        customer.setUser(user);
        user.setCustomer(customer);
        try{
            // Salvataggio user & customer
        userRepository.save(user);

        // old passwords
        oldPasswordRepository.save(new OldPasswords (user,user.getPassword(),now));

        // invio otpcode via elail

            emailService.sendMail((UserService.sendOtp(user)));

        }catch (MessagingException e) {
            log.error((">>>>" + e.getMessage()));
            throw  new Exception500("si è verificto un errore nel invio del email");
        } catch (Exception e) {
            log.error((">>>>" + e.getMessage()));
            throw  new Exception500("si è verificto un errore durante la ffase di registrazione");
        }
           return "Registrazione avenuto con successo,perfavore controlla il tuo account di posta per la verifica dell'email";
        }
        @Transactional
    public String otpVerification(String otp,String email){
        User user = userRepository.otpVerification(otp,email)
                .orElseThrow(()->new Exception404("email o codice otp non corretti"));
        user.setOtpCode(null);
        user.setVerified(true);
        user.setEnabled(true);
        LocalDateTime now = LocalDateTime.now();
        user.setLastChangePassword(now);
        userRepository.save(user);
        oldPasswordRepository.save(new OldPasswords(user,user.getPassword(),now));
        return "complimenti "+ user.getFirstname() + " " + user.getLastname() + ", verifica avvenuta con successo. Ora puoi loggarti.";


    }




    public LoginResponse login(LoginRequest req) {
        User user = userRepository.findByUsername(req.getUsername())
                .orElseThrow(()-> new Exception403("Credenziali errate"));
        if(!passwordEncoder.matches(req.getPassword(), user.getPassword()))
            throw new Exception403("Credenziali errate");
        if(!user.isEnabled() && user.isVerified())
            throw new Exception401("Utenza disabilitata. Contattare l'amministratore.");
        if(!user.isEnabled() && !user.isVerified())
            throw new Exception401("Ti è stata inviata un'email il giorno "+user.getCreateAt().toLocalDate()+" per verficare la tua registrazione. Per favore controlla il tuo account di posta elettronica.");
        if(user.getLastChangePassword().plusDays(passwordExpireIDays).isBefore(LocalDateTime.now()))
            throw new Exception401("La tua password è scaduta. Procedi al reset.");

        loginTracesRepository.save(new LoginTraces(user));

        LoginResponse res = new LoginResponse();
        res.setId(user.getId());
        res.setUsername(user.getUsername());
        res.setRole(user.getRole().name());
        res.setJwt(jwtService.generateAccessToken(user));

        return res;
    }


    public String requestResetPassword( String email) {

        User user = userRepository.findByEmailAndEnabledTrueAndVerifiedTrue( email)
                .orElseThrow(() -> new Exception404("\"Utente inesistente o non verificato o disabilitato\"") );
        String otpCode = UserService.generateOtpCode(6);
        GenericMail mail = new GenericMail ();
        mail.setTo(user.getEmail());
        mail.setSubject("Rest Password");
        mail.setBody("Gentile "+user.getFirstname()+" "+user.getLastname()+",\n clicca su seguente link per resettare la password.\nIl tuo codice otp è "+otpCode);
         try {
          emailService.sendMail(mail);
          user.setVerified(false);
          user.setEnabled(false);
          user.setOtpCode(otpCode);
          userRepository.save(user);

         }catch (MessagingException e) {

            throw new Exception500("error nel invio dell'email ");


         }

       return  "Richiesta di reset password inviata correctamenre,controlla il tuo account di posta elettronica per procedere";



    }
    @Transactional
    public String resetPassword(@Valid ResetPasswordRequest req) {

        User user = userRepository.otpVerification(req.getOtpCode(),req.getEmail())
                .orElseThrow(() -> new Exception404("utente non trovato"));
        if(!req.getPassword1().equals(req.getPassword2()))
            throw  new Exception400("le password non coincidono.");
        List<OldPasswords> oldPasswordsList = oldPasswordRepository.findTop3ByUserOrderByLastChangePasswordDesc(user);
        for(OldPasswords o : oldPasswordsList){
            if(passwordEncoder.matches(req.getPassword1(),o.getOldPassword()))
                  throw new Exception400("la password scelta è già stata utilizzata in uno dei tre precedenti cambi.");

        }
        LocalDateTime now = LocalDateTime.now();
        oldPasswordRepository.save(new OldPasswords(user,user.getPassword(),now));
         user.setVerified(true);
         user.setEnabled(true);
         user.setPassword(passwordEncoder.encode(req.getPassword1()));
         user.setOtpCode(null);
         user.setLastChangePassword(now);

        return "Password resettata con successo. Puoi precedere al login.";

    }
}
