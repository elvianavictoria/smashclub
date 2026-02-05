package com.backendsyndicate.smashclub.common.constant;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

//@Component
public class PaymentMethodConstant {
    private static final Map<Integer, Map<String, Object>> paymentMethodCategories = new HashMap<>();
    private static final Map<Integer, Map<String, Object>> paymentMethod = new HashMap<>();
    
    public static final int CATEGORY_VIRTUAL_ACCOUNT = 1;
    public static final int CATEGORY_EWALLET = 2;
    public static final int CATEGORY_QRIS = 3;
    
    public static final int VA_BCA = 1;
    public static final int VA_MANDIRI = 2;
    public static final int VA_BRI = 3;
    public static final int VA_BNI = 4;
    public static final int EW_DANA = 5;
    public static final int EW_SHOPEEPAY = 6;
    public static final int EW_OVO = 7;
    public static final int QRIS_DANA = 8;
    public static final int QRIS_SHOPEEPAY = 9;

    public static void initLoad() {
        loadPaymentMethodCategories();
        loadPaymentMethods();
    }

    private static void loadPaymentMethodCategories() {
        paymentMethodCategories.put(CATEGORY_VIRTUAL_ACCOUNT, Map.of("name", "Virtual Account"));
        paymentMethodCategories.put(CATEGORY_EWALLET, Map.of("name", "E-wallet"));
        paymentMethodCategories.put(CATEGORY_QRIS, Map.of("name", "QRIS"));
    }

    private static void loadPaymentMethods() {
        paymentMethod.put(VA_BCA, Map.of(
                "name", "BCA Virtual Account",
                "img", "",
                "categoryId", CATEGORY_VIRTUAL_ACCOUNT
        ));

        paymentMethod.put(VA_MANDIRI, Map.of(
            "name", "Mandiri Virtual Account",
                "img", "",
                "categoryId", CATEGORY_VIRTUAL_ACCOUNT
        ));

        paymentMethod.put(VA_BRI, Map.of(
                "name", "BRI Virtual Account",
                "img", "",
                "categoryId", CATEGORY_VIRTUAL_ACCOUNT
        ));

        paymentMethod.put(VA_BNI, Map.of(
                "name", "BNI Virtual Account",
                "img", "",
                "categoryId", CATEGORY_VIRTUAL_ACCOUNT
        ));

        paymentMethod.put(EW_DANA, Map.of(
                "name", "DANA",
                "img", "",
                "categoryId", CATEGORY_EWALLET
        ));

        paymentMethod.put(EW_SHOPEEPAY, Map.of(
                "name", "Shopeepay",
                "img", "",
                "categoryId", CATEGORY_EWALLET
        ));

        paymentMethod.put(EW_OVO, Map.of(
                "name", "OVO",
                "img", "",
                "categoryId", CATEGORY_EWALLET
        ));

        paymentMethod.put(QRIS_DANA, Map.of(
                "name", "QRIS DANA",
                "img", "",
                "categoryId", CATEGORY_QRIS
        ));

        paymentMethod.put(QRIS_SHOPEEPAY, Map.of(
                "name", "QRIS Shopeepay",
                "img", "",
                "categoryId", CATEGORY_QRIS
        ));
    }

    public static Map<Integer, Map<String, Object>> getPaymentMethodCategories() {
        return paymentMethodCategories;
    }

    public static Map<Integer, Map<String, Object>> getPaymentMethods() {
        return paymentMethod;
    }

    public static Map<String, Object> getPaymentMethodCategory(int payMethodCategoryId) {
        return paymentMethodCategories.get(payMethodCategoryId);
    }

    public static Object getPaymentMethodCategory(int payMethodCategoryId, String attr) {
        return paymentMethodCategories.get(payMethodCategoryId).get(attr);
    }

    public static Map<String, Object> getPaymentMethod(int payMethodId) {
        return paymentMethod.get(payMethodId);
    }

    public static Object getPaymentMethod(int payMethodId, String attr) {
        return paymentMethod.get(payMethodId).get(attr);
    }
}
