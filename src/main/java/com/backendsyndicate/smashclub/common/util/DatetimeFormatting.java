package com.backendsyndicate.smashclub.common.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DatetimeFormatting {
    private static final String datetimeFormat = "yyyy-MM-dd HH:mm:ss";
    private static final String dateFormat = "yyyy-MM-dd";
    private static final String clockFormat = "HH:mm";

    public static String getDateFormat(LocalDate dt) {
        return dt.format(DateTimeFormatter.ofPattern(dateFormat));
    }

    public static String getDatetimeFormat(LocalDateTime dt) {
        return dt.format(DateTimeFormatter.ofPattern(datetimeFormat));
    }

    public static String getClockFormat(LocalTime t) {
        return t.format(DateTimeFormatter.ofPattern(clockFormat));
    }
}
