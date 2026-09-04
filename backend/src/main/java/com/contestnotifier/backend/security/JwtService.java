package com.contestnotifier.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {
    private final SecretKey key;

    public JwtService(@Value("${jwt.secret:change-this-secret-key-to-a-long-value}") String secret) {
        key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // create JWT token
    public String createToken(String email, String name, String googleId) {
        return Jwts.builder().subject(email).claim("name", name).claim("googleId", googleId)
                .issuedAt(new Date()).expiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(key).compact();
    }

    public Claims getClaims(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }
}