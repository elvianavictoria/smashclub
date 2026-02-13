package com.backendsyndicate.smashclub.payment.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ReqPaymentTransactionDTO {
    private int paymentMethodId;
}
