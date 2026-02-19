package com.backendsyndicate.smashclub.external.service.payment;

import com.backendsyndicate.smashclub.common.constant.TransactionConstant;
import com.backendsyndicate.smashclub.common.util.GlobalResponse;
import com.backendsyndicate.smashclub.common.util.Logging;
import com.backendsyndicate.smashclub.external.constant.XenditPaymentStatusConstant;
import com.backendsyndicate.smashclub.external.core.IWebhook;
import com.backendsyndicate.smashclub.external.dto.XenditWebhookDTO;
import com.backendsyndicate.smashclub.payment.model.Transaction;
import com.backendsyndicate.smashclub.payment.repo.TransactionRepo;
import com.backendsyndicate.smashclub.payment.service.helper.PaymentHelper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class XenditCallbackService implements IWebhook<XenditWebhookDTO> {
    @Autowired
    private TransactionRepo transactionRepo;
    @Autowired
    private PaymentHelper paymentService;

    private String generateErrorCode(String methodNo, String errorNo) {
        return "CLBK-XEN-" + methodNo + "E" + errorNo;
    }

    /**
     * 1. Take externalId, uuid, and status
     * 2. Find transaction using externalId
     * 3. Check if transaction status is UNPAID
     * 4. Check callback status, if it is PAID, change transaction status to PAID
     * 5. If it is EXPIRED, change transaction status to EXPIRED
     * 6. Remember, transactionLog must be filled when there is transaction status changes
     *
     * @param dto
     * @param request
     * @return
     */
    public ResponseEntity<Object> callback(XenditWebhookDTO dto, HttpServletRequest request) {
        try {
            switch(dto.getStatus()) {
                case XenditPaymentStatusConstant.PAID_STATUS:
                    paymentService.paymentTransaction(dto.getExternalId());
                    break;
                case XenditPaymentStatusConstant.EXPIRED_STATUS:
                    paymentService.expireTransaction(dto.getExternalId());
                    break;
            }

        } catch(Exception e) {
            Logging.handleException("XenditCallbackService", "callback(XenditWebhookDTO dto, HttpServletRequest request)", 41, generateErrorCode("01", "010"), e.getMessage());
            return GlobalResponse.success("Failed to process payment notification!",null, request);
        }

        return GlobalResponse.success("Payment notification has been processed!", null, request);
    }
}
