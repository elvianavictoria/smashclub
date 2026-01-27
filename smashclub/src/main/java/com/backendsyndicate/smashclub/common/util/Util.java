package com.backendsyndicate.smashclub.common.util;

import java.util.Random;

public class Util {
    private static Random rand = new Random();

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
}
