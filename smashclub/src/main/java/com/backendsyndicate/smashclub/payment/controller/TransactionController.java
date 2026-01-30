package com.backendsyndicate.smashclub.payment.controller;

import com.backendsyndicate.smashclub.common.util.Logging;
import com.backendsyndicate.smashclub.payment.service.PaymentService;
import com.backendsyndicate.smashclub.payment.service.TransactionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("transaction")
public class TransactionController {
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private TransactionService transactionService;

    @GetMapping
    public ResponseEntity<Object> transactionList(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam Integer page,
            @RequestParam Integer size,
            HttpServletRequest request
    ) {
        Logging.printConsole("Test Call Trx List!");
        Pageable pageable = PageRequest.of(page, size, Sort.by("CreatedAt").descending());
        return transactionService.findAll(pageable, startDate, endDate, request);
    }

    @GetMapping("/{transactionCode}")
    public ResponseEntity<Object> transactionDetail(@PathVariable String transactionCode, HttpServletRequest request) {
        return transactionService.findByCode(transactionCode, request);
    }

//    @PostMapping("/create")
//    public ResponseEntity<Object> transactionOrder(@Valid @RequestBody ReqCreateTransactionDTO dto, HttpServletRequest request) {
//        return paymentService.createTransaction(dto.getCustomerId(), dto.getTotalPrice(), dto.getReferenceCode(), dto.getTransactionType());
//    }

    @PostMapping("/payment/{transactionCode}")
    public ResponseEntity<Object> transactionPayment(@PathVariable String transactionCode, @RequestBody byte paymentMethodId, HttpServletRequest request) {
        return paymentService.paymentTransaction(transactionCode, paymentMethodId, request);
    }

//    @PostMapping("/refund/{transactionCode}")
//    public ResponseEntity<Object> transactionRefund(@PathVariable String transactionCode, @RequestBody String notes, HttpServletRequest request) {
//        return paymentService.refundTransaction(transactionCode, notes);
//    }

    @GetMapping("/payment-method")
    public ResponseEntity<Object> paymentMethodList(HttpServletRequest request) {
        return paymentService.paymentMethodList(request);
    }
}
