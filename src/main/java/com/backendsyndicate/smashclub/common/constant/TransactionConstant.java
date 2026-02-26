package com.backendsyndicate.smashclub.common.constant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@Component
public class TransactionConstant {
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

    public static final String TRANSACTION_SERVICE_ERROR_LIST_UNAUTHORIZED = "TRX-01E001";
    public static final String TRANSACTION_SERVICE_ERROR_LIST_EMPTY = "TRX-01E002";
    public static final String TRANSACTION_SERVICE_ERROR_LIST_EXCEPTION = "TRX-01E010";
    public static final String TRANSACTION_SERVICE_ERROR_DETAIL_UNAUTHORIZED = "TRX-02E001";
    public static final String TRANSACTION_SERVICE_ERROR_DETAIL_CODE_REQUIRED = "TRX-02E002";
    public static final String TRANSACTION_SERVICE_ERROR_DETAIL_NOT_FOUND = "TRX-02E003";
    public static final String TRANSACTION_SERVICE_ERROR_DETAIL_EXCEPTION = "TRX-02E010";
    public static final String TRANSACTION_HELPER_ERROR_DETAIL_EXCEPTION = "TRX-12E010";

    public static final String WALLET_SERVICE_ERROR_BALANCE_USERID_REQUIRED = "WLLT-01E001";
    public static final String WALLET_SERVICE_ERROR_BALANCE_WALLET_NOT_FOUND = "WLLT-01E002";
    public static final String WALLET_SERVICE_ERROR_BALANCE_EXCEPTION = "WLLT-01E010";
    public static final String WALLET_SERVICE_ERROR_LOG_EMPTY = "WLLT-02E001";
    public static final String WALLET_SERVICE_ERROR_LOG_EXCEPTION = "WLLT-02E010";
    public static final String WALLET_SERVICE_ERROR_TOPUP_USERID_REQUIRED = "WLLT-03E001";
    public static final String WALLET_SERVICE_ERROR_TOPUP_TRANSACTION_FAILED = "WLLT-03E002";
    public static final String WALLET_SERVICE_ERROR_TOPUP_EXCEPTION = "WLLT-03E010";
    public static final String WALLET_SERVICE_ERROR_UPDATE_USERID_REQUIRED = "WLLT-04E001";
    public static final String WALLET_SERVICE_ERROR_UPDATE_REQUEST_INVALID = "WLLT-04E002";
    public static final String WALLET_SERVICE_ERROR_UPDATE_WALLET_NOT_FOUND = "WLLT-04E003";
    public static final String WALLET_SERVICE_ERROR_UPDATE_EXCEPTION = "WLLT-04E010";
    public static final String WALLET_SERVICE_ERROR_CREATE_USERID_REQUIRED = "WLLT-05E001";
    public static final String WALLET_SERVICE_ERROR_CREATE_WALLET_EXISTS = "WLLT-05E002";
    public static final String WALLET_SERVICE_ERROR_CREATE_EXCEPTION = "WLLT-05E010";

    public static final String PAYMENT_SERVICE_ERROR_CREATE_PARAM_REQUIRED = "PYMT-01E001";
    public static final String PAYMENT_SERVICE_ERROR_CREATE_USER_NOT_FOUND = "PYMT-01E002";
    public static final String PAYMENT_SERVICE_ERROR_CREATE_TRX_NOT_FOUND = "PYMT-01E003";
    public static final String PAYMENT_SERVICE_ERROR_CREATE_PG_FAILED = "PYMT-01E004";
    public static final String PAYMENT_SERVICE_ERROR_CREATE_EXCEPTION = "PYMT-01E010";
    public static final String PAYMENT_SERVICE_ERROR_PAYMENT_CODE_REQUIRED = "PYMT-02E001";
    public static final String PAYMENT_SERVICE_ERROR_PAYMENT_TRX_NOT_FOUND = "PYMT-02E002";
    public static final String PAYMENT_SERVICE_ERROR_PAYMENT_PAID = "PYMT-02E009";
    public static final String PAYMENT_SERVICE_ERROR_PAYMENT_EXCEPTION = "PYMT-02E010";
    public static final String PAYMENT_SERVICE_ERROR_CANCEL_TRX_NOT_FOUND = "PYMT-03E001";
    public static final String PAYMENT_SERVICE_ERROR_CANCEL_WALLET_TOPUP = "PYMT-03E002";
    public static final String PAYMENT_SERVICE_ERROR_CANCEL_CANCELLED = "PYMT-03E003";
    public static final String PAYMENT_SERVICE_ERROR_CANCEL_EXCEPTION = "PYMT-03E010";
    public static final String PAYMENT_SERVICE_ERROR_EXPIRE_TRX_NOT_FOUND = "PYMT-04E001";
    public static final String PAYMENT_SERVICE_ERROR_EXPIRE_STATUS_NOT_ALLOWED = "PYMT-04E002";
    public static final String PAYMENT_SERVICE_ERROR_EXPIRE_EXCEPTION = "PYMT-04E010";
    public static final String PAYMENT_SERVICE_ERROR_UPDATE_STATUS_REFERENCE_CODE_REQUIRED = "PYMT-05E001";
    public static final String PAYMENT_SERVICE_ERROR_UPDATE_STATUS_TRX_NOT_FOUND = "PYMT-05E002";
    public static final String PAYMENT_SERVICE_ERROR_UPDATE_STATUS_NOT_ALLOWED = "PYMT-05E003";
    public static final String PAYMENT_SERVICE_ERROR_UPDATE_STATUS_EXCEPTION = "PYMT-05E010";

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
