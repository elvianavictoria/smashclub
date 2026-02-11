package com.backendsyndicate.smashclub.admin.service.report;

import com.backendsyndicate.smashclub.admin.core.IStatistic;
import com.backendsyndicate.smashclub.admin.dto.extra.ExtAdminTransactionItemDTO;
import com.backendsyndicate.smashclub.admin.dto.extra.ExtAdminTransactionMonthlyDTO;
import com.backendsyndicate.smashclub.admin.dto.relation.RelAdminTransactionListDTO;
import com.backendsyndicate.smashclub.admin.dto.response.RespAdminTransactionDetailDTO;
import com.backendsyndicate.smashclub.admin.dto.response.RespAdminTransactionListDTO;
import com.backendsyndicate.smashclub.admin.dto.response.RespAdminTransactionStatisticDTO;
import com.backendsyndicate.smashclub.common.constant.TransactionConstant;
import com.backendsyndicate.smashclub.common.constant.TransactionTypeConstant;
import com.backendsyndicate.smashclub.common.util.DatetimeFormatting;
import com.backendsyndicate.smashclub.common.util.GlobalResponse;
import com.backendsyndicate.smashclub.common.util.Logging;
import com.backendsyndicate.smashclub.common.util.Util;
import com.backendsyndicate.smashclub.payment.model.Transaction;
import com.backendsyndicate.smashclub.payment.repo.TransactionRepo;
import jakarta.servlet.http.HttpServletRequest;
import org.hibernate.Hibernate;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Service
@Transactional
public class AdminSalesService implements IStatistic {
    @Autowired
    private TransactionRepo transactionRepo;
    private ModelMapper modelMapper = new ModelMapper();

    private String generateErrorCode(String methodNo, String errorNo) {
        return "ADM-SLS" + "-" + methodNo + "-" + errorNo;
    }

    /**
     * Display:
     * 1. Total Income
     * 2. Average Income
     * 3. Monthly Income
     *
     * @param yearStart
     * @param request
     * @return
     */
    @Override
    public ResponseEntity<Object> statistic(int yearStart, HttpServletRequest request) {
        RespAdminTransactionStatisticDTO response = null;

        try {
            LocalDateTime startYear = LocalDateTime.of(LocalDate.of(yearStart, 1, 1), LocalTime.of(0, 0, 0));
            LocalDateTime endYear = startYear.plusYears(1);

            BigDecimal totalTransaction = transactionRepo.sumTotalPriceByCreatedAt(startYear, endYear);
            BigDecimal averageTransaction = transactionRepo.averageTotalPriceByCreatedAt(startYear, endYear);
            List<Map<String, Object>> monthlyTransaction = transactionRepo.findAllGroupByCreatedAt(startYear, endYear);
            List<ExtAdminTransactionMonthlyDTO> monthlyTransactionDTOs = new ArrayList<>();

            response = new RespAdminTransactionStatisticDTO();
            response.setTotalTransactionValue(totalTransaction);
            response.setAverageTransactionValue(averageTransaction);
            for( Map<String, Object> item: monthlyTransaction ) {
                monthlyTransactionDTOs.add(Util.mapToModel(item, ExtAdminTransactionMonthlyDTO.class));
            }
            response.setMonthlyTransactionValue(monthlyTransactionDTOs);
        } catch(Exception e) {
            Logging.handleException("AdminSalesService", "statistic(LocalDate yearStart, HttpServletRequest request)", 55, generateErrorCode("01", "010"), e.getMessage());
            return GlobalResponse.failed("Failed to get sales statistics!", generateErrorCode("01", "010"), null, request);
        }

        return GlobalResponse.success("Successfully fetch sales statistics!", response, request);
    }

