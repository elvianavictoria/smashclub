package com.backendsyndicate.smashclub.payment.constant;

import java.util.HashMap;
import java.util.Map;

public class TransactionTypeConstant {
    private static final Map<Integer, String> transactionType = new HashMap<>();

    public static void initLoad() {
        loadTransactionType();
    }

    private static void loadTransactionType() {
        transactionType.put(1, "Court Booking");
        transactionType.put(2, "E-commerce Shopping");
    }

    public static Map<Integer, String> getTransactionType() {
        return transactionType;
    }

    public static String getTransactionType(int type) {
        return transactionType.getOrDefault(type, "Unknown");
    }

    public static int getTransactionType(String typeName) {
        int result = -1;
        for( Map.Entry<Integer, String> entry : transactionType.entrySet() ) {
            if( entry.getValue().equalsIgnoreCase(typeName) ) result = entry.getKey();
        }

        return result;
    }
}
