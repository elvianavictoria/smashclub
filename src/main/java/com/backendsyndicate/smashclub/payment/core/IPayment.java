package com.backendsyndicate.smashclub.payment.core;

import com.backendsyndicate.smashclub.payment.dto.response.RespCreateTransactionDTO;
import com.backendsyndicate.smashclub.payment.dto.response.RespRefundTransactionDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

public interface IPayment {
    /*
    * Create => Create Transaction
    * Payment => Payment Transaction
    * Refund => Refund Transaction -> Perubahan status
    * */

    public RespCreateTransactionDTO createTransaction(String customerId, BigDecimal totalPrice, String referenceCode, int transactionType, int paymentMethodId);
    public ResponseEntity<Object> paymentTransaction(String transactionCode, int paymentMethodId, HttpServletRequest request);
    public RespRefundTransactionDTO cancelTransaction(String transactionCode, String notes, byte isRefund);

    public ResponseEntity<Object> paymentMethodList(HttpServletRequest request);
}
