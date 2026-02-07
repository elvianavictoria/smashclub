package com.backendsyndicate.smashclub.common.util;

import tools.jackson.databind.ObjectMapper;

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
            Logging.handleException("Util", "mapToModel(String json, Class<T> cls)", 29, "UTLMTME010", e.getMessage());
            return null;
        }
    }
}
