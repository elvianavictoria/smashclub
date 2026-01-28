package com.backendsyndicate.smashclub.payment.dto.response;

import java.math.BigDecimal;

public class RespUpdateBalanceDTO {
    private BigDecimal previousBalance;
    private BigDecimal currentBalance;
    private BigDecimal balanceDiff;

    public BigDecimal getPreviousBalance() {
        return previousBalance;
    }

    public void setPreviousBalance(BigDecimal previousBalance) {
        this.previousBalance = previousBalance;
    }

    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(BigDecimal currentBalance) {
        this.currentBalance = currentBalance;
    }

    public BigDecimal getBalanceDiff() {
        return balanceDiff;
    }

    public void setBalanceDiff(BigDecimal balanceDiff) {
        this.balanceDiff = balanceDiff;
    }
}
