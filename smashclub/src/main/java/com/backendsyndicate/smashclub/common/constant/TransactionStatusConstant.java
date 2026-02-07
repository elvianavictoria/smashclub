package com.backendsyndicate.smashclub.common.constant;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@Component
public class TransactionStatusConstant {
    private static final Map<Integer, String> status = new HashMap<>();
    private static final Map<Integer, String> refundStatus = new HashMap<>();
    private static final Map<Integer, List<Integer>> statusWorkflow = new HashMap<>();

    public static final int PAYMENT_UNPAID = 0;
    public static final int PAYMENT_PAID = 1;
    public static final int PAYMENT_COMPLETED = 2;
    public static final int PAYMENT_CANCELLED = 3;
    public static final int PAYMENT_REFUNDED = 4;
    public static final int PAYMENT_EXPIRED = 5;

    public static final int REFUND_REQUESTED = 0;
    public static final int REFUND_APPROVED = 1;
    public static final int REFUND_REJECTED = 2;

    public static void initLoad() {
        loadStatus();
        loadRefundStatus();
        loadStatusWorkflow();
    }

    private static void loadStatus() {
        status.put(PAYMENT_UNPAID, "Menunggu Pembayaran");
        status.put(PAYMENT_PAID, "Sudah Dibayar");
        status.put(PAYMENT_COMPLETED, "Selesai");
        status.put(PAYMENT_CANCELLED, "Cancel");
        status.put(PAYMENT_REFUNDED, "Sudah Direfund");
        status.put(PAYMENT_EXPIRED, "Expired");
    }

    private static void loadRefundStatus() {
        refundStatus.put(REFUND_REQUESTED, "Dalam Pengajuan");
        refundStatus.put(REFUND_APPROVED, "Disetujui");
        refundStatus.put(REFUND_REJECTED, "Ditolak");
    }

    private static void loadStatusWorkflow() {
        statusWorkflow.put(PAYMENT_UNPAID, List.of(PAYMENT_PAID, PAYMENT_EXPIRED));
        statusWorkflow.put(PAYMENT_PAID, List.of(PAYMENT_COMPLETED, PAYMENT_CANCELLED));
        statusWorkflow.put(PAYMENT_COMPLETED, List.of());
        statusWorkflow.put(PAYMENT_CANCELLED, List.of(PAYMENT_REFUNDED));
        statusWorkflow.put(PAYMENT_REFUNDED, List.of());
        statusWorkflow.put(PAYMENT_EXPIRED, List.of());
    }

    public static Map<Integer, String> getStatuses () {
        return status;
    }

    public static String getStatus(int value) {
        return status.getOrDefault(value, "Unknown");
    }

    public static Map<Integer, String> getRefundStatuses () {
        return refundStatus;
    }

    public static String getRefundStatus(int value) {
        return status.getOrDefault(value, "Unknown");
    }

    public static Map<Integer, List<Integer>> getTransactionWorkflows () {
        return statusWorkflow;
    }

    public static boolean isStatusAllowed(int previousValue, int currentValue) {
        return statusWorkflow.containsKey(previousValue) && statusWorkflow.get(previousValue).contains(currentValue);
    }
}
