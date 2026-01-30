package com.backendsyndicate.smashclub.common.constant;

public class AuthenticationConstant {
    //user status
    public static final byte PENDING = 0;
    public static final byte ACTIVE = 1;
    public static final byte LOCKED = 2;

    public static final int MAX_FAILED_ATTEMPTS = 5;
    public static final int LOCK_DURATION_MINUTES = 30;
    public static final int OTP_EXPIRY_MINUTES = 10;
    public static final int VERIFICATION_TOKEN_EXPIRY_HOURS = 24;
    public static final int RESET_TOKEN_EXPIRY_HOURS = 1;
    public static final int ACCESS_TOKEN_EXPIRY_HOURS = 24; // From JwtService
    public static final int REFRESH_TOKEN_EXPIRY_DAYS = 7;
}