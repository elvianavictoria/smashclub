package com.backendsyndicate.smashclub.payment.dto.request;

import java.math.BigDecimal;

public class ReqUpdateBalanceDTO {
    private boolean isAddition;
    private BigDecimal value;

    public boolean isAddition() {
        return isAddition;
    }

    public void setAddition(boolean addition) {
        isAddition = addition;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }
}
