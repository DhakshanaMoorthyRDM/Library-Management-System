package com.library.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.library.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expirationTime;


    // =========================
    // CREATE SECRET KEY
    // =========================

    private SecretKey createSecretKey() {

        return Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
    }


    // =========================
    // GENERATE TOKEN
    // =========================

    public String generateToken(User user) {

        Date now = new Date();

        Date expiry =
                new Date(
                        now.getTime()
                                + expirationTime);

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("userId", user.getId())
                .claim("name", user.getName())
                .claim("role", user.getRole())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(createSecretKey())
                .compact();
    }


    // =========================
    // EXTRACT USERNAME
    // =========================

    public String extractUsername(String token) {

        return extractAllClaims(token)
                .getSubject();
    }


    // =========================
    // EXTRACT ALL CLAIMS
    // =========================

    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(createSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


    // =========================
    // CHECK TOKEN VALID
    // =========================

    public boolean isTokenValid(String token) {

        try {

            extractAllClaims(token);

            return true;

        } catch (Exception exception) {

            return false;
        }
    }


    // =========================
    // GET SECRET KEY
    // =========================

    public SecretKey getSecretKey() {

        return createSecretKey();
    }
}