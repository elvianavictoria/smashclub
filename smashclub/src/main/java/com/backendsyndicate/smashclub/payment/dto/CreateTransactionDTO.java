package com.backendsyndicate.smashclub.payment.dto;


public class CreateTransactionDTO {
    private long customerId;
    private double totalPrice;
    private String referenceCode;
    private byte transactionType;
    private byte paymentMethodId;

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getReferenceCode() {
        return referenceCode;
    }

    public void setReferenceCode(String referenceCode) {
        this.referenceCode = referenceCode;
    }

    public byte getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(byte transactionType) {
        this.transactionType = transactionType;
    }

    public byte getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(byte paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }
}
