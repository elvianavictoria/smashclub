package com.backendsyndicate.smashclub.auth.service;

import com.backendsyndicate.smashclub.auth.AuthTestBase;
import com.backendsyndicate.smashclub.auth.dto.request.ForgotPasswordRequest;
import com.backendsyndicate.smashclub.auth.dto.request.ResetPasswordRequest;
import com.backendsyndicate.smashclub.auth.model.PasswordResetTokens;
import com.backendsyndicate.smashclub.auth.model.User;
import com.backendsyndicate.smashclub.auth.repository.PasswordResetTokenRepository;
import com.backendsyndicate.smashclub.auth.repository.UserRepository;
import com.backendsyndicate.smashclub.common.constant.AuthenticationConstant;
import com.backendsyndicate.smashclub.common.handler.ResponseHandler;
import com.backendsyndicate.smashclub.common.security.PasswordHasher;
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
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
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
@MockitoSettings(strictness = Strictness.LENIENT)
class PasswordServiceTest extends AuthTestBase {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private EmailServiceImpl emailServiceImpl;

    @Mock
    private PasswordHasher passwordHasher;

    @Mock
    private SessionService sessionService;

    @Mock
    private ResponseHandler responseHandler;

    @Mock
    private HttpServletRequest httpRequest;

    @InjectMocks
    private PasswordService passwordService;

    private User activeUser;
    private User pendingUser;
    private User lockedUser;
    private PasswordResetTokens validResetToken;
    private PasswordResetTokens expiredResetToken;
    private ForgotPasswordRequest forgotPasswordRequest;
    private ResetPasswordRequest resetPasswordRequest;
    private Map<String, Object> responseData;
    private ResponseEntity<Object> successResponse;
    private ResponseEntity<Object> errorResponse;
    private ResponseEntity<Object> notFoundResponse;

    @BeforeEach
    void setUp() {
        activeUser = createActiveUser();
        pendingUser = createPendingUser();
        lockedUser = createLockedUser();

        validResetToken = createPasswordResetToken(activeUser);
        validResetToken.setExpiresAt(LocalDateTime.now().plusHours(1));

        expiredResetToken = createPasswordResetToken(activeUser);
        expiredResetToken.setExpiresAt(LocalDateTime.now().minusHours(1));

        forgotPasswordRequest = new ForgotPasswordRequest();
        forgotPasswordRequest.setEmail(TEST_EMAIL);

        resetPasswordRequest = new ResetPasswordRequest();
        resetPasswordRequest.setToken(TEST_TOKEN);
        resetPasswordRequest.setNewPassword("NewPassword123");

        responseData = new HashMap<>();
        successResponse = ResponseEntity.ok(responseData);
        errorResponse = ResponseEntity.badRequest().body(responseData);
        notFoundResponse = ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseData);

