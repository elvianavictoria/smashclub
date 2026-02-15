package com.backendsyndicate.smashclub.auth.service;

import com.backendsyndicate.smashclub.auth.dto.response.LoginResponse;
import com.backendsyndicate.smashclub.auth.model.Sessions;
import com.backendsyndicate.smashclub.auth.model.User;
import com.backendsyndicate.smashclub.auth.repository.SessionRepository;
import com.backendsyndicate.smashclub.common.security.JwtService;
import com.backendsyndicate.smashclub.common.handler.ResponseHandler;
import com.backendsyndicate.smashclub.common.constant.AuthenticationConstant;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {

    private final JwtService jwtService;
    private final SessionRepository sessionRepository;

    @Transactional
    public ResponseEntity<Object> generateJwtTokensAfterOtpVerification(User user,
                                                                        ResponseHandler responseHandler,
                                                                        HttpServletRequest httpRequest) {
        // 1. GENERATE JWT TOKENS
        String accessToken = jwtService.generateToken(user.getId(), user.getEmail(), user.getFullName());
        String refreshToken = jwtService.generateRefreshToken(user.getId());

        // 2. INVALIDATE EXISTING TOKENS & SAVE NEW TOKENS
        sessionRepository.invalidateUserTokens(user.getId(), LocalDateTime.now());

        // Save ACCESS token
        saveTokenToSession(user, accessToken, AuthenticationConstant.TOKEN_TYPE_ACCESS,
                AuthenticationConstant.ACCESS_TOKEN_EXPIRY_HOURS * 60L);

        // Save REFRESH token
        saveTokenToSession(user, refreshToken, AuthenticationConstant.TOKEN_TYPE_REFRESH,
                AuthenticationConstant.REFRESH_TOKEN_EXPIRY_DAYS * 24L * 60L);

        // 3. Buat response
        LoginResponse loginResponse = LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(AuthenticationConstant.ACCESS_TOKEN_EXPIRY_HOURS * 3600L)
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .build();

        log.info("Login successful with JWT - userId: {}, email: {}", user.getId(), user.getEmail());

        return responseHandler.handleResponse(
                "Login berhasil",
                HttpStatus.OK,
                null,
                loginResponse,
                httpRequest
        );
    }

    @Transactional
    public ResponseEntity<Object> refreshToken(String refreshToken, HttpServletRequest httpRequest,
                                               ResponseHandler responseHandler) {
        log.info("Refresh token request");

        try {
            // 1. Validasi refresh token di JWT
            if (!jwtService.isTokenValid(refreshToken)) {
                log.warn("Invalid refresh token - JWT validation failed");
                return responseHandler.handleResponse(
                        "Refresh token tidak valid",
                        HttpStatus.BAD_REQUEST,
                        "AUTH_023",
                        null,
                        httpRequest
                );
            }

            // 2. Cek di database apakah refresh token masih valid
            Sessions refreshTokenSession = validateRefreshTokenInDatabase(refreshToken);
            if (refreshTokenSession == null) {
                return responseHandler.handleResponse(
                        "Refresh token tidak valid",
                        HttpStatus.BAD_REQUEST,
                        "AUTH_023",
                        null,
                        httpRequest
                );
            }

            User user = refreshTokenSession.getUser();

            // 3. Cek user masih aktif
            if (user.getStatus() != AuthenticationConstant.ACTIVE) {
                log.warn("User not active during token refresh - userId: {}", user.getId());
                return responseHandler.handleResponse(
                        "Akun tidak aktif",
                        HttpStatus.BAD_REQUEST,
                        "AUTH_018",
                        null,
                        httpRequest
                );
            }

            // 4. Invalidate old refresh token
            refreshTokenSession.setInvalidatedAt(LocalDateTime.now());
            sessionRepository.save(refreshTokenSession);

            // 5. Generate new tokens
            String newAccessToken = jwtService.generateToken(user.getId(), user.getEmail(), user.getFullName());
            String newRefreshToken = jwtService.generateRefreshToken(user.getId());

            // 6. Save new tokens
            saveTokenToSession(user, newAccessToken, AuthenticationConstant.TOKEN_TYPE_ACCESS,
                    AuthenticationConstant.ACCESS_TOKEN_EXPIRY_HOURS * 60L);
            saveTokenToSession(user, newRefreshToken, AuthenticationConstant.TOKEN_TYPE_REFRESH,
                    AuthenticationConstant.REFRESH_TOKEN_EXPIRY_DAYS * 24L * 60L);

            // 7. Buat response
            var responseData = java.util.Map.of(
                    "accessToken", newAccessToken,
                    "refreshToken", newRefreshToken,
                    "tokenType", "Bearer",
                    "expiresIn", AuthenticationConstant.ACCESS_TOKEN_EXPIRY_HOURS * 3600L
            );

            log.info("Token refreshed successfully - userId: {}", user.getId());

            return responseHandler.handleResponse(
                    "Token berhasil diperbarui",
                    HttpStatus.OK,
                    null,
                    responseData,
                    httpRequest
            );

        } catch (Exception e) {
            log.error("Token refresh error: {}", e.getMessage());
            return responseHandler.handleResponse(
                    "Refresh token tidak valid",
                    HttpStatus.BAD_REQUEST,
                    "AUTH_023",
                    null,
                    httpRequest
            );
        }
    }

    private Sessions validateRefreshTokenInDatabase(String refreshToken) {
        return sessionRepository.findBySessionTokenAndTokenTypeAndExpiresAtAfterAndInvalidatedAtIsNull(
                refreshToken,
                AuthenticationConstant.TOKEN_TYPE_REFRESH,
                LocalDateTime.now()
        ).orElse(null);
    }

    private void saveTokenToSession(User user, String token, String tokenType, Long expiryMinutes) {
        Sessions session = new Sessions();
        session.setUser(user);
        session.setSessionToken(token);
        session.setTokenType(tokenType);
        session.setExpiresAt(LocalDateTime.now().plusMinutes(expiryMinutes));
        session.setCreatedAt(LocalDateTime.now());
        session.setLastAccessedAt(LocalDateTime.now());

        sessionRepository.save(session);
    }
}