package com.backendsyndicate.smashclub.common.security;

import com.backendsyndicate.smashclub.auth.AuthTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest extends AuthTestBase {

    @InjectMocks
    private JwtService jwtService;

    private final String testSecretKey = "dGhpcy1pcy1hLXRlc3Qtc2VjcmV0LWtleS1mb3Itand0LXNlcnZpY2UtdGVzdGluZw=="; // 32-byte Base64
    private final long expirationHours = 24L;
    private final long refreshExpirationDays = 7L;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtService, "secretKey", testSecretKey);
        ReflectionTestUtils.setField(jwtService, "expirationHours", expirationHours);
        ReflectionTestUtils.setField(jwtService, "refreshExpirationDays", refreshExpirationDays);
    }

    @Nested
    @DisplayName("Token Generation Tests")
    class TokenGenerationTests {

        @Test
        @DisplayName("Should generate valid access token with all claims")
        void generateToken_ShouldCreateValidAccessToken() {
            // When
            String token = jwtService.generateToken(TEST_USER_ID, TEST_EMAIL, TEST_FULL_NAME);

            // Then
            assertNotNull(token);
            assertFalse(token.isEmpty());
            assertTrue(jwtService.isTokenValid(token));

            // Extract claims using public methods
            assertEquals(TEST_USER_ID, jwtService.extractUserId(token));
            assertEquals(TEST_EMAIL, jwtService.extractEmail(token));
            assertEquals(TEST_FULL_NAME, jwtService.extractFullName(token));

            Date expiration = jwtService.extractExpiration(token);
            assertNotNull(expiration);
            assertTrue(expiration.after(new Date()));
        }

        @Test
        @DisplayName("Should generate valid refresh token")
        void generateRefreshToken_ShouldCreateValidRefreshToken() {
            // When
            String token = jwtService.generateRefreshToken(TEST_USER_ID);

            // Then
            assertNotNull(token);
            assertFalse(token.isEmpty());
            assertTrue(jwtService.isTokenValid(token));

            assertEquals(TEST_USER_ID, jwtService.extractUserId(token));
            assertNull(jwtService.extractEmail(token));
            assertNull(jwtService.extractFullName(token));
        }

        @Test
        @DisplayName("Access token should expire after configured hours")
        void generateToken_ShouldHaveCorrectExpiration() {
            // When
            String token = jwtService.generateToken(TEST_USER_ID, TEST_EMAIL, TEST_FULL_NAME);

            // Get expiration
            Date expiration = jwtService.extractExpiration(token);
            Date now = new Date();

            // Calculate difference in milliseconds
            long diffInMillies = expiration.getTime() - now.getTime();
            long diffInHours = diffInMillies / (1000 * 60 * 60);

            // Should be approximately 24 hours (allow 1 hour tolerance for test execution)
            assertTrue(diffInHours >= 23 && diffInHours <= 24,
                    "Expected expiration around 24 hours, but got " + diffInHours + " hours");
        }

        @Test
        @DisplayName("Refresh token should expire after configured days")
        void generateRefreshToken_ShouldHaveCorrectExpiration() {
            // When
            String token = jwtService.generateRefreshToken(TEST_USER_ID);

            // Get expiration
            Date expiration = jwtService.extractExpiration(token);
            Date now = new Date();

            // Calculate difference in milliseconds
            long diffInMillies = expiration.getTime() - now.getTime();
            long diffInDays = diffInMillies / (1000 * 60 * 60 * 24);

            // Should be approximately 7 days (allow 1 day tolerance)
            assertTrue(diffInDays >= 6 && diffInDays <= 7,
                    "Expected expiration around 7 days, but got " + diffInDays + " days");
        }

        @Test
        @DisplayName("Should generate different tokens for same user")
        void generateToken_ShouldGenerateDifferentTokens() {
            String token1 = jwtService.generateToken(TEST_USER_ID, TEST_EMAIL, TEST_FULL_NAME);
            String token2 = jwtService.generateToken(TEST_USER_ID, TEST_EMAIL, TEST_FULL_NAME);

            assertNotEquals(token1, token2);
        }
    }

    @Nested
    @DisplayName("Token Validation Tests")
    class TokenValidationTests {

        private String validAccessToken;
        private String validRefreshToken;

        @BeforeEach
        void setUp() {
            validAccessToken = jwtService.generateToken(TEST_USER_ID, TEST_EMAIL, TEST_FULL_NAME);
            validRefreshToken = jwtService.generateRefreshToken(TEST_USER_ID);
        }

        @Test
        @DisplayName("isTokenValid should return true for valid tokens")
        void isTokenValid_WithValidTokens_ShouldReturnTrue() {
            assertTrue(jwtService.isTokenValid(validAccessToken));
            assertTrue(jwtService.isTokenValid(validRefreshToken));
        }

        @Test
        @DisplayName("isTokenValid should return false for malformed token")
        void isTokenValid_WithMalformedToken_ShouldReturnFalse() {
            String malformedToken = validAccessToken + "malformed";
            assertFalse(jwtService.isTokenValid(malformedToken));
        }

        @Test
        @DisplayName("isTokenValid should return false for empty token")
        void isTokenValid_WithEmptyToken_ShouldReturnFalse() {
            assertFalse(jwtService.isTokenValid(""));
            assertFalse(jwtService.isTokenValid(null));
            assertFalse(jwtService.isTokenValid("   "));
        }

        @Test
        @DisplayName("isTokenValid should return false for expired token")
        void isTokenValid_WithExpiredToken_ShouldReturnFalse() throws Exception {
            // Set expiration ke 1 detik
            ReflectionTestUtils.setField(jwtService, "expirationHours", 0L);
            String expiredToken = jwtService.generateToken(TEST_USER_ID, TEST_EMAIL, TEST_FULL_NAME);

            // Tunggu sebentar
            Thread.sleep(10);

            assertFalse(jwtService.isTokenValid(expiredToken));
        }

        @Test
        @DisplayName("isTokenValid should return false for token with wrong signature")
        void isTokenValid_WithWrongSignature_ShouldReturnFalse() {
            // Set different secret key
            String differentKey = Base64.getEncoder().encodeToString("different-key-32-bytes-long-here!".getBytes());
            ReflectionTestUtils.setField(jwtService, "secretKey", differentKey);

            // Token generated with original key should be invalid with new key
            assertFalse(jwtService.isTokenValid(validAccessToken));
        }
    }

    @Nested
    @DisplayName("Token Extraction Tests")
    class TokenExtractionTests {

        private String validAccessToken;
        private String validRefreshToken;

        @BeforeEach
        void setUp() {
            validAccessToken = jwtService.generateToken(TEST_USER_ID, TEST_EMAIL, TEST_FULL_NAME);
            validRefreshToken = jwtService.generateRefreshToken(TEST_USER_ID);
        }

        @Test
        @DisplayName("extractUserId should return correct user ID")
        void extractUserId_ShouldReturnCorrectUserId() {
            assertEquals(TEST_USER_ID, jwtService.extractUserId(validAccessToken));
            assertEquals(TEST_USER_ID, jwtService.extractUserId(validRefreshToken));
        }

        @Test
        @DisplayName("extractEmail should return email from access token")
        void extractEmail_FromAccessToken_ShouldReturnEmail() {
            assertEquals(TEST_EMAIL, jwtService.extractEmail(validAccessToken));
        }

        @Test
        @DisplayName("extractEmail should return null from refresh token")
        void extractEmail_FromRefreshToken_ShouldReturnNull() {
            assertNull(jwtService.extractEmail(validRefreshToken));
        }

        @Test
        @DisplayName("extractFullName should return full name from access token")
        void extractFullName_FromAccessToken_ShouldReturnFullName() {
            assertEquals(TEST_FULL_NAME, jwtService.extractFullName(validAccessToken));
        }

        @Test
        @DisplayName("extractFullName should return null from refresh token")
        void extractFullName_FromRefreshToken_ShouldReturnNull() {
            assertNull(jwtService.extractFullName(validRefreshToken));
        }

        @Test
        @DisplayName("extractExpiration should return expiration date")
        void extractExpiration_ShouldReturnDate() {
            Date expiration = jwtService.extractExpiration(validAccessToken);
            assertNotNull(expiration);
            assertTrue(expiration.after(new Date()));
        }

        @Test
        @DisplayName("Should throw exception when extracting from invalid token")
        void extractUserId_WithInvalidToken_ShouldThrowException() {
            assertThrows(Exception.class, () -> {
                jwtService.extractUserId("invalid.token.here");
            });
        }

        @Test
        @DisplayName("Should throw exception when extracting from null token")
        void extractUserId_WithNullToken_ShouldThrowException() {
            assertThrows(Exception.class, () -> {
                jwtService.extractUserId(null);
            });
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle empty secret key gracefully")
        void generateToken_WithEmptySecretKey_ShouldStillWork() {
            ReflectionTestUtils.setField(jwtService, "secretKey", "");

            String token = jwtService.generateToken(TEST_USER_ID, TEST_EMAIL, TEST_FULL_NAME);
            assertNotNull(token);

            // Token should still be valid (uses fallback)
            assertTrue(jwtService.isTokenValid(token));
        }

        @Test
        @DisplayName("Should handle null secret key gracefully")
        void generateToken_WithNullSecretKey_ShouldStillWork() {
            ReflectionTestUtils.setField(jwtService, "secretKey", null);

            String token = jwtService.generateToken(TEST_USER_ID, TEST_EMAIL, TEST_FULL_NAME);
            assertNotNull(token);

            // Token should still be valid (uses fallback)
            assertTrue(jwtService.isTokenValid(token));
        }

        @Test
        @DisplayName("Should handle very long secret key")
        void generateToken_WithVeryLongSecretKey_ShouldWork() {
            String longKey = Base64.getEncoder().encodeToString(new byte[100]); // 100 bytes
            ReflectionTestUtils.setField(jwtService, "secretKey", longKey);

            String token = jwtService.generateToken(TEST_USER_ID, TEST_EMAIL, TEST_FULL_NAME);
            assertNotNull(token);
            assertTrue(jwtService.isTokenValid(token));
        }

        @Test
        @DisplayName("Should extract claims correctly with special characters")
        void generateToken_WithSpecialCharacters_ShouldWork() {
            String specialName = "John @#$% Doe";
            String specialEmail = "john.doe+test@example.com";

            String token = jwtService.generateToken(TEST_USER_ID, specialEmail, specialName);

            assertEquals(TEST_USER_ID, jwtService.extractUserId(token));
            assertEquals(specialEmail, jwtService.extractEmail(token));
            assertEquals(specialName, jwtService.extractFullName(token));
        }
    }
}