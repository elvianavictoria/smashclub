package com.backendsyndicate.smashclub.payment.service;

import com.backendsyndicate.smashclub.auth.model.User;
import com.backendsyndicate.smashclub.common.util.GlobalResponse;
import com.backendsyndicate.smashclub.common.util.Util;
import com.backendsyndicate.smashclub.payment.constant.PaymentMethodConstant;
import com.backendsyndicate.smashclub.payment.constant.TransactionConstant;
import com.backendsyndicate.smashclub.payment.constant.TransactionTypeConstant;
import com.backendsyndicate.smashclub.payment.core.IPayment;
import com.backendsyndicate.smashclub.payment.dto.response.RespCreateTransactionDTO;
import com.backendsyndicate.smashclub.payment.model.Transaction;
import com.backendsyndicate.smashclub.payment.model.TransactionLog;
import com.backendsyndicate.smashclub.payment.repo.TransactionRepo;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Module Code: PYMT
 */
@Service
@Transactional
public class PaymentService implements IPayment {
    private TransactionRepo transactionRepo;
    private ModelMapper modelMapper = new ModelMapper();

    private String generateErrorCode(String methodNo, String errorNo) {
        return "PYMT-" + methodNo + "E" + errorNo;
    }

    /**
     * Code: 01
     * Steps:
     * 1. Map to TransactionModel
     * 2. Save to Transaction Table
     * 3. Save to Transaction Log Table
     * 4. Set transaction status to unpaid (0)
     *
     * @param customerId
     * @param totalPrice
     * @param referenceCode
     * @param transactionType
     * @param request
     * @return
     */
    @Override
    public ResponseEntity<Object> createTransaction(Long customerId, BigDecimal totalPrice, String referenceCode, int transactionType, HttpServletRequest request) {
        try {
            String trxCode = generateTransactionCode();
            String trxLabel = "Transaksi " + trxCode + ": " + TransactionTypeConstant.getTransactionType(transactionType);

            Transaction trx = new Transaction();
            User user = new User();
            user.setId(customerId);

            trx.setUser(user);
            trx.setTransactionCode(trxCode);
            trx.setStatus((byte) TransactionConstant.getStatus("Menunggu Pembayaran"));
            trx.setTransactionLabel(trxLabel);
            trx.setTotalPrice(totalPrice);
            trx.setReferenceCode(referenceCode);
            trx.setTransactionType((byte) transactionType);

            logTransactionUpdate(trx, -1);

            RespCreateTransactionDTO response = modelMapper.map(trx, RespCreateTransactionDTO.class);

            return GlobalResponse.success("Get Transaction Code!", response, request);
        } catch(Exception e) {
            return GlobalResponse.failed("Failed to create transaction!", generateErrorCode("01", "010"), null, request);
        }
    }

    /**
     * Code: 02
     *
     * @param transactionCode
     * @param paymentMethodId
     * @param request
     * @return
     */
    @Override
    public ResponseEntity<Object> paymentTransaction(String transactionCode, int paymentMethodId, HttpServletRequest request) {
        return null;
    }

    /**
     * Code: 03
     *
     * @param transactionCode
     * @param notes
     * @param request
     * @return
     */
    @Override
    public ResponseEntity<Object> refundTransaction(String transactionCode, String notes, HttpServletRequest request) {
        return GlobalResponse.internalServerError("Error Refund", generateErrorCode("03", "10"), request);
    }

    /**
     * Code: 04
     *
     * @param request
     * @return
     */
    @Override
    public ResponseEntity<Object> paymentMethodList(HttpServletRequest request) {
        return GlobalResponse.success("Payment Method has been found!", PaymentMethodConstant.getPaymentMethods(), request);
    }

    private String generateTransactionCode() {
        LocalDate currentDt = LocalDate.now();
        String currentDtString = ("" + currentDt.getYear()).substring(2) + currentDt.getMonth() + currentDt.getDayOfMonth();
        String randomStr = Util.generateRandomString(4, false);

        long trxCounter = transactionRepo.countTodayTransaction();
        trxCounter += 1;
        String strCounter = "00" + trxCounter;

        return currentDtString + randomStr + strCounter.substring(strCounter.length() - 3);
    }

    private void logTransactionUpdate(Transaction transaction, int previousStatus) {
        TransactionLog trxLog = new TransactionLog();
        trxLog.setPreviousStatus(previousStatus);
        trxLog.setCurrentStatus(transaction.getStatus());
        trxLog.setTransaction(transaction);
    }
}
