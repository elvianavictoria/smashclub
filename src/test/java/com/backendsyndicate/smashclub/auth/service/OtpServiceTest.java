package com.backendsyndicate.smashclub.auth.service;

import com.backendsyndicate.smashclub.auth.AuthTestBase;
import com.backendsyndicate.smashclub.auth.dto.request.OtpVerificationRequest;
import com.backendsyndicate.smashclub.auth.dto.request.ResendOtpRequest;
import com.backendsyndicate.smashclub.auth.model.LoginOtpTokens;
import com.backendsyndicate.smashclub.auth.model.User;
import com.backendsyndicate.smashclub.auth.repository.LoginOtpTokenRepository;
import com.backendsyndicate.smashclub.auth.repository.UserRepository;
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
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OtpServiceTest extends AuthTestBase {

    @Mock
    private LoginOtpTokenRepository loginOtpTokenRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailServiceImpl emailServiceImpl;

    @Mock
    private TokenService tokenService;

    @Mock
    private JwtService jwtService;

    @Mock
    private ResponseHandler responseHandler;

    @Mock
    private HttpServletRequest httpRequest;

    @InjectMocks
    private OtpService otpService;

    private User activeUser;
    private User pendingUser;
    private LoginOtpTokens validOtpToken;
    private LoginOtpTokens expiredOtpToken;
    private OtpVerificationRequest otpVerificationRequest;
    private ResendOtpRequest resendOtpRequest;
    private Map<String, Object> responseData;
    private ResponseEntity<Object> successResponse;
    private ResponseEntity<Object> errorResponse;
    private ResponseEntity<Object> notFoundResponse;

    @BeforeEach
    void setUp() {
        activeUser = createActiveUser();
        pendingUser = createPendingUser();

        validOtpToken = createLoginOtpToken(activeUser);
        validOtpToken.setExpiresAt(LocalDateTime.now().plusMinutes(5));

        expiredOtpToken = createLoginOtpToken(activeUser);
        expiredOtpToken.setExpiresAt(LocalDateTime.now().minusMinutes(5));

        otpVerificationRequest = new OtpVerificationRequest();
        otpVerificationRequest.setUserId(TEST_USER_ID);
        otpVerificationRequest.setOtp(TEST_OTP);

        resendOtpRequest = new ResendOtpRequest();
        resendOtpRequest.setUserId(TEST_USER_ID);

        responseData = new HashMap<>();
        successResponse = ResponseEntity.ok(responseData);
        errorResponse = ResponseEntity.badRequest().body(responseData);
        notFoundResponse = ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseData);
    }

    @Nested
    @DisplayName("Generate and Send OTP Tests")
    class GenerateAndSendOtpTests {

        @Test
        @DisplayName("Should generate and send OTP for valid user")
        void generateAndSendOtp_ShouldCreateOTPAndSendEmail() {
            // Arrange
            responseData.put("userId", activeUser.getId());
            responseData.put("email", activeUser.getEmail());
            responseData.put("requiresOtp", true);
            responseData.put("otpExpiresIn", AuthenticationConstant.OTP_EXPIRY_MINUTES);

            doReturn(successResponse)
                    .when(responseHandler).handleResponse(
                            eq("OTP telah dikirim ke email Anda"),
                            eq(HttpStatus.OK),
                            isNull(),
                            eq(responseData),
                            eq(httpRequest)
                    );

            // Act
            ResponseEntity<Object> response = otpService.generateAndSendOtp(
                    activeUser, "127.0.0.1", responseHandler, httpRequest);

            // Assert
            assertNotNull(response);
            verify(loginOtpTokenRepository).invalidateUserTokens(eq(activeUser.getId()), any(LocalDateTime.class));
            verify(loginOtpTokenRepository).save(any(LoginOtpTokens.class));
            verify(emailServiceImpl).sendOtpEmail(eq(activeUser.getEmail()), anyString());
        }

        @Test
        @DisplayName("Should generate 6-digit OTP")
        void generateAndSendOtp_ShouldGenerate6DigitOTP() {
            // Arrange
            ArgumentCaptor<LoginOtpTokens> tokenCaptor = ArgumentCaptor.forClass(LoginOtpTokens.class);

            doReturn(successResponse)
                    .when(responseHandler).handleResponse(anyString(), any(), any(), any(), eq(httpRequest));

            // Act
            otpService.generateAndSendOtp(activeUser, "127.0.0.1", responseHandler, httpRequest);

            // Assert
            verify(loginOtpTokenRepository).save(tokenCaptor.capture());
            LoginOtpTokens savedToken = tokenCaptor.getValue();

            assertNotNull(savedToken.getOtpCode());
            assertEquals(6, savedToken.getOtpCode().length());
            assertTrue(savedToken.getOtpCode().matches("\\d{6}"));
        }

        @Test
        @DisplayName("Should set OTP expiry to configured minutes")
        void generateAndSendOtp_ShouldSetCorrectExpiry() {
            // Arrange
            ArgumentCaptor<LoginOtpTokens> tokenCaptor = ArgumentCaptor.forClass(LoginOtpTokens.class);

            doReturn(successResponse)
                    .when(responseHandler).handleResponse(anyString(), any(), any(), any(), eq(httpRequest));

            // Act
            otpService.generateAndSendOtp(activeUser, "127.0.0.1", responseHandler, httpRequest);

            // Assert
            verify(loginOtpTokenRepository).save(tokenCaptor.capture());
            LoginOtpTokens savedToken = tokenCaptor.getValue();

            LocalDateTime expectedExpiry = LocalDateTime.now().plusMinutes(AuthenticationConstant.OTP_EXPIRY_MINUTES);
            assertTrue(savedToken.getExpiresAt().isAfter(LocalDateTime.now().plusMinutes(9)));
            assertTrue(savedToken.getExpiresAt().isBefore(expectedExpiry.plusMinutes(1)));
        }

        @Test
        @DisplayName("Should invalidate existing OTPs before creating new one")
        void generateAndSendOtp_ShouldInvalidateOldOTPs() {
            // Arrange
            doReturn(successResponse)
                    .when(responseHandler).handleResponse(anyString(), any(), any(), any(), eq(httpRequest));

            // Act
            otpService.generateAndSendOtp(activeUser, "127.0.0.1", responseHandler, httpRequest);

            // Assert
            verify(loginOtpTokenRepository).invalidateUserTokens(eq(activeUser.getId()), any(LocalDateTime.class));
        }

        @Test
        @DisplayName("Should continue even if email sending fails")
        void generateAndSendOtp_WhenEmailFails_ShouldStillReturnSuccess() {
            // Arrange
            doThrow(new RuntimeException("Email service down"))
                    .when(emailServiceImpl).sendOtpEmail(anyString(), anyString());

            responseData.put("userId", activeUser.getId());
            responseData.put("email", activeUser.getEmail());
            responseData.put("requiresOtp", true);
            responseData.put("otpExpiresIn", AuthenticationConstant.OTP_EXPIRY_MINUTES);

            doReturn(successResponse)
                    .when(responseHandler).handleResponse(
                            eq("OTP telah dikirim ke email Anda"),
                            eq(HttpStatus.OK),
                            isNull(),
                            eq(responseData),
                            eq(httpRequest)
                    );

            // Act
            ResponseEntity<Object> response = otpService.generateAndSendOtp(
                    activeUser, "127.0.0.1", responseHandler, httpRequest);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(loginOtpTokenRepository).save(any(LoginOtpTokens.class));
        }
    }

    @Nested
    @DisplayName("Verify OTP Tests")
    class VerifyOtpTests {

        @Test
        @DisplayName("Should verify valid OTP and generate tokens")
        void verifyOtp_WithValidOtp_ShouldGenerateTokens() {
            // Arrange
            when(loginOtpTokenRepository.findByUserIdAndOtpCodeAndUsedAtIsNull(
                    TEST_USER_ID, TEST_OTP)).thenReturn(Optional.of(validOtpToken));
            when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(activeUser));

            doReturn(successResponse)
                    .when(tokenService).generateJwtTokensAfterOtpVerification(
                            eq(activeUser), eq(responseHandler), eq(httpRequest));

            // Act
            ResponseEntity<Object> response = otpService.verifyOtp(
                    otpVerificationRequest, httpRequest, responseHandler);

            // Assert
            assertNotNull(response);
            verify(loginOtpTokenRepository).save(validOtpToken);
            assertNotNull(validOtpToken.getUsedAt());
            verify(tokenService).generateJwtTokensAfterOtpVerification(
                    eq(activeUser), eq(responseHandler), eq(httpRequest));
        }

        @Test
        @DisplayName("Should return error when OTP is invalid")
        void verifyOtp_WithInvalidOtp_ShouldReturnError() {
            // Arrange
            when(loginOtpTokenRepository.findByUserIdAndOtpCodeAndUsedAtIsNull(
                    TEST_USER_ID, "000000")).thenReturn(Optional.empty());
            when(loginOtpTokenRepository.findByUserIdAndOtpCodeAndUsedAtIsNotNull(
                    TEST_USER_ID, "000000")).thenReturn(Optional.empty());

            doReturn(errorResponse)
                    .when(responseHandler).handleResponse(
                            eq("OTP tidak valid"),
                            eq(HttpStatus.BAD_REQUEST),
                            eq("AUTH_007"),
                            isNull(),
                            eq(httpRequest)
                    );

            // Act
            otpVerificationRequest.setOtp("000000");
            ResponseEntity<Object> response = otpService.verifyOtp(
                    otpVerificationRequest, httpRequest, responseHandler);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            verify(tokenService, never()).generateJwtTokensAfterOtpVerification(any(), any(), any());
        }

        @Test
        @DisplayName("Should return error when OTP is already used")
        void verifyOtp_WithUsedOtp_ShouldReturnError() {
            // Arrange
            when(loginOtpTokenRepository.findByUserIdAndOtpCodeAndUsedAtIsNull(
                    TEST_USER_ID, TEST_OTP)).thenReturn(Optional.empty());
            when(loginOtpTokenRepository.findByUserIdAndOtpCodeAndUsedAtIsNotNull(
                    TEST_USER_ID, TEST_OTP)).thenReturn(Optional.of(validOtpToken));

            doReturn(errorResponse)
                    .when(responseHandler).handleResponse(
                            eq("OTP sudah digunakan"),
                            eq(HttpStatus.BAD_REQUEST),
                            eq("AUTH_017"),
                            isNull(),
                            eq(httpRequest)
                    );

            // Act
            ResponseEntity<Object> response = otpService.verifyOtp(
                    otpVerificationRequest, httpRequest, responseHandler);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }

        @Test
        @DisplayName("Should return error when OTP is expired")
        void verifyOtp_WithExpiredOtp_ShouldReturnError() {
            // Arrange
            when(loginOtpTokenRepository.findByUserIdAndOtpCodeAndUsedAtIsNull(
                    TEST_USER_ID, TEST_OTP)).thenReturn(Optional.of(expiredOtpToken));

            doReturn(errorResponse)
                    .when(responseHandler).handleResponse(
                            eq("OTP telah kadaluarsa"),
                            eq(HttpStatus.BAD_REQUEST),
                            eq("AUTH_008"),
                            isNull(),
                            eq(httpRequest)
                    );

            // Act
            ResponseEntity<Object> response = otpService.verifyOtp(
                    otpVerificationRequest, httpRequest, responseHandler);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            verify(loginOtpTokenRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should return error when user is not active")
        void verifyOtp_WithInactiveUser_ShouldReturnError() {
            // Arrange
            when(loginOtpTokenRepository.findByUserIdAndOtpCodeAndUsedAtIsNull(
                    TEST_USER_ID, TEST_OTP)).thenReturn(Optional.of(validOtpToken));
            when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(pendingUser));

            doReturn(errorResponse)
                    .when(responseHandler).handleResponse(
                            eq("Akun tidak aktif"),
                            eq(HttpStatus.BAD_REQUEST),
                            eq("AUTH_018"),
                            isNull(),
                            eq(httpRequest)
                    );

            // Act
            ResponseEntity<Object> response = otpService.verifyOtp(
                    otpVerificationRequest, httpRequest, responseHandler);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            verify(loginOtpTokenRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Resend OTP Tests")
    class ResendOtpTests {

        @Test
        @DisplayName("Should resend OTP for valid user")
        void resendOtp_WithValidUser_ShouldSendNewOTP() {
            // Arrange
            when(httpRequest.getRemoteAddr()).thenReturn("127.0.0.1");
            when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(activeUser));
            when(loginOtpTokenRepository.findTopByUserOrderByCreatedAtDesc(activeUser))
                    .thenReturn(Optional.empty());

            responseData.put("userId", activeUser.getId());
            responseData.put("email", activeUser.getEmail());
            responseData.put("otpExpiresIn", AuthenticationConstant.OTP_EXPIRY_MINUTES);
            responseData.put("nextResendAvailableIn", 60);

            doReturn(successResponse)
                    .when(responseHandler).handleResponse(
                            eq("OTP baru telah dikirim ke email Anda"),
                            eq(HttpStatus.OK),
                            isNull(),
                            eq(responseData),
                            eq(httpRequest)
                    );

            // Act
            ResponseEntity<Object> response = otpService.resendOtp(
                    resendOtpRequest, httpRequest, responseHandler);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(loginOtpTokenRepository).invalidateUserTokens(eq(TEST_USER_ID), any(LocalDateTime.class));
            verify(loginOtpTokenRepository).save(any(LoginOtpTokens.class));
            verify(emailServiceImpl).sendOtpEmail(eq(activeUser.getEmail()), anyString());
        }

        @Test
        @DisplayName("Should return error when user not found")
        void resendOtp_WithNonExistentUser_ShouldReturnError() {
            // Arrange
            when(httpRequest.getRemoteAddr()).thenReturn("127.0.0.1");
            when(userRepository.findById("invalid-id")).thenReturn(Optional.empty());

            doReturn(notFoundResponse)
                    .when(responseHandler).handleResponse(
                            eq("User tidak ditemukan"),
                            eq(HttpStatus.NOT_FOUND),
                            eq("AUTH_013"),
                            isNull(),
                            eq(httpRequest)
                    );

            // Act
            resendOtpRequest.setUserId("invalid-id");
            ResponseEntity<Object> response = otpService.resendOtp(
                    resendOtpRequest, httpRequest, responseHandler);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            verify(loginOtpTokenRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should return error when user is not active")
        void resendOtp_WithInactiveUser_ShouldReturnError() {
            // Arrange
            when(httpRequest.getRemoteAddr()).thenReturn("127.0.0.1");
            when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(pendingUser));

            doReturn(errorResponse)
                    .when(responseHandler).handleResponse(
                            eq("Akun tidak aktif"),
                            eq(HttpStatus.BAD_REQUEST),
                            eq("AUTH_018"),
                            isNull(),
                            eq(httpRequest)
                    );

            // Act
            ResponseEntity<Object> response = otpService.resendOtp(
                    resendOtpRequest, httpRequest, responseHandler);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            verify(loginOtpTokenRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should enforce rate limiting - prevent resend within 60 seconds")
        void resendOtp_WithRecentOTP_ShouldReturnRateLimitError() {
            // Arrange
            when(httpRequest.getRemoteAddr()).thenReturn("127.0.0.1");

            LoginOtpTokens recentOtp = createLoginOtpToken(activeUser);
            recentOtp.setCreatedAt(LocalDateTime.now().minusSeconds(30));

            when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(activeUser));
            when(loginOtpTokenRepository.findTopByUserOrderByCreatedAtDesc(activeUser))
                    .thenReturn(Optional.of(recentOtp));

            Map<String, Object> errorData = new HashMap<>();
            errorData.put("waitSeconds", 30L);
            errorData.put("userId", activeUser.getId());

            doReturn(errorResponse)
                    .when(responseHandler).handleResponse(
                            contains("Silakan tunggu 30 detik"),
                            eq(HttpStatus.BAD_REQUEST),
                            eq("AUTH_022"),
                            eq(errorData),
                            eq(httpRequest)
                    );

            // Act
            ResponseEntity<Object> response = otpService.resendOtp(
                    resendOtpRequest, httpRequest, responseHandler);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            verify(loginOtpTokenRepository, never()).invalidateUserTokens(any(), any());
            verify(loginOtpTokenRepository, never()).save(any());
            verify(emailServiceImpl, never()).sendOtpEmail(anyString(), anyString());
        }

        @Test
        @DisplayName("Should allow resend after 60 seconds")
        void resendOtp_AfterOneMinute_ShouldAllowResend() {
            // Arrange
            when(httpRequest.getRemoteAddr()).thenReturn("127.0.0.1");

            LoginOtpTokens oldOtp = createLoginOtpToken(activeUser);
            oldOtp.setCreatedAt(LocalDateTime.now().minusMinutes(2));

            when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(activeUser));
            when(loginOtpTokenRepository.findTopByUserOrderByCreatedAtDesc(activeUser))
                    .thenReturn(Optional.of(oldOtp));

            responseData.put("userId", activeUser.getId());
            responseData.put("email", activeUser.getEmail());
            responseData.put("otpExpiresIn", AuthenticationConstant.OTP_EXPIRY_MINUTES);
            responseData.put("nextResendAvailableIn", 60);

            doReturn(successResponse)
                    .when(responseHandler).handleResponse(
                            eq("OTP baru telah dikirim ke email Anda"),
                            eq(HttpStatus.OK),
                            isNull(),
                            eq(responseData),
                            eq(httpRequest)
                    );

            // Act
            ResponseEntity<Object> response = otpService.resendOtp(
                    resendOtpRequest, httpRequest, responseHandler);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(loginOtpTokenRepository).invalidateUserTokens(any(), any());
            verify(loginOtpTokenRepository).save(any(LoginOtpTokens.class));
        }

        @Test
        @DisplayName("Should handle email failure during resend")
        void resendOtp_WhenEmailFails_ShouldReturnError() {
            // Arrange
            when(httpRequest.getRemoteAddr()).thenReturn("127.0.0.1");

            when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(activeUser));
            when(loginOtpTokenRepository.findTopByUserOrderByCreatedAtDesc(activeUser))
                    .thenReturn(Optional.empty());

            doThrow(new RuntimeException("Email service down"))
                    .when(emailServiceImpl).sendOtpEmail(anyString(), anyString());

            doReturn(errorResponse)
                    .when(responseHandler).handleResponse(
                            eq("Gagal mengirim OTP ke email. Silakan coba lagi nanti."),
                            eq(HttpStatus.BAD_REQUEST),
                            eq("AUTH_019"),
                            isNull(),
                            eq(httpRequest)
                    );

            // Act
            ResponseEntity<Object> response = otpService.resendOtp(
                    resendOtpRequest, httpRequest, responseHandler);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            verify(loginOtpTokenRepository).invalidateUserTokens(any(), any());
            verify(loginOtpTokenRepository).save(any(LoginOtpTokens.class));
        }
    }

    @Nested
    @DisplayName("OTP Generation Tests")
    class OtpGenerationTests {

        @Test
        @DisplayName("Should generate random 6-digit OTP")
        void generateSecureOtp_ShouldReturn6DigitNumber() {
            try {
                java.lang.reflect.Method method = OtpService.class.getDeclaredMethod("generateSecureOtp");
                method.setAccessible(true);
                String otp = (String) method.invoke(otpService);

                assertNotNull(otp);
                assertEquals(6, otp.length());
                assertTrue(otp.matches("\\d{6}"));
                assertFalse(otp.startsWith("0"));
            } catch (Exception e) {
                fail("Could not test private method: " + e.getMessage());
            }
        }

        @Test
        @DisplayName("Should generate different OTPs each time")
        void generateSecureOtp_ShouldGenerateDifferentValues() {
            try {
                java.lang.reflect.Method method = OtpService.class.getDeclaredMethod("generateSecureOtp");
                method.setAccessible(true);

                String otp1 = (String) method.invoke(otpService);
                String otp2 = (String) method.invoke(otpService);

                assertNotEquals(otp1, otp2);
            } catch (Exception e) {
                fail("Could not test private method: " + e.getMessage());
            }
        }
    }
}