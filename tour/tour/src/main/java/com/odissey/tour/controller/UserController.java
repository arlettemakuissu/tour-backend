package com.odissey.tour.controller;

import com.odissey.tour.model.dto.request.UserRequest;
import com.odissey.tour.model.dto.request.UserRoleRequest;
import com.odissey.tour.model.dto.response.UserDetailResponse;
import com.odissey.tour.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // INSERIRE A SISTEMA UN UTENTE DI TIPO ADMIN OPPURE OPERATOR
    @PostMapping
    public ResponseEntity<UserDetailResponse> register(@RequestBody @Valid UserRoleRequest req){
        return new ResponseEntity<>(userService.register(req),HttpStatus.CREATED);
    }


}
