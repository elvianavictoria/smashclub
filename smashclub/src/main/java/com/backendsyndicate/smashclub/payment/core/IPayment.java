package com.backendsyndicate.smashclub.payment.core;

import com.backendsyndicate.smashclub.payment.dto.response.RespCreateTransactionDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

public interface IPayment {
    /*
    * Create => Create Transaction
    * Payment => Payment Transaction
    * Refund => Refund Transaction -> Perubahan status
    * */

    public RespCreateTransactionDTO createTransaction(String customerId, BigDecimal totalPrice, String referenceCode, int transactionType);
    public ResponseEntity<Object> paymentTransaction(String transactionCode, int paymentMethodId, HttpServletRequest request);
    public boolean refundTransaction(String transactionCode, String notes);

    public ResponseEntity<Object> paymentMethodList(HttpServletRequest request);
}
