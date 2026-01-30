package com.backendsyndicate.smashclub.payment.dto.response;

import com.backendsyndicate.smashclub.auth.model.User;

import java.math.BigDecimal;

public class RespPaymentTransactionDTO {
    private String transactionCode;
    private int transactionType;
    private BigDecimal totalPrice;
    private User user;

    public String getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }


    public int getTransactionType() { return transactionType; }

    public void setTransactionType(int transactionType) { this.transactionType = transactionType; }

    public BigDecimal getTotalPrice() { return totalPrice; }

    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }

    public User getUser() { return user; }

    public void setUser(User user) { this.user = user; }
}
