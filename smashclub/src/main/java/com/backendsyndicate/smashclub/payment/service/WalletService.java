package com.backendsyndicate.smashclub.payment.service;

import com.backendsyndicate.smashclub.common.util.GlobalResponse;
import com.backendsyndicate.smashclub.payment.core.IWallet;
import com.backendsyndicate.smashclub.payment.dto.request.ReqUpdateBalanceDTO;
import com.backendsyndicate.smashclub.payment.dto.response.RespUpdateBalanceDTO;
import com.backendsyndicate.smashclub.payment.model.Wallet;
import com.backendsyndicate.smashclub.payment.model.WalletLog;
import com.backendsyndicate.smashclub.payment.repo.WalletLogRepo;
import com.backendsyndicate.smashclub.payment.repo.WalletRepo;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Module Code: WLLT
 */
@Service
@Transactional
public class WalletService implements IWallet {
    private WalletRepo walletRepo;
    private WalletLogRepo walletLogRepo;
    private ModelMapper modelMapper = new ModelMapper();

    private String generateErrorCode(String methodNo, String errorNo) {
        return "WLLT-" + methodNo + "E" + errorNo;
    }

    /**
     * Code: 01
     *
     * @param userId
     * @param request
     * @return
     */
    @Override
    public ResponseEntity<Object> getBalance(String userId, HttpServletRequest request) {
        if( userId == null ) {
            return GlobalResponse.failed("Auth Token is invalid!", generateErrorCode("01", "001"), null, request);
        }

        Wallet wallet = null;

        try {
            Optional<Wallet> optionalWallet = walletRepo.findByUserId(userId);
            if( optionalWallet.isEmpty() ) {
                return GlobalResponse.failed("Wallet data not found!", generateErrorCode("01", "002"), null, request);
            }

            wallet = optionalWallet.get();

        } catch(Exception e) {
            return GlobalResponse.failed("Wallet info not found!", generateErrorCode("01", "010"), null, request);
        }

        return GlobalResponse.success("Successfully get wallet info!", wallet, request);
    }

    /**
     * Code: 02
     *
     * @param userId
     * @param startDate
     * @param endDate
     * @param pageable
     * @param request
     * @return
     */
    @Override
    public ResponseEntity<Object> getBalanceLog(String userId, LocalDate startDate, LocalDate endDate, Pageable pageable,  HttpServletRequest request) {
        Page page = null;

        try {
            page = walletLogRepo.findByWallet_UserIdAndCreatedAtBetween(userId, startDate, endDate, pageable);
            if( page.isEmpty() ) {
                return GlobalResponse.failed("Wallet log not found!", generateErrorCode("02", "001"), null, request);
            }
        } catch(Exception e) {
            return GlobalResponse.failed("Failed to get wallet log!", generateErrorCode("02", "010"), null, request);
        }

        return GlobalResponse.success("Wallet log found!", page, request);
    }

    /**
     * Code: 03
     *
     * @param userId
     * @param reqUpdateBalanceDTO
     * @param request
     * @return
     */
    @Override
    public ResponseEntity<Object> updateBalance(String userId, ReqUpdateBalanceDTO reqUpdateBalanceDTO, HttpServletRequest request) {
        if( userId == null ) {
            return GlobalResponse.failed("Auth Token is invalid!", generateErrorCode("03", "001"), null, request);
        }

        if( reqUpdateBalanceDTO == null ) {
            return GlobalResponse.failed("Invalid request!", generateErrorCode("03", "002"), null, request);
        }

        Wallet wallet = null;
        RespUpdateBalanceDTO response = null;

        try {
            Optional<Wallet> optionalWallet = walletRepo.findByUserId(userId);
            if( optionalWallet.isEmpty() ) {
                return GlobalResponse.failed("Wallet data not found!", generateErrorCode("03", "003"), null, request);
            }

            wallet = optionalWallet.get();
            BigDecimal previousBalance = wallet.getUserBalance();
            BigDecimal updatedBalance = reqUpdateBalanceDTO.isAddition() ? previousBalance.add(reqUpdateBalanceDTO.getValue()) : previousBalance.subtract(reqUpdateBalanceDTO.getValue());
            wallet.setUserBalance(updatedBalance);

            logWalletUpdate(wallet, previousBalance);

            response = new RespUpdateBalanceDTO();
            response.setPreviousBalance(previousBalance);
            response.setCurrentBalance(updatedBalance);
            response.setBalanceDiff(reqUpdateBalanceDTO.getValue());

        } catch(Exception e) {
            return GlobalResponse.failed("Failed to update balance!", generateErrorCode("03", "010"), null, request);
        }

        return GlobalResponse.success("Successfully updated balance", response, request);
    }

    private void logWalletUpdate(Wallet wallet, BigDecimal previousBalance) {
        BigDecimal balanceDiff = wallet.getUserBalance().subtract(previousBalance);

        WalletLog log = new WalletLog();
        log.setPreviousBalance(previousBalance);
        log.setCurrentBalance(wallet.getUserBalance());
        log.setUsageValue(balanceDiff.abs());
        log.setUsageType(balanceDiff.compareTo(BigDecimal.valueOf(0)) > 0);
        log.setWallet(wallet);

        walletLogRepo.save(log);
    }
}
