package com.backendsyndicate.smashclub.auth.controller;

import com.backendsyndicate.smashclub.auth.AuthTestBase;
import com.backendsyndicate.smashclub.auth.dto.request.*;
import com.backendsyndicate.smashclub.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest extends AuthTestBase {

    @Mock
    private AuthService authService;

    @Mock
    private HttpServletRequest httpRequest;

    @InjectMocks
    private AuthController authController;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private OtpVerificationRequest otpVerificationRequest;
    private ForgotPasswordRequest forgotPasswordRequest;
    private ResetPasswordRequest resetPasswordRequest;
    private ResendOtpRequest resendOtpRequest;
    private Map<String, Object> responseData;
    private ResponseEntity<Object> successResponse;
    private ResponseEntity<Object> errorResponse;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setFullName(TEST_FULL_NAME);
        registerRequest.setEmail(TEST_EMAIL);
        registerRequest.setPassword(TEST_PASSWORD);

        loginRequest = new LoginRequest();
        loginRequest.setEmail(TEST_EMAIL);
        loginRequest.setPassword(TEST_PASSWORD);

        otpVerificationRequest = new OtpVerificationRequest();
        otpVerificationRequest.setUserId(TEST_USER_ID);
        otpVerificationRequest.setOtp(TEST_OTP);

        forgotPasswordRequest = new ForgotPasswordRequest();
        forgotPasswordRequest.setEmail(TEST_EMAIL);

        resetPasswordRequest = new ResetPasswordRequest();
        resetPasswordRequest.setToken(TEST_TOKEN);
        resetPasswordRequest.setNewPassword("NewPassword123");

        resendOtpRequest = new ResendOtpRequest();
        resendOtpRequest.setUserId(TEST_USER_ID);

        responseData = new HashMap<>();
        responseData.put("message", "Success");

        successResponse = ResponseEntity.ok(responseData);
        errorResponse = ResponseEntity.badRequest().body(responseData);
    }

    @Nested
    @DisplayName("POST /api/v1/auth/register")
    class RegisterEndpointTests {

        @Test
        @DisplayName("Should call authService.register with valid request")
        void register_WithValidRequest_ShouldCallService() {
            // Arrange
            when(authService.register(any(RegisterRequest.class), eq(httpRequest)))
                    .thenReturn(successResponse);

            // Act
            ResponseEntity<Object> response = authController.register(registerRequest, httpRequest);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(authService).register(eq(registerRequest), eq(httpRequest));
        }

        @Test
        @DisplayName("Should return error when service returns error")
        void register_WhenServiceReturnsError_ShouldReturnError() {
            // Arrange
            when(authService.register(any(RegisterRequest.class), eq(httpRequest)))
                    .thenReturn(errorResponse);

            // Act
            ResponseEntity<Object> response = authController.register(registerRequest, httpRequest);

            // Assert
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            verify(authService).register(eq(registerRequest), eq(httpRequest));
        }

        @Test
        @DisplayName("Should pass HttpServletRequest to service")
        void register_ShouldPassHttpRequestToService() {
            // Arrange
            when(authService.register(any(RegisterRequest.class), eq(httpRequest)))
                    .thenReturn(successResponse);

            // Act
            authController.register(registerRequest, httpRequest);

            // Assert
            verify(authService).register(any(RegisterRequest.class), eq(httpRequest));
        }
    }

    @Nested
    @DisplayName("POST /api/v1/auth/login")
    class LoginEndpointTests {

        @Test
        @DisplayName("Should call authService.login with valid request")
        void login_WithValidRequest_ShouldCallService() {
            // Arrange
            when(authService.login(any(LoginRequest.class), eq(httpRequest)))
                    .thenReturn(successResponse);

            // Act
            ResponseEntity<Object> response = authController.login(loginRequest, httpRequest);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(authService).login(eq(loginRequest), eq(httpRequest));
        }

        @Test
        @DisplayName("Should return error when credentials are invalid")
        void login_WithInvalidCredentials_ShouldReturnError() {
            // Arrange
            when(authService.login(any(LoginRequest.class), eq(httpRequest)))
                    .thenReturn(errorResponse);

            // Act
            ResponseEntity<Object> response = authController.login(loginRequest, httpRequest);

            // Assert
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/auth/verify-otp")
    class VerifyOtpEndpointTests {

        @Test
        @DisplayName("Should call authService.verifyOtp with valid request")
        void verifyOtp_WithValidRequest_ShouldCallService() {
            // Arrange
            when(authService.verifyOtp(any(OtpVerificationRequest.class), eq(httpRequest)))
                    .thenReturn(successResponse);

            // Act
            ResponseEntity<Object> response = authController.verifyOtp(otpVerificationRequest, httpRequest);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(authService).verifyOtp(eq(otpVerificationRequest), eq(httpRequest));
        }

        @Test
        @DisplayName("Should return error when OTP is invalid")
        void verifyOtp_WithInvalidOtp_ShouldReturnError() {
            // Arrange
            when(authService.verifyOtp(any(OtpVerificationRequest.class), eq(httpRequest)))
                    .thenReturn(errorResponse);

            // Act
            ResponseEntity<Object> response = authController.verifyOtp(otpVerificationRequest, httpRequest);

            // Assert
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/auth/verify-email")
    class VerifyEmailEndpointTests {

        private final String verificationToken = "test-verification-token";

        @Test
        @DisplayName("Should call authService.verifyEmail with token")
        void verifyEmail_WithValidToken_ShouldCallService() {
            // Arrange
            when(authService.verifyEmail(eq(verificationToken), eq(httpRequest)))
                    .thenReturn(successResponse);

            // Act
            ResponseEntity<Object> response = authController.verifyEmail(verificationToken, httpRequest);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(authService).verifyEmail(eq(verificationToken), eq(httpRequest));
        }

        @Test
        @DisplayName("Should return error when token is invalid")
        void verifyEmail_WithInvalidToken_ShouldReturnError() {
            // Arrange
            when(authService.verifyEmail(anyString(), eq(httpRequest)))
                    .thenReturn(errorResponse);

            // Act
            ResponseEntity<Object> response = authController.verifyEmail("invalid-token", httpRequest);

            // Assert
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/auth/forgot-password")
    class ForgotPasswordEndpointTests {

        @Test
        @DisplayName("Should call authService.forgotPassword with valid request")
        void forgotPassword_WithValidEmail_ShouldCallService() {
            // Arrange
            when(authService.forgotPassword(any(ForgotPasswordRequest.class), eq(httpRequest)))
                    .thenReturn(successResponse);

            // Act
            ResponseEntity<Object> response = authController.forgotPassword(forgotPasswordRequest, httpRequest);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(authService).forgotPassword(eq(forgotPasswordRequest), eq(httpRequest));
        }

        @Test
        @DisplayName("Should return success even when email not found (security)")
        void forgotPassword_WithNonExistentEmail_ShouldReturnSuccess() {
            // Arrange
            when(authService.forgotPassword(any(ForgotPasswordRequest.class), eq(httpRequest)))
                    .thenReturn(successResponse);

            // Act
            ResponseEntity<Object> response = authController.forgotPassword(forgotPasswordRequest, httpRequest);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/auth/reset-password")
    class ResetPasswordEndpointTests {

        @Test
        @DisplayName("Should call authService.resetPassword with valid request")
        void resetPassword_WithValidRequest_ShouldCallService() {
            // Arrange
            when(authService.resetPassword(any(ResetPasswordRequest.class), eq(httpRequest)))
                    .thenReturn(successResponse);

            // Act
            ResponseEntity<Object> response = authController.resetPassword(resetPasswordRequest, httpRequest);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(authService).resetPassword(eq(resetPasswordRequest), eq(httpRequest));
        }

        @Test
        @DisplayName("Should return error when token is invalid")
        void resetPassword_WithInvalidToken_ShouldReturnError() {
            // Arrange
            when(authService.resetPassword(any(ResetPasswordRequest.class), eq(httpRequest)))
                    .thenReturn(errorResponse);

            // Act
            ResponseEntity<Object> response = authController.resetPassword(resetPasswordRequest, httpRequest);

            // Assert
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/auth/validate-reset-token")
    class ValidateResetTokenEndpointTests {

        @Test
        @DisplayName("Should call authService.validateResetToken with token")
        void validateResetToken_WithValidToken_ShouldCallService() {
            // Arrange
            when(authService.validateResetToken(eq(TEST_TOKEN), eq(httpRequest)))
                    .thenReturn(successResponse);

            // Act
            ResponseEntity<Object> response = authController.validateResetToken(TEST_TOKEN, httpRequest);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(authService).validateResetToken(eq(TEST_TOKEN), eq(httpRequest));
        }

        @Test
        @DisplayName("Should return error when token is invalid")
        void validateResetToken_WithInvalidToken_ShouldReturnError() {
            // Arrange
            when(authService.validateResetToken(anyString(), eq(httpRequest)))
                    .thenReturn(errorResponse);

            // Act
            ResponseEntity<Object> response = authController.validateResetToken("invalid-token", httpRequest);

            // Assert
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/auth/resend-verification")
    class ResendVerificationEndpointTests {

        private final String email = "test@example.com";

        @Test
        @DisplayName("Should call authService.resendVerificationEmail with email")
        void resendVerification_WithValidEmail_ShouldCallService() {
            // Arrange
            when(authService.resendVerificationEmail(eq(email), eq(httpRequest)))
                    .thenReturn(successResponse);

            // Act
            ResponseEntity<Object> response = authController.resendVerificationEmail(email, httpRequest);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(authService).resendVerificationEmail(eq(email), eq(httpRequest));
        }

        @Test
        @DisplayName("Should return error when email not found")
        void resendVerification_WithNonExistentEmail_ShouldReturnError() {
            // Arrange
            when(authService.resendVerificationEmail(anyString(), eq(httpRequest)))
                    .thenReturn(errorResponse);

            // Act
            ResponseEntity<Object> response = authController.resendVerificationEmail("nonexistent@example.com", httpRequest);

            // Assert
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/auth/resend-otp")
    class ResendOtpEndpointTests {

        @Test
        @DisplayName("Should call authService.resendOtp with valid request")
        void resendOtp_WithValidRequest_ShouldCallService() {
            // Arrange
            when(authService.resendOtp(any(ResendOtpRequest.class), eq(httpRequest)))
                    .thenReturn(successResponse);

            // Act
            ResponseEntity<Object> response = authController.resendOtp(resendOtpRequest, httpRequest);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(authService).resendOtp(eq(resendOtpRequest), eq(httpRequest));
        }

        @Test
        @DisplayName("Should return error when user not found")
        void resendOtp_WithInvalidUserId_ShouldReturnError() {
            // Arrange
            when(authService.resendOtp(any(ResendOtpRequest.class), eq(httpRequest)))
                    .thenReturn(errorResponse);

            // Act
            ResponseEntity<Object> response = authController.resendOtp(resendOtpRequest, httpRequest);

            // Assert
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/auth/logout")
    class LogoutEndpointTests {

        private final String refreshToken = "test-refresh-token";

        @Test
        @DisplayName("Should call authService.logout with refresh token")
        void logout_WithValidToken_ShouldCallService() {
            // Arrange
            when(authService.logout(eq(refreshToken), eq(httpRequest)))
                    .thenReturn(successResponse);

            // Act
            ResponseEntity<Object> response = authController.logout(refreshToken, httpRequest);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(authService).logout(eq(refreshToken), eq(httpRequest));
        }

        @Test
        @DisplayName("Should return success even with invalid token")
        void logout_WithInvalidToken_ShouldStillReturnSuccess() {
            // Arrange
            when(authService.logout(anyString(), eq(httpRequest)))
                    .thenReturn(successResponse);

            // Act
            ResponseEntity<Object> response = authController.logout("invalid-token", httpRequest);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/auth/logout-all")
    class LogoutAllEndpointTests {

        private final String userId = "test-user-id";

        @Test
        @DisplayName("Should call authService.logoutAll with user ID")
        void logoutAll_WithValidUserId_ShouldCallService() {
            // Arrange
            when(authService.logoutAll(eq(userId), eq(httpRequest)))
                    .thenReturn(successResponse);

            // Act
            ResponseEntity<Object> response = authController.logoutAll(userId, httpRequest);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(authService).logoutAll(eq(userId), eq(httpRequest));
        }

        @Test
        @DisplayName("Should return error when user not found")
        void logoutAll_WithInvalidUserId_ShouldReturnError() {
            // Arrange
            when(authService.logoutAll(anyString(), eq(httpRequest)))
                    .thenReturn(errorResponse);

            // Act
            ResponseEntity<Object> response = authController.logoutAll("invalid-id", httpRequest);

            // Assert
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/auth/check-session")
    class CheckSessionEndpointTests {

        private final String authHeader = "Bearer valid.token.here";

        @Test
        @DisplayName("Should extract token and call authService.checkSession")
        void checkSession_WithValidHeader_ShouldExtractTokenAndCallService() {
            // Arrange
            when(authService.checkSession(eq("valid.token.here"), eq(httpRequest)))
                    .thenReturn(successResponse);

            // Act
            ResponseEntity<Object> response = authController.checkSession(authHeader, httpRequest);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(authService).checkSession(eq("valid.token.here"), eq(httpRequest));
        }

        @Test
        @DisplayName("Should handle header without Bearer prefix")
        void checkSession_WithHeaderWithoutBearer_ShouldPassFullHeader() {
            // Arrange
            String tokenOnly = "valid.token.here";
            when(authService.checkSession(eq(tokenOnly), eq(httpRequest)))
                    .thenReturn(successResponse);

            // Act
            ResponseEntity<Object> response = authController.checkSession(tokenOnly, httpRequest);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(authService).checkSession(eq(tokenOnly), eq(httpRequest));
        }

        @Test
        @DisplayName("Should handle null header")
        void checkSession_WithNullHeader_ShouldPassNull() {
            // Arrange
            when(authService.checkSession(isNull(), eq(httpRequest)))
                    .thenReturn(errorResponse);

            // Act
            ResponseEntity<Object> response = authController.checkSession(null, httpRequest);

            // Assert
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            verify(authService).checkSession(isNull(), eq(httpRequest));
        }

        @Test
        @DisplayName("Should extract token correctly from Bearer header")
        void checkSession_ShouldExtractTokenCorrectly() {
            // Arrange
            String bearerHeader = "Bearer token123";
            when(authService.checkSession(eq("token123"), eq(httpRequest)))
                    .thenReturn(successResponse);

            // Act
            authController.checkSession(bearerHeader, httpRequest);

            // Assert
            verify(authService).checkSession(eq("token123"), eq(httpRequest));
        }
    }

    @Nested
    @DisplayName("POST /api/v1/auth/refresh-token")
    class RefreshTokenEndpointTests {

        private final String refreshToken = "test-refresh-token";

        @Test
        @DisplayName("Should call authService.refreshToken with refresh token")
        void refreshToken_WithValidToken_ShouldCallService() {
            // Arrange
            when(authService.refreshToken(eq(refreshToken), eq(httpRequest)))
                    .thenReturn(successResponse);

            // Act
            ResponseEntity<Object> response = authController.refreshToken(refreshToken, httpRequest);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(authService).refreshToken(eq(refreshToken), eq(httpRequest));
        }

        @Test
        @DisplayName("Should return error when token is invalid")
        void refreshToken_WithInvalidToken_ShouldReturnError() {
            // Arrange
            when(authService.refreshToken(anyString(), eq(httpRequest)))
                    .thenReturn(errorResponse);

            // Act
            ResponseEntity<Object> response = authController.refreshToken("invalid-token", httpRequest);

            // Assert
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
    }

    @Nested
    @DisplayName("Helper Method: extractTokenFromHeader")
    class ExtractTokenFromHeaderTests {

        @Test
        @DisplayName("Should extract token from Bearer header")
        void extractTokenFromHeader_WithBearerHeader_ShouldExtractToken() {
            // Use reflection to test private method
            try {
                java.lang.reflect.Method method = AuthController.class.getDeclaredMethod(
                        "extractTokenFromHeader", String.class);
                method.setAccessible(true);

                // Test dengan berbagai format Bearer header
                String result1 = (String) method.invoke(authController, "Bearer token123");
                assertEquals("token123", result1, "Should extract token123");

                String result2 = (String) method.invoke(authController, "Bearer   token456");
                assertEquals("token456", result2, "Should extract token456 and trim spaces");

                String result3 = (String) method.invoke(authController, "Bearer token789 with spaces");
                assertEquals("token789 with spaces", result3, "Should handle tokens with spaces");

            } catch (Exception e) {
                fail("Could not test private method: " + e.getMessage());
            }
        }

        @Test
        @DisplayName("Should return full header if not Bearer format")
        void extractTokenFromHeader_WithNonBearerHeader_ShouldReturnFullHeader() {
            try {
                java.lang.reflect.Method method = AuthController.class.getDeclaredMethod(
                        "extractTokenFromHeader", String.class);
                method.setAccessible(true);

                String result1 = (String) method.invoke(authController, "Basic credentials");
                assertEquals("Basic credentials", result1);

                String result2 = (String) method.invoke(authController, "token123");
                assertEquals("token123", result2);

                String result3 = (String) method.invoke(authController, "");
                assertEquals("", result3);

            } catch (Exception e) {
                fail("Could not test private method: " + e.getMessage());
            }
        }

        @Test
        @DisplayName("Should handle null header")
        void extractTokenFromHeader_WithNullHeader_ShouldReturnNull() {
            try {
                java.lang.reflect.Method method = AuthController.class.getDeclaredMethod(
                        "extractTokenFromHeader", String.class);
                method.setAccessible(true);

                String result = (String) method.invoke(authController, (Object) null);
                assertNull(result);

            } catch (Exception e) {
                fail("Could not test private method: " + e.getMessage());
            }
        }

        @Test
        @DisplayName("Should handle Bearer with only spaces after")
        void extractTokenFromHeader_WithBearerAndSpacesOnly_ShouldReturnEmptyString() {
            try {
                java.lang.reflect.Method method = AuthController.class.getDeclaredMethod(
                        "extractTokenFromHeader", String.class);
                method.setAccessible(true);

                String result1 = (String) method.invoke(authController, "Bearer   ");
                assertEquals("", result1, "Should return empty string when only spaces after Bearer");

                String result2 = (String) method.invoke(authController, "Bearer");
                // "Bearer" without space after is NOT a valid Bearer header format
                // karena startsWith("Bearer ") akan return false
                assertEquals("Bearer", result2, "Should return 'Bearer' as is (not valid Bearer format)");

            } catch (Exception e) {
                fail("Could not test private method: " + e.getMessage());
            }
        }

        @Test
        @DisplayName("Should handle Bearer with exact 7 characters prefix")
        void extractTokenFromHeader_WithExactBearerPrefix_ShouldWork() {
            try {
                java.lang.reflect.Method method = AuthController.class.getDeclaredMethod(
                        "extractTokenFromHeader", String.class);
                method.setAccessible(true);

                // "Bearer " is exactly 7 characters
                String result = (String) method.invoke(authController, "Bearer token123");
                assertEquals("token123", result);

                // Test with different lengths
                String result2 = (String) method.invoke(authController, "BearerX token123");
                assertEquals("BearerX token123", result2); // Not Bearer prefix

            } catch (Exception e) {
                fail("Could not test private method: " + e.getMessage());
            }
        }

        @Test
        @DisplayName("Should handle Bearer with empty token")
        void extractTokenFromHeader_WithBearerAndEmptyToken_ShouldReturnEmptyString() {
            try {
                java.lang.reflect.Method method = AuthController.class.getDeclaredMethod(
                        "extractTokenFromHeader", String.class);
                method.setAccessible(true);

                String result = (String) method.invoke(authController, "Bearer ");
                assertEquals("", result, "Should return empty string when token is empty");

            } catch (Exception e) {
                fail("Could not test private method: " + e.getMessage());
            }
        }
    }
}