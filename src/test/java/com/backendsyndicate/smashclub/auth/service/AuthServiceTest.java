package com.backendsyndicate.smashclub.auth.service;

import com.backendsyndicate.smashclub.auth.AuthTestBase;
import com.backendsyndicate.smashclub.auth.dto.request.*;
import com.backendsyndicate.smashclub.common.handler.ResponseHandler;
import com.backendsyndicate.smashclub.common.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest extends AuthTestBase {

    @Mock
    private RegistrationService registrationService;

    @Mock
    private LoginService loginService;

    @Mock
    private OtpService otpService;

    @Mock
    private PasswordService passwordService;

    @Mock
    private TokenService tokenService;

    @Mock
    private SessionService sessionService;

    @Mock
    private JwtService jwtService;

    @Mock
    private ProfileManagementService profileManagementService;

    @Mock
    private ResponseHandler responseHandler;

    @Mock
    private HttpServletRequest httpRequest;

    @Mock
    private MultipartFile mockFile;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private OtpVerificationRequest otpVerificationRequest;
    private ForgotPasswordRequest forgotPasswordRequest;
    private ResetPasswordRequest resetPasswordRequest;
    private ResendOtpRequest resendOtpRequest;
    private ProfileUpdateRequest profileUpdateRequest;
    private ChangePasswordRequest changePasswordRequest;
    private VerifyEmailChangeRequest verifyEmailChangeRequest;
    private ResponseEntity<Object> mockResponse;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setEmail(TEST_EMAIL);
        registerRequest.setPassword(TEST_PASSWORD);
        registerRequest.setFullName(TEST_FULL_NAME);

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

        profileUpdateRequest = new ProfileUpdateRequest();
        profileUpdateRequest.setFullName("Updated Name");

        changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setCurrentPassword(TEST_PASSWORD);
        changePasswordRequest.setNewPassword("NewPassword123");
        changePasswordRequest.setConfirmPassword("NewPassword123");

        verifyEmailChangeRequest = new VerifyEmailChangeRequest();
        verifyEmailChangeRequest.setToken(TEST_TOKEN);

        mockResponse = ResponseEntity.ok().build();
    }

    @Nested
    @DisplayName("Auth Service - Delegation Tests")
    class DelegationTests {

        @Test
        @DisplayName("register should delegate to RegistrationService")
        void register_ShouldDelegateToRegistrationService() {
            // Arrange
            when(registrationService.register(registerRequest, httpRequest))
                    .thenReturn(mockResponse);

            // Act
            ResponseEntity<Object> response = authService.register(registerRequest, httpRequest);

            // Assert
            assertNotNull(response);
            verify(registrationService).register(registerRequest, httpRequest);
        }

        @Test
        @DisplayName("login should delegate to LoginService")
        void login_ShouldDelegateToLoginService() {
            // Arrange
            when(loginService.login(loginRequest, httpRequest, responseHandler))
                    .thenReturn(mockResponse);

            // Act
            ResponseEntity<Object> response = authService.login(loginRequest, httpRequest);

            // Assert
            assertNotNull(response);
            verify(loginService).login(loginRequest, httpRequest, responseHandler);
        }

        @Test
        @DisplayName("verifyOtp should delegate to OtpService")
        void verifyOtp_ShouldDelegateToOtpService() {
            // Arrange
            when(otpService.verifyOtp(otpVerificationRequest, httpRequest, responseHandler))
                    .thenReturn(mockResponse);

            // Act
            ResponseEntity<Object> response = authService.verifyOtp(otpVerificationRequest, httpRequest);

            // Assert
            assertNotNull(response);
            verify(otpService).verifyOtp(otpVerificationRequest, httpRequest, responseHandler);
        }

        @Test
        @DisplayName("verifyEmail should delegate to RegistrationService")
        void verifyEmail_ShouldDelegateToRegistrationService() {
            // Arrange
            when(registrationService.verifyEmail(TEST_TOKEN, httpRequest))
                    .thenReturn(mockResponse);

            // Act
            ResponseEntity<Object> response = authService.verifyEmail(TEST_TOKEN, httpRequest);

            // Assert
            assertNotNull(response);
            verify(registrationService).verifyEmail(TEST_TOKEN, httpRequest);
        }

        @Test
        @DisplayName("forgotPassword should delegate to PasswordService")
        void forgotPassword_ShouldDelegateToPasswordService() {
            // Arrange
            when(passwordService.forgotPassword(forgotPasswordRequest, httpRequest, responseHandler))
                    .thenReturn(mockResponse);

            // Act
            ResponseEntity<Object> response = authService.forgotPassword(forgotPasswordRequest, httpRequest);

            // Assert
            assertNotNull(response);
            verify(passwordService).forgotPassword(forgotPasswordRequest, httpRequest, responseHandler);
        }

        @Test
        @DisplayName("resetPassword should delegate to PasswordService")
        void resetPassword_ShouldDelegateToPasswordService() {
            // Arrange
            when(passwordService.resetPassword(resetPasswordRequest, httpRequest, responseHandler))
                    .thenReturn(mockResponse);

            // Act
            ResponseEntity<Object> response = authService.resetPassword(resetPasswordRequest, httpRequest);

            // Assert
            assertNotNull(response);
            verify(passwordService).resetPassword(resetPasswordRequest, httpRequest, responseHandler);
        }

        @Test
        @DisplayName("logout should delegate to SessionService")
        void logout_ShouldDelegateToSessionService() {
            // Arrange
            when(sessionService.logout("refresh-token", httpRequest, responseHandler))
                    .thenReturn(mockResponse);

            // Act
            ResponseEntity<Object> response = authService.logout("refresh-token", httpRequest);

            // Assert
            assertNotNull(response);
            verify(sessionService).logout("refresh-token", httpRequest, responseHandler);
        }

        @Test
        @DisplayName("logoutAll should delegate to SessionService")
        void logoutAll_ShouldDelegateToSessionService() {
            // Arrange
            when(sessionService.logoutAll(TEST_USER_ID, httpRequest, responseHandler))
                    .thenReturn(mockResponse);

            // Act
            ResponseEntity<Object> response = authService.logoutAll(TEST_USER_ID, httpRequest);

            // Assert
            assertNotNull(response);
            verify(sessionService).logoutAll(TEST_USER_ID, httpRequest, responseHandler);
        }

        @Test
        @DisplayName("resendVerificationEmail should delegate to RegistrationService")
        void resendVerificationEmail_ShouldDelegateToRegistrationService() {
            // Arrange
            when(registrationService.resendVerificationEmail(TEST_EMAIL, httpRequest))
                    .thenReturn(mockResponse);

            // Act
            ResponseEntity<Object> response = authService.resendVerificationEmail(TEST_EMAIL, httpRequest);

            // Assert
            assertNotNull(response);
            verify(registrationService).resendVerificationEmail(TEST_EMAIL, httpRequest);
        }

        @Test
        @DisplayName("resendOtp should delegate to OtpService")
        void resendOtp_ShouldDelegateToOtpService() {
            // Arrange
            when(otpService.resendOtp(resendOtpRequest, httpRequest, responseHandler))
                    .thenReturn(mockResponse);

            // Act
            ResponseEntity<Object> response = authService.resendOtp(resendOtpRequest, httpRequest);

            // Assert
            assertNotNull(response);
            verify(otpService).resendOtp(resendOtpRequest, httpRequest, responseHandler);
        }

        @Test
        @DisplayName("checkSession should delegate to SessionService")
        void checkSession_ShouldDelegateToSessionService() {
            // Arrange
            when(sessionService.checkSession("access-token", httpRequest, responseHandler))
                    .thenReturn(mockResponse);

            // Act
            ResponseEntity<Object> response = authService.checkSession("access-token", httpRequest);

            // Assert
            assertNotNull(response);
            verify(sessionService).checkSession("access-token", httpRequest, responseHandler);
        }

        @Test
        @DisplayName("refreshToken should delegate to TokenService")
        void refreshToken_ShouldDelegateToTokenService() {
            // Arrange
            when(tokenService.refreshToken("refresh-token", httpRequest, responseHandler))
                    .thenReturn(mockResponse);

            // Act
            ResponseEntity<Object> response = authService.refreshToken("refresh-token", httpRequest);

            // Assert
            assertNotNull(response);
            verify(tokenService).refreshToken("refresh-token", httpRequest, responseHandler);
        }

        @Test
        @DisplayName("validateResetToken should delegate to PasswordService")
        void validateResetToken_ShouldDelegateToPasswordService() {
            // Arrange
            when(passwordService.validateResetToken(TEST_TOKEN, httpRequest, responseHandler))
                    .thenReturn(mockResponse);

            // Act
            ResponseEntity<Object> response = authService.validateResetToken(TEST_TOKEN, httpRequest);

            // Assert
            assertNotNull(response);
            verify(passwordService).validateResetToken(TEST_TOKEN, httpRequest, responseHandler);
        }

        @Test
        @DisplayName("getProfile should delegate to ProfileManagementService")
        void getProfile_ShouldDelegateToProfileManagementService() {
            // Arrange
            when(profileManagementService.getProfile(TEST_USER_ID, httpRequest, responseHandler))
                    .thenReturn(mockResponse);

            // Act
            ResponseEntity<Object> response = authService.getProfile(TEST_USER_ID, httpRequest);

            // Assert
            assertNotNull(response);
            verify(profileManagementService).getProfile(TEST_USER_ID, httpRequest, responseHandler);
        }

        @Test
        @DisplayName("updateProfile should delegate to ProfileManagementService")
        void updateProfile_ShouldDelegateToProfileManagementService() {
            // Arrange
            when(profileManagementService.updateProfile(
                    TEST_USER_ID, profileUpdateRequest, httpRequest, responseHandler))
                    .thenReturn(mockResponse);

            // Act
            ResponseEntity<Object> response = authService.updateProfile(
                    TEST_USER_ID, profileUpdateRequest, httpRequest);

            // Assert
            assertNotNull(response);
            verify(profileManagementService).updateProfile(
                    TEST_USER_ID, profileUpdateRequest, httpRequest, responseHandler);
        }

        @Test
        @DisplayName("changePassword should delegate to ProfileManagementService")
        void changePassword_ShouldDelegateToProfileManagementService() {
            // Arrange
            when(profileManagementService.changePassword(
                    TEST_USER_ID, changePasswordRequest, httpRequest, responseHandler))
                    .thenReturn(mockResponse);

            // Act
            ResponseEntity<Object> response = authService.changePassword(
                    TEST_USER_ID, changePasswordRequest, httpRequest);

            // Assert
            assertNotNull(response);
            verify(profileManagementService).changePassword(
                    TEST_USER_ID, changePasswordRequest, httpRequest, responseHandler);
        }

        @Test
        @DisplayName("verifyEmailChange should delegate to ProfileManagementService")
        void verifyEmailChange_ShouldDelegateToProfileManagementService() {
            // Arrange
            when(profileManagementService.verifyEmailChange(
                    verifyEmailChangeRequest, httpRequest, responseHandler))
                    .thenReturn(mockResponse);

            // Act
            ResponseEntity<Object> response = authService.verifyEmailChange(
                    verifyEmailChangeRequest, httpRequest);

            // Assert
            assertNotNull(response);
            verify(profileManagementService).verifyEmailChange(
                    verifyEmailChangeRequest, httpRequest, responseHandler);
        }

        @Test
        @DisplayName("cancelEmailChange should delegate to ProfileManagementService")
        void cancelEmailChange_ShouldDelegateToProfileManagementService() {
            // Arrange
            when(profileManagementService.cancelEmailChange(TEST_USER_ID, httpRequest, responseHandler))
                    .thenReturn(mockResponse);

            // Act
            ResponseEntity<Object> response = authService.cancelEmailChange(TEST_USER_ID, httpRequest);

            // Assert
            assertNotNull(response);
            verify(profileManagementService).cancelEmailChange(TEST_USER_ID, httpRequest, responseHandler);
        }

        @Test
        @DisplayName("uploadProfilePicture should delegate to ProfileManagementService")
        void uploadProfilePicture_ShouldDelegateToProfileManagementService() {
            // Arrange
            when(profileManagementService.uploadProfilePicture(
                    TEST_USER_ID, mockFile, httpRequest, responseHandler))
                    .thenReturn(mockResponse);

            // Act
            ResponseEntity<Object> response = authService.uploadProfilePicture(
                    TEST_USER_ID, mockFile, httpRequest);

            // Assert
            assertNotNull(response);
            verify(profileManagementService).uploadProfilePicture(
                    TEST_USER_ID, mockFile, httpRequest, responseHandler);
        }

        @Test
        @DisplayName("deleteProfilePicture should delegate to ProfileManagementService")
        void deleteProfilePicture_ShouldDelegateToProfileManagementService() {
            // Arrange
            when(profileManagementService.deleteProfilePicture(TEST_USER_ID, httpRequest, responseHandler))
                    .thenReturn(mockResponse);

            // Act
            ResponseEntity<Object> response = authService.deleteProfilePicture(TEST_USER_ID, httpRequest);

            // Assert
            assertNotNull(response);
            verify(profileManagementService).deleteProfilePicture(TEST_USER_ID, httpRequest, responseHandler);
        }
    }

    @Nested
    @DisplayName("getUserIdFromToken Tests")
    class GetUserIdFromTokenTests {

        @Test
        @DisplayName("Should return user ID when token is valid")
        void getUserIdFromToken_WithValidToken_ShouldReturnUserId() {
            // Arrange
            when(jwtService.extractUserId("valid-token")).thenReturn(TEST_USER_ID);

            // Act
            String result = authService.getUserIdFromToken("valid-token");

            // Assert
            assertEquals(TEST_USER_ID, result);
        }

        @Test
        @DisplayName("Should return null when token has no user ID")
        void getUserIdFromToken_WithTokenWithoutUserId_ShouldReturnNull() {
            // Arrange
            when(jwtService.extractUserId("no-user-id-token")).thenReturn(null);

            // Act
            String result = authService.getUserIdFromToken("no-user-id-token");

            // Assert
            assertNull(result);
        }

        @Test
        @DisplayName("Should return null when token is invalid")
        void getUserIdFromToken_WithInvalidToken_ShouldReturnNull() {
            // Arrange
            when(jwtService.extractUserId("invalid-token"))
                    .thenThrow(new RuntimeException("Invalid token"));

            // Act
            String result = authService.getUserIdFromToken("invalid-token");

            // Assert
            assertNull(result);
        }

        @Test
        @DisplayName("Should handle null token")
        void getUserIdFromToken_WithNullToken_ShouldReturnNull() {
            // Arrange
            when(jwtService.extractUserId(null)).thenThrow(new RuntimeException("Null token"));

            // Act
            String result = authService.getUserIdFromToken(null);

            // Assert
            assertNull(result);
        }
    }
}