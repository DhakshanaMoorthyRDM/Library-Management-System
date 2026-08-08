package com.library.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.library.dto.LoginRequest;
import com.library.dto.LoginResponse;
import com.library.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger =
            LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest loginRequest) {

        logger.info(
                "Login request received for email: {}",
                loginRequest.getEmail());

        LoginResponse response =
                authService.login(loginRequest);

        logger.info(
                "Login successful for email: {}",
                loginRequest.getEmail());

        return ResponseEntity.ok(response);
    }
}