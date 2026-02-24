package com.backendsyndicate.smashclub.auth.service;

import com.backendsyndicate.smashclub.auth.AuthTestBase;
import com.backendsyndicate.smashclub.auth.dto.response.LoginResponse;
import com.backendsyndicate.smashclub.auth.model.Sessions;
import com.backendsyndicate.smashclub.auth.model.User;
import com.backendsyndicate.smashclub.auth.repository.SessionRepository;
import com.backendsyndicate.smashclub.common.constant.AuthenticationConstant;
import com.backendsyndicate.smashclub.common.handler.ResponseHandler;
import com.backendsyndicate.smashclub.common.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest extends AuthTestBase {

    @Mock
    private JwtService jwtService;

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private ResponseHandler responseHandler;

    @Mock
    private HttpServletRequest httpRequest;

    @InjectMocks
    private TokenService tokenService;

    private User activeUser;
    private User inactiveUser;
    private String accessToken;
    private String refreshToken;
    private String newAccessToken;
    private String newRefreshToken;
    private Sessions refreshTokenSession;
    private Map<String, Object> responseData;
    private ResponseEntity<Object> successResponse;
    private ResponseEntity<Object> errorResponse;

    @BeforeEach
    void setUp() {
        activeUser = createActiveUser();
        inactiveUser = createPendingUser();

        accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0LXVzZXItaWQifQ.signature";
        refreshToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0LXVzZXItaWQifQ.signature";
        newAccessToken = "new-access-token";
        newRefreshToken = "new-refresh-token";

        refreshTokenSession = createRefreshTokenSession(activeUser, refreshToken);

        responseData = new HashMap<>();
        successResponse = ResponseEntity.ok(responseData);
        errorResponse = ResponseEntity.badRequest().body(responseData);

        // REMOVED: when(httpRequest.getRemoteAddr()).thenReturn("127.0.0.1");
        // Stubbing ini tidak diperlukan karena tidak digunakan di test
    }

    @Nested
    @DisplayName("Generate JWT Tokens After OTP Verification Tests")
    class GenerateJwtTokensAfterOtpVerificationTests {

        @Test
        @DisplayName("Should generate both access and refresh tokens")
        void generateJwtTokensAfterOtpVerification_ShouldCreateBothTokens() {
            // Arrange
            when(jwtService.generateToken(activeUser.getId(), activeUser.getEmail(), activeUser.getFullName()))
                    .thenReturn(accessToken);
            when(jwtService.generateRefreshToken(activeUser.getId()))
                    .thenReturn(refreshToken);

            ArgumentCaptor<Sessions> sessionCaptor = ArgumentCaptor.forClass(Sessions.class);

            LoginResponse expectedResponse = LoginResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .expiresIn(AuthenticationConstant.ACCESS_TOKEN_EXPIRY_HOURS * 3600L)
                    .userId(activeUser.getId())
                    .email(activeUser.getEmail())
                    .fullName(activeUser.getFullName())
                    .build();

            doReturn(successResponse)
                    .when(responseHandler).handleResponse(
                            eq("Login berhasil"),
                            eq(HttpStatus.OK),
                            isNull(),
                            eq(expectedResponse),
                            eq(httpRequest)
                    );

            // Act
            ResponseEntity<Object> response = tokenService.generateJwtTokensAfterOtpVerification(
                    activeUser, responseHandler, httpRequest);

            // Assert
            assertNotNull(response);

            verify(sessionRepository).invalidateUserTokens(eq(activeUser.getId()), any(LocalDateTime.class));
            verify(sessionRepository, times(2)).save(sessionCaptor.capture());

            List<Sessions> savedSessions = sessionCaptor.getAllValues();
            assertEquals(2, savedSessions.size());
        }

        @Test
        @DisplayName("Should invalidate all existing tokens before creating new ones")
        void generateJwtTokensAfterOtpVerification_ShouldInvalidateOldTokens() {
            // Arrange
            when(jwtService.generateToken(anyString(), anyString(), anyString())).thenReturn(accessToken);
            when(jwtService.generateRefreshToken(anyString())).thenReturn(refreshToken);

            doReturn(successResponse)
                    .when(responseHandler).handleResponse(anyString(), any(), any(), any(), eq(httpRequest));

            // Act
            tokenService.generateJwtTokensAfterOtpVerification(activeUser, responseHandler, httpRequest);

            // Assert
            verify(sessionRepository).invalidateUserTokens(eq(activeUser.getId()), any(LocalDateTime.class));
        }
    }

    @Nested
    @DisplayName("Refresh Token Tests")
    class RefreshTokenTests {

        @Test
        @DisplayName("Should refresh tokens with valid refresh token")
        void refreshToken_WithValidToken_ShouldGenerateNewTokens() {
            // Arrange
            when(jwtService.isTokenValid(refreshToken)).thenReturn(true);
            when(sessionRepository.findBySessionTokenAndTokenTypeAndExpiresAtAfterAndInvalidatedAtIsNull(
                    eq(refreshToken),
                    eq(AuthenticationConstant.TOKEN_TYPE_REFRESH),
                    any(LocalDateTime.class)
            )).thenReturn(Optional.of(refreshTokenSession));

            when(jwtService.generateToken(activeUser.getId(), activeUser.getEmail(), activeUser.getFullName()))
                    .thenReturn(newAccessToken);
            when(jwtService.generateRefreshToken(activeUser.getId()))
                    .thenReturn(newRefreshToken);

            Map<String, Object> expectedData = Map.of(
                    "accessToken", newAccessToken,
                    "refreshToken", newRefreshToken,
                    "tokenType", "Bearer",
                    "expiresIn", AuthenticationConstant.ACCESS_TOKEN_EXPIRY_HOURS * 3600L
            );

            doReturn(successResponse)
                    .when(responseHandler).handleResponse(
                            eq("Token berhasil diperbarui"),
                            eq(HttpStatus.OK),
                            isNull(),
                            eq(expectedData),
                            eq(httpRequest)
                    );

            // Act
            ResponseEntity<Object> response = tokenService.refreshToken(
                    refreshToken, httpRequest, responseHandler);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());

            verify(sessionRepository).save(refreshTokenSession);
            assertNotNull(refreshTokenSession.getInvalidatedAt());

            // Verify total saves = 3 (1 invalidate + 2 new tokens)
            verify(sessionRepository, times(3)).save(any(Sessions.class));
        }

        @Test
        @DisplayName("Should return error when refresh token is invalid in JWT")
        void refreshToken_WithInvalidJwt_ShouldReturnError() {
            // Arrange
            when(jwtService.isTokenValid("invalid-token")).thenReturn(false);

            doReturn(errorResponse)
                    .when(responseHandler).handleResponse(
                            eq("Refresh token tidak valid"),
                            eq(HttpStatus.BAD_REQUEST),
                            eq("AUTH_023"),
                            isNull(),
                            eq(httpRequest)
                    );

            // Act
            ResponseEntity<Object> response = tokenService.refreshToken(
                    "invalid-token", httpRequest, responseHandler);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            verify(sessionRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should return error when refresh token not found in database")
        void refreshToken_WithTokenNotFoundInDb_ShouldReturnError() {
            // Arrange
            when(jwtService.isTokenValid(refreshToken)).thenReturn(true);
            when(sessionRepository.findBySessionTokenAndTokenTypeAndExpiresAtAfterAndInvalidatedAtIsNull(
                    anyString(), anyString(), any(LocalDateTime.class)))
                    .thenReturn(Optional.empty());

            doReturn(errorResponse)
                    .when(responseHandler).handleResponse(
                            eq("Refresh token tidak valid"),
                            eq(HttpStatus.BAD_REQUEST),
                            eq("AUTH_023"),
                            isNull(),
                            eq(httpRequest)
                    );

            // Act
            ResponseEntity<Object> response = tokenService.refreshToken(
                    refreshToken, httpRequest, responseHandler);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            verify(sessionRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should return error when user is not active")
        void refreshToken_WithInactiveUser_ShouldReturnError() {
            // Arrange
            refreshTokenSession.setUser(inactiveUser);

            when(jwtService.isTokenValid(refreshToken)).thenReturn(true);
            when(sessionRepository.findBySessionTokenAndTokenTypeAndExpiresAtAfterAndInvalidatedAtIsNull(
                    anyString(), anyString(), any(LocalDateTime.class)))
                    .thenReturn(Optional.of(refreshTokenSession));

            doReturn(errorResponse)
                    .when(responseHandler).handleResponse(
                            eq("Akun tidak aktif"),
                            eq(HttpStatus.BAD_REQUEST),
                            eq("AUTH_018"),
                            isNull(),
                            eq(httpRequest)
                    );

            // Act
            ResponseEntity<Object> response = tokenService.refreshToken(
                    refreshToken, httpRequest, responseHandler);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            verify(sessionRepository, never()).save(any(Sessions.class));
        }

        @Test
        @DisplayName("Should handle exceptions gracefully")
        void refreshToken_WithException_ShouldReturnError() {
            // Arrange
            when(jwtService.isTokenValid(refreshToken)).thenThrow(new RuntimeException("JWT error"));

            doReturn(errorResponse)
                    .when(responseHandler).handleResponse(
                            eq("Refresh token tidak valid"),
                            eq(HttpStatus.BAD_REQUEST),
                            eq("AUTH_023"),
                            isNull(),
                            eq(httpRequest)
                    );

            // Act
            ResponseEntity<Object> response = tokenService.refreshToken(
                    refreshToken, httpRequest, responseHandler);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
    }
}