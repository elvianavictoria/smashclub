package com.backendsyndicate.smashclub.payment.service.helper;

import com.backendsyndicate.smashclub.admin.dto.extra.ExtAdminTransactionItemDTO;
import com.backendsyndicate.smashclub.admin.service.log.LogService;
import com.backendsyndicate.smashclub.booking.dto.request.BookingStatusUpdateRequest;
import com.backendsyndicate.smashclub.booking.dto.response.BookingResponse;
import com.backendsyndicate.smashclub.booking.dto.response.CoachDetailResponse;
import com.backendsyndicate.smashclub.booking.dto.response.EquipmentDetailResponse;
import com.backendsyndicate.smashclub.booking.service.helper.BookingHelper;
import com.backendsyndicate.smashclub.common.constant.BookingConstant;
import com.backendsyndicate.smashclub.common.constant.TransactionConstant;
import com.backendsyndicate.smashclub.common.constant.TransactionTypeConstant;
import com.backendsyndicate.smashclub.common.util.GlobalResponse;
import com.backendsyndicate.smashclub.common.util.Logging;
import com.backendsyndicate.smashclub.common.util.Util;
import com.backendsyndicate.smashclub.ecommerce.dto.response.RespOrderDetailDTO;
import com.backendsyndicate.smashclub.ecommerce.dto.response.RespOrderItemDTO;
import com.backendsyndicate.smashclub.ecommerce.service.helper.OrderHelper;
import com.backendsyndicate.smashclub.payment.dto.extra.ExtTransactionItemDTO;
import com.backendsyndicate.smashclub.payment.dto.response.RespTransactionDetailDTO;
import com.backendsyndicate.smashclub.payment.service.TransactionService;
import jakarta.servlet.http.HttpServletRequest;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class TransactionHelper extends TransactionService {
    @Autowired
    private LogService logService;
    @Autowired
    private BookingHelper bookingHelper;
    @Autowired
    private OrderHelper orderHelper;

    @Override
    public ResponseEntity<Object> findByCode(String code, HttpServletRequest request) {
        ResponseEntity<Object> response = super.findByCode(code, request);

        try {
            RespTransactionDetailDTO trx = Util.mapToModel(response.getBody(), RespTransactionDetailDTO.class);
            Hibernate.initialize(trx.getUser());
            List<ExtTransactionItemDTO> itemList = new ArrayList<ExtTransactionItemDTO>();

            switch(trx.getTransactionType()) {
                case TransactionTypeConstant.COURT_BOOKING:
                    BookingResponse booking = bookingHelper.getBookingDetails(trx.getReferenceCode());
                    if( booking != null ) {
                        ExtTransactionItemDTO court = new ExtTransactionItemDTO();
                        court.setItemName(booking.getCourt().getCourtCode() + " - " + booking.getCourt().getCourtName());
                        court.setItemQty((int) Duration.between(booking.getStartTime(), booking.getEndTime()).toHours());
                        court.setItemUnit("jam");
                        court.setItemPrice(booking.getBasePrice().divide(BigDecimal.valueOf(court.getItemQty())));

                        itemList.add(court);

                        if(!booking.getCoaches().isEmpty()) {
                            for(CoachDetailResponse item: booking.getCoaches()) {
                                ExtTransactionItemDTO coach = new ExtTransactionItemDTO();
                                coach.setItemName(item.getCoachCode() + " - " + item.getCoachName());
                                coach.setItemQty(court.getItemQty());
                                coach.setItemUnit("jam");
                                coach.setItemPrice(item.getPricePerHour());

                                itemList.add(coach);
                            }
                        }

                        if(!booking.getEquipment().isEmpty()) {
                            for(EquipmentDetailResponse item: booking.getEquipment()) {
                                ExtTransactionItemDTO equipment = new ExtTransactionItemDTO();
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
                    RespOrderDetailDTO order = orderHelper.getOrderDetail(Long.parseLong(trx.getReferenceCode()), trx.getUser().getId());
                    if( order != null ) {
                        for(RespOrderItemDTO item: order.getItems()) {
                            ExtTransactionItemDTO orderItem = new ExtTransactionItemDTO();
                            orderItem.setItemName(item.getVariantName());
                            orderItem.setItemQty(item.getQuantity());
                            orderItem.setItemUnit("");
                            orderItem.setItemPrice(item.getTotalPrice());

                            itemList.add(orderItem);
                        }
                    }

                    break;
                case TransactionTypeConstant.WALLET_TOPUP:
                    ExtTransactionItemDTO walletTopup = new ExtTransactionItemDTO();
                    walletTopup.setItemName("Wallet Topup");
                    walletTopup.setItemQty(1);
                    walletTopup.setItemPrice(trx.getTotalPrice());
                    walletTopup.setItemUnit("kali");

                    itemList.add(walletTopup);
                    break;
            }

            trx.setItems(itemList);

        } catch(Exception e) {
            Logging.handleException("TransactionService", "findByCode(String code, HttpServletRequest request)", 79, TransactionConstant.TRANSACTION_HELPER_ERROR_DETAIL_EXCEPTION, e.getMessage());
            logService.writeErrorLog(TransactionConstant.TRANSACTION_HELPER_ERROR_DETAIL_EXCEPTION, "TransactionService@findByCode()", e.getMessage());
            return GlobalResponse.failed("Failed to get transaction data!", TransactionConstant.TRANSACTION_HELPER_ERROR_DETAIL_EXCEPTION, null, request);
        }

        return response;

    }
}
