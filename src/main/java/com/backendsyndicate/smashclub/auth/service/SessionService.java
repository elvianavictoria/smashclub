package com.backendsyndicate.smashclub.auth.service;

import com.backendsyndicate.smashclub.auth.dto.response.SessionResponse;
import com.backendsyndicate.smashclub.auth.model.Sessions;
import com.backendsyndicate.smashclub.auth.model.User;
import com.backendsyndicate.smashclub.auth.repository.SessionRepository;
import com.backendsyndicate.smashclub.auth.repository.UserRepository;
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
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SessionService {

    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Transactional
    public ResponseEntity<Object> checkSession(String accessToken, HttpServletRequest httpRequest,
                                               ResponseHandler responseHandler) {
        log.debug("Check session request");

        try {
            // 1. Validasi JWT token format
            if (!jwtService.isTokenValid(accessToken)) {
                log.warn("Invalid JWT token");
                return responseHandler.handleResponse(
                        "Token tidak valid atau telah expired",
                        HttpStatus.BAD_REQUEST,
                        "AUTH_015",
                        null,
                        httpRequest
                );
            }

            // 2. Cek di database apakah token belum di-invalidate
            Optional<Sessions> sessionOpt = sessionRepository.findBySessionTokenAndTokenTypeAndExpiresAtAfterAndInvalidatedAtIsNull(
                    accessToken,
                    AuthenticationConstant.TOKEN_TYPE_ACCESS,
                    LocalDateTime.now()
            );

            if (sessionOpt.isEmpty()) {
                log.warn("Token revoked or expired in database");
                return responseHandler.handleResponse(
                        "Session telah di-revoke",
                        HttpStatus.BAD_REQUEST,
                        "AUTH_024",
                        null,
                        httpRequest
                );
            }

            // 3. Update last accessed time
            Sessions session = sessionOpt.get();
            session.setLastAccessedAt(LocalDateTime.now());
            sessionRepository.save(session);

            // 4. Extract data dari JWT
            String userId = jwtService.extractUserId(accessToken);
            String email = jwtService.extractEmail(accessToken);
            String fullName = jwtService.extractFullName(accessToken);
            Date expiresAt = jwtService.extractExpiration(accessToken);

            // 5. Cek user masih aktif
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty() || userOpt.get().getStatus() != AuthenticationConstant.ACTIVE) {
                return responseHandler.handleResponse(
                        "User tidak aktif",
                        HttpStatus.BAD_REQUEST,
                        "AUTH_018",
                        null,
                        httpRequest
                );
            }

            User user = userOpt.get();

            // 6. Buat response
            SessionResponse sessionResponse = SessionResponse.builder()
                    .userId(userId)
                    .email(email)
                    .fullName(fullName != null ? fullName : user.getFullName())
                    .expiresAt(expiresAt.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                    .tokenValid(true)
                    .build();

            log.debug("Session check successful - userId: {}", userId);

            return responseHandler.handleResponse(
                    "Token valid",
                    HttpStatus.OK,
                    null,
                    sessionResponse,
                    httpRequest
            );

        } catch (Exception e) {
            log.error("Session check error: {}", e.getMessage());
            return responseHandler.handleResponse(
                    "Token tidak valid",
                    HttpStatus.BAD_REQUEST,
                    "AUTH_015",
                    null,
                    httpRequest
            );
        }
    }

    @Transactional
    public ResponseEntity<Object> logout(String refreshToken, HttpServletRequest httpRequest,
                                         ResponseHandler responseHandler) {
        log.info("Logout request");

        try {
            // 1. Extract user ID from refresh token
            String userId = jwtService.extractUserId(refreshToken);

            // 2. Invalidate all tokens for this user
            invalidateAllUserTokens(userId);

            log.info("Logout successful - userId: {}", userId);

            return responseHandler.handleResponse(
                    "Logout berhasil",
                    HttpStatus.OK,
                    null,
                    null,
                    httpRequest
            );

        } catch (Exception e) {
            log.warn("Logout with invalid token - error: {}", e.getMessage());
            // Tetap return success karena token sudah invalid
            return responseHandler.handleResponse(
                    "Logout berhasil",
                    HttpStatus.OK,
                    null,
                    null,
                    httpRequest
            );
        }
    }

    @Transactional
    public ResponseEntity<Object> logoutAll(String userId, HttpServletRequest httpRequest,
                                            ResponseHandler responseHandler) {
        log.info("Logout all sessions request - userId: {}", userId);

        // Cek user exists
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return responseHandler.handleResponse(
                    "User tidak ditemukan",
                    HttpStatus.NOT_FOUND,
                    "AUTH_013",
                    null,
                    httpRequest
            );
        }

        // Invalidate all tokens for this user
        invalidateAllUserTokens(userId);

        log.info("All sessions logged out - userId: {}", userId);

        return responseHandler.handleResponse(
                "Semua sesi berhasil di-logout",
                HttpStatus.OK,
                null,
                null,
                httpRequest
        );
    }

    public void invalidateAllUserTokens(String userId) {
        sessionRepository.invalidateUserTokens(userId, LocalDateTime.now());
    }
}