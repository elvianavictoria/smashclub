package com.backendsyndicate.smashclub.payment.service.helper;

import com.backendsyndicate.smashclub.common.constant.TransactionConstant;
import com.backendsyndicate.smashclub.common.util.GlobalResponse;
import com.backendsyndicate.smashclub.common.util.Logging;
import com.backendsyndicate.smashclub.common.constant.TransactionTypeConstant;
import com.backendsyndicate.smashclub.common.util.Util;
import com.backendsyndicate.smashclub.payment.dto.request.ReqUpdateBalanceDTO;
import com.backendsyndicate.smashclub.payment.dto.response.RespPaymentTransactionDTO;
import com.backendsyndicate.smashclub.payment.dto.response.RespRefundTransactionDTO;
import com.backendsyndicate.smashclub.payment.model.RefundRequest;
import com.backendsyndicate.smashclub.payment.model.Transaction;
import com.backendsyndicate.smashclub.payment.service.PaymentService;
import com.backendsyndicate.smashclub.payment.service.WalletService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.Optional;

@Service
public class PaymentHelper extends PaymentService {
    @Autowired
    private WalletService walletService;
    private ModelMapper modelMapper = new ModelMapper();

    public ResponseEntity<Object> paymentTransaction(String transactionCode, HttpServletRequest request) {
        ResponseEntity<Object> response = super.paymentTransaction(transactionCode, request);
        RespPaymentTransactionDTO dto = Util.mapToModel(response.getBody().toString(), RespPaymentTransactionDTO.class);

        Logging.printConsole(dto.getTransactionCode());
        Logging.printConsole(dto.getTransactionType() + "");

        try {
            switch( dto.getTransactionType() ) {
                case TransactionTypeConstant.COURT_BOOKING:
                    // Update booking status
                    break;
                case TransactionTypeConstant.ECOMMERCE_SHOPPING:
                    // Update order status
                    break;
                case TransactionTypeConstant.WALLET_TOPUP:
                    // Update balance
                    ReqUpdateBalanceDTO updateDTO = new ReqUpdateBalanceDTO();
                    updateDTO.setValue(dto.getTotalPrice());
                    updateDTO.setAddition(true);
                    boolean isTopupSuccess = walletService.updateBalance(dto.getUser().getId(), updateDTO);

                    if( !isTopupSuccess ) {
                        return GlobalResponse.failed("Failed to process transaction!", "PYMTCH-01E009", null, request);
                    }

                    break;
            }
        } catch(Exception e) {
            Logging.handleException("PaymentHelper", "paymentTransaction", 27, "PYMTCH-01E010", e.getMessage());
            return GlobalResponse.failed("Failed to process payment action!", "PYMTCH-01E010", null, request);
        }

        return response;
    }

    /**
     * Code: 03
     * Desc: Refund Request Procedure
     * 1. Check if transaction exists
     * 2. Check status
     * 3. Update status
     * 4. Write to log
     * 5. Write refund request
     *
     * (-) Controller
     *
     * @param transactionCode
     * @param refundReason
     * @return
     */
    public RespRefundTransactionDTO cancelTransaction(String transactionCode, String refundReason) {
        RespRefundTransactionDTO response = super.cancelTransaction(transactionCode, refundReason);

        try {

        } catch(Exception e) {
            Logging.handleException("PaymentService", "refundTransaction", 147, "PYMTCH-03E010", e.getMessage());
        }

        return response;
    }
}
