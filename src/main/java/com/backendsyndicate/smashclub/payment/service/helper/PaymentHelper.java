package com.backendsyndicate.smashclub.payment.service.helper;

import com.backendsyndicate.smashclub.common.util.Logging;
import com.backendsyndicate.smashclub.common.constant.TransactionTypeConstant;
import com.backendsyndicate.smashclub.payment.dto.request.ReqUpdateBalanceDTO;
import com.backendsyndicate.smashclub.payment.dto.response.RespPaymentTransactionDTO;
import com.backendsyndicate.smashclub.payment.dto.response.RespCancelTransactionDTO;
import com.backendsyndicate.smashclub.payment.model.Transaction;
import com.backendsyndicate.smashclub.payment.service.PaymentService;
import com.backendsyndicate.smashclub.payment.service.WalletService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentHelper extends PaymentService {
    @Autowired
    private WalletService walletService;

    public RespPaymentTransactionDTO paymentTransaction(String transactionCode, HttpServletRequest request) {
        RespPaymentTransactionDTO response = null;

        try {
            Transaction trx = getTransaction(transactionCode);

            switch( trx.getTransactionType() ) {
                case TransactionTypeConstant.COURT_BOOKING:
                    // Update booking status
                    break;
                case TransactionTypeConstant.ECOMMERCE_SHOPPING:
                    // Update order status
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

            response = super.paymentTransaction(transactionCode, request);
        } catch(Exception e) {
            Logging.handleException("PaymentHelper", "paymentTransaction", 27, "PYMTCH-01E010", e.getMessage());
            return null;
        }

        return response;
    }

    /**
     * Code: 03
     *
     * @param transactionCode
     * @param refundReason
     * @return
     */
    public RespCancelTransactionDTO cancelTransaction(String transactionCode, String refundReason) {
        RespCancelTransactionDTO response = super.cancelTransaction(transactionCode, refundReason);

        if( response == null ) {
            return null;
        }

        try {
            switch( response.getTransactionType() ) {
                case TransactionTypeConstant.COURT_BOOKING:
                    // Update booking status
                    break;
                case TransactionTypeConstant.ECOMMERCE_SHOPPING:
                    // Update order status
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
}
