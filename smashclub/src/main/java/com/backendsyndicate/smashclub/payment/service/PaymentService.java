package com.backendsyndicate.smashclub.payment.service;

import com.backendsyndicate.smashclub.common.util.GlobalResponse;
import com.backendsyndicate.smashclub.payment.core.IPayment;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Module Code: PYMT
 */
@Service
@Transactional
public class PaymentService implements IPayment {
    // Code: 01
    @Override
    public ResponseEntity<Object> createTransaction(Long customerId, double totalPrice, String referenceCode, byte transactionType, HttpServletRequest request) {
        return null;
    }

    // Code: 02
    @Override
    public ResponseEntity<Object> paymentTransaction(String transactionCode, byte paymentMethodId, HttpServletRequest request) {
        return null;
    }

    /**
     * Module mana?
     * Method mana?
     * Errornya apa?
     *
     * Code: 03
     *
     * @param transactionCode
     * @param notes
     * @param request
     * @return
     */
    @Override
    public ResponseEntity<Object> refundTransaction(String transactionCode, String notes, HttpServletRequest request) {
        return GlobalResponse.internalServerError("Error Refund", "PYMT-RFND-E01", request);
    }

    @Override
    public ResponseEntity<Object> paymentMethodList(HttpServletRequest request) {
        return GlobalResponse.success("Payment Method has been found!", List.of(), request);
    }
}