        when(httpRequest.getRemoteAddr()).thenReturn("127.0.0.1");
    }

    @Nested
    @DisplayName("Forgot Password Tests")
    class ForgotPasswordTests {

        @Test
        @DisplayName("Should create reset token and send email for valid user")
        void forgotPassword_WithValidUser_ShouldCreateTokenAndSendEmail() {
            // Arrange
            when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(activeUser));
            when(passwordResetTokenRepository.countByUserAndUsedAtIsNull(activeUser)).thenReturn(0L);
            when(passwordResetTokenRepository.save(any(PasswordResetTokens.class)))
                    .thenAnswer(i -> i.getArgument(0));

            doReturn(successResponse)
                    .when(responseHandler).handleResponse(
                            eq("Link reset password dikirim ke email"),
                            eq(HttpStatus.OK),
                            isNull(),
                            isNull(),
                            eq(httpRequest)
                    );

            // Act
            ResponseEntity<Object> response = passwordService.forgotPassword(
                    forgotPasswordRequest, httpRequest, responseHandler);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(passwordResetTokenRepository).save(any(PasswordResetTokens.class));
            verify(emailServiceImpl).sendResetPasswordEmail(eq(TEST_EMAIL), anyString());
        }

        @Test
        @DisplayName("Should return success even when email not found (security)")
        void forgotPassword_WithNonExistentEmail_ShouldReturnSuccess() {
            // Arrange
            when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

            doReturn(successResponse)
                    .when(responseHandler).handleResponse(
                            eq("Jika email terdaftar, link reset akan dikirim"),
                            eq(HttpStatus.OK),
                            isNull(),
                            isNull(),
                            eq(httpRequest)
                    );

            // Act
            ResponseEntity<Object> response = passwordService.forgotPassword(
                    forgotPasswordRequest, httpRequest, responseHandler);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(passwordResetTokenRepository, never()).save(any());
            verify(emailServiceImpl, never()).sendResetPasswordEmail(anyString(), anyString());
        }

        @Test
        @DisplayName("Should return error for pending account")
        void forgotPassword_WithPendingAccount_ShouldReturnError() {
            // Arrange
            when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(pendingUser));

            doReturn(errorResponse)
                    .when(responseHandler).handleResponse(
                            eq("Akun belum aktif. Silakan verifikasi email terlebih dahulu"),
                            eq(HttpStatus.BAD_REQUEST),
                            eq("AUTH_004"),
                            isNull(),
                            eq(httpRequest)
                    );

            // Act
            ResponseEntity<Object> response = passwordService.forgotPassword(
                    forgotPasswordRequest, httpRequest, responseHandler);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            verify(passwordResetTokenRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should return error for locked account")
        void forgotPassword_WithLockedAccount_ShouldReturnError() {
            // Arrange
            when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(lockedUser));

            doReturn(errorResponse)
                    .when(responseHandler).handleResponse(
                            eq("Akun terkunci. Tidak dapat reset password"),
                            eq(HttpStatus.BAD_REQUEST),
                            eq("AUTH_005"),
                            isNull(),
                            eq(httpRequest)
                    );

            // Act
            ResponseEntity<Object> response = passwordService.forgotPassword(
                    forgotPasswordRequest, httpRequest, responseHandler);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            verify(passwordResetTokenRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should invalidate existing tokens before creating new one")
        void forgotPassword_WithExistingTokens_ShouldInvalidateThem() {
            // Arrange
            when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(activeUser));
            when(passwordResetTokenRepository.countByUserAndUsedAtIsNull(activeUser)).thenReturn(2L);
            when(passwordResetTokenRepository.invalidateByUser(eq(activeUser), any(LocalDateTime.class)))
                    .thenReturn(2);
            when(passwordResetTokenRepository.save(any(PasswordResetTokens.class)))
                    .thenAnswer(i -> i.getArgument(0));

            doReturn(successResponse)
                    .when(responseHandler).handleResponse(anyString(), any(), any(), any(), eq(httpRequest));

            // Act
            passwordService.forgotPassword(forgotPasswordRequest, httpRequest, responseHandler);

            // Assert
            verify(passwordResetTokenRepository).invalidateByUser(eq(activeUser), any(LocalDateTime.class));
            verify(passwordResetTokenRepository).save(any(PasswordResetTokens.class));
        }

        @Test
        @DisplayName("Should set token expiry to 1 hour")
        void forgotPassword_ShouldSetCorrectExpiry() {
            // Arrange
            when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(activeUser));
            when(passwordResetTokenRepository.countByUserAndUsedAtIsNull(activeUser)).thenReturn(0L);

            ArgumentCaptor<PasswordResetTokens> tokenCaptor =
                    ArgumentCaptor.forClass(PasswordResetTokens.class);
            when(passwordResetTokenRepository.save(tokenCaptor.capture()))
                    .thenAnswer(i -> i.getArgument(0));

            doReturn(successResponse)
                    .when(responseHandler).handleResponse(anyString(), any(), any(), any(), eq(httpRequest));

            // Act
            passwordService.forgotPassword(forgotPasswordRequest, httpRequest, responseHandler);

            // Assert
            PasswordResetTokens savedToken = tokenCaptor.getValue();
            LocalDateTime expectedExpiry = LocalDateTime.now().plusHours(1);
            assertTrue(savedToken.getExpiresAt().isAfter(LocalDateTime.now().plusMinutes(59)));
            assertTrue(savedToken.getExpiresAt().isBefore(expectedExpiry.plusMinutes(1)));
        }

        @Test
        @DisplayName("Should handle email sending failure gracefully")
        void forgotPassword_WhenEmailFails_ShouldStillReturnSuccess() {
            // Arrange
            when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(activeUser));
            when(passwordResetTokenRepository.countByUserAndUsedAtIsNull(activeUser)).thenReturn(0L);
            when(passwordResetTokenRepository.save(any(PasswordResetTokens.class)))
                    .thenAnswer(i -> i.getArgument(0));

            doThrow(new RuntimeException("Email service down"))
                    .when(emailServiceImpl).sendResetPasswordEmail(anyString(), anyString());

            doReturn(successResponse)
                    .when(responseHandler).handleResponse(
                            eq("Link reset password dikirim ke email"),
                            eq(HttpStatus.OK),
                            isNull(),
                            isNull(),
                            eq(httpRequest)
                    );

            // Act
            ResponseEntity<Object> response = passwordService.forgotPassword(
                    forgotPasswordRequest, httpRequest, responseHandler);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(passwordResetTokenRepository).save(any(PasswordResetTokens.class));
        }
    }

    @Nested
    @DisplayName("Reset Password Tests")
    class ResetPasswordTests {

        @Test
        @DisplayName("Should reset password with valid token")
        void resetPassword_WithValidToken_ShouldUpdatePassword() {
            // Arrange
            when(passwordResetTokenRepository.findByTokenAndUsedAtIsNull(TEST_TOKEN))
                    .thenReturn(Optional.of(validResetToken));
            when(passwordHasher.hash("NewPassword123")).thenReturn("newHashedPassword");

            doReturn(successResponse)
                    .when(responseHandler).handleResponse(
                            eq("Password berhasil direset"),
                            eq(HttpStatus.OK),
                            isNull(),
                            isNull(),
                            eq(httpRequest)
                    );

            // Act
            ResponseEntity<Object> response = passwordService.resetPassword(
                    resetPasswordRequest, httpRequest, responseHandler);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(passwordResetTokenRepository).save(validResetToken);
            assertNotNull(validResetToken.getUsedAt());
            verify(sessionService).invalidateAllUserTokens(activeUser.getId());
        }

        @Test
        @DisplayName("Should return error when token is invalid")
        void resetPassword_WithInvalidToken_ShouldReturnError() {
            // Arrange
            when(passwordResetTokenRepository.findByTokenAndUsedAtIsNull("invalid-token"))
                    .thenReturn(Optional.empty());

            doReturn(errorResponse)
                    .when(responseHandler).handleResponse(
                            eq("Token reset tidak valid"),
                            eq(HttpStatus.BAD_REQUEST),
                            eq("AUTH_011"),
                            isNull(),
                            eq(httpRequest)
                    );

            // Act
            resetPasswordRequest.setToken("invalid-token");
            ResponseEntity<Object> response = passwordService.resetPassword(
                    resetPasswordRequest, httpRequest, responseHandler);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should return error when token is expired")
        void resetPassword_WithExpiredToken_ShouldReturnError() {
            // Arrange
            when(passwordResetTokenRepository.findByTokenAndUsedAtIsNull(TEST_TOKEN))
                    .thenReturn(Optional.of(expiredResetToken));

            doReturn(errorResponse)
                    .when(responseHandler).handleResponse(
                            eq("Token reset telah kadaluarsa"),
                            eq(HttpStatus.BAD_REQUEST),
                            eq("AUTH_012"),
                            isNull(),
                            eq(httpRequest)
                    );

            // Act
            ResponseEntity<Object> response = passwordService.resetPassword(
                    resetPasswordRequest, httpRequest, responseHandler);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            verify(passwordResetTokenRepository, never()).save(any());
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should return error when new password is too short")
        void resetPassword_WithShortPassword_ShouldReturnError() {
            // Arrange
            when(httpRequest.getRemoteAddr()).thenReturn("127.0.0.1");
            resetPasswordRequest.setNewPassword("short");

            when(passwordResetTokenRepository.findByTokenAndUsedAtIsNull(anyString()))
                    .thenReturn(Optional.of(validResetToken));

            doReturn(errorResponse)
                    .when(responseHandler).handleResponse(
                            anyString(),
                            any(HttpStatus.class),
                            anyString(),
                            isNull(),
                            eq(httpRequest)
                    );

            // Act
            ResponseEntity<Object> response = passwordService.resetPassword(
                    resetPasswordRequest, httpRequest, responseHandler);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should reset failed attempts and unlock account")
        void resetPassword_ShouldResetFailedAttemptsAndUnlock() {
            // Arrange
            when(httpRequest.getRemoteAddr()).thenReturn("127.0.0.1");

            activeUser.setFailedLoginAttempt(3);
            activeUser.setStatus(AuthenticationConstant.LOCKED);
            activeUser.setLockedUntil(LocalDateTime.now().plusMinutes(30));

            validResetToken.setUser(activeUser);

            when(passwordResetTokenRepository.findByTokenAndUsedAtIsNull(TEST_TOKEN))
                    .thenReturn(Optional.of(validResetToken));
            when(passwordHasher.hash(anyString())).thenReturn("newHashedPassword");

            doReturn(successResponse)
                    .when(responseHandler).handleResponse(
                            eq("Password berhasil direset"),
                            eq(HttpStatus.OK),
                            isNull(),
                            isNull(),
                            eq(httpRequest)
                    );

            // Act
            ResponseEntity<Object> response = passwordService.resetPassword(
                    resetPasswordRequest, httpRequest, responseHandler);

            // Assert
            assertNotNull(response);

            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository, atLeastOnce()).save(userCaptor.capture());

            User savedUser = userCaptor.getValue();

            // Verifikasi state user
            assertEquals(0, savedUser.getFailedLoginAttempt(), "Failed attempts should be reset");
            assertEquals(AuthenticationConstant.LOCKED, savedUser.getStatus(), "Status should remain LOCKED (auto-unlock later)");
            assertNull(savedUser.getLockedUntil(), "Locked until should be cleared");
            assertEquals("newHashedPassword", savedUser.getPasswordHash(), "Password should be updated");

            // Verifikasi token sudah digunakan
            verify(passwordResetTokenRepository).save(validResetToken);
            assertNotNull(validResetToken.getUsedAt());

            // Verifikasi session di-invalidate
            verify(sessionService).invalidateAllUserTokens(activeUser.getId());
        }
    }

    @Nested
    @DisplayName("Validate Reset Token Tests")
    class ValidateResetTokenTests {

        @Test
        @DisplayName("Should validate valid token")
        void validateResetToken_WithValidToken_ShouldReturnUserInfo() {
            // Arrange
            when(passwordResetTokenRepository.findByTokenAndUsedAtIsNull(TEST_TOKEN))
                    .thenReturn(Optional.of(validResetToken));

            responseData.put("email", activeUser.getEmail());
            responseData.put("userId", activeUser.getId());
            responseData.put("expiresAt", validResetToken.getExpiresAt());

            doReturn(successResponse)
                    .when(responseHandler).handleResponse(
                            eq("Token valid"),
                            eq(HttpStatus.OK),
                            isNull(),
                            eq(responseData),
                            eq(httpRequest)
                    );

            // Act
            ResponseEntity<Object> response = passwordService.validateResetToken(
                    TEST_TOKEN, httpRequest, responseHandler);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }

        @Test
        @DisplayName("Should return error for invalid token")
        void validateResetToken_WithInvalidToken_ShouldReturnError() {
            // Arrange
            when(passwordResetTokenRepository.findByTokenAndUsedAtIsNull("invalid-token"))
                    .thenReturn(Optional.empty());

            doReturn(errorResponse)
                    .when(responseHandler).handleResponse(
                            eq("Token reset tidak valid"),
                            eq(HttpStatus.BAD_REQUEST),
                            eq("AUTH_011"),
                            isNull(),
                            eq(httpRequest)
                    );

            // Act
            ResponseEntity<Object> response = passwordService.validateResetToken(
                    "invalid-token", httpRequest, responseHandler);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }

        @Test
        @DisplayName("Should return error for expired token")
        void validateResetToken_WithExpiredToken_ShouldReturnError() {
            // Arrange
            when(passwordResetTokenRepository.findByTokenAndUsedAtIsNull(TEST_TOKEN))
                    .thenReturn(Optional.of(expiredResetToken));

            doReturn(errorResponse)
                    .when(responseHandler).handleResponse(
                            eq("Token reset telah kadaluarsa"),
                            eq(HttpStatus.BAD_REQUEST),
                            eq("AUTH_012"),
                            isNull(),
                            eq(httpRequest)
                    );

            // Act
            ResponseEntity<Object> response = passwordService.validateResetToken(
                    TEST_TOKEN, httpRequest, responseHandler);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
    }
}