package com.backendsyndicate.smashclub.payment.service;

import com.backendsyndicate.smashclub.common.util.GlobalResponse;
import com.backendsyndicate.smashclub.payment.core.IPayment;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PaymentService implements IPayment {
    @Override
    public ResponseEntity<Object> createTransaction(Long customerId, double totalPrice, String referenceCode, byte transactionType, HttpServletRequest request) {
        return null;
    }

    @Override
    public ResponseEntity<Object> paymentTransaction(String transactionCode, byte paymentMethodId, HttpServletRequest request) {
        return null;
    }

    @Override
    public ResponseEntity<Object> refundTransaction(String transactionCode, String notes, HttpServletRequest request) {
        return null;
    }

    @Override
    public ResponseEntity<Object> paymentMethodList(HttpServletRequest request) {
        return GlobalResponse.success("Payment Method has been found!", List.of(), request);
    }
}
