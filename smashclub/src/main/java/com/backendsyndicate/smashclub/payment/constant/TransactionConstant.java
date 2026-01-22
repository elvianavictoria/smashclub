package com.backendsyndicate.smashclub.payment.constant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionConstant {
    private static final Map<Integer, String> status = new HashMap<>();
    private static final Map<Integer, String> refundStatus = new HashMap<>();
    private static final Map<Integer, List<Integer>> statusWorkflow = new HashMap<>();

    public static void initLoad() {
        loadStatus();
        loadRefundStatus();
        loadStatusWorkflow();
    }

    private static void loadStatus() {
        status.put(0, "Menunggu Pembayaran");
        status.put(1, "Sudah Dibayar");
        status.put(2, "Selesai");
        status.put(3, "Cancel");
        status.put(4, "Sudah Direfund");
        status.put(5, "Expired");
    }

    private static void loadRefundStatus() {
        refundStatus.put(0, "Dalam Pengajuan");
        refundStatus.put(1, "Disetujui");
        refundStatus.put(2, "Ditolak");
    }

    private static void loadStatusWorkflow() {
        statusWorkflow.put(0, List.of(1, 5));
        statusWorkflow.put(1, List.of(2, 3));
        statusWorkflow.put(2, List.of());
        statusWorkflow.put(3, List.of(4));
        statusWorkflow.put(4, List.of());
        statusWorkflow.put(5, List.of());
    }

    public static Map<Integer, String> getStatuses () {
        return status;
    }

    public static String getStatus(int value) {
        return status.getOrDefault(value, "Unknown");
    }

    public static int getStatus(String statusName) {
        int result = -1;
        for( Map.Entry<Integer, String> entry : status.entrySet() ) {
            if( entry.getValue().equalsIgnoreCase(statusName) ) result = entry.getKey();
        }

        return result;
    }

    public static Map<Integer, String> getRefundStatuses () {
        return refundStatus;
    }

    public static String getRefundStatus(int value) {
        return status.getOrDefault(value, "Unknown");
    }

    public static int getRefundStatus(String statusName) {
        int result = -1;
        for( Map.Entry<Integer, String> entry : refundStatus.entrySet() ) {
            if( entry.getValue().equalsIgnoreCase(statusName) ) result = entry.getKey();
        }

        return result;
    }

    public static Map<Integer, List<Integer>> getTransactionWorkflows () {
        return statusWorkflow;
    }

    public static boolean isStatusAllowed(int value) {
        return statusWorkflow.containsKey(value);
    }

    public static boolean isStatusAllowed(String statusName) {
        return statusWorkflow.containsValue(statusName);
    }
}
