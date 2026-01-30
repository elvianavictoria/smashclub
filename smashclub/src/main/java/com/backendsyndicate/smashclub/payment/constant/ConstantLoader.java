package com.backendsyndicate.smashclub.payment.constant;

import com.backendsyndicate.smashclub.common.util.Logging;

public class ConstantLoader {
    public static void load() {
        Logging.printConsole("Loading all constant variables");

        PaymentMethodConstant.initLoad();
        TransactionStatusConstant.initLoad();
        TransactionTypeConstant.initLoad();
        WalletConstant.initLoad();
    }
}
