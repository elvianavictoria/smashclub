package com.backendsyndicate.smashclub.payment.service.helper;

import com.backendsyndicate.smashclub.booking.dto.request.BookingStatusUpdateRequest;
import com.backendsyndicate.smashclub.booking.service.helper.BookingHelper;
import com.backendsyndicate.smashclub.common.constant.BookingConstant;
import com.backendsyndicate.smashclub.common.constant.OrderStatusConstant;
import com.backendsyndicate.smashclub.common.util.Logging;
import com.backendsyndicate.smashclub.common.constant.TransactionTypeConstant;
import com.backendsyndicate.smashclub.ecommerce.service.OrderService;
import com.backendsyndicate.smashclub.payment.dto.request.ReqUpdateBalanceDTO;
import com.backendsyndicate.smashclub.payment.dto.response.RespExpireTransactionDTO;
import com.backendsyndicate.smashclub.payment.dto.response.RespPaymentTransactionDTO;
import com.backendsyndicate.smashclub.payment.dto.response.RespCancelTransactionDTO;
import com.backendsyndicate.smashclub.payment.model.Transaction;
import com.backendsyndicate.smashclub.payment.service.PaymentService;
import com.backendsyndicate.smashclub.payment.service.TransactionService;
import com.backendsyndicate.smashclub.payment.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentHelper extends PaymentService {
    @Autowired
    private WalletService walletService;
    @Autowired
    private BookingHelper bookingService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private TransactionService transactionService;

    @Override
    public RespPaymentTransactionDTO paymentTransaction(String transactionCode) {
        RespPaymentTransactionDTO response = null;

        try {
            Transaction trx = transactionService.getTransaction(transactionCode);

            switch( trx.getTransactionType() ) {
                case TransactionTypeConstant.COURT_BOOKING:
                    // Update booking status
                    BookingStatusUpdateRequest statusUpdateData = new BookingStatusUpdateRequest();
                    statusUpdateData.setStatus(BookingConstant.BOOKING_CONFIRMED);
                    bookingService.updateBookingStatus(trx.getReferenceCode(), statusUpdateData);
                    break;
                case TransactionTypeConstant.ECOMMERCE_SHOPPING:
                    // Update order status
                    orderService.updateOrderStatus(trx.getReferenceCode(), OrderStatusConstant.ORDER_READY_FOR_PICKUP);
                    break;
                case TransactionTypeConstant.WALLET_TOPUP:
                    // Update balance
                    ReqUpdateBalanceDTO updateDTO = new ReqUpdateBalanceDTO();
                    updateDTO.setValue(trx.getTotalPrice());
                    updateDTO.setAddition(true);
                    boolean isTopupSuccess = walletService.updateBalance(trx.getUser().getId(), updateDTO);

                    if( !isTopupSuccess ) {
                        return null;
                    }

                    break;
            }

            response = super.paymentTransaction(transactionCode);
        } catch(Exception e) {
            Logging.handleException("PaymentHelper", "paymentTransaction(String transactionCode)", 27, "PYMTCH-01E010", e.getMessage());
            return null;
        }

        return response;
    }

    @Override
    public RespCancelTransactionDTO cancelTransaction(String transactionCode, String refundReason) {
        RespCancelTransactionDTO response = super.cancelTransaction(transactionCode, refundReason);

        if( response == null ) {
            return null;
        }

        try {
            switch( response.getTransactionType() ) {
                case TransactionTypeConstant.COURT_BOOKING:
                    // Update booking status
                    BookingStatusUpdateRequest statusUpdateData = new BookingStatusUpdateRequest();
                    statusUpdateData.setStatus(BookingConstant.BOOKING_CANCELLED);
                    bookingService.updateBookingStatus(response.getReferenceCode(), statusUpdateData);
                    break;
                case TransactionTypeConstant.ECOMMERCE_SHOPPING:
                    // Update order status
                    orderService.cancelOrder(response.getReferenceCode());
                    break;
                case TransactionTypeConstant.WALLET_TOPUP:
                    // Do nothing, since wallet is the refund container
                    break;
            }
        } catch(Exception e) {
            Logging.handleException("PaymentService", "cancelTransaction(String transactionCode, String refundReason)", 147, "PYMTCH-03E010", e.getMessage());
        }

        return response;
    }

    @Override
    public RespExpireTransactionDTO expireTransaction(String transactionCode) {
        RespExpireTransactionDTO response = super.expireTransaction(transactionCode);

        if( response == null ) {
            return null;
        }

        try {
            switch( response.getTransactionType() ) {
                case TransactionTypeConstant.COURT_BOOKING:
                    // Update booking status
                    BookingStatusUpdateRequest statusUpdateData = new BookingStatusUpdateRequest();
                    statusUpdateData.setStatus(BookingConstant.BOOKING_CANCELLED);
                    bookingService.updateBookingStatus(response.getReferenceCode(), statusUpdateData);
                    break;
                case TransactionTypeConstant.ECOMMERCE_SHOPPING:
                    // Update order status
                    orderService.cancelOrder(response.getReferenceCode());
                    break;
                case TransactionTypeConstant.WALLET_TOPUP:
                    // Do nothing, since wallet is the refund container
                    break;
            }
        } catch(Exception e) {
            Logging.handleException("PaymentService", "expireTransaction(String transactionCode)", 147, "PYMTCH-04E010", e.getMessage());
        }

        return response;
    }
}
