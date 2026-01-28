package com.backendsyndicate.smashclub.payment.core;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

public interface IPayment {
    /*
    * Create => Create Transaction
    * Payment => Payment Transaction
    * Refund => Refund Transaction -> Perubahan status
    * */

    public ResponseEntity<Object> createTransaction(String customerId, BigDecimal totalPrice, String referenceCode, int transactionType, HttpServletRequest request);
    public ResponseEntity<Object> paymentTransaction(String transactionCode, int paymentMethodId, HttpServletRequest request);
    public ResponseEntity<Object> refundTransaction(String transactionCode, String notes, HttpServletRequest request);

    public ResponseEntity<Object> paymentMethodList(HttpServletRequest request);
}
