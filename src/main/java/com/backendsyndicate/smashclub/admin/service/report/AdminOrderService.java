package com.backendsyndicate.smashclub.admin.service.report;

import com.backendsyndicate.smashclub.admin.core.IStatistic;
import com.backendsyndicate.smashclub.admin.dto.extra.ExtAdminOrderCategoryRankingDTO;
import com.backendsyndicate.smashclub.admin.dto.extra.ExtAdminOrderMonthlyDTO;
import com.backendsyndicate.smashclub.admin.dto.response.RespAdminOrderDetailDTO;
import com.backendsyndicate.smashclub.admin.dto.response.RespAdminOrderListDTO;
import com.backendsyndicate.smashclub.admin.dto.response.RespAdminOrderStatisticDTO;
import com.backendsyndicate.smashclub.admin.service.log.LogService;
import com.backendsyndicate.smashclub.common.constant.AdminConstant;
import com.backendsyndicate.smashclub.common.constant.OrderStatusConstant;
import com.backendsyndicate.smashclub.common.util.GlobalResponse;
import com.backendsyndicate.smashclub.common.util.Logging;
import com.backendsyndicate.smashclub.common.util.Util;
import com.backendsyndicate.smashclub.ecommerce.model.Order;
import com.backendsyndicate.smashclub.ecommerce.repo.OrderItemRepo;
import com.backendsyndicate.smashclub.ecommerce.repo.OrderRepo;
import com.backendsyndicate.smashclub.ecommerce.service.helper.OrderHelper;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Service
@Transactional
public class AdminOrderService implements IStatistic {
    @Autowired
    private OrderRepo orderRepo;
    @Autowired
    private OrderItemRepo orderItemRepo;
    @Autowired
    private OrderHelper orderHelper;
    @Autowired
    private LogService logService;

    private ModelMapper modelMapper = new ModelMapper();

    /**
     * Display:
     * 1. Total quantity sold
     * 2. Most sold category
     * 3. Average Order Value
     * 4. Sold category ranking
     * 5. Monthly Order
     *
     * @param yearStart
     * @param request
     * @return
     */
    @Override
    public ResponseEntity<Object> statistic(int yearStart, HttpServletRequest request) {
        RespAdminOrderStatisticDTO response = null;

        try {
            LocalDateTime startYear = LocalDateTime.of(LocalDate.of(yearStart, 1, 1), LocalTime.of(0, 0, 0));
            LocalDateTime endYear = startYear.plusYears(1);

            int totalQtySold = orderItemRepo.sumQuantityByOrderDateBetween(startYear, endYear);
            BigDecimal averageOrderValue = orderRepo.averageTotalPriceByOrderDate(startYear, endYear);
            List<Map<String, Object>> soldCategoryRanking = orderItemRepo.findAllGroupByProduct_Category(startYear, endYear);
            List<Map<String, Object>> monthlyOrders = orderRepo.findAllGroupByOrderDateMonthly(startYear, endYear);

            response = new RespAdminOrderStatisticDTO();
            response.setTotalQtySold(totalQtySold);
            response.setAverageOrderValue(averageOrderValue);
            response.setSoldCategoryRanking(soldCategoryRanking.stream().map(new Function<Map<String, Object>, ExtAdminOrderCategoryRankingDTO>() {
                @Override
                public ExtAdminOrderCategoryRankingDTO apply(Map<String, Object> data) {
                    return Util.mapToModel(data, ExtAdminOrderCategoryRankingDTO.class);
                }
            }).toList());
            response.setMonthlyOrders(monthlyOrders.stream().map(new Function<Map<String, Object>, ExtAdminOrderMonthlyDTO>() {
                @Override
                public ExtAdminOrderMonthlyDTO apply(Map<String, Object> data) {
                    return Util.mapToModel(data, ExtAdminOrderMonthlyDTO.class);
                }
            }).toList());

        } catch(Exception e) {
            Logging.handleException("AdminOrderService", "statistic(LocalDate yearStart, HttpServletRequest request)", 64, AdminConstant.ADMIN_ORDER_SERVICE_STATISTIC_EXCEPTION, e.getMessage());
            logService.writeErrorLog(AdminConstant.ADMIN_ORDER_SERVICE_STATISTIC_EXCEPTION, "AdminOrderService@statistic()", e.getMessage());
            return GlobalResponse.failed("Failed to get order statistics!", AdminConstant.ADMIN_ORDER_SERVICE_STATISTIC_EXCEPTION, null, request);
        }

        return GlobalResponse.success("Successfully fetch order statistics!", response, request);
    }

