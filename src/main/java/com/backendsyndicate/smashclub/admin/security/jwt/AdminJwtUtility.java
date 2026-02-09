package com.backendsyndicate.smashclub.admin.security.jwt;

import com.backendsyndicate.smashclub.common.config.AdminJwtConfig;
import com.backendsyndicate.smashclub.common.util.Logging;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

/**
 * class untuk fungsional Json Web Token
 */
@Component
public class AdminJwtUtility {
    public AdminJwtModel mapToken(String token) {
        Claims claims = extractToken(token);
        AdminJwtModel body = new AdminJwtModel();
        body.setUsername(claims.getSubject());

        return body;
    }

    private SecretKey getSigningKey() {
        String secretKey = AdminJwtConfig.getSecretKey();

        try {
            // Coba sebagai Base64
            byte[] keyBytes = Decoders.BASE64.decode(secretKey);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception e) {
            // Jika gagal, gunakan string langsung
            byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);

            // Pastikan panjang 32 bytes untuk HS256
            byte[] fixedBytes = new byte[32];
            System.arraycopy(keyBytes, 0, fixedBytes, 0, Math.min(keyBytes.length, 32));

            return Keys.hmacShaKeyFor(fixedBytes);
        }
    }

    private Claims extractToken(String token) {
        return Jwts.parser().verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractToken(token);
        return claimsResolver.apply(claims);
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /** fungsi ini dipanggil saat login */
    public String doGenerateToken(Map<String, Object> claims, String subject) {
        Long timeMilis = System.currentTimeMillis();
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(timeMilis))
                .expiration(new Date(timeMilis + AdminJwtConfig.getExpirationHours() * 3600000L))
                .signWith(getSigningKey()).compact();
    }

    public Boolean validateToken(String token) {
        /** Sudah otomatis tervalidaasi jika expired date masih aktif */
        String username = getUsernameFromToken(token);
        return (username!=null && !isTokenExpired(token));
    }

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }
}