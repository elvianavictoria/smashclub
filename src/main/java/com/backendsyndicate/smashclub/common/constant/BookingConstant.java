package com.backendsyndicate.smashclub.common.constant;

public class BookingConstant {

    // ============ RESOURCE STATUS (Court, Coach, Equipment) ============
    public static final byte RESOURCE_INACTIVE = 0;  // Tidak bisa dipesan (maintenance/cuti)
    public static final byte RESOURCE_ACTIVE = 1;    // Bisa dipesan

    // ============ BOOKING STATUS ============
    public static final byte BOOKING_CANCELLED = 0;
    public static final byte BOOKING_PENDING = 1;
    public static final byte BOOKING_CONFIRMED = 2;
    public static final byte BOOKING_ONGOING = 3;
    public static final byte BOOKING_COMPLETED = 4;

    // ============ BUSINESS RULES ============
    public static final double DEFAULT_COURT_PRICE_PER_HOUR = 100000.00;
    public static final int MIN_BOOKING_DURATION_HOURS = 1;
    public static final int MAX_BOOKING_DURATION_HOURS = 6;
    public static final int MAX_ADVANCE_BOOKING_DAYS = 30;
    public static final int MIN_CANCELLATION_HOURS = 24;
    public static final int OPEN_TIME_HOUR = 6;
    public static final int CLOSE_TIME_HOUR = 22;
    public static final int DEFAULT_PAGE_SIZE = 10;

    // ============ ERROR CODES ============
    public static final String ERROR_BOOKING_NOT_FOUND = "BOOK_001";
    public static final String ERROR_COURT_NOT_FOUND = "BOOK_002";
    public static final String ERROR_COACH_NOT_FOUND = "BOOK_003";
    public static final String ERROR_EQUIPMENT_NOT_FOUND = "BOOK_004";
    public static final String ERROR_COURT_NOT_AVAILABLE = "BOOK_005";
    public static final String ERROR_COACH_NOT_AVAILABLE = "BOOK_006";
    public static final String ERROR_EQUIPMENT_OUT_OF_STOCK = "BOOK_007";
    public static final String ERROR_INVALID_BOOKING_TIME = "BOOK_008";
    public static final String ERROR_BOOKING_CANCELLATION_NOT_ALLOWED = "BOOK_009";
    public static final String ERROR_USER_NOT_AUTHORIZED = "BOOK_010";
    public static final String ERROR_INVALID_DURATION = "BOOK_011";
    public static final String ERROR_INVALID_DATE_TIME = "BOOK_012";
    public static final String ERROR_BOOKING_DATE_PAST = "BOOK_013";
    public static final String ERROR_BOOKING_TOO_FAR_ADVANCE = "BOOK_014";
    public static final String ERROR_START_TIME_AFTER_END = "BOOK_015";
    public static final String ERROR_OUTSIDE_BUSINESS_HOURS = "BOOK_016";
    public static final String ERROR_COURT_ALREADY_BOOKED = "BOOK_017";
    public static final String ERROR_COACH_ALREADY_BOOKED = "BOOK_018";
    public static final String ERROR_MAX_DURATION_EXCEEDED = "BOOK_019";
    public static final String ERROR_MIN_DURATION_NOT_MET = "BOOK_020";
    public static final String ERROR_INVALID_STATUS_TRANSITION = "BOOK_021";
    public static final String ERROR_COACH_MAX_ONE = "BOOK_022";

    // ============ EQUIPMENT CATEGORIES ============
    public static final String CATEGORY_RACKET = "Racket";
    public static final String CATEGORY_BALL = "Ball";
    public static final String CATEGORY_ACCESSORIES = "Accessories";

    // ============ FORMATS ============
    public static final String TIME_SLOT_FORMAT = "HH:mm:ss";
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    // ============ HELPER METHODS ============
    public static boolean isResourceActive(byte status) {
        return status == RESOURCE_ACTIVE;
    }

    public static boolean isResourceInactive(byte status) {
        return status == RESOURCE_INACTIVE;
    }

    // ============ HELPER METHODS ============
    public static boolean isBookingActive(byte status) {
        return status == BOOKING_PENDING ||
                status == BOOKING_CONFIRMED ||
                status == BOOKING_ONGOING;
    }

    public static boolean isBookingCancellable(byte status) {
        return status == BOOKING_PENDING || status == BOOKING_CONFIRMED;
    }

    public static String getBookingStatusDescription(byte status) {
        switch (status) {
            case BOOKING_CANCELLED: return "CANCELLED";
            case BOOKING_PENDING: return "PENDING";
            case BOOKING_CONFIRMED: return "CONFIRMED";
            case BOOKING_ONGOING: return "ONGOING";
            case BOOKING_COMPLETED: return "COMPLETED";
            default: return "UNKNOWN";
        }
    }
}