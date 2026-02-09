package com.backendsyndicate.smashclub.common.constant;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class WalletConstant {
    private static Map<Integer, String> usageStatus = new HashMap<>();

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
}
