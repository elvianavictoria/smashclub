package com.backendsyndicate.smashclub.payment.service.helper;

import com.backendsyndicate.smashclub.common.util.GlobalResponse;
import com.backendsyndicate.smashclub.common.util.Logging;
import com.backendsyndicate.smashclub.common.constant.TransactionTypeConstant;
import com.backendsyndicate.smashclub.payment.dto.request.ReqUpdateBalanceDTO;
import com.backendsyndicate.smashclub.payment.dto.response.RespPaymentTransactionDTO;
import com.backendsyndicate.smashclub.payment.service.PaymentService;
import com.backendsyndicate.smashclub.payment.service.WalletService;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class PaymentHelper extends PaymentService {
    @Autowired
    private WalletService walletService;
    private ModelMapper modelMapper = new ModelMapper();

    public ResponseEntity<Object> paymentTransaction(String transactionCode, int paymentMethodId, HttpServletRequest request) {
        ResponseEntity<Object> response = super.paymentTransaction(transactionCode, paymentMethodId, request);
        RespPaymentTransactionDTO dto = modelMapper.map(response, RespPaymentTransactionDTO.class);

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
            Logging.handleException("PaymentHelper", "paymentTransaction", 27, "PYMT-CH-01-E-010", e.getMessage());
            return GlobalResponse.failed("Failed to process payment action!", "PYMTCH-01E010", null, request);
        }

        return response;
    }
}
