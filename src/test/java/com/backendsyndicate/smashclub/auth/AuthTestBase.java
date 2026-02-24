package com.backendsyndicate.smashclub.auth;

import com.backendsyndicate.smashclub.auth.model.*;
import com.backendsyndicate.smashclub.common.constant.AuthenticationConstant;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.UUID;

@SpringBootTest
public abstract class AuthTestBase {

    protected static final String TEST_USER_ID = "test-user-id";
    protected static final String TEST_EMAIL = "test@example.com";
    protected static final String TEST_NEW_EMAIL = "newemail@example.com";
    protected static final String TEST_FULL_NAME = "Test User";
    protected static final String TEST_PASSWORD = "Password123";
    protected static final String TEST_OTP = "123456";
    protected static final String TEST_TOKEN = UUID.randomUUID().toString();

    protected PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
    protected String hashedPassword;

    @BeforeEach
    void setUpBase() {
        hashedPassword = passwordEncoder.encode(TEST_PASSWORD);
    }

    // ============ HELPER METHODS TO CREATE TEST ENTITIES ============

    protected User createActiveUser() {
        User user = new User();
        user.setId(TEST_USER_ID);
        user.setFullName(TEST_FULL_NAME);
        user.setEmail(TEST_EMAIL);
        user.setPasswordHash(hashedPassword);
        user.setStatus(AuthenticationConstant.ACTIVE);
        user.setFailedLoginAttempt(0);
        user.setCreatedDate(LocalDateTime.now());
        user.setUpdatedDate(LocalDateTime.now());
        return user;
    }

    protected User createPendingUser() {
        User user = createActiveUser();
        user.setStatus(AuthenticationConstant.PENDING);
        return user;
    }

    protected User createLockedUser() {
        User user = createActiveUser();
        user.setStatus(AuthenticationConstant.LOCKED);
        user.setFailedLoginAttempt(5);
        user.setLockedUntil(LocalDateTime.now().plusMinutes(30));
        return user;
    }

    protected Sessions createAccessTokenSession(User user, String token) {
        Sessions session = new Sessions();
        session.setId(UUID.randomUUID().toString());
        session.setUser(user);
        session.setSessionToken(token);
        session.setTokenType(AuthenticationConstant.TOKEN_TYPE_ACCESS);
        session.setExpiresAt(LocalDateTime.now().plusHours(24));
        session.setCreatedAt(LocalDateTime.now());
        session.setLastAccessedAt(LocalDateTime.now());
        return session;
    }

    protected Sessions createRefreshTokenSession(User user, String token) {
        Sessions session = createAccessTokenSession(user, token);
        session.setTokenType(AuthenticationConstant.TOKEN_TYPE_REFRESH);
        session.setExpiresAt(LocalDateTime.now().plusDays(7));
        return session;
    }

    protected EmailVerificationTokens createEmailVerificationToken(User user) {
        EmailVerificationTokens token = new EmailVerificationTokens();
        token.setId(UUID.randomUUID().toString());
        token.setUser(user);
        token.setToken(TEST_TOKEN);
        token.setExpiresAt(LocalDateTime.now().plusHours(24));
        token.setCreatedAt(LocalDateTime.now());
        return token;
    }

    protected PasswordResetTokens createPasswordResetToken(User user) {
        PasswordResetTokens token = new PasswordResetTokens();
        token.setId(UUID.randomUUID().toString());
        token.setUser(user);
        token.setToken(TEST_TOKEN);
        token.setExpiresAt(LocalDateTime.now().plusHours(1));
        token.setCreatedAt(LocalDateTime.now());
        return token;
    }

    protected LoginOtpTokens createLoginOtpToken(User user) {
        LoginOtpTokens token = new LoginOtpTokens();
        token.setId(UUID.randomUUID().toString());
        token.setUser(user);
        token.setOtpCode(TEST_OTP);
        token.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        token.setCreatedAt(LocalDateTime.now());
        return token;
    }

    protected EmailChangeToken createEmailChangeToken(User user, String newEmail) {
        EmailChangeToken token = EmailChangeToken.builder()
                .id(UUID.randomUUID().toString())
                .user(user)
                .token(TEST_TOKEN)
                .oldEmail(user.getEmail())
                .newEmail(newEmail)
                .expiresAt(LocalDateTime.now().plusHours(24))
                .createdAt(LocalDateTime.now())
                .build();
        return token;
    }
}