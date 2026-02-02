package com.backendsyndicate.smashclub.payment.init;

import com.backendsyndicate.smashclub.common.util.Logging;
import com.backendsyndicate.smashclub.common.constant.PaymentMethodConstant;
import com.backendsyndicate.smashclub.common.constant.TransactionStatusConstant;
import com.backendsyndicate.smashclub.common.constant.TransactionTypeConstant;
import com.backendsyndicate.smashclub.common.constant.WalletConstant;

public class PaymentInitLoader {
    public static void load() {
        loadConstant();
    }

    private static void loadConstant() {
        Logging.printConsole("Loading all constant variables");

        PaymentMethodConstant.initLoad();
        TransactionStatusConstant.initLoad();
        TransactionTypeConstant.initLoad();
        WalletConstant.initLoad();
    }
}
