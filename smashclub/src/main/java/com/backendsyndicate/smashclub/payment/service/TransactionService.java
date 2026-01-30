package com.backendsyndicate.smashclub.payment.service;

import com.backendsyndicate.smashclub.common.util.GlobalResponse;
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
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Module Code: TRX
 */
@Service
@Transactional
public class TransactionService implements IHistory<Object> {
    @Autowired
    private TransactionRepo transactionRepo;
    private ModelMapper modelMapper = new ModelMapper();

    private String generateErrorCode(String methodNo, String errorNo) {
        return "TRX-" + methodNo + "E" + errorNo;
    }

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
            page = transactionRepo.findByCreatedAtBetween(startDate, endDate, pageable);
            if( page.isEmpty() ) {
                return GlobalResponse.failed("Transaction data not found!", generateErrorCode("01", "001"), null, request);
            }
        } catch(Exception e) {
            return GlobalResponse.failed("Failed to get transaction list!", generateErrorCode("01", "010"), null, request);
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
            return GlobalResponse.failed("Transaction code is required!", generateErrorCode("02", "001"), null, request);
        }

        try {
            Optional<Transaction> optionalTrx = transactionRepo.findByTransactionCode(code);
            if( optionalTrx.isEmpty() ) {
                return GlobalResponse.failed("Transaction not found!", generateErrorCode("02", "002"), null, request);
            }

            trx = optionalTrx.get();
        } catch(Exception e) {
            return GlobalResponse.failed("Failed to get transaction data!", generateErrorCode("02", "010"), null, request);
        }

        return GlobalResponse.success("Transaction data found!", trx, request);
    }
}