    @Override
    public ResponseEntity<Object> list(int yearStart, int monthStart, String keyword, Pageable pageable, HttpServletRequest request) {
        RespAdminTransactionListDTO response = null;

        try {
            LocalDateTime startMonth = LocalDateTime.of(LocalDate.of(yearStart, monthStart, 1), LocalTime.of(0, 0, 0));
            LocalDateTime endMonth = startMonth.plusMonths(1);

            BigDecimal totalTransaction = transactionRepo.sumTotalPriceByCreatedAt(startMonth, endMonth);
            BigDecimal averageTransaction = transactionRepo.averageTotalPriceByCreatedAt(startMonth, endMonth);
            Page<Transaction> transactions = null;
            if( !keyword.isEmpty() ) {
                transactions = transactionRepo.findAllByCreatedAtBetweenAndTransactionCodeContainsIgnoreCase(startMonth, endMonth, keyword, pageable);
            } else {
                transactions = transactionRepo.findAllByCreatedAtBetween(startMonth, endMonth, pageable);
            }

            response = new RespAdminTransactionListDTO();
            response.setTotalTransactionValue(totalTransaction);
            response.setAverageTransactionValue(averageTransaction);
            Page<RelAdminTransactionListDTO> listDTO = transactions.map(new Function<Transaction, RelAdminTransactionListDTO>() {
                @Override
                public RelAdminTransactionListDTO apply(Transaction trx) {
                    return mapListToDTO(trx);
                }
            });
            response.setTransactions(listDTO);
        } catch(Exception e) {
            Logging.handleException("AdminSalesService", "list(LocalDate monthStart, HttpServletRequest request)", 83, generateErrorCode("02", "010"), e.getMessage());
            return GlobalResponse.failed("Failed to get sales statistics!", generateErrorCode("02", "010"), null, request);
        }

        return GlobalResponse.success("Successfully fetch sales statistics!", response, request);
    }

    public ResponseEntity<Object> detail(String transactionCode, HttpServletRequest request) {
        if( transactionCode == null || transactionCode.isEmpty() ) {
            return GlobalResponse.failed("Failed to get sales detail!", generateErrorCode("03", "001"), null, request);
        }

        RespAdminTransactionDetailDTO response = null;

        try {
            Optional<Transaction> opt = transactionRepo.findByTransactionCode(transactionCode);
            if( opt.isEmpty() ) {
                return GlobalResponse.failed("Failed to get sales detail!", generateErrorCode("03", "002"), null, request);
            }

            // Map trx to DTO
            Transaction transaction = opt.get();
            Hibernate.initialize(transaction.getUser());
            response = modelMapper.map(transaction, RespAdminTransactionDetailDTO.class);
            response.setCreatedAt(DatetimeFormatting.getDatetimeFormat(transaction.getCreatedAt()));
            response.setUpdatedAt(DatetimeFormatting.getDatetimeFormat(transaction.getUpdatedAt()));
            response.setTransactionTypeDesc(TransactionTypeConstant.getTransactionType(transaction.getTransactionType()));
            response.setStatusDesc(TransactionConstant.getStatus(transaction.getStatus()));
            // Map item based on trx type
            switch( response.getTransactionType() ) {
                case TransactionTypeConstant.COURT_BOOKING:
                    break;
                case TransactionTypeConstant.ECOMMERCE_SHOPPING:
                    break;
                case TransactionTypeConstant.WALLET_TOPUP:
                    ExtAdminTransactionItemDTO walletTopup = new ExtAdminTransactionItemDTO();
                    walletTopup.setItemName("Wallet Topup");
                    walletTopup.setItemQty(1);
                    walletTopup.setItemPrice(response.getTotalPrice());
                    walletTopup.setItemUnit("kali");

                    response.setItems(List.of(walletTopup));
                    break;
            }

        } catch(Exception e) {
            Logging.handleException("AdminSalesService", "detail(String transactionCode, HttpServletRequest request)", 109, generateErrorCode("03", "010"), e.getMessage());
            return GlobalResponse.failed("Failed to get sales statistics!", generateErrorCode("03", "010"), null, request);
        }

        return GlobalResponse.success("Successfully fetch sales statistics!", response, request);
    }

    private RelAdminTransactionListDTO mapListToDTO(Transaction transaction) {
        RelAdminTransactionListDTO result = modelMapper.map(transaction, RelAdminTransactionListDTO.class);
        result.setStatusDesc(TransactionConstant.getStatus(transaction.getStatus()));

        if( transaction.getCreatedAt() != null ) {
            result.setCreatedAt(DatetimeFormatting.getDatetimeFormat(transaction.getCreatedAt()));
        }
        if( transaction.getUpdatedAt() != null ) {
            result.setUpdatedAt(DatetimeFormatting.getDatetimeFormat(transaction.getUpdatedAt()));
        }

        return result;
    }
}
