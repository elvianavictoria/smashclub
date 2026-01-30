package com.backendsyndicate.smashclub.payment.dto.request;

public class ReqPaymentTransactionDTO {
    private int paymentMethodId;

    public int getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(int paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }
}
