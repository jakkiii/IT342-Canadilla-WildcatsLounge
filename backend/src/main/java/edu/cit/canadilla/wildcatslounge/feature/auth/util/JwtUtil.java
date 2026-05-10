package edu.cit.canadilla.wildcatslounge.feature.auth.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret:wildcatslounge-secret-key-must-be-at-least-256-bits-long-for-hs256}")
    private String secret;

    @Value("${jwt.access-token-expiry:86400000}")
    private long accessTokenExpiry; // 24 hours default

    @Value("${jwt.refresh-token-expiry:604800000}")
    private long refreshTokenExpiry; // 7 days default

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generate a short-lived access token for the given email
     */
    public String generateAccessToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiry))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Generate a long-lived refresh token for the given email
     */
    public String generateRefreshToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiry))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}
