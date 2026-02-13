package com.backendsyndicate.smashclub.common.util;

import com.backendsyndicate.smashclub.common.constant.RegexConstant;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CustomRegex {
    private static boolean regexMatch(Pattern pattern, String str) {
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    public static String getAlphanumericRegex(int min, int max) {
        return RegexConstant.alphanumericRegex.replaceFirst("%d", min + "").replaceFirst("%d", max + "");
    }

    public static String getStringRegex(int min, int max) {
        return RegexConstant.stringRegex.replaceFirst("%d", min + "").replaceFirst("%d", max + "");
    }

    public static boolean alphanumericRegex(String str, int min, int max) {
        Pattern pattern = Pattern.compile(getAlphanumericRegex(min, max));
        return regexMatch(pattern, str);
    }

    public static boolean stringRegex(String str, int min, int max) {
        Pattern pattern = Pattern.compile(getStringRegex(min, max));
        return regexMatch(pattern, str);
    }
}
