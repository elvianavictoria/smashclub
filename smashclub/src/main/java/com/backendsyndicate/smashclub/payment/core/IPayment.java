package com.backendsyndicate.smashclub.payment.core;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface IPayment {
    /*
    * Create => Create Transaction
    * Payment => Payment Transaction
    * Refund => Refund Transaction -> Perubahan status
    * */

    public ResponseEntity<Object> createTransaction(Long customerId, double totalPrice, String referenceCode, byte transactionType, HttpServletRequest request);
    public ResponseEntity<Object> paymentTransaction(String transactionCode, byte paymentMethodId, HttpServletRequest request);
    public ResponseEntity<Object> refundTransaction(String transactionCode, String notes, HttpServletRequest request);

    public ResponseEntity<Object> paymentMethodList(HttpServletRequest request);
}
