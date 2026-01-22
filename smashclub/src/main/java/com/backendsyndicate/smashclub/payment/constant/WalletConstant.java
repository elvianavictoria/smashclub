package com.backendsyndicate.smashclub.payment.constant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WalletConstant {
    private static final Map<Integer, String> usageStatus = new HashMap<>();

    public static void initLoad() {
        loadUsageStatus();
    }

    private static void loadUsageStatus() {
        usageStatus.put(0, "Berkurang");
        usageStatus.put(1, "Bertambah");
    }

    public static Map<Integer, String> getUsageStatus() {
        return usageStatus;
    }

    public static String getUsageStatus(int status) {
        return usageStatus.getOrDefault(status, "Unknown");
    }

    public static int getUsageStatus(String statusName) {
        int result = -1;
        for( Map.Entry<Integer, String> entry : usageStatus.entrySet() ) {
            if( entry.getValue().equalsIgnoreCase(statusName) ) result = entry.getKey();
        }

        return result;
    }
}
