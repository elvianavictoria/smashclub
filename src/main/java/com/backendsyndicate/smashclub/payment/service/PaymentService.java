package com.backendsyndicate.smashclub.payment.service;

import com.backendsyndicate.smashclub.auth.model.User;
import com.backendsyndicate.smashclub.auth.repository.UserRepository;
import com.backendsyndicate.smashclub.common.service.TemplateService;
import com.backendsyndicate.smashclub.common.util.DatetimeFormatting;
import com.backendsyndicate.smashclub.common.util.Logging;
import com.backendsyndicate.smashclub.common.util.Util;
import com.backendsyndicate.smashclub.common.constant.PaymentMethodConstant;
import com.backendsyndicate.smashclub.common.constant.TransactionConstant;
import com.backendsyndicate.smashclub.common.constant.TransactionTypeConstant;
import com.backendsyndicate.smashclub.external.config.XenditConfig;
import com.backendsyndicate.smashclub.external.dto.XenditResponseDTO;
import com.backendsyndicate.smashclub.external.service.notification.MailService;
import com.backendsyndicate.smashclub.external.service.payment.XenditService;
import com.backendsyndicate.smashclub.payment.core.IPayment;
import com.backendsyndicate.smashclub.payment.dto.relation.RelTransactionUserDTO;
import com.backendsyndicate.smashclub.payment.dto.response.RespCreateTransactionDTO;
import com.backendsyndicate.smashclub.payment.dto.response.RespExpireTransactionDTO;
import com.backendsyndicate.smashclub.payment.dto.response.RespPaymentTransactionDTO;
import com.backendsyndicate.smashclub.payment.dto.response.RespCancelTransactionDTO;
import com.backendsyndicate.smashclub.payment.model.PaymentLog;
import com.backendsyndicate.smashclub.payment.model.RefundRequest;
import com.backendsyndicate.smashclub.payment.model.Transaction;
import com.backendsyndicate.smashclub.payment.model.TransactionLog;
import com.backendsyndicate.smashclub.payment.repo.PaymentLogRepo;
import com.backendsyndicate.smashclub.payment.repo.RefundRequestRepo;
import com.backendsyndicate.smashclub.payment.repo.TransactionLogRepo;
import com.backendsyndicate.smashclub.payment.repo.TransactionRepo;
import jakarta.servlet.http.HttpServletRequest;
import org.hibernate.Hibernate;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
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
    @Autowired
    private PaymentLogRepo paymentLogRepo;
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private XenditService xenditService;
    @Autowired
    private MailService mailService;

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
        RespCreateTransactionDTO response = null;

        try {
            String trxCode = generateTransactionCode();
            String trxLabel = "Transaksi " + trxCode + ": " + TransactionTypeConstant.getTransactionType(transactionType);

            Transaction trx = new Transaction();
            Optional<User> optUser = userRepo.findById(customerId);
            if( optUser.isEmpty() ) {
                Logging.handleException("PaymentService", "createTransaction", 97, generateErrorCode("01", "001"), "Failed to get created transaction!");
                return null;
            }

            User user = optUser.get();
            trx.setUser(user);
            trx.setTransactionCode(trxCode);
            trx.setStatus((byte) TransactionConstant.PAYMENT_UNPAID);
            trx.setTransactionLabel(trxLabel);
            trx.setTotalPrice(totalPrice);
            trx.setReferenceCode(referenceCode);
            trx.setTransactionType((byte) transactionType);

            transactionRepo.save(trx);

            logTransactionUpdate(trx, -1);

            // Create payment link
            Optional<Transaction> opt = transactionRepo.findByTransactionCode(trxCode);
            if( opt.isEmpty() ) {
                Logging.handleException("PaymentService", "createTransaction", 97, generateErrorCode("01", "002"), "Failed to get created transaction!");
            } else {
                Transaction transaction = opt.get();
                Hibernate.initialize(transaction.getUser());

                XenditResponseDTO pgResponse = new XenditResponseDTO();
                if(XenditConfig.getUseInvoice() == 'y') {
                    pgResponse = xenditService.createPayment(trxCode, totalPrice, transaction.getUser().getEmail(), transaction.getTransactionLabel());
                    if( pgResponse.getInvoiceUrl() != null ) {
                        transaction.setPaymentLink(pgResponse.getInvoiceUrl());
                        // Write to payment log
                        logPaymentCreate(transaction, pgResponse.getInvoiceUrl());
                    }
                } else {
                    /*
                    * VA: Safe
                    * E-wallet: Callback URL Issue
                    * QRIS: Safe
                    * */
                    pgResponse = xenditService.createPayment(trxCode, totalPrice, transaction.getUser().getEmail(), transaction.getTransactionLabel(), PaymentMethodConstant.QRIS_DANA);
                }

                Logging.printConsole("Sending email!");
                LocalDateTime expiredDt = transaction.getCreatedAt().plusHours(24);
                Map<String, Object> mailObject = Map.of(
                        "transactionCode", transaction.getTransactionCode(),
                        "fullName", transaction.getUser().getFullName(),
                        "totalPrice", Util.formatCurrency(transaction.getTotalPrice()),
                        "expiredAt", DatetimeFormatting.getDatetimeFormat(expiredDt),
                        "url", pgResponse.getInvoiceUrl()
                );
                mailService.sendMail(TemplateService.TEMPLATE_PAYMENT_NOTIFY_UNPAID, transaction.getUser().getEmail(), "Smashclub - Pesanan Menunggu Pembayaran", mailObject);

                response = new RespCreateTransactionDTO();
                response.setTransactionCode(trxCode);
                response.setPaymentData(pgResponse.asMap());
            }
        } catch(Exception e) {
            Logging.handleException("PaymentService", "createTransaction", 107, generateErrorCode("01", "010"), e.getMessage());
            return null;
        }

        return response;
    }

    /**
     * Code: 02
     *
     * (-) Controller
     * @param transactionCode
     * @param request
     * @return
     */
    @Override
    public RespPaymentTransactionDTO paymentTransaction(String transactionCode) {
        if( transactionCode == null ) {
            Logging.handleException("PaymentService", "paymentTransaction(String transactionCode)", 177, generateErrorCode("02", "001"), "Transaction code is required!");

            return null;
        }

        Transaction trx = null;
        RespPaymentTransactionDTO response = null;

        try {
            trx = getTransaction(transactionCode);
            if( trx == null ) {
                Logging.handleException("PaymentService", "paymentTransaction(String transactionCode)", 177, generateErrorCode("02", "002"), "Transaction not found!");
                return null;
            }

            Hibernate.initialize(trx.getUser());
            byte previousStatus = trx.getStatus();
            if( TransactionConstant.isStatusAllowed(previousStatus, TransactionConstant.PAYMENT_PAID) ) {
                trx.setStatus((byte) TransactionConstant.PAYMENT_PAID);
                logTransactionUpdate(trx, previousStatus);

                Logging.printConsole("Sending payment success email!");
                Map<String, Object> mailObject = Map.of(
                        "transactionCode", trx.getTransactionCode(),
                        "fullName", trx.getUser().getFullName(),
                        "totalPrice", Util.formatCurrency(trx.getTotalPrice()),
                        "createdAt", DatetimeFormatting.getDatetimeFormat(trx.getCreatedAt()),
                        "url", "https://localhost:5173/transaction/" + trx.getTransactionCode()
                );
                Logging.printConsole(mailObject.toString());
                mailService.sendMail(TemplateService.TEMPLATE_PAYMENT_NOTIFY_PAID, trx.getUser().getEmail(), "Smashclub - Pembayaran Berhasil", mailObject);

                response = modelMapper.map(trx, RespPaymentTransactionDTO.class);
            } else {
                Logging.handleException("PaymentService", "paymentTransaction(String transactionCode)", 212, generateErrorCode("02", "009"), "This transaction has been paid!");
                return null;
            }
        } catch(Exception e) {
            Logging.handleException("PaymentService", "paymentTransaction(String transactionCode)", 216, generateErrorCode("02", "010"), "Failed to process payment transaction!");
            return null;
        }

        return response;
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
     * @param refundReason
     * @return
     */
    @Override
    public RespCancelTransactionDTO cancelTransaction(String transactionCode, String refundReason) {
        RespCancelTransactionDTO response = null;

        try {
            Transaction trx = getTransaction(transactionCode);
            if( trx == null ) {
                Logging.handleException("PaymentService", "cancelTransaction(String transactionCode, String refundReason)", 152, generateErrorCode("03", "001"), "Transaction not found!");
                return null;
            }

            if( trx.getTransactionType() == TransactionTypeConstant.WALLET_TOPUP ) {
                Logging.handleException("PaymentService", "cancelTransaction(String transactionCode, String refundReason)", 158, generateErrorCode("03", "002"), "Wallet topup cannot be refunded!");
                return null;
            }

            int previousStatus = trx.getStatus();
            if( !TransactionConstant.isStatusAllowed(previousStatus, TransactionConstant.PAYMENT_CANCELLED) ) {
                Logging.handleException("PaymentService", "cancelTransaction(String transactionCode, String refundReason)", 158, generateErrorCode("03", "003"), "Status update is not allowed!");
                return null;
            }

            trx.setStatus((byte) TransactionConstant.PAYMENT_CANCELLED);
            logTransactionUpdate(trx, previousStatus);

            boolean isRefund = previousStatus > TransactionConstant.PAYMENT_UNPAID;

            if( isRefund ) {
                RefundRequest refundRequest = new RefundRequest();
                refundRequest.setTransaction(trx);
                refundRequest.setRefundStatus((byte) TransactionConstant.REFUND_REQUESTED);
                refundRequest.setRefundReason(refundReason);

                refundRequestRepo.save(refundRequest);
            }

            response = new RespCancelTransactionDTO();
            response.setRequested(true);
            response.setTransactionCode(transactionCode);
            response.setTotalPrice(trx.getTotalPrice());
            response.setTransactionType(trx.getTransactionType());
            response.setReferenceCode(trx.getReferenceCode());
            response.setUser(modelMapper.map(trx.getUser(), RelTransactionUserDTO.class));
        } catch(Exception e) {
            Logging.handleException("PaymentService", "cancelTransaction(String transactionCode, String refundReason)", 147, generateErrorCode("03", "010"), e.getMessage());
        }

        return response;
    }

    /**
     * Code: 04
     * Desc: Expire Transaction
     *
     * (-) Controller
     *
     * @param transactionCode
     * @return
     */
    @Override
    public RespExpireTransactionDTO expireTransaction(String transactionCode) {
        RespExpireTransactionDTO response = null;

        try {
            Transaction trx = getTransaction(transactionCode);
            if( trx == null ) {
                Logging.handleException("PaymentService", "expireTransaction(String transactionCode)", 152, generateErrorCode("04", "001"), "Transaction not found!");
                return null;
            }
            int previousStatus = trx.getStatus();

            if( !TransactionConstant.isStatusAllowed(previousStatus, TransactionConstant.PAYMENT_CANCELLED) ) {
                Logging.handleException("PaymentService", "expireTransaction(String transactionCode)", 158, generateErrorCode("04", "002"), "Status update is not allowed!");
                return null;
            }

            trx.setStatus((byte) TransactionConstant.PAYMENT_EXPIRED);
            logTransactionUpdate(trx, previousStatus);

            response = new RespExpireTransactionDTO();
            response.setTransactionCode(transactionCode);
            response.setTotalPrice(trx.getTotalPrice());
            response.setTransactionType(trx.getTransactionType());
            response.setUser(modelMapper.map(trx.getUser(), RelTransactionUserDTO.class));
        } catch(Exception e) {
            Logging.handleException("PaymentService", "expireTransaction(String transactionCode)", 147, generateErrorCode("04", "010"), e.getMessage());
        }

        return response;
    }

    protected Transaction getTransaction(String transactionCode) {
        Optional<Transaction> optionalTrx = transactionRepo.findByTransactionCode(transactionCode);
        return optionalTrx.orElse(null);
    }

    private String generateTransactionCode() {
        LocalDate currentDt = LocalDate.now();
        String strYear = "" + currentDt.getYear();
        String strMonth = "0" + currentDt.getMonthValue();
        String strDate = "0" + currentDt.getDayOfMonth();

        String currentDtString = strYear.substring(2) + strMonth.substring(strMonth.length() - 2) + strDate.substring(strDate.length() - 2);
        String randomStr = Util.generateRandomString(4, true);

        long trxCounter = transactionRepo.countTodayTransaction();
        trxCounter += 1;
        String strCounter = "00" + trxCounter;

        return currentDtString + "-" + randomStr + "-" + strCounter.substring(strCounter.length() - 3);
    }

    public void logTransactionUpdate(Transaction transaction, int previousStatus) {
        TransactionLog trxLog = new TransactionLog();
        trxLog.setPreviousStatus((byte) previousStatus);
        trxLog.setCurrentStatus(transaction.getStatus());
        trxLog.setTransaction(transaction);

        transactionLogRepo.save(trxLog);
    }

    private void logPaymentCreate(Transaction transaction, String paymentLink) {
        PaymentLog paymentLog = new PaymentLog();
        paymentLog.setPaymentLink(paymentLink);
        paymentLog.setTransaction(transaction);

        paymentLogRepo.save(paymentLog);
    }
}
