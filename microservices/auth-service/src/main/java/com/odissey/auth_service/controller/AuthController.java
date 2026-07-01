package com.odissey.auth_service.controller;

import com.odissey.auth_service.dto.request.*;
import com.odissey.auth_service.dto.response.LoginResponse;
import com.odissey.auth_service.dto.response.UserResponse;
import com.odissey.auth_service.dto.response.UserStatusResponse;
import com.odissey.auth_service.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
            @RequestBody @Valid RegisterRequest registerRequest, @RequestHeader("X-User-Id") int createdBy){
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(registerRequest, createdBy));
    }

    @PostMapping("/login")
    //@CrossOrigin("*")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest loginRequest, HttpServletRequest request){
        return ResponseEntity.status(HttpStatus.OK).body(authService.login(loginRequest, request));
    }

    @PostMapping("/refresh")
    //@CrossOrigin("*")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid RefreshTokenRequest refreshTokenRequest, HttpServletRequest request){
        return ResponseEntity.status(HttpStatus.OK).body(authService.refresh(refreshTokenRequest.getRefreshTokenId(), request));
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@RequestBody @Valid RefreshTokenRequest refreshTokenRequest){
        authService.logout(refreshTokenRequest.getRefreshTokenId());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserStatusResponse> enableDisableUser(
            @PathVariable @Min(1) int id,
            @RequestHeader("X-User-Id") int updatedBy){
        return ResponseEntity.status(HttpStatus.OK).body(authService.enableDisableUser(id, updatedBy));
    }


    // CUSTOMER ------------------------------------------------

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody @Valid CustomerRequest customerRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.signup(customerRequest));
    }

    @PatchMapping("/confirm")
    public ResponseEntity<String> confirm(@RequestBody @Valid RegistrationConfirmRequest registrationConfirmRequest){
        return ResponseEntity.status(HttpStatus.OK).body(authService.confirm(registrationConfirmRequest.getOtpCode(), registrationConfirmRequest.getEmail().toLowerCase()));
    }


}
