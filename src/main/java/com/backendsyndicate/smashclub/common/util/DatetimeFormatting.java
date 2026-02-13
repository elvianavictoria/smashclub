package com.backendsyndicate.smashclub.common.util;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DatetimeFormatting {
    private static final String datetimeFormat = "yyyy-MM-dd HH:mm:ss";
    private static final String clockFormat = "HH:mm";

    public static String getDatetimeFormat(LocalDateTime dt) {
        return dt.format(DateTimeFormatter.ofPattern(datetimeFormat));
    }

    public static String getClockFormat(LocalTime t) {
        return t.format(DateTimeFormatter.ofPattern(clockFormat));
    }
}
