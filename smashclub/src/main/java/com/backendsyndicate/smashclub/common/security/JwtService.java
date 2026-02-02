package com.backendsyndicate.smashclub.common.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@Slf4j
public class JwtService {

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.expiration-hours:24}")
    private Long expirationHours;

    @Value("${jwt.refresh-expiration-days:7}")
    private Long refreshExpirationDays;

    @Value("${jwt.issuer:smashclub-backend}")
    private String issuer;

    @Value("${jwt.audience:smashclub-client}")
    private String audience;

    // ============ KEY MANAGEMENT ============
    private SecretKey getSigningKey() {
        try {
            byte[] keyBytes = Decoders.BASE64.decode(secretKey);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (IllegalArgumentException e) {
            log.error("Invalid JWT secret key format. Must be Base64 encoded.");
            throw new RuntimeException("Invalid JWT configuration", e);
        }
    }

    // ============ TOKEN GENERATION ============
    public String generateToken(String userId, String email, String fullName) {
        return buildToken(userId, email, fullName, expirationHours * 3600000L, "ACCESS");
    }

    public String generateToken(String userId, String email, String fullName, int customExpiryHours) {
        return buildToken(userId, email, fullName, customExpiryHours * 3600000L, "ACCESS");
    }

    public String generateRefreshToken(String userId) {
        return buildRefreshToken(userId, refreshExpirationDays * 24 * 3600000L);
    }

    public String generateRefreshToken(String userId, int customExpiryDays) {
        return buildRefreshToken(userId, customExpiryDays * 24 * 3600000L);
    }

    private String buildToken(String userId, String email, String fullName,
                              long expirationMillis, String tokenType) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("fullName", fullName);
        claims.put("tokenType", tokenType);

        return Jwts.builder()
                .claims(claims)
                .subject(userId)
                .issuer(issuer)
                .audience().add(audience).and()
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    private String buildRefreshToken(String userId, long expirationMillis) {
        return Jwts.builder()
                .subject(userId)
                .issuer(issuer)
                .audience().add(audience).and()
                .claim("tokenType", "REFRESH")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    // ============ TOKEN VALIDATION ============
    public boolean isTokenValid(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("JWT token expired: {}", e.getMessage());
            return false;
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT token: {}", e.getMessage());
            return false;
        } catch (MalformedJwtException e) {
            log.warn("Malformed JWT token: {}", e.getMessage());
            return false;
        } catch (SecurityException | SignatureException e) {
            log.warn("Invalid JWT signature: {}", e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            log.warn("JWT claims string is empty: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Unexpected JWT validation error: {}", e.getMessage());
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (Exception e) {
            log.warn("Failed to check token expiration: {}", e.getMessage());
            return true; // Consider expired if cannot parse
        }
    }

    public boolean validateToken(String token, String userId) {
        try {
            final String extractedUserId = extractUserId(token);
            return extractedUserId.equals(userId) && !isTokenExpired(token);
        } catch (Exception e) {
            log.warn("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    // ============ CLAIM EXTRACTION ============
    public String extractUserId(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractEmail(String token) {
        return extractClaim(token, claims -> claims.get("email", String.class));
    }

    // ⭐️ TAMBAHKAN METHOD INI! (Sangat penting)
    public String extractFullName(String token) {
        return extractClaim(token, claims -> claims.get("fullName", String.class));
    }

    public String extractTokenType(String token) {
        return extractClaim(token, claims -> claims.get("tokenType", String.class));
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public Date extractIssuedAt(String token) {
        return extractClaim(token, Claims::getIssuedAt);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        try {
            final Claims claims = extractAllClaims(token);
            return claimsResolver.apply(claims);
        } catch (JwtException e) {
            log.error("Failed to extract claim from token: {}", e.getMessage());
            throw e;
        }
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // ============ UTILITY METHODS ============
    public Long getRemainingTimeSeconds(String token) {
        try {
            Date expiration = extractExpiration(token);
            long remainingMillis = expiration.getTime() - System.currentTimeMillis();
            return Math.max(0, remainingMillis / 1000);
        } catch (Exception e) {
            log.warn("Failed to calculate remaining time: {}", e.getMessage());
            return 0L;
        }
    }

    public boolean shouldTokenBeRefreshed(String token) {
        Long remainingSeconds = getRemainingTimeSeconds(token);
        // Refresh jika sisa waktu kurang dari 5 menit
        return remainingSeconds > 0 && remainingSeconds <= 300;
    }

    public Map<String, Object> getTokenMetadata(String token) {
        Map<String, Object> metadata = new HashMap<>();
        try {
            metadata.put("userId", extractUserId(token));
            metadata.put("email", extractEmail(token));
            metadata.put("fullName", extractFullName(token));
            metadata.put("tokenType", extractTokenType(token));
            metadata.put("expiration", extractExpiration(token));
            metadata.put("issuedAt", extractIssuedAt(token));
            metadata.put("remainingSeconds", getRemainingTimeSeconds(token));
            metadata.put("issuer", issuer);
            metadata.put("audience", audience);
        } catch (Exception e) {
            log.error("Failed to extract token metadata: {}", e.getMessage());
        }
        return metadata;
    }

    // ============ TOKEN PARSING FROM HEADER ============
    public String extractTokenFromHeader(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }

    // ============ BULK VALIDATION (Optional) ============
    public boolean isAccessToken(String token) {
        try {
            return "ACCESS".equals(extractTokenType(token));
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isRefreshToken(String token) {
        try {
            return "REFRESH".equals(extractTokenType(token));
        } catch (Exception e) {
            return false;
        }
    }
}