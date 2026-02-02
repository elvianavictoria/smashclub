package com.backendsyndicate.smashclub.common.constant;

import java.util.HashMap;
import java.util.Map;

public class TransactionTypeConstant {
    private static final Map<Integer, String> transactionType = new HashMap<>();
    public static final int COURT_BOOKING = 1;
    public static final int ECOMMERCE_SHOPPING = 2;
    public static final int WALLET_TOPUP = 3;

    public static void initLoad() {
        loadTransactionType();
    }

    private static void loadTransactionType() {
        transactionType.put(COURT_BOOKING, "Court Booking");
        transactionType.put(ECOMMERCE_SHOPPING, "E-commerce Shopping");
        transactionType.put(WALLET_TOPUP, "Wallet Topup");
    }

    public static Map<Integer, String> getTransactionType() {
        return transactionType;
    }

    public static String getTransactionType(int type) {
        return transactionType.getOrDefault(type, "Unknown");
    }
}
