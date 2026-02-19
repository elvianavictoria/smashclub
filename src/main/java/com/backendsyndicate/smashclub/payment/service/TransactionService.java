package com.backendsyndicate.smashclub.payment.service;

import com.backendsyndicate.smashclub.admin.service.log.LogService;
import com.backendsyndicate.smashclub.common.constant.TransactionConstant;
import com.backendsyndicate.smashclub.common.util.GlobalResponse;
import com.backendsyndicate.smashclub.common.util.Logging;
import com.backendsyndicate.smashclub.payment.core.IHistory;
import com.backendsyndicate.smashclub.payment.model.Transaction;
import com.backendsyndicate.smashclub.payment.repo.TransactionRepo;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Module Code: TRX
 */
@Service
@Transactional
public class TransactionService implements IHistory {
    @Autowired
    private TransactionRepo transactionRepo;
    @Autowired
    private LogService logService;
    private ModelMapper modelMapper = new ModelMapper();

    /**
     * Code: 01
     *
     * @param pageable
     * @param startDate
     * @param endDate
     * @param request
     * @return
     */
    @Override
    public ResponseEntity<Object> findAll(Pageable pageable, LocalDate startDate, LocalDate endDate, HttpServletRequest request) {
        Page page = null;

        try {
            page = transactionRepo.findAllByCreatedAtBetween(startDate.atStartOfDay(), endDate.atStartOfDay(), pageable);
            if( page.isEmpty() ) {
                return GlobalResponse.failed("Transaction data not found!", TransactionConstant.TRANSACTION_SERVICE_ERROR_LIST_EMPTY, null, request);
            }
        } catch(Exception e) {
            Logging.handleException("TransactionService", "findAll(Pageable pageable, LocalDate startDate, LocalDate endDate, HttpServletRequest request)", 51, TransactionConstant.TRANSACTION_SERVICE_ERROR_LIST_EXCEPTION, e.getMessage());
            logService.writeErrorLog(TransactionConstant.TRANSACTION_SERVICE_ERROR_LIST_EXCEPTION, "TransactionService@findAll()", e.getMessage());
            return GlobalResponse.failed("Failed to get transaction list!", TransactionConstant.TRANSACTION_SERVICE_ERROR_LIST_EXCEPTION, null, request);
        }

        return GlobalResponse.success("Transaction list found!", page, request);
    }

    /**
     * Code: 02
     *
     * @param code
     * @param request
     * @return
     */
    @Override
    public ResponseEntity<Object> findByCode(String code, HttpServletRequest request) {
        Transaction trx = null;

        if( code == null ) {
            return GlobalResponse.failed("Transaction code is required!", TransactionConstant.TRANSACTION_SERVICE_ERROR_DETAIL_CODE_REQUIRED, null, request);
        }

        try {
            Optional<Transaction> optionalTrx = transactionRepo.findByTransactionCode(code);
            if( optionalTrx.isEmpty() ) {
                return GlobalResponse.failed("Transaction not found!", TransactionConstant.TRANSACTION_SERVICE_ERROR_DETAIL_NOT_FOUND, null, request);
            }

            trx = optionalTrx.get();
        } catch(Exception e) {
            Logging.handleException("TransactionService", "findByCode(String code, HttpServletRequest request)", 79, TransactionConstant.TRANSACTION_SERVICE_ERROR_DETAIL_EXCEPTION, e.getMessage());
            logService.writeErrorLog(TransactionConstant.TRANSACTION_SERVICE_ERROR_DETAIL_EXCEPTION, "TransactionService@findByCode()", e.getMessage());
            return GlobalResponse.failed("Failed to get transaction data!", TransactionConstant.TRANSACTION_SERVICE_ERROR_DETAIL_EXCEPTION, null, request);
        }

        return GlobalResponse.success("Transaction data found!", trx, request);
    }
}
