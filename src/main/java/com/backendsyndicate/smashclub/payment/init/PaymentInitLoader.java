package com.backendsyndicate.smashclub.payment.init;

import com.backendsyndicate.smashclub.common.util.Logging;
import com.backendsyndicate.smashclub.common.constant.PaymentMethodConstant;
import com.backendsyndicate.smashclub.common.constant.TransactionConstant;
import com.backendsyndicate.smashclub.common.constant.TransactionTypeConstant;
import com.backendsyndicate.smashclub.common.constant.WalletConstant;
import org.springframework.stereotype.Component;

@Component
public class PaymentInitLoader {
    public static void load() {
        loadConstant();
    }

    private static void loadConstant() {
        Logging.printConsole("Loading payment constant variables");

        PaymentMethodConstant.initLoad();
        TransactionConstant.initLoad();
        TransactionTypeConstant.initLoad();
        WalletConstant.initLoad();
    }
}
