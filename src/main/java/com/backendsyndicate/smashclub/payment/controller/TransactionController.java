package com.backendsyndicate.smashclub.payment.controller;

import com.backendsyndicate.smashclub.common.security.JwtService;
import com.backendsyndicate.smashclub.common.util.GlobalResponse;
import com.backendsyndicate.smashclub.common.util.Logging;
import com.backendsyndicate.smashclub.payment.dto.request.ReqCreateTransactionDTO;
import com.backendsyndicate.smashclub.payment.dto.request.ReqPaymentTransactionDTO;
import com.backendsyndicate.smashclub.payment.dto.response.RespCreateTransactionDTO;
import com.backendsyndicate.smashclub.payment.dto.response.RespPaymentTransactionDTO;
import com.backendsyndicate.smashclub.payment.dto.response.RespCancelTransactionDTO;
import com.backendsyndicate.smashclub.payment.model.Transaction;
import com.backendsyndicate.smashclub.payment.service.TransactionService;
import com.backendsyndicate.smashclub.payment.service.helper.PaymentHelper;
import com.backendsyndicate.smashclub.payment.service.helper.TransactionHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("api/v1/transaction")
public class TransactionController {
//    @Autowired
//    private PaymentService paymentService;
    @Autowired
    private PaymentHelper paymentService;
    @Autowired
    private TransactionHelper transactionService;
    @Autowired
    private JwtService jwtService;

    @GetMapping
    public ResponseEntity<Object> transactionList(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam Integer page,
            @RequestParam Integer size,
            HttpServletRequest request
    ) {
        String accessToken = request.getHeader("Authorization");
        String userId = jwtService.extractUserId(accessToken.replaceAll("Bearer ", ""));
        Pageable pageable = PageRequest.of(page, size, Sort.by("CreatedAt").descending());
        return transactionService.findAll(userId, pageable, startDate, endDate, request);
    }

    @GetMapping("/{transactionCode}")
    public ResponseEntity<Object> transactionDetail(@PathVariable String transactionCode, HttpServletRequest request) {
        String accessToken = request.getHeader("Authorization");
        String userId = jwtService.extractUserId(accessToken.replaceAll("Bearer ", ""));

        return transactionService.findByCode(userId, transactionCode, request);
    }

    @PostMapping("/create")
    public ResponseEntity<Object> transactionOrder(@Valid @RequestBody ReqCreateTransactionDTO dto, HttpServletRequest request) {
        RespCreateTransactionDTO payment = paymentService.createTransaction(dto.getCustomerId(), dto.getTotalPrice(), dto.getReferenceCode(), dto.getTransactionType());
        return GlobalResponse.success("Successfully create transaction!", payment, request);
    }

    @PostMapping("/payment/{transactionCode}")
    public ResponseEntity<Object> transactionPayment(@PathVariable String transactionCode, HttpServletRequest request) {
        RespPaymentTransactionDTO payment = paymentService.paymentTransaction(transactionCode);
        return GlobalResponse.success("Successfully paid transaction!", payment, request);
    }

    @PostMapping("/cancel/{transactionCode}")
    public ResponseEntity<Object> transactionCancel(@PathVariable String transactionCode, @RequestBody String refundReason, HttpServletRequest request) {
        RespCancelTransactionDTO refund = paymentService.cancelTransaction(transactionCode, refundReason);
        return GlobalResponse.success("Successfully cancel transaction!", refund, request);
    }

    @PostMapping("cancel-by-reference/{referenceCode}")
    public ResponseEntity<Object> transactionCancelByReference(@PathVariable String referenceCode, @RequestBody String refundReason, HttpServletRequest request) {
        Transaction transaction = transactionService.getTransactionByReferenceCode(referenceCode);
        if( transaction == null ) {
            return GlobalResponse.failed("Failed to cancel transaction!", "PYMTCBR01E001", null, request);
        }

        RespCancelTransactionDTO cancel = paymentService.cancelTransaction(transaction.getTransactionCode(), refundReason);
        return GlobalResponse.success("Successfully cancel transaction!", cancel, request);
    }
}
