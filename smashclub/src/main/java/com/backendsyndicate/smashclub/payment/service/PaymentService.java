package com.backendsyndicate.smashclub.payment.service;

import com.backendsyndicate.smashclub.auth.model.User;
import com.backendsyndicate.smashclub.common.util.GlobalResponse;
import com.backendsyndicate.smashclub.common.util.Logging;
import com.backendsyndicate.smashclub.common.util.Util;
import com.backendsyndicate.smashclub.common.constant.PaymentMethodConstant;
import com.backendsyndicate.smashclub.common.constant.TransactionStatusConstant;
import com.backendsyndicate.smashclub.common.constant.TransactionTypeConstant;
import com.backendsyndicate.smashclub.payment.core.IPayment;
import com.backendsyndicate.smashclub.payment.dto.response.RespCreateTransactionDTO;
import com.backendsyndicate.smashclub.payment.dto.response.RespPaymentTransactionDTO;
import com.backendsyndicate.smashclub.payment.model.RefundRequest;
import com.backendsyndicate.smashclub.payment.model.Transaction;
import com.backendsyndicate.smashclub.payment.model.TransactionLog;
import com.backendsyndicate.smashclub.payment.repo.RefundRequestRepo;
import com.backendsyndicate.smashclub.payment.repo.TransactionLogRepo;
import com.backendsyndicate.smashclub.payment.repo.TransactionRepo;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Module Code: PYMT
 */
@Service
@Transactional
public class PaymentService implements IPayment {
    @Autowired
    private TransactionRepo transactionRepo;
    @Autowired
    private TransactionLogRepo transactionLogRepo;
    @Autowired
    private RefundRequestRepo refundRequestRepo;


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
     * (-) Controller
     *
     * @param customerId
     * @param totalPrice
     * @param referenceCode
     * @param transactionType
     * @return
     */
    @Override
    public RespCreateTransactionDTO createTransaction(String customerId, BigDecimal totalPrice, String referenceCode, int transactionType) {
        RespCreateTransactionDTO response = new RespCreateTransactionDTO();

        try {
            String trxCode = generateTransactionCode();
            String trxLabel = "Transaksi " + trxCode + ": " + TransactionTypeConstant.getTransactionType(transactionType);

            Transaction trx = new Transaction();
            User user = new User();
            user.setId(customerId);

            trx.setUser(user);
            trx.setTransactionCode(trxCode);
            trx.setStatus((byte) TransactionStatusConstant.PAYMENT_UNPAID);
            trx.setTransactionLabel(trxLabel);
            trx.setTotalPrice(totalPrice);
            trx.setReferenceCode(referenceCode);
            trx.setTransactionType((byte) transactionType);

            logTransactionUpdate(trx, -1);

            response = modelMapper.map(trx, RespCreateTransactionDTO.class);

//            return GlobalResponse.success("Get Transaction Code!", response, request);
        } catch(Exception e) {
            Logging.handleException("PaymentService", "createTransaction", 65, generateErrorCode("01", "010"), e.getMessage());
//            return GlobalResponse.failed("Failed to create transaction!", generateErrorCode("01", "010"), null, request);
        }

        return response;
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
        if( transactionCode == null ) {
            return GlobalResponse.failed("Transaction code is required!", generateErrorCode("02", "001"), null, request);
        }

        Transaction trx = null;
        RespPaymentTransactionDTO response = null;

        try {
            Optional<Transaction> optionalTrx = transactionRepo.findByTransactionCode(transactionCode);
            if( optionalTrx.isEmpty() ) {
                return GlobalResponse.failed("Transaction not found!", generateErrorCode("02", "002"), null, request);
            }

            trx = optionalTrx.get();
            byte previousStatus = trx.getStatus();
            if( TransactionStatusConstant.isStatusAllowed(previousStatus, TransactionStatusConstant.PAYMENT_PAID) ) {
                trx.setStatus((byte) TransactionStatusConstant.PAYMENT_PAID);
                logTransactionUpdate(trx, previousStatus);

                response = modelMapper.map(trx, RespPaymentTransactionDTO.class);
            } else {
                return GlobalResponse.failed("This transaction has been paid!", generateErrorCode("02", "009"), null, request);
            }
        } catch(Exception e) {
            return GlobalResponse.failed("Failed to process payment transaction!", generateErrorCode("02", "010"), null, request);
        }

        return GlobalResponse.success("Successfully paid the transaction!", response, request);
    }

    /**
     * Code: 03
     * Desc: Refund Request Procedure
     * 1. Check if transaction exists
     * 2. Check status
     * 3. Update status
     * 4. Write to log
     * 5. Write refund request
     *
     * (-) Controller
     *
     * @param transactionCode
     * @param notes
     * @return
     */
    @Override
    public boolean refundTransaction(String transactionCode, String notes) {
        boolean isRequested = false;

        try {
            Optional<Transaction> optionalTrx = transactionRepo.findByTransactionCode(transactionCode);
            if( optionalTrx.isEmpty() ) {
                Logging.handleException("PaymentService", "refundTransaction", 152, generateErrorCode("03", "001"), "Transaction not found!");
                return isRequested;
            }
            Transaction trx = optionalTrx.get();
            int previousStatus = trx.getStatus();

            if( !TransactionStatusConstant.isStatusAllowed(previousStatus, TransactionStatusConstant.PAYMENT_CANCELLED) ) {
                Logging.handleException("PaymentService", "refundTransaction", 158, generateErrorCode("03", "002"), "Status update is not allowed!");
            }

            trx.setStatus((byte) TransactionStatusConstant.PAYMENT_CANCELLED);
            logTransactionUpdate(trx, previousStatus);

            RefundRequest refundRequest = new RefundRequest();
            refundRequest.setTransaction(trx);
            refundRequest.setRefundStatus((byte) TransactionStatusConstant.REFUND_REQUESTED);

            refundRequestRepo.save(refundRequest);

            isRequested = true;
        } catch(Exception e) {
            Logging.handleException("PaymentService", "refundTransaction", 147, generateErrorCode("03", "010"), e.getMessage());
        }

        return isRequested;
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
        trxLog.setPreviousStatus((byte) previousStatus);
        trxLog.setCurrentStatus(transaction.getStatus());
        trxLog.setTransaction(transaction);

        transactionLogRepo.save(trxLog);
    }
}
