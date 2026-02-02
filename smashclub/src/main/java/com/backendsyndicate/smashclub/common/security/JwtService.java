package com.backendsyndicate.smashclub.common.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class JwtService {

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.expiration-hours:24}")
    private Long expirationHours;

    @Value("${jwt.refresh-expiration-days:7}")
    private Long refreshExpirationDays;

    // ============ SIMPLE FIX ============
    private SecretKey getSigningKey() {
        try {
            // Coba sebagai Base64
            byte[] keyBytes = Decoders.BASE64.decode(secretKey);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception e) {
            // Jika gagal, gunakan string langsung
            log.warn("Using string as JWT key. Generate proper Base64 key for production!");
            byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);

            // Pastikan panjang 32 bytes untuk HS256
            byte[] fixedBytes = new byte[32];
            System.arraycopy(keyBytes, 0, fixedBytes, 0, Math.min(keyBytes.length, 32));

            return Keys.hmacShaKeyFor(fixedBytes);
        }
    }

    // ============ GENERATE TOKEN ============
    public String generateToken(String userId, String email, String fullName) {
        return Jwts.builder()
                .subject(userId)
                .claim("email", email)
                .claim("fullName", fullName)
                .claim("tokenType", "ACCESS")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationHours * 3600000L))
                .signWith(getSigningKey())
                .compact();
    }

    public String generateRefreshToken(String userId) {
        return Jwts.builder()
                .subject(userId)
                .claim("tokenType", "REFRESH")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshExpirationDays * 24 * 3600000L))
                .signWith(getSigningKey())
                .compact();
    }

    // ============ VALIDATE TOKEN ============
    public boolean isTokenValid(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            log.warn("Invalid token: {}", e.getMessage());
            return false;
        }
    }

    // ============ EXTRACT DATA ============
    public String extractUserId(String token) {
        return parseToken(token).getSubject();
    }

    public String extractEmail(String token) {
        return parseToken(token).get("email", String.class);
    }

    public String extractFullName(String token) {
        return parseToken(token).get("fullName", String.class);
    }

    public Date extractExpiration(String token) {
        return parseToken(token).getExpiration();
    }

    private Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}