package com.backendsyndicate.smashclub.auth.service;

import com.backendsyndicate.smashclub.auth.AuthTestBase;
import com.backendsyndicate.smashclub.auth.dto.request.RegisterRequest;
import com.backendsyndicate.smashclub.auth.model.EmailVerificationTokens;
import com.backendsyndicate.smashclub.auth.model.User;
import com.backendsyndicate.smashclub.auth.repository.EmailVerificationTokenRepository;
import com.backendsyndicate.smashclub.auth.repository.UserRepository;
import com.backendsyndicate.smashclub.common.constant.AuthenticationConstant;
import com.backendsyndicate.smashclub.common.handler.ResponseHandler;
import com.backendsyndicate.smashclub.common.security.PasswordHasher;
import com.backendsyndicate.smashclub.common.util.ValidationError;
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
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest extends AuthTestBase {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailVerificationTokenRepository emailVerificationTokenRepository;

    @Mock
    private PasswordHasher passwordHasher;

    @Mock
    private EmailServiceImpl emailServiceImpl;

    @Mock
    private ValidationService validationService;

    @Mock
    private ResponseHandler responseHandler;

    @Mock
    private HttpServletRequest httpRequest;

    @InjectMocks
    private RegistrationService registrationService;

    private RegisterRequest registerRequest;
    private User testUser;
    private User activeUser;
    private EmailVerificationTokens verificationToken;
    private EmailVerificationTokens expiredToken;
    private Map<String, Object> responseData;
    private ResponseEntity<Object> successResponse;
    private ResponseEntity<Object> errorResponse;
    private ResponseEntity<Object> notFoundResponse;
    private ResponseEntity<Object> serverErrorResponse;
    private ResponseEntity<Object> createdResponse;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setFullName(TEST_FULL_NAME);
        registerRequest.setEmail(TEST_EMAIL);
        registerRequest.setPassword(TEST_PASSWORD);

        testUser = createPendingUser();
        activeUser = createActiveUser();

        verificationToken = createEmailVerificationToken(testUser);
        verificationToken.setExpiresAt(LocalDateTime.now().plusHours(24));

        expiredToken = createEmailVerificationToken(testUser);
        expiredToken.setExpiresAt(LocalDateTime.now().minusHours(1));

        responseData = new HashMap<>();
        successResponse = ResponseEntity.ok(responseData);
        errorResponse = ResponseEntity.badRequest().body(responseData);
        notFoundResponse = ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseData);
        serverErrorResponse = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseData);
        createdResponse = ResponseEntity.status(HttpStatus.CREATED).body(responseData);
    }

    @Nested
    @DisplayName("Register Tests")
    class RegisterTests {

        @Test
        @DisplayName("Should successfully register new user")
        void register_WithValidData_ShouldCreateUserAndSendEmail() {
            // Arrange
            when(validationService.validateRegistrationData(any(RegisterRequest.class)))
                    .thenReturn(Collections.emptyList());
            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(passwordHasher.hash(anyString())).thenReturn(hashedPassword);
            when(userRepository.saveAndFlush(any(User.class))).thenReturn(testUser);
            when(emailVerificationTokenRepository.saveAndFlush(any(EmailVerificationTokens.class)))
                    .thenReturn(verificationToken);

            responseData.put("userId", testUser.getId());
            responseData.put("email", testUser.getEmail());

            doReturn(createdResponse)
                    .when(responseHandler).handleResponse(
                            eq("Registrasi berhasil, cek email untuk verifikasi"),
                            eq(HttpStatus.CREATED),
                            isNull(),
                            eq(responseData),
                            eq(httpRequest)
                    );

            // Act
            ResponseEntity<Object> response = registrationService.register(registerRequest, httpRequest);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.CREATED, response.getStatusCode());

            verify(userRepository).saveAndFlush(any(User.class));
            verify(emailVerificationTokenRepository).saveAndFlush(any(EmailVerificationTokens.class));
            verify(emailServiceImpl).sendActivationEmail(eq(TEST_EMAIL), anyString());
        }

        @Test
        @DisplayName("Should return validation errors when data invalid")
        void register_WithInvalidData_ShouldReturnValidationErrors() {
            // Arrange
            List<ValidationError> errors = List.of(
                    ValidationError.builder().field("email").message("Invalid email").build()
            );
            when(validationService.validateRegistrationData(any(RegisterRequest.class)))
                    .thenReturn(errors);

            Map<String, Object> errorData = new HashMap<>();
            errorData.put("errors", errors);

            doReturn(errorResponse)
                    .when(responseHandler).handleResponse(
                            eq("Data registrasi tidak valid"),
                            eq(HttpStatus.BAD_REQUEST),
                            eq("AUTH_001"),
                            eq(errorData),
                            eq(httpRequest)
                    );

            // Act
            ResponseEntity<Object> response = registrationService.register(registerRequest, httpRequest);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            verify(userRepository, never()).save(any());
            verify(emailServiceImpl, never()).sendActivationEmail(anyString(), anyString());
        }

        @Test
        @DisplayName("Should return error when email already exists")
        void register_WithExistingEmail_ShouldReturnError() {
            // Arrange
            when(validationService.validateRegistrationData(any(RegisterRequest.class)))
                    .thenReturn(Collections.emptyList());
            when(userRepository.existsByEmail(anyString())).thenReturn(true);

            Map<String, Object> errorData = new HashMap<>();
            errorData.put("field", "email");
            errorData.put("reason", "already_exists");

            doReturn(errorResponse)
                    .when(responseHandler).handleResponse(
                            eq("Email sudah terdaftar"),
                            eq(HttpStatus.BAD_REQUEST),
                            eq("AUTH_002"),
                            eq(errorData),
                            eq(httpRequest)
                    );

            // Act
            ResponseEntity<Object> response = registrationService.register(registerRequest, httpRequest);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should hash password before saving")
        void register_ShouldHashPassword() {
            // Arrange
            when(validationService.validateRegistrationData(any(RegisterRequest.class)))
                    .thenReturn(Collections.emptyList());
            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(passwordHasher.hash(TEST_PASSWORD)).thenReturn("hashedPassword123");

            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

            when(userRepository.saveAndFlush(userCaptor.capture())).thenReturn(testUser);
            when(emailVerificationTokenRepository.saveAndFlush(any())).thenReturn(verificationToken);

            doReturn(createdResponse)
                    .when(responseHandler).handleResponse(anyString(), any(), any(), any(), eq(httpRequest));

            // Act
            registrationService.register(registerRequest, httpRequest);

            // Assert
            User savedUser = userCaptor.getValue();
            assertEquals("hashedPassword123", savedUser.getPasswordHash());
            verify(passwordHasher).hash(TEST_PASSWORD);
        }

        @Test
        @DisplayName("Should set user status to PENDING")
        void register_ShouldSetUserStatusToPending() {
            // Arrange
            when(validationService.validateRegistrationData(any(RegisterRequest.class)))
                    .thenReturn(Collections.emptyList());
            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(passwordHasher.hash(anyString())).thenReturn(hashedPassword);

            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

            when(userRepository.saveAndFlush(userCaptor.capture())).thenReturn(testUser);
            when(emailVerificationTokenRepository.saveAndFlush(any())).thenReturn(verificationToken);

            doReturn(createdResponse)
                    .when(responseHandler).handleResponse(anyString(), any(), any(), any(), eq(httpRequest));

            // Act
            registrationService.register(registerRequest, httpRequest);

            // Assert
            User savedUser = userCaptor.getValue();
            assertEquals(AuthenticationConstant.PENDING, savedUser.getStatus());
        }

        @Test
        @DisplayName("Should create verification token with 24 hours expiry")
        void register_ShouldCreateTokenWith24HoursExpiry() {
            // Arrange
            when(validationService.validateRegistrationData(any(RegisterRequest.class)))
                    .thenReturn(Collections.emptyList());
            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(passwordHasher.hash(anyString())).thenReturn(hashedPassword);
            when(userRepository.saveAndFlush(any(User.class))).thenReturn(testUser);

            ArgumentCaptor<EmailVerificationTokens> tokenCaptor =
                    ArgumentCaptor.forClass(EmailVerificationTokens.class);

            when(emailVerificationTokenRepository.saveAndFlush(tokenCaptor.capture()))
                    .thenReturn(verificationToken);

            doReturn(createdResponse)
                    .when(responseHandler).handleResponse(anyString(), any(), any(), any(), eq(httpRequest));

            // Act
            registrationService.register(registerRequest, httpRequest);

            // Assert
            EmailVerificationTokens savedToken = tokenCaptor.getValue();
            assertNotNull(savedToken.getExpiresAt());

            LocalDateTime expectedExpiry = LocalDateTime.now().plusHours(24);
            assertTrue(savedToken.getExpiresAt().isAfter(LocalDateTime.now().plusHours(23)));
            assertTrue(savedToken.getExpiresAt().isBefore(expectedExpiry.plusMinutes(1)));
        }

        @Test
        @DisplayName("Should continue even if email sending fails")
        void register_WhenEmailFails_ShouldStillReturnSuccess() {
            // Arrange
            when(validationService.validateRegistrationData(any(RegisterRequest.class)))
                    .thenReturn(Collections.emptyList());
            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(passwordHasher.hash(anyString())).thenReturn(hashedPassword);
            when(userRepository.saveAndFlush(any(User.class))).thenReturn(testUser);
            when(emailVerificationTokenRepository.saveAndFlush(any(EmailVerificationTokens.class)))
                    .thenReturn(verificationToken);

            doThrow(new RuntimeException("Email service down"))
                    .when(emailServiceImpl).sendActivationEmail(anyString(), anyString());

            responseData.put("userId", testUser.getId());
            responseData.put("email", testUser.getEmail());

            doReturn(createdResponse)
                    .when(responseHandler).handleResponse(
                            eq("Registrasi berhasil, cek email untuk verifikasi"),
                            eq(HttpStatus.CREATED),
                            isNull(),
                            eq(responseData),
                            eq(httpRequest)
                    );

            // Act
            ResponseEntity<Object> response = registrationService.register(registerRequest, httpRequest);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            verify(userRepository).saveAndFlush(any(User.class));
        }
    }

    @Nested
    @DisplayName("Verify Email Tests")
    class VerifyEmailTests {

        @Test
        @DisplayName("Should successfully verify email with valid token")
        void verifyEmail_WithValidToken_ShouldActivateUser() {
            // Arrange
            when(emailVerificationTokenRepository.findByTokenAndUsedAtIsNull(TEST_TOKEN))
                    .thenReturn(Optional.of(verificationToken));

            responseData.put("userId", testUser.getId());
            responseData.put("email", testUser.getEmail());

            doReturn(successResponse)
                    .when(responseHandler).handleResponse(
                            eq("Email berhasil diverifikasi"),
                            eq(HttpStatus.OK),
                            isNull(),
                            any(),
                            eq(httpRequest)
                    );

            // Act
            ResponseEntity<Object> response = registrationService.verifyEmail(TEST_TOKEN, httpRequest);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(emailVerificationTokenRepository).save(verificationToken);
            verify(userRepository).save(testUser);
            assertEquals(AuthenticationConstant.ACTIVE, testUser.getStatus());
            assertNotNull(verificationToken.getUsedAt());
        }

        @Test
        @DisplayName("Should return error when token is invalid")
        void verifyEmail_WithInvalidToken_ShouldReturnError() {
            // Arrange
            when(emailVerificationTokenRepository.findByTokenAndUsedAtIsNull("invalid-token"))
                    .thenReturn(Optional.empty());

            Map<String, Object> errorData = new HashMap<>();
            errorData.put("token", "invalid-token");
            errorData.put("reason", "invalid_or_used");

            doReturn(errorResponse)
                    .when(responseHandler).handleResponse(
                            eq("Token verifikasi tidak valid"),
                            eq(HttpStatus.BAD_REQUEST),
                            eq("AUTH_009"),
                            eq(errorData),
                            eq(httpRequest)
                    );

            // Act
            ResponseEntity<Object> response = registrationService.verifyEmail("invalid-token", httpRequest);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            verify(emailVerificationTokenRepository, never()).save(any());
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should return error when token is expired")
        void verifyEmail_WithExpiredToken_ShouldReturnError() {
            // Arrange
            when(emailVerificationTokenRepository.findByTokenAndUsedAtIsNull(TEST_TOKEN))
                    .thenReturn(Optional.of(expiredToken));

            Map<String, Object> errorData = new HashMap<>();
            errorData.put("token", TEST_TOKEN);
            errorData.put("expiredAt", expiredToken.getExpiresAt().toString());

            doReturn(errorResponse)
                    .when(responseHandler).handleResponse(
                            eq("Token verifikasi telah kadaluarsa"),
                            eq(HttpStatus.BAD_REQUEST),
                            eq("AUTH_010"),
                            eq(errorData),
                            eq(httpRequest)
                    );

            // Act
            ResponseEntity<Object> response = registrationService.verifyEmail(TEST_TOKEN, httpRequest);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            verify(emailVerificationTokenRepository, never()).save(any());
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should mark token as used after verification")
        void verifyEmail_ShouldMarkTokenAsUsed() {
            // Arrange
            when(emailVerificationTokenRepository.findByTokenAndUsedAtIsNull(TEST_TOKEN))
                    .thenReturn(Optional.of(verificationToken));

            ArgumentCaptor<EmailVerificationTokens> tokenCaptor =
                    ArgumentCaptor.forClass(EmailVerificationTokens.class);

            doReturn(successResponse)
                    .when(responseHandler).handleResponse(anyString(), any(), any(), any(), eq(httpRequest));

            // Act
            registrationService.verifyEmail(TEST_TOKEN, httpRequest);

            // Assert
            verify(emailVerificationTokenRepository).save(tokenCaptor.capture());
            EmailVerificationTokens savedToken = tokenCaptor.getValue();
            assertNotNull(savedToken.getUsedAt());
        }
    }

    @Nested
    @DisplayName("Resend Verification Email Tests")
    class ResendVerificationEmailTests {

        @Test
        @DisplayName("Should resend verification email to pending user")
        void resendVerificationEmail_WithValidPendingUser_ShouldSendNewEmail() {
            // Arrange
            when(httpRequest.getRemoteAddr()).thenReturn("127.0.0.1");

            when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));
            when(emailVerificationTokenRepository.findTopByUserOrderByCreatedAtDesc(testUser))
                    .thenReturn(Optional.empty());

            responseData.put("email", TEST_EMAIL);
            responseData.put("nextResendAvailableIn", 60);

            doReturn(successResponse)
                    .when(responseHandler).handleResponse(
                            eq("Email verifikasi telah dikirim ulang"),
                            eq(HttpStatus.OK),
                            isNull(),
                            eq(responseData),
                            eq(httpRequest)
                    );

            // Act
            ResponseEntity<Object> response = registrationService.resendVerificationEmail(
                    TEST_EMAIL, httpRequest);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(emailVerificationTokenRepository).invalidateUserTokens(
                    eq(testUser.getId()), any(LocalDateTime.class));
            verify(emailVerificationTokenRepository).save(any(EmailVerificationTokens.class));
            verify(emailServiceImpl).sendActivationEmail(eq(TEST_EMAIL), anyString());
        }

        @Test
        @DisplayName("Should return error when email not found")
        void resendVerificationEmail_WithNonExistentEmail_ShouldReturnError() {
            // Arrange
            when(httpRequest.getRemoteAddr()).thenReturn("127.0.0.1");

            when(userRepository.findByEmail("nonexistent@example.com"))
                    .thenReturn(Optional.empty());

            Map<String, Object> errorData = new HashMap<>();
            errorData.put("email", "nonexistent@example.com");

            doReturn(notFoundResponse)
                    .when(responseHandler).handleResponse(
                            eq("Email tidak ditemukan"),
                            eq(HttpStatus.NOT_FOUND),
                            eq("AUTH_013"),
                            eq(errorData),
                            eq(httpRequest)
                    );

            // Act
            ResponseEntity<Object> response = registrationService.resendVerificationEmail(
                    "nonexistent@example.com", httpRequest);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            verify(emailServiceImpl, never()).sendActivationEmail(anyString(), anyString());
        }

        @Test
        @DisplayName("Should return error when user is already active")
        void resendVerificationEmail_WithActiveUser_ShouldReturnError() {
            // Arrange
            when(httpRequest.getRemoteAddr()).thenReturn("127.0.0.1");

            when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(activeUser));

            Map<String, Object> errorData = new HashMap<>();
            errorData.put("email", TEST_EMAIL);
            errorData.put("status", AuthenticationConstant.ACTIVE);

            doReturn(errorResponse)
                    .when(responseHandler).handleResponse(
                            eq("Akun sudah aktif atau tidak memerlukan verifikasi"),
                            eq(HttpStatus.BAD_REQUEST),
                            eq("AUTH_014"),
                            eq(errorData),
                            eq(httpRequest)
                    );

            // Act
            ResponseEntity<Object> response = registrationService.resendVerificationEmail(
                    TEST_EMAIL, httpRequest);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            verify(emailServiceImpl, never()).sendActivationEmail(anyString(), anyString());
        }

        @Test
        @DisplayName("Should enforce rate limiting - prevent resend within 1 minute")
        void resendVerificationEmail_WithRecentToken_ShouldReturnRateLimitError() {
            // Arrange
            when(httpRequest.getRemoteAddr()).thenReturn("127.0.0.1");

            EmailVerificationTokens recentToken = createEmailVerificationToken(testUser);
            recentToken.setCreatedAt(LocalDateTime.now().minusSeconds(30));

            when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));
            when(emailVerificationTokenRepository.findTopByUserOrderByCreatedAtDesc(testUser))
                    .thenReturn(Optional.of(recentToken));

            Map<String, Object> errorData = new HashMap<>();
            errorData.put("email", TEST_EMAIL);
            errorData.put("lastSent", recentToken.getCreatedAt().toString());
            errorData.put("waitSeconds", 30L);

            doReturn(errorResponse)
                    .when(responseHandler).handleResponse(
                            eq("Silakan tunggu 1 menit sebelum meminta email verifikasi lagi"),
                            eq(HttpStatus.BAD_REQUEST),
                            eq("AUTH_020"),
                            eq(errorData),
                            eq(httpRequest)
                    );

            // Act
            ResponseEntity<Object> response = registrationService.resendVerificationEmail(
                    TEST_EMAIL, httpRequest);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            verify(emailVerificationTokenRepository, never()).invalidateUserTokens(any(), any());
            verify(emailServiceImpl, never()).sendActivationEmail(anyString(), anyString());
        }

        @Test
        @DisplayName("Should allow resend after 1 minute")
        void resendVerificationEmail_AfterOneMinute_ShouldAllowResend() {
            // Arrange
            when(httpRequest.getRemoteAddr()).thenReturn("127.0.0.1");

            EmailVerificationTokens oldToken = createEmailVerificationToken(testUser);
            oldToken.setCreatedAt(LocalDateTime.now().minusMinutes(2));

            when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));
            when(emailVerificationTokenRepository.findTopByUserOrderByCreatedAtDesc(testUser))
                    .thenReturn(Optional.of(oldToken));

            responseData.put("email", TEST_EMAIL);
            responseData.put("nextResendAvailableIn", 60);

            doReturn(successResponse)
                    .when(responseHandler).handleResponse(
                            eq("Email verifikasi telah dikirim ulang"),
                            eq(HttpStatus.OK),
                            isNull(),
                            eq(responseData),
                            eq(httpRequest)
                    );

            // Act
            ResponseEntity<Object> response = registrationService.resendVerificationEmail(
                    TEST_EMAIL, httpRequest);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(emailVerificationTokenRepository).invalidateUserTokens(any(), any());
            verify(emailVerificationTokenRepository).save(any(EmailVerificationTokens.class));
        }

        @Test
        @DisplayName("Should handle email sending failure gracefully")
        void resendVerificationEmail_WhenEmailFails_ShouldReturnError() {
            // Arrange
            when(httpRequest.getRemoteAddr()).thenReturn("127.0.0.1");

            when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));
            when(emailVerificationTokenRepository.findTopByUserOrderByCreatedAtDesc(testUser))
                    .thenReturn(Optional.empty());

            doThrow(new RuntimeException("Email service down"))
                    .when(emailServiceImpl).sendActivationEmail(anyString(), anyString());

            Map<String, Object> errorData = new HashMap<>();
            errorData.put("email", TEST_EMAIL);

            doReturn(serverErrorResponse)
                    .when(responseHandler).handleResponse(
                            eq("Gagal mengirim email verifikasi. Silakan coba lagi nanti."),
                            eq(HttpStatus.INTERNAL_SERVER_ERROR),
                            eq("AUTH_021"),
                            eq(errorData),
                            eq(httpRequest)
                    );

            // Act
            ResponseEntity<Object> response = registrationService.resendVerificationEmail(
                    TEST_EMAIL, httpRequest);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        }
    }
}