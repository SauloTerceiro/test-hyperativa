package com.hyperativa.Components;

import com.hyperativa.Services.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    
    private final SecretKey secretKey = Keys.hmacShaKeyFor("Yh7sJ9kTkq2XvA2dL3eZsNcBfT6uWrQx".getBytes(StandardCharsets.UTF_8));

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String token = getJwtFromRequest(request);

        if (token != null && validateToken(token)) {
            Claims claims = extractClaims(token);
            Integer userId = claims.get("id", Integer.class);            
            System.out.println("📧 Token user: " + userId);
            // Here, you can set the authenticated user to use it anywhere in the application
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userId, null, null);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Removes the "Bearer " prefix
        }
        return null;
    }

    private boolean validateToken(String token) {
        try {
            Jwts.parser()
                .setSigningKey(secretKey) // ⚠️ Possible inconsistency here
                .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims extractClaims(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }
}
