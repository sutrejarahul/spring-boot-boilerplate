package com.example.security;

import com.example.exception.JwtAuthenticationException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${app.jwtSecret:JwtSecretKey}")
    private String jwtSecret;

    @Value("${app.jwtExpirationInMs:3600000}") // Default: 1 hour
    private int jwtExpirationInMs;

    // Generate a JWT token using the authenticated user's details
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);
        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getKey())
                .compact();
    }

    public SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Extract username from token
    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getKey())
                .build().parseSignedClaims(token).getPayload();
        return claims.getSubject();
    }

    // Validate JWT token and throw exceptions with detailed messages
    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(authToken);
            return true;
        } catch (SignatureException ex) {
            throw new JwtAuthenticationException("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            throw new JwtAuthenticationException("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            throw new JwtAuthenticationException("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            throw new JwtAuthenticationException("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            throw new JwtAuthenticationException("JWT claims string is empty.");
        }
    }
}

