package com.hyperativa.Services;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class TokenService {


    private final SecretKey secretKey = Keys.hmacShaKeyFor("Yh7sJ9kTkq2XvA2dL3eZsNcBfT6uWrQx".getBytes(StandardCharsets.UTF_8));

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token);
            return true; // valid
        } catch (ExpiredJwtException e) {
            System.out.println("Expired token");
            return false;
        } catch (JwtException e) {
            System.out.println("Invalid token");
            return false;
        }
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .getBody();
    }
}
