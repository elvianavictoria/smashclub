package com.backendsyndicate.smashclub.util;

import com.backendsyndicate.smashclub.common.util.Logging;
import com.github.javafaker.Faker;

import java.util.Locale;
import java.util.regex.Matcher;

public class DataGenerator {
    private Faker faker = new Faker(new Locale("in_ID", "ID"));
    private boolean isValid = false;
    private Matcher matcher;
    private int intLoop = 0;

    public String getFullname() {
        try {
            return faker.name().fullName();
        } catch(Exception e) {
            Logging.handleException("DataGenerator", "getFullName", 17, "TEST-FAKER-001", e.getMessage());
            return "";
        }
    }

    public String getEmail() {
        try {
            return String.format("%s@gmail.com", String.join(" ", this.getFullname()));
        } catch(Exception e) {
            Logging.handleException("DataGenerator", "getEmail()", 17, "TEST-FAKER-002", e.getMessage());
            return "";
        }
    }
}
