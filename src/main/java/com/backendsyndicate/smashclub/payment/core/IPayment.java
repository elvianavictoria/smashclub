package com.backendsyndicate.smashclub.payment.core;

import com.backendsyndicate.smashclub.payment.dto.response.RespCreateTransactionDTO;
import com.backendsyndicate.smashclub.payment.dto.response.RespExpireTransactionDTO;
import com.backendsyndicate.smashclub.payment.dto.response.RespPaymentTransactionDTO;
import com.backendsyndicate.smashclub.payment.dto.response.RespCancelTransactionDTO;
import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;

public interface IPayment {
    /*
    * Create => Create Transaction
    * Payment => Payment Transaction
    * Refund => Refund Transaction -> Perubahan status
    * */

    public RespCreateTransactionDTO createTransaction(String customerId, BigDecimal totalPrice, String referenceCode, int transactionType);
    public RespPaymentTransactionDTO paymentTransaction(String transactionCode, HttpServletRequest request);
    public RespCancelTransactionDTO cancelTransaction(String transactionCode, String notes);
    public RespExpireTransactionDTO expireTransaction(String transactionCode);
}
