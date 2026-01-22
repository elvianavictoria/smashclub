package com.backendsyndicate.smashclub.payment.constant;

import java.util.HashMap;
import java.util.Map;

public class PaymentMethodConstant {
    private static final Map<Integer, Map<String, Object>> paymentMethodCategories = new HashMap<>();
    private static final Map<Integer, Map<String, Object>> paymentMethod = new HashMap<>();

    public static void initLoad() {
        loadPaymentMethodCategories();
        loadPaymentMethods();
    }

    private static void loadPaymentMethodCategories() {
        paymentMethodCategories.put(1, Map.of("name", "Virtual Account"));
        paymentMethodCategories.put(2, Map.of("name", "E-wallet"));
        paymentMethodCategories.put(3, Map.of("name", "QRIS"));
    }

    private static void loadPaymentMethods() {
        paymentMethod.put(1, Map.of(
                "name", "BCA Virtual Account",
                "img", "",
                "categoryId", 1
        ));

        paymentMethod.put(2, Map.of(
            "name", "Mandiri Virtual Account",
                "img", "",
                "categoryId", 1
        ));

        paymentMethod.put(3, Map.of(
                "name", "BRI Virtual Account",
                "img", "",
                "categoryId", 1
        ));

        paymentMethod.put(4, Map.of(
                "name", "BNI Virtual Account",
                "img", "",
                "categoryId", 1
        ));

        paymentMethod.put(5, Map.of(
                "name", "DANA",
                "img", "",
                "categoryId", 2
        ));

        paymentMethod.put(6, Map.of(
                "name", "Shopeepay",
                "img", "",
                "categoryId", 2
        ));

        paymentMethod.put(7, Map.of(
                "name", "OVO",
                "img", "",
                "categoryId", 2
        ));

        paymentMethod.put(8, Map.of(
                "name", "QRIS DANA",
                "img", "",
                "categoryId", 3
        ));

        paymentMethod.put(9, Map.of(
                "name", "QRIS Shopeepay",
                "img", "",
                "categoryId", 3
        ));
    }

    public static Map<Integer, Map<String, Object>> getPaymentMethodCategories() {
        return paymentMethodCategories;
    }

    public static Map<Integer, Map<String, Object>> getPaymentMethods() {
        return paymentMethod;
    }

    public static Object getPaymentMethodCategory(int payMethodCategoryId) {
        return paymentMethodCategories.get(payMethodCategoryId);
    }

    public static Object getPaymentMethodCategory(int payMethodCategoryId, String attr) {
        return paymentMethodCategories.get(payMethodCategoryId).get(attr);
    }

    public static int getPaymentMethodCategory(String name) {
        int result = -1;
        for( Map.Entry<Integer, Map<String, Object>> entry : paymentMethodCategories.entrySet() ) {
            String categoryName = (String) entry.getValue().get("name");
            if( categoryName.equalsIgnoreCase(name) ) result = entry.getKey();
        }

        return result;
    }

    public static Object getPaymentMethod(int payMethodId) {
        return paymentMethod.get(payMethodId);
    }

    public static Object getPaymentMethod(int payMethodId, String attr) {
        return paymentMethod.get(payMethodId).get(attr);
    }

    public static int getPaymentMethod(String name) {
        int result = -1;
        for( Map.Entry<Integer, Map<String, Object>> entry : paymentMethodCategories.entrySet() ) {
            String payMethodName = (String) entry.getValue().get("name");
            if( payMethodName.equalsIgnoreCase(name) ) result = entry.getKey();
        }

        return result;
    }
}
