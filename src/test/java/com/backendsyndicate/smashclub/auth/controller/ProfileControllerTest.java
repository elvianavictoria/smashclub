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
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ProfileControllerTest extends AuthTestBase {

    @Mock
    private AuthService authService;

    @Mock
    private HttpServletRequest httpRequest;

    @Mock
    private MultipartFile mockFile;

    @InjectMocks
    private ProfileController profileController;

    private final String authHeader = "Bearer valid.jwt.token";
    private final String userId = "test-user-id";
    private ProfileUpdateRequest profileUpdateRequest;
    private ChangePasswordRequest changePasswordRequest;
    private VerifyEmailChangeRequest verifyEmailChangeRequest;
    private ProfilePictureRequest profilePictureRequest;
    private Map<String, Object> responseData;
    private ResponseEntity<Object> successResponse;
    private ResponseEntity<Object> errorResponse;
    private ResponseEntity<Object> unauthorizedResponse;

    @BeforeEach
    void setUp() {
        profileUpdateRequest = new ProfileUpdateRequest();
        profileUpdateRequest.setFullName("Updated Name");
        profileUpdateRequest.setEmail(TEST_NEW_EMAIL);

        changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setCurrentPassword(TEST_PASSWORD);
        changePasswordRequest.setNewPassword("NewPassword123");
        changePasswordRequest.setConfirmPassword("NewPassword123");

        verifyEmailChangeRequest = new VerifyEmailChangeRequest();
        verifyEmailChangeRequest.setToken(TEST_TOKEN);

        profilePictureRequest = new ProfilePictureRequest();
        profilePictureRequest.setProfilePicture(mockFile);

        responseData = new HashMap<>();
        responseData.put("message", "Success");

        successResponse = ResponseEntity.ok(responseData);
        errorResponse = ResponseEntity.badRequest().body(responseData);
        unauthorizedResponse = ResponseEntity.status(401).body("Unauthorized");

        // HAPUS stubbing yang tidak perlu di sini
        // when(authService.getUserIdFromToken("valid.jwt.token")).thenReturn(userId);
        // when(authService.getUserIdFromToken("invalid-token")).thenReturn(null);
    }

    @Nested
    @DisplayName("GET /api/v1/profile")
    class GetProfileEndpointTests {

        @Test
        @DisplayName("Should return profile when authorized")
        void getProfile_WithValidToken_ShouldReturnProfile() {
            // Arrange
            when(authService.getUserIdFromToken("valid.jwt.token")).thenReturn(userId);
            when(authService.getProfile(eq(userId), eq(httpRequest)))
                    .thenReturn(successResponse);

            // Act
            ResponseEntity<Object> response = profileController.getProfile(authHeader, httpRequest);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(authService).getProfile(eq(userId), eq(httpRequest));
        }

        @Test
        @DisplayName("Should return 401 when token is invalid")
        void getProfile_WithInvalidToken_ShouldReturnUnauthorized() {
            // Arrange
            String invalidHeader = "Bearer invalid-token";
            when(authService.getUserIdFromToken("invalid-token")).thenReturn(null);

            // Act
            ResponseEntity<Object> response = profileController.getProfile(invalidHeader, httpRequest);

            // Assert
            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            verify(authService, never()).getProfile(anyString(), any());
        }

        @Test
        @DisplayName("Should return 401 when header is null")
        void getProfile_WithNullHeader_ShouldReturnUnauthorized() {
            // Act
            ResponseEntity<Object> response = profileController.getProfile(null, httpRequest);

            // Assert
            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            verify(authService, never()).getProfile(anyString(), any());
        }

        @Test
        @DisplayName("Should return 401 when header doesn't start with Bearer")
        void getProfile_WithNonBearerHeader_ShouldReturnUnauthorized() {
            // Act
            ResponseEntity<Object> response = profileController.getProfile("Basic credentials", httpRequest);

            // Assert
            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            verify(authService, never()).getProfile(anyString(), any());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/profile")
    class UpdateProfileEndpointTests {

        @Test
        @DisplayName("Should update profile when authorized")
        void updateProfile_WithValidToken_ShouldUpdate() {
            // Arrange
            when(authService.getUserIdFromToken("valid.jwt.token")).thenReturn(userId);
            when(authService.updateProfile(eq(userId), eq(profileUpdateRequest), eq(httpRequest)))
                    .thenReturn(successResponse);

            // Act
            ResponseEntity<Object> response = profileController.updateProfile(
                    authHeader, profileUpdateRequest, httpRequest);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(authService).updateProfile(eq(userId), eq(profileUpdateRequest), eq(httpRequest));
        }

        @Test
        @DisplayName("Should return 401 when token is invalid")
        void updateProfile_WithInvalidToken_ShouldReturnUnauthorized() {
            // Arrange
            String invalidHeader = "Bearer invalid-token";
            when(authService.getUserIdFromToken("invalid-token")).thenReturn(null);

            // Act
            ResponseEntity<Object> response = profileController.updateProfile(
                    invalidHeader, profileUpdateRequest, httpRequest);

            // Assert
            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            verify(authService, never()).updateProfile(anyString(), any(), any());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/profile/change-password")
    class ChangePasswordEndpointTests {

        @Test
        @DisplayName("Should change password when authorized")
        void changePassword_WithValidToken_ShouldChange() {
            // Arrange
            when(authService.getUserIdFromToken("valid.jwt.token")).thenReturn(userId);
            when(authService.changePassword(eq(userId), eq(changePasswordRequest), eq(httpRequest)))
                    .thenReturn(successResponse);

            // Act
            ResponseEntity<Object> response = profileController.changePassword(
                    authHeader, changePasswordRequest, httpRequest);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(authService).changePassword(eq(userId), eq(changePasswordRequest), eq(httpRequest));
        }

        @Test
        @DisplayName("Should return 401 when token is invalid")
        void changePassword_WithInvalidToken_ShouldReturnUnauthorized() {
            // Arrange
            String invalidHeader = "Bearer invalid-token";
            when(authService.getUserIdFromToken("invalid-token")).thenReturn(null);

            // Act
            ResponseEntity<Object> response = profileController.changePassword(
                    invalidHeader, changePasswordRequest, httpRequest);

            // Assert
            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            verify(authService, never()).changePassword(anyString(), any(), any());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/profile/verify-email-change")
    class VerifyEmailChangeEndpointTests {

        @Test
        @DisplayName("Should verify email change")
        void verifyEmailChange_WithValidToken_ShouldVerify() {
            // Arrange
            when(authService.verifyEmailChange(eq(verifyEmailChangeRequest), eq(httpRequest)))
                    .thenReturn(successResponse);

            // Act
            ResponseEntity<Object> response = profileController.verifyEmailChange(
                    verifyEmailChangeRequest, httpRequest);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(authService).verifyEmailChange(eq(verifyEmailChangeRequest), eq(httpRequest));
        }

        @Test
        @DisplayName("Should return error when token invalid")
        void verifyEmailChange_WithInvalidToken_ShouldReturnError() {
            // Arrange
            when(authService.verifyEmailChange(any(VerifyEmailChangeRequest.class), eq(httpRequest)))
                    .thenReturn(errorResponse);

            // Act
            ResponseEntity<Object> response = profileController.verifyEmailChange(
                    verifyEmailChangeRequest, httpRequest);

            // Assert
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/profile/cancel-email-change")
    class CancelEmailChangeEndpointTests {

        @Test
        @DisplayName("Should cancel email change when authorized")
        void cancelEmailChange_WithValidToken_ShouldCancel() {
            // Arrange
            when(authService.getUserIdFromToken("valid.jwt.token")).thenReturn(userId);
            when(authService.cancelEmailChange(eq(userId), eq(httpRequest)))
                    .thenReturn(successResponse);

            // Act
            ResponseEntity<Object> response = profileController.cancelEmailChange(
                    authHeader, httpRequest);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(authService).cancelEmailChange(eq(userId), eq(httpRequest));
        }

        @Test
        @DisplayName("Should return 401 when token is invalid")
        void cancelEmailChange_WithInvalidToken_ShouldReturnUnauthorized() {
            // Arrange
            String invalidHeader = "Bearer invalid-token";
            when(authService.getUserIdFromToken("invalid-token")).thenReturn(null);

            // Act
            ResponseEntity<Object> response = profileController.cancelEmailChange(
                    invalidHeader, httpRequest);

            // Assert
            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            verify(authService, never()).cancelEmailChange(anyString(), any());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/profile/profile-picture")
    class UploadProfilePictureEndpointTests {

        @Test
        @DisplayName("Should upload profile picture when authorized")
        void uploadProfilePicture_WithValidToken_ShouldUpload() throws Exception {
            // Arrange
            when(authService.getUserIdFromToken("valid.jwt.token")).thenReturn(userId);
            when(authService.uploadProfilePicture(eq(userId), eq(mockFile), eq(httpRequest)))
                    .thenReturn(successResponse);

            // Act
            ResponseEntity<Object> response = profileController.uploadProfilePicture(
                    authHeader, profilePictureRequest, httpRequest);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(authService).uploadProfilePicture(eq(userId), eq(mockFile), eq(httpRequest));
        }

        @Test
        @DisplayName("Should return 401 when token is invalid")
        void uploadProfilePicture_WithInvalidToken_ShouldReturnUnauthorized() {
            // Arrange
            String invalidHeader = "Bearer invalid-token";
            when(authService.getUserIdFromToken("invalid-token")).thenReturn(null);

            // Act
            ResponseEntity<Object> response = profileController.uploadProfilePicture(
                    invalidHeader, profilePictureRequest, httpRequest);

            // Assert
            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            verify(authService, never()).uploadProfilePicture(anyString(), any(), any());
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/profile/profile-picture")
    class DeleteProfilePictureEndpointTests {

        @Test
        @DisplayName("Should delete profile picture when authorized")
        void deleteProfilePicture_WithValidToken_ShouldDelete() {
            // Arrange
            when(authService.getUserIdFromToken("valid.jwt.token")).thenReturn(userId);
            when(authService.deleteProfilePicture(eq(userId), eq(httpRequest)))
                    .thenReturn(successResponse);

            // Act
            ResponseEntity<Object> response = profileController.deleteProfilePicture(
                    authHeader, httpRequest);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(authService).deleteProfilePicture(eq(userId), eq(httpRequest));
        }

        @Test
        @DisplayName("Should return 401 when token is invalid")
        void deleteProfilePicture_WithInvalidToken_ShouldReturnUnauthorized() {
            // Arrange
            String invalidHeader = "Bearer invalid-token";
            when(authService.getUserIdFromToken("invalid-token")).thenReturn(null);

            // Act
            ResponseEntity<Object> response = profileController.deleteProfilePicture(
                    invalidHeader, httpRequest);

            // Assert
            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            verify(authService, never()).deleteProfilePicture(anyString(), any());
        }
    }
}