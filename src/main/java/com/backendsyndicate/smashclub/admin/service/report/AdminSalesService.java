package com.backendsyndicate.smashclub.admin.service.report;

import com.backendsyndicate.smashclub.admin.core.IStatistic;
import com.backendsyndicate.smashclub.admin.dto.extra.ExtAdminTransactionItemDTO;
import com.backendsyndicate.smashclub.admin.dto.extra.ExtAdminTransactionMonthlyDTO;
import com.backendsyndicate.smashclub.admin.dto.relation.RelAdminTransactionListDTO;
import com.backendsyndicate.smashclub.admin.dto.response.RespAdminTransactionDetailDTO;
import com.backendsyndicate.smashclub.admin.dto.response.RespAdminTransactionListDTO;
import com.backendsyndicate.smashclub.admin.dto.response.RespAdminTransactionStatisticDTO;
import com.backendsyndicate.smashclub.admin.service.log.LogService;
import com.backendsyndicate.smashclub.booking.dto.response.BookingResponse;
import com.backendsyndicate.smashclub.booking.dto.response.CoachDetailResponse;
import com.backendsyndicate.smashclub.booking.dto.response.EquipmentDetailResponse;
import com.backendsyndicate.smashclub.booking.service.helper.BookingHelper;
import com.backendsyndicate.smashclub.common.constant.AdminConstant;
import com.backendsyndicate.smashclub.common.constant.TransactionConstant;
import com.backendsyndicate.smashclub.common.constant.TransactionTypeConstant;
import com.backendsyndicate.smashclub.common.util.DatetimeFormatting;
import com.backendsyndicate.smashclub.common.util.GlobalResponse;
import com.backendsyndicate.smashclub.common.util.Logging;
import com.backendsyndicate.smashclub.common.util.Util;
import com.backendsyndicate.smashclub.ecommerce.dto.response.RespOrderDetailDTO;
import com.backendsyndicate.smashclub.ecommerce.dto.response.RespOrderItemDTO;
import com.backendsyndicate.smashclub.ecommerce.service.helper.OrderHelper;
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
import java.time.Duration;
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
    @Autowired
    private BookingHelper bookingHelper;
    @Autowired
    private OrderHelper orderHelper;
    @Autowired
    private LogService logService;

    private ModelMapper modelMapper = new ModelMapper();

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
            List<Map<String, Object>> monthlyTransaction = transactionRepo.findAllGroupByCreatedAtMonthly(startYear, endYear);
            List<ExtAdminTransactionMonthlyDTO> monthlyTransactionDTOs = new ArrayList<>();

            response = new RespAdminTransactionStatisticDTO();
            response.setTotalTransactionValue(totalTransaction);
            response.setAverageTransactionValue(averageTransaction);
            for( Map<String, Object> item: monthlyTransaction ) {
                monthlyTransactionDTOs.add(Util.mapToModel(item, ExtAdminTransactionMonthlyDTO.class));
            }
            response.setMonthlyTransactionValue(monthlyTransactionDTOs);
        } catch(Exception e) {
            Logging.handleException("AdminSalesService", "statistic(LocalDate yearStart, HttpServletRequest request)", 55, AdminConstant.ADMIN_SALES_SERVICE_STATISTIC_EXCEPTION, e.getMessage());
            logService.writeErrorLog(AdminConstant.ADMIN_SALES_SERVICE_STATISTIC_EXCEPTION, "AdminSalesService@statistic()", e.getMessage());
            return GlobalResponse.failed("Failed to get sales statistics!", AdminConstant.ADMIN_SALES_SERVICE_STATISTIC_EXCEPTION, null, request);
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
                transactions = transactionRepo.findAllByCreatedAtBetweenAndTransactionCodeContainsIgnoreCaseOrderByCreatedAt(startMonth, endMonth, keyword, pageable);
            } else {
                transactions = transactionRepo.findAllByCreatedAtBetweenOrderByCreatedAt(startMonth, endMonth, pageable);
            }

            if( transactions.isEmpty() ) {
                Logging.handleException("AdminSalesService", "list(int yearStart, int monthStart, String keyword, Pageable pageable, HttpServletRequest request)", 119, AdminConstant.ADMIN_SALES_SERVICE_LIST_EMPTY, "Sales list is empty");
                return GlobalResponse.failed("Failed to get sales list!", AdminConstant.ADMIN_SALES_SERVICE_LIST_EMPTY, null, request);
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
            Logging.handleException("AdminSalesService", "list(LocalDate monthStart, HttpServletRequest request)", 83, AdminConstant.ADMIN_SALES_SERVICE_LIST_EXCEPTION, e.getMessage());
            logService.writeErrorLog(AdminConstant.ADMIN_SALES_SERVICE_LIST_EXCEPTION, "AdminSalesService@list()", e.getMessage());
            return GlobalResponse.failed("Failed to get sales statistics!", AdminConstant.ADMIN_SALES_SERVICE_LIST_EXCEPTION, null, request);
        }

        return GlobalResponse.success("Successfully fetch sales statistics!", response, request);
    }

    public ResponseEntity<Object> detail(String transactionCode, HttpServletRequest request) {
        if( transactionCode == null || transactionCode.isEmpty() ) {
            return GlobalResponse.failed("Failed to get sales detail!", AdminConstant.ADMIN_SALES_SERVICE_DETAIL_CODE_REQUIRED, null, request);
        }

        RespAdminTransactionDetailDTO response = null;

        try {
            Optional<Transaction> opt = transactionRepo.findByTransactionCode(transactionCode);
            if( opt.isEmpty() ) {
                return GlobalResponse.failed("Failed to get sales detail!", AdminConstant.ADMIN_SALES_SERVICE_DETAIL_NOT_FOUND, null, request);
            }

            // Map trx to DTO
            Transaction transaction = opt.get();
            Hibernate.initialize(transaction.getUser());
            response = modelMapper.map(transaction, RespAdminTransactionDetailDTO.class);
            response.setCreatedAt(DatetimeFormatting.getDatetimeFormat(transaction.getCreatedAt()));
            response.setUpdatedAt(DatetimeFormatting.getDatetimeFormat(transaction.getUpdatedAt()));
            response.setTransactionTypeDesc(TransactionTypeConstant.getTransactionType(transaction.getTransactionType()));
            response.setStatusDesc(TransactionConstant.getStatus(transaction.getStatus()));

            List<ExtAdminTransactionItemDTO> itemList = new ArrayList<>();

            // Map item based on trx type
            switch( response.getTransactionType() ) {
                case TransactionTypeConstant.COURT_BOOKING:
                    BookingResponse booking = bookingHelper.getBookingDetails(response.getReferenceCode());
                    if( booking != null ) {
                        ExtAdminTransactionItemDTO court = new ExtAdminTransactionItemDTO();
                        court.setItemName(booking.getCourt().getCourtCode() + " - " + booking.getCourt().getCourtName());
                        court.setItemQty((int) Duration.between(booking.getStartTime(), booking.getEndTime()).toHours());
                        court.setItemUnit("jam");
                        court.setItemPrice(booking.getBasePrice().divide(BigDecimal.valueOf(court.getItemQty())));

                        itemList.add(court);

                        if( booking.getCoaches().size() > 0 ) {
                            for(CoachDetailResponse item: booking.getCoaches()) {
                                ExtAdminTransactionItemDTO coach = new ExtAdminTransactionItemDTO();
                                coach.setItemName(item.getCoachCode() + " - " + item.getCoachName());
                                coach.setItemQty(court.getItemQty());
                                coach.setItemUnit("jam");
                                coach.setItemPrice(item.getPricePerHour());

                                itemList.add(coach);
                            }
                        }

                        if( booking.getEquipment().size() > 0 ) {
                            for(EquipmentDetailResponse item: booking.getEquipment()) {
                                ExtAdminTransactionItemDTO equipment = new ExtAdminTransactionItemDTO();
                                equipment.setItemName(item.getEquipmentName());
                                equipment.setItemQty(item.getQuantity());
                                equipment.setItemUnit("");
                                equipment.setItemPrice(item.getEquipmentPrice());

                                itemList.add(equipment);
                            }
                        }
                    }

                    break;
                case TransactionTypeConstant.ECOMMERCE_SHOPPING:
                    RespOrderDetailDTO order = orderHelper.getOrderDetail(Long.parseLong(response.getReferenceCode()), response.getUser().getId());
                    if( order != null ) {
                        for(RespOrderItemDTO item: order.getItems()) {
                            ExtAdminTransactionItemDTO orderItem = new ExtAdminTransactionItemDTO();
                            orderItem.setItemName(item.getVariantName());
                            orderItem.setItemQty(item.getQuantity());
                            orderItem.setItemUnit("");
                            orderItem.setItemPrice(item.getTotalPrice());

                            itemList.add(orderItem);
                        }
                    }

                    break;
                case TransactionTypeConstant.WALLET_TOPUP:
                    ExtAdminTransactionItemDTO walletTopup = new ExtAdminTransactionItemDTO();
                    walletTopup.setItemName("Wallet Topup");
                    walletTopup.setItemQty(1);
                    walletTopup.setItemPrice(response.getTotalPrice());
                    walletTopup.setItemUnit("kali");

                    itemList.add(walletTopup);
                    break;
            }

            response.setItems(itemList);

        } catch(Exception e) {
            Logging.handleException("AdminSalesService", "detail(String transactionCode, HttpServletRequest request)", 109, AdminConstant.ADMIN_SALES_SERVICE_DETAIL_EXCEPTION, e.getMessage());
            logService.writeErrorLog(AdminConstant.ADMIN_SALES_SERVICE_DETAIL_EXCEPTION, "AdminSalesService@detail()", e.getMessage());
            return GlobalResponse.failed("Failed to get sales statistics!", AdminConstant.ADMIN_SALES_SERVICE_DETAIL_EXCEPTION, null, request);
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
