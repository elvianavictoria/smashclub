package com.backendsyndicate.smashclub.common.util;

import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class Util {
    private static Random rand = new Random();
    private static ObjectMapper objectMapper = new ObjectMapper();

    public static String generateRandomString(int length, boolean isUppercase) {
        String lettersAndNumbers = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        String result = "";
        int i = 0;
        char[] arr = lettersAndNumbers.toCharArray();

        while(i < length) {
            result += "" + arr[rand.nextInt(arr.length)];
            i++;
        }

        if( isUppercase ) result = result.toUpperCase();

        return result;
    }

    public static <T> T mapToModel(String json, Class<T> cls) {
        try {
            return objectMapper.readValue(json, cls);
        } catch(Exception e) {
            Logging.handleException("Util", "mapToModel(String json, Class<T> cls)", 33, "UTLMTME010", e.getMessage());
            return null;
        }
    }

    public static <T> T mapToModel(Map<String, Object> map, Class<T> cls) {
        try {
            String json = objectMapper.writeValueAsString(map);
            return mapToModel(json, cls);
        } catch(Exception e) {
            Logging.handleException("Util", "mapToModel(Map<String, Object> map, Class<T> cls)", 42, "UTLMTME020", e.getMessage());
            return null;
        }
    }

    public static <T> T mapToModel(Object object, Class<T> cls) {
        try {
            String json = objectMapper.writeValueAsString(object);
            return mapToModel(json, cls);
        } catch(Exception e) {
            Logging.handleException("Util", "mapToModel(Object object, Class<T> cls)", 52, "UTLMTME030", e.getMessage());
            return null;
        }
    }

    public static String formatCurrency(BigDecimal value) {
        // Using locale-specific currency format
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        return nf.format(value);
    }
}
