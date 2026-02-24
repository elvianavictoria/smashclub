package com.backendsyndicate.smashclub.common.security;

import com.backendsyndicate.smashclub.auth.AuthTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PasswordHasherTest extends AuthTestBase {

    @Autowired
    private PasswordHasher passwordHasher;

    private final String plainPassword = "MySecretPassword123!";
    private String hashedPassword;

    @BeforeEach
    void setUp() {
        hashedPassword = passwordHasher.hash(plainPassword);
    }

    @Nested
    @DisplayName("Hash Password Tests")
    class HashPasswordTests {

        @Test
        @DisplayName("hash should return BCrypt hash starting with $2a$")
        void hash_ShouldReturnBCryptHash() {
            assertTrue(hashedPassword.startsWith("$2a$"));
            assertEquals(60, hashedPassword.length()); // BCrypt hash always 60 chars
        }

        @Test
        @DisplayName("hash should generate different hashes for same password")
        void hash_ShouldGenerateDifferentHashes() {
            String hash1 = passwordHasher.hash(plainPassword);
            String hash2 = passwordHasher.hash(plainPassword);

            assertNotEquals(hash1, hash2); // Different salts
        }

        @Test
        @DisplayName("hash should handle empty password")
        void hash_WithEmptyPassword_ShouldReturnHash() {
            String hash = passwordHasher.hash("");
            assertNotNull(hash);
            assertTrue(hash.startsWith("$2a$"));
        }

        @Test
        @DisplayName("hash should handle null password")
        void hash_WithNullPassword_ShouldReturnHash() {
            // BCrypt can handle null (treats as empty string)
            String hash = passwordHasher.hash(null);
            assertNotNull(hash);
        }
    }

    @Nested
    @DisplayName("Verify Password Tests")
    class VerifyPasswordTests {

        @Test
        @DisplayName("verify should return true for correct password")
        void verify_WithCorrectPassword_ShouldReturnTrue() {
            assertTrue(passwordHasher.verify(plainPassword, hashedPassword));
        }

        @Test
        @DisplayName("verify should return false for incorrect password")
        void verify_WithIncorrectPassword_ShouldReturnFalse() {
            assertFalse(passwordHasher.verify("WrongPassword123", hashedPassword));
        }

        @Test
        @DisplayName("verify should return false for null password")
        void verify_WithNullPassword_ShouldReturnFalse() {
            assertFalse(passwordHasher.verify(null, hashedPassword));
        }

        @Test
        @DisplayName("verify should return false for empty password")
        void verify_WithEmptyPassword_ShouldReturnFalse() {
            assertFalse(passwordHasher.verify("", hashedPassword));
        }

        @Test
        @DisplayName("verify should return false for invalid hash")
        void verify_WithInvalidHash_ShouldReturnFalse() {
            assertFalse(passwordHasher.verify(plainPassword, "invalid-hash"));
        }
    }

    @Nested
    @DisplayName("isValidBcryptHash Tests")
    class IsValidBcryptHashTests {

        @Test
        @DisplayName("isValidBcryptHash should return true for valid BCrypt hash")
        void isValidBcryptHash_WithValidHash_ShouldReturnTrue() {
            assertTrue(passwordHasher.isValidBcryptHash(hashedPassword));
        }

        @Test
        @DisplayName("isValidBcryptHash should return false for invalid hash")
        void isValidBcryptHash_WithInvalidHash_ShouldReturnFalse() {
            assertFalse(passwordHasher.isValidBcryptHash("invalid"));
            assertFalse(passwordHasher.isValidBcryptHash("$2a$invalid"));
            assertFalse(passwordHasher.isValidBcryptHash(null));
        }

        @Test
        @DisplayName("isValidBcryptHash should accept all BCrypt variants")
        void isValidBcryptHash_ShouldAcceptAllVariants() {
            assertTrue(passwordHasher.isValidBcryptHash("$2a$10$N9qo8uLOickgx2ZMRZoMy.MrL5C7Vc5qX5Q5X5Q5X5Q5X5Q5X5Q5X"));
            assertTrue(passwordHasher.isValidBcryptHash("$2b$10$N9qo8uLOickgx2ZMRZoMy.MrL5C7Vc5qX5Q5X5Q5X5Q5X5Q5X5Q5X"));
            assertTrue(passwordHasher.isValidBcryptHash("$2y$10$N9qo8uLOickgx2ZMRZoMy.MrL5C7Vc5qX5Q5X5Q5X5Q5X5Q5X5Q5X"));
        }
    }
}