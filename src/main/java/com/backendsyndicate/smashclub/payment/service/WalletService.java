package com.backendsyndicate.smashclub.payment.service;

import com.backendsyndicate.smashclub.admin.service.log.LogService;
import com.backendsyndicate.smashclub.auth.model.User;
import com.backendsyndicate.smashclub.common.constant.TransactionConstant;
import com.backendsyndicate.smashclub.common.util.GlobalResponse;
import com.backendsyndicate.smashclub.common.util.Logging;
import com.backendsyndicate.smashclub.common.constant.TransactionTypeConstant;
import com.backendsyndicate.smashclub.common.util.Util;
import com.backendsyndicate.smashclub.payment.core.IWallet;
import com.backendsyndicate.smashclub.payment.dto.request.ReqUpdateBalanceDTO;
import com.backendsyndicate.smashclub.payment.dto.response.RespCreateTransactionDTO;
import com.backendsyndicate.smashclub.payment.dto.response.RespGetBalanceInfoDTO;
import com.backendsyndicate.smashclub.payment.dto.response.RespGetBalanceLogDTO;
import com.backendsyndicate.smashclub.payment.dto.response.RespUpdateBalanceDTO;
import com.backendsyndicate.smashclub.payment.model.Wallet;
import com.backendsyndicate.smashclub.payment.model.WalletLog;
import com.backendsyndicate.smashclub.payment.repo.WalletLogRepo;
import com.backendsyndicate.smashclub.payment.repo.WalletRepo;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.function.Function;

/**
 * Module Code: WLLT
 */
@Service
@Transactional
public class WalletService implements IWallet {
    @Autowired
    private WalletRepo walletRepo;
    @Autowired
    private WalletLogRepo walletLogRepo;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private LogService logService;

    private ModelMapper modelMapper = new ModelMapper();

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
            return GlobalResponse.failed("Auth Token is invalid!", TransactionConstant.WALLET_SERVICE_ERROR_BALANCE_USERID_REQUIRED, null, request);
        }

        RespGetBalanceInfoDTO response = null;

        try {
            Optional<Wallet> optionalWallet = walletRepo.findByUserId(userId);
            if( optionalWallet.isEmpty() ) {
                return GlobalResponse.failed("Wallet data not found!", TransactionConstant.WALLET_SERVICE_ERROR_BALANCE_WALLET_NOT_FOUND, null, request);
            }

            Wallet wallet = optionalWallet.get();
            response = modelMapper.map(wallet, RespGetBalanceInfoDTO.class);

        } catch(Exception e) {
            Logging.handleException("Wallet Service", "getBalance(String userId, HttpServletRequest request)", 74, TransactionConstant.WALLET_SERVICE_ERROR_BALANCE_EXCEPTION, e.getMessage());
            logService.writeErrorLog(TransactionConstant.WALLET_SERVICE_ERROR_BALANCE_EXCEPTION, "WalletService@getBalance()", e.getMessage());
            return GlobalResponse.failed("Wallet info not found!", TransactionConstant.WALLET_SERVICE_ERROR_BALANCE_EXCEPTION, null, request);
        }

        return GlobalResponse.success("Successfully get wallet info!", response, request);
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
                return GlobalResponse.failed("Wallet log not found!", TransactionConstant.WALLET_SERVICE_ERROR_LOG_EMPTY, null, request);
            }

            page = page.map(new Function<WalletLog, RespGetBalanceLogDTO>() {
                @Override
                public RespGetBalanceLogDTO apply(WalletLog log) {
                    return mapLogToDTO(log);
                }
            });
        } catch(Exception e) {
            Logging.handleException("Wallet Service", "getBalanceLog(String userId, LocalDate startDate, LocalDate endDate, Pageable pageable,  HttpServletRequest request)", 97, TransactionConstant.WALLET_SERVICE_ERROR_LOG_EXCEPTION, e.getMessage());
            logService.writeErrorLog(TransactionConstant.WALLET_SERVICE_ERROR_LOG_EXCEPTION, "WalletService@getBalanceLog()", e.getMessage());
            return GlobalResponse.failed("Failed to get wallet log!", TransactionConstant.WALLET_SERVICE_ERROR_LOG_EXCEPTION, null, request);
        }

        return GlobalResponse.success("Wallet log found!", page, request);
    }

    /**
     * Code: 03
     *
     * @param userId
     * @param balance
     * @return
     */
    @Override
    public ResponseEntity<Object> topupBalance(String userId, BigDecimal balance, HttpServletRequest request) {
        if( userId == null ) {
            return GlobalResponse.failed("Auth Token is invalid!", TransactionConstant.WALLET_SERVICE_ERROR_TOPUP_USERID_REQUIRED, null, request);
        }

        RespCreateTransactionDTO response = null;

        try {
            response = paymentService.createTransaction(userId, balance, generateReferenceCode(), TransactionTypeConstant.WALLET_TOPUP);
            if( response == null ) {
                Logging.handleException("Wallet Service", "topupBalance(String userId, BigDecimal balance, HttpServletRequest request)", 137, TransactionConstant.WALLET_SERVICE_ERROR_TOPUP_TRANSACTION_FAILED, "Failed to request topup balance!");
                return GlobalResponse.failed("Failed to request topup balance!", TransactionConstant.WALLET_SERVICE_ERROR_TOPUP_TRANSACTION_FAILED, null, request);
            }

        } catch(Exception e) {
            Logging.handleException("Wallet Service", "topupBalance(String userId, BigDecimal balance, HttpServletRequest request)", 135, TransactionConstant.WALLET_SERVICE_ERROR_TOPUP_EXCEPTION, e.getMessage());
            logService.writeErrorLog(TransactionConstant.WALLET_SERVICE_ERROR_TOPUP_EXCEPTION, "WalletService@topupBalance()", e.getMessage());
            return GlobalResponse.failed("Failed to request topup balance!", TransactionConstant.WALLET_SERVICE_ERROR_TOPUP_EXCEPTION, null, request);
        }

        return GlobalResponse.success("Successfully request topup balance", response, request);
    }

    /**
     * Code: 04
     *
     * (-) Controller
     *
     * @param userId
     * @param reqUpdateBalanceDTO
     * @return
     */
    @Override
    public boolean updateBalance(String userId, ReqUpdateBalanceDTO reqUpdateBalanceDTO) {
        if( userId == null ) {
            Logging.handleException("WalletService", "updateBalance", 145, TransactionConstant.WALLET_SERVICE_ERROR_UPDATE_USERID_REQUIRED, "User ID is required!");
            return false;
        }

        if( reqUpdateBalanceDTO == null ) {
            Logging.handleException("WalletService", "updateBalance", 150, TransactionConstant.WALLET_SERVICE_ERROR_UPDATE_REQUEST_INVALID, "Update balance DTO is null!");
            return false;
        }

        Wallet wallet = null;
        RespUpdateBalanceDTO response = null;

        try {
            Optional<Wallet> optionalWallet = walletRepo.findByUserId(userId);
            if( optionalWallet.isEmpty() ) {
                Logging.handleException("WalletService", "updateBalance", 160, TransactionConstant.WALLET_SERVICE_ERROR_UPDATE_WALLET_NOT_FOUND, "Wallet not found!");
                return false;
            }

            wallet = optionalWallet.get();
            BigDecimal previousBalance = wallet.getUserBalance();
            BigDecimal updatedBalance = reqUpdateBalanceDTO.isAddition() ? previousBalance.add(reqUpdateBalanceDTO.getValue()) : previousBalance.subtract(reqUpdateBalanceDTO.getValue());
            Logging.printConsole("Update balance to " + updatedBalance + "!");
            wallet.setUserBalance(updatedBalance);

            logWalletUpdate(wallet, previousBalance);

            response = new RespUpdateBalanceDTO();
            response.setPreviousBalance(previousBalance);
            response.setCurrentBalance(updatedBalance);
            response.setBalanceDiff(reqUpdateBalanceDTO.getValue());

        } catch(Exception e) {
            Logging.handleException("WalletService", "updateBalance(String userId, ReqUpdateBalanceDTO reqUpdateBalanceDTO)", 161, TransactionConstant.WALLET_SERVICE_ERROR_UPDATE_EXCEPTION, e.getMessage());
            logService.writeErrorLog(TransactionConstant.WALLET_SERVICE_ERROR_UPDATE_EXCEPTION, "WalletService@updateBalance()", e.getMessage());
            return false;
        }

        return true;
    }

    public boolean createWallet(String userId) {
        if( userId == null ) {
            Logging.handleException("WalletService", "createWallet(String userId)", 180, TransactionConstant.WALLET_SERVICE_ERROR_CREATE_USERID_REQUIRED, "User ID is required!");
            return false;
        }

        try {
            Optional<Wallet> optionalWallet = walletRepo.findByUserId(userId);
            if( optionalWallet.isPresent() ) {
                Logging.handleException("WalletService", "createWallet(String userId)", 189, TransactionConstant.WALLET_SERVICE_ERROR_CREATE_WALLET_EXISTS, "This user wallet already exists!");
                return false;
            }

            Wallet wallet = new Wallet();
            User user = new User();
            user.setId(userId);
            wallet.setUser(user);

            walletRepo.save(wallet);
        } catch(Exception e) {
            Logging.handleException("WalletService", "createWallet(String userId)", 186, TransactionConstant.WALLET_SERVICE_ERROR_CREATE_EXCEPTION, e.getMessage());
            logService.writeErrorLog(TransactionConstant.WALLET_SERVICE_ERROR_CREATE_EXCEPTION, "WalletService@createWallet()", e.getMessage());
            return false;
        }

        return true;
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

    private RespGetBalanceLogDTO mapLogToDTO(WalletLog log) {
        RespGetBalanceLogDTO result = modelMapper.map(log, RespGetBalanceLogDTO.class);
        return result;
    }

    private String generateReferenceCode() {
        LocalDate currentDt = LocalDate.now();
        String strYear = "" + currentDt.getYear();
        String strMonth = "0" + currentDt.getMonthValue();
        String strDate = "0" + currentDt.getDayOfMonth();

        String currentDtString = strYear.substring(2) + strMonth.substring(strMonth.length() - 2) + strDate.substring(strDate.length() - 2);

        Long logCounter = walletLogRepo.countTodayLog();
        if( logCounter == null ) logCounter = 0L;
        logCounter += 1;
        String strCounter = "00" + logCounter;

        return "WLLT-" + currentDtString + "-" + strCounter.substring(strCounter.length() - 3);
    }
}
