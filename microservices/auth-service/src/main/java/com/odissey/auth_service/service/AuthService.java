package com.odissey.auth_service.service;

import com.odissey.auth_service.config.AuthProperties;
import com.odissey.auth_service.dto.request.CustomerRequest;
import com.odissey.auth_service.dto.request.LoginRequest;
import com.odissey.auth_service.dto.request.RegisterRequest;
import com.odissey.auth_service.dto.response.LoginResponse;
import com.odissey.auth_service.dto.response.UserResponse;
import com.odissey.auth_service.dto.response.UserStatusResponse;
import com.odissey.auth_service.entity.Customer;
import com.odissey.auth_service.entity.RefreshToken;
import com.odissey.auth_service.entity.Role;
import com.odissey.auth_service.entity.User;
import com.odissey.auth_service.exception.AuthException;
import com.odissey.auth_service.exception.ErrMsg;
import com.odissey.auth_service.repository.CustomerRepository;
import com.odissey.auth_service.repository.TokenRefreshRepository;
import com.odissey.auth_service.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;
    private final JwtService jwtService;
    private final AuthProperties authProperties;
    private final TokenRefreshRepository tokenRefreshRepository;
    private final CustomerRepository customerRepository;


    public UserResponse register(RegisterRequest registerRequest, int userId) {
        if (userRepository.existsByUsername(registerRequest.getUsername()))
            throw new AuthException(ErrMsg.USERNAME_TAKEN);
        if (userRepository.existsByEmail(registerRequest.getEmail()))
            throw new AuthException(ErrMsg.EMAIL_TAKEN);
        String roles = null;
        try {
            roles = Role.valueOf(registerRequest.getRoles().toUpperCase()).name();
        } catch (IllegalArgumentException e) {
            throw new AuthException(ErrMsg.INVALID_ROLE);
        }
        User user = new User(
                registerRequest.getUsername(),
                registerRequest.getEmail(),
                encoder.encode(registerRequest.getPassword()),
                roles,
                userId, null,registerRequest.getDisplayName()
        );
        userRepository.save(user);
        return UserResponse.fromEntityToDto(user);
    }

    public LoginResponse login(LoginRequest loginRequest, HttpServletRequest request) {
        User user = userRepository.findByUsernameOrEmail(loginRequest.getUsernameOrEmail(), loginRequest.getUsernameOrEmail())
                .orElseThrow(() -> new AuthException(ErrMsg.BAD_CREDENTIALS));
        if (!user.isEnabled())
            throw new AuthException(ErrMsg.USER_DISABLED);
        if (!encoder.matches(loginRequest.getPassword(), user.getPasswordHash()))
            throw new AuthException(ErrMsg.BAD_CREDENTIALS);
        String jwt = jwtService.jwtToken(user);

        LocalDateTime now = LocalDateTime.now();
        RefreshToken refreshToken = new RefreshToken(
                UUID.randomUUID().toString(),
                user.getId(),
                request.getHeader("User-Agent"),
                getIp(request),
                false,
                now,
                now.plusSeconds(authProperties.getRefreshTokenTtlSeconds() + authProperties.getAccessTokenTtlSeconds())
        );
        // invalida vecchi refresh token
        tokenRefreshRepository.revokeOldRefreshTokensByUser(user.getId());
        // creo il nuovo refresh token
        tokenRefreshRepository.save(refreshToken);
        String name = user.getDisplayName() == null?user.getUsername(): user.getDisplayName();
        return new LoginResponse(name,user.getEmail(), user.getRoles(), "Bearer " + jwt, refreshToken.getId());
    }


    @Transactional
    public LoginResponse refresh(String refreshTokenId, HttpServletRequest request) {
        RefreshToken refreshToken = tokenRefreshRepository.findById(refreshTokenId)
                .orElseThrow(() -> new AuthException(ErrMsg.INVALID_REFRESH_TOKEN));
        if (refreshToken.isRevoked())
            throw new AuthException(ErrMsg.REFRESH_TOKEN_REVOKED);
        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now()))
            throw new AuthException(ErrMsg.REFRESH_TOKEN_EXPIRED);

        User user = userRepository.findById(refreshToken.getUserId())
                .orElseThrow(() -> new AuthException(ErrMsg.BAD_CREDENTIALS));

        String refreshJwtToken = jwtService.jwtRefreshToken(user, refreshToken);
        LocalDateTime now = LocalDateTime.now();
        RefreshToken newRefreshToken = new RefreshToken(
                UUID.randomUUID().toString(),
                user.getId(),
                refreshToken.getUserAgent(),
                getIp(request),
                false,
                now,
                now.plusSeconds(authProperties.getRefreshTokenTtlSeconds() + authProperties.getAccessTokenTtlSeconds())
        );
        // invalido vecchio refresh token
        refreshToken.setRevoked(true);
        // creo il nuovo refresh token
        tokenRefreshRepository.save(newRefreshToken);
    String  name = user.getDisplayName() == null?user.getUsername(): user.getDisplayName();
        return new LoginResponse(name,user.getEmail(), user.getRoles(), "Bearer " + refreshJwtToken, newRefreshToken.getId());
    }

    @Transactional
    public void logout(String refreshTokenId){
        tokenRefreshRepository.findById(refreshTokenId)
                .ifPresent(rt -> rt.setRevoked(true));
    }



    private String getIp(HttpServletRequest request){
        String ip = request.getHeader("X-Forwarded-For");
        if(ip != null && !ip.isBlank()) {
            int index = ip.indexOf(',');
            return (index > 0 ? ip.substring(0, index) : ip);
        }
        return request.getRemoteAddr();
    }

    @Transactional
    public UserStatusResponse enableDisableUser(int id, int updatedBy) {
        User user = userRepository.findById(id)
                .orElseThrow(()-> new AuthException(ErrMsg.USER_NOT_FOUND));
        if(id == updatedBy)
            throw new AuthException(ErrMsg.FORBIDDEN_CHANGE_ENABLE_FLAG);
        user.setEnabled(!user.isEnabled());
        user.setUpdatedBy(updatedBy);
        return UserStatusResponse.fromEntityToDto(user);
    }

    public String signup(@Valid CustomerRequest customerRequest) {
      System.out.println("helooooooooo");
      try{
        if(!customerRequest.isAcceptServiceTerms())
            throw new AuthException((ErrMsg.TERMS_NOT_ACCEPTED));
        if(userRepository.existsByUsername(customerRequest.getUsername()))
            throw new AuthException(ErrMsg.USERNAME_TAKEN);
        if(userRepository.existsByEmail(ErrMsg.EMAIL_TAKEN));
           String roles = null;
           System.out.println(customerRequest.getRoles().toUpperCase());
        System.out.println(Role.CUSTOMER.name());
        try {
            if ((Role.valueOf(customerRequest.getRoles().toUpperCase()).name()).equals(Role.CUSTOMER.name()));

                 roles = Role.CUSTOMER.name();
                 System.out.println(roles);
                 System.out.println("hhhhhhhhhhhhhhhhhh");
        } catch (IllegalArgumentException e) {
            throw new AuthException(ErrMsg.INVALID_ROLE);
        }

        User user = new User (
                customerRequest.getUsername(),
                customerRequest.getEmail(),
                encoder.encode(customerRequest.getPassword()),
                roles,
                null,null,

                customerRequest.getDisplayName()

        );

        user.setEnabled(false);
        userRepository.save(user);
        Customer customer = new Customer(
               user,

               customerRequest.getAddress(),
                customerRequest.getCity(),
                customerRequest.isReceiveNewsletter(),
                customerRequest.isAcceptServiceTerms()
        );


        customerRepository.save(customer);
      } catch (Exception e) {
          throw new RuntimeException(e.getMessage());
      }
        return "controlla la tua email e conferma la registrazione";
    }
}