    @Override
    public ResponseEntity<Object> list(int yearStart, int monthStart, String keyword, Pageable pageable, HttpServletRequest request) {
        Page<RespAdminOrderListDTO> response = null;

        try {
            LocalDateTime startMonth = LocalDateTime.of(LocalDate.of(yearStart, monthStart, 1), LocalTime.of(0, 0, 0));
            LocalDateTime endMonth = startMonth.plusMonths(1);

            Page<Order> orders = null;
            if( !keyword.isEmpty() ) {
                orders = orderRepo.findAllByOrderDateBetweenAndOrderCodeContainsIgnoreCase(startMonth, endMonth, keyword, pageable);
            } else {
                orders = orderRepo.findAllByOrderDateBetween(startMonth, endMonth, pageable);
            }

            if( orders.isEmpty() ) {
                Logging.handleException("AdminOrderService", "list(int yearStart, int monthStart, String keyword, Pageable pageable, HttpServletRequest request)", 102, AdminConstant.ADMIN_ORDER_SERVICE_LIST_EMPTY, "Order list is empty");
                return GlobalResponse.failed("Failed to get order list!", AdminConstant.ADMIN_ORDER_SERVICE_LIST_EMPTY, null, request);
            }

            response = orders.map(new Function<Order, RespAdminOrderListDTO>() {
                @Override
                public RespAdminOrderListDTO apply(Order order) {
                    return mapListToDTO(order);
                }
            });
            
        } catch(Exception e) {
            Logging.handleException("AdminOrderService", "list(LocalDate monthStart, HttpServletRequest request)", 83, AdminConstant.ADMIN_ORDER_SERVICE_LIST_EXCEPTION, e.getMessage());
            logService.writeErrorLog(AdminConstant.ADMIN_ORDER_SERVICE_LIST_EXCEPTION, "AdminOrderService@list()", e.getMessage());
            return GlobalResponse.failed("Failed to get order list!", AdminConstant.ADMIN_ORDER_SERVICE_LIST_EXCEPTION, null, request);
        }

        return GlobalResponse.success("Successfully fetch order statistics!", response, request);
    }

    public ResponseEntity<Object> detail(String orderCode, HttpServletRequest request) {
        if( orderCode == null || orderCode.isEmpty() ) {
            return GlobalResponse.failed("Failed to get order detail!", AdminConstant.ADMIN_ORDER_SERVICE_DETAIL_CODE_REQUIRED, null, request);
        }

        RespAdminOrderDetailDTO response = null;

        try {
            Optional<Order> opt = orderRepo.findByOrderCode(orderCode);
            if( opt.isEmpty() ) {
                return GlobalResponse.failed("Failed to get order detail!", AdminConstant.ADMIN_ORDER_SERVICE_DETAIL_NOT_FOUND, null, request);
            }

            // Map trx to DTO
            Order order = opt.get();
            Hibernate.initialize(order.getUser());
            Hibernate.initialize(order.getOrderItem());
            response = modelMapper.map(order, RespAdminOrderDetailDTO.class);
            response.setStatusDesc(OrderStatusConstant.getStatusLabel(order.getStatus()));
        } catch(Exception e) {
            Logging.handleException("AdminOrderService", "detail(String orderCode, HttpServletRequest request)", 109, AdminConstant.ADMIN_ORDER_SERVICE_DETAIL_EXCEPTION, e.getMessage());
            logService.writeErrorLog(AdminConstant.ADMIN_ORDER_SERVICE_DETAIL_EXCEPTION, "AdminOrderService@detail()", e.getMessage());
            return GlobalResponse.failed("Failed to get order detail!", AdminConstant.ADMIN_ORDER_SERVICE_DETAIL_EXCEPTION, null, request);
        }

        return GlobalResponse.success("Successfully fetch order detail!", response, request);
    }

    public ResponseEntity<Object> process(String orderCode, int status, HttpServletRequest request) {
        if( orderCode == null ) {
            return GlobalResponse.failed("Failed to process order!", AdminConstant.ADMIN_ORDER_SERVICE_PROCESS_CODE_REQUIRED, null, request);
        }

        try {
            orderHelper.updateOrderStatus(orderCode, (byte) status);
        } catch(Exception e) {
            Logging.handleException("AdminOrderService", "process(String orderCode, int status, HttpServletRequest request)", 206, AdminConstant.ADMIN_ORDER_SERVICE_PROCESS_EXCEPTION, e.getMessage());
            logService.writeErrorLog(AdminConstant.ADMIN_ORDER_SERVICE_PROCESS_EXCEPTION, "AdminOrderService@process()", e.getMessage());
            return GlobalResponse.failed("Failed to process order!", AdminConstant.ADMIN_ORDER_SERVICE_PROCESS_EXCEPTION, null, request);
        }

        return GlobalResponse.success("Successfully process order!", null, request);
    }

    private RespAdminOrderListDTO mapListToDTO(Order order) {
        RespAdminOrderListDTO result = modelMapper.map(order, RespAdminOrderListDTO.class);
        result.setStatusDesc(OrderStatusConstant.getStatusLabel(order.getStatus()));
        result.setCreatedAt(order.getOrderDate());

        return result;
    }
}
