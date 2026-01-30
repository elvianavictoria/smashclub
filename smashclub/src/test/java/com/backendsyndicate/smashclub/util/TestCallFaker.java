package com.backendsyndicate.smashclub.util;

import com.backendsyndicate.smashclub.common.util.Logging;

public class TestCallFaker {
    public static void main(String[] args) {
        DataGenerator d = new DataGenerator();
        int i;
        for(i = 1; i <= 1000; i++) {
            System.out.println(i + ": " + d.getFullname());
        }
    }
}
