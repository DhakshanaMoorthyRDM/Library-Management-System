package com.library.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.library.dto.LoginRequest;
import com.library.dto.LoginResponse;
import com.library.entity.User;
import com.library.exception.BorrowException;
import com.library.repository.UserRepository;
import com.library.service.AuthService;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {

        User user = userRepository
                .findByEmail(loginRequest.getEmail())
                .orElse(null);

        if (user == null) {
            throw new BorrowException("Invalid email or password");
        }

        if (!user.getPassword().equals(loginRequest.getPassword())) {
            throw new BorrowException("Invalid email or password");
        }

        if (!"ACTIVE".equals(user.getStatus())) {
            throw new BorrowException("User account is inactive");
        }

        return new LoginResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getStatus()
        );
    }
}