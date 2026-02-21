package com.backendsyndicate.smashclub.admin.service.report;

import com.backendsyndicate.smashclub.admin.dto.relation.RelAdminTransactionListDTO;
import com.backendsyndicate.smashclub.admin.dto.response.RespAdminRefundRequestListDTO;
import com.backendsyndicate.smashclub.admin.service.log.LogService;
import com.backendsyndicate.smashclub.common.constant.AdminConstant;
import com.backendsyndicate.smashclub.common.constant.CommonConstant;
import com.backendsyndicate.smashclub.common.constant.TransactionConstant;
import com.backendsyndicate.smashclub.common.service.TemplateService;
import com.backendsyndicate.smashclub.common.util.DatetimeFormatting;
import com.backendsyndicate.smashclub.common.util.GlobalResponse;
import com.backendsyndicate.smashclub.common.util.Logging;
import com.backendsyndicate.smashclub.common.util.Util;
import com.backendsyndicate.smashclub.external.service.notification.MailService;
import com.backendsyndicate.smashclub.payment.dto.request.ReqUpdateBalanceDTO;
import com.backendsyndicate.smashclub.payment.model.RefundRequest;
import com.backendsyndicate.smashclub.payment.model.Transaction;
import com.backendsyndicate.smashclub.payment.repo.RefundRequestRepo;
import com.backendsyndicate.smashclub.payment.service.PaymentService;
import com.backendsyndicate.smashclub.payment.service.WalletService;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Service
@Transactional
public class AdminRefundRequestService {
    @Autowired
    private RefundRequestRepo refundRequestRepo;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private WalletService walletService;
    @Autowired
    private MailService mailService;
    @Autowired
    private LogService logService;

    private ModelMapper modelMapper = new ModelMapper();

    public ResponseEntity<Object> list(LocalDate startDate, LocalDate endDate, String keyword, Integer status, Pageable pageable, HttpServletRequest request) {
        Page<RespAdminRefundRequestListDTO> response = null;

        try {
            Page<RefundRequest> page = refundRequestList(LocalDateTime.of(startDate, LocalTime.of(0, 0)), LocalDateTime.of(endDate, LocalTime.of(0, 0)), status, pageable);
            if( page == null || page.isEmpty() ) {
                return GlobalResponse.failed("Failed to get refund request list!", AdminConstant.ADMIN_REFUND_REQUEST_SERVICE_LIST_EMPTY, null, request);
            }

            response = page.map(new Function<RefundRequest, RespAdminRefundRequestListDTO>() {
                @Override
                public RespAdminRefundRequestListDTO apply(RefundRequest refundRequest) {
                    return mapListToDTO(refundRequest);
                }
            });

        } catch(Exception e) {
            Logging.handleException("AdminRefundRequestService", "list(LocalDate startDate, LocalDate endDate, Pageable pageable, HttpServletRequest request)", 32, AdminConstant.ADMIN_REFUND_REQUEST_SERVICE_LIST_EXCEPTION, e.getMessage());
            logService.writeErrorLog(AdminConstant.ADMIN_REFUND_REQUEST_SERVICE_LIST_EXCEPTION, "AdminRefundRequestService@list()", e.getMessage());
            return GlobalResponse.failed("Failed to get refund request list!", AdminConstant.ADMIN_REFUND_REQUEST_SERVICE_LIST_EXCEPTION, null, request);

        }

        return GlobalResponse.success("Successfully get refund request list!", response, request);
    }

    public ResponseEntity<Object> process(Long id, int refundStatus, String refundNotes, HttpServletRequest request) {
        if( id == null ) {
            return GlobalResponse.failed("Failed to process refund request list!", AdminConstant.ADMIN_REFUND_REQUEST_SERVICE_PROCESS_ID_REQUIRED, null, request);
        }

        String action = "processed";

        try {
            Optional<RefundRequest> opt = refundRequestRepo.findById(id);
            if( opt.isEmpty() ) {
                return GlobalResponse.failed("Failed to process refund request!", AdminConstant.ADMIN_REFUND_REQUEST_SERVICE_PROCESS_NOT_FOUND, null, request);
            }

            RefundRequest refundRequest = opt.get();
            if( refundRequest.getRefundStatus() != TransactionConstant.REFUND_REQUESTED ) {
                return GlobalResponse.failed("This refund request has been processed!", AdminConstant.ADMIN_REFUND_REQUEST_SERVICE_PROCESS_PROCESSED, null, request);
            }

            refundRequest.setRefundStatus((byte) refundStatus);
            refundRequest.setRefundNotes(refundNotes);

            if( refundStatus == TransactionConstant.REFUND_APPROVED ) {
                action = "approved";

                refundRequest.getTransaction().setStatus((byte) TransactionConstant.PAYMENT_REFUNDED);
                refundRequest.getTransaction().setIsRefunded((byte) CommonConstant.STATUS_ACTIVE);

                ReqUpdateBalanceDTO updateBalanceDTO = new ReqUpdateBalanceDTO();
                updateBalanceDTO.setValue(refundRequest.getTransaction().getTotalPrice());
                updateBalanceDTO.setAddition(true);
                walletService.updateBalance(refundRequest.getTransaction().getUser().getId(), updateBalanceDTO);

                Map<String, Object> data = new HashMap<>();
                data.put("fullName", refundRequest.getTransaction().getUser().getFullName());
                data.put("transactionCode", refundRequest.getTransaction().getTransactionCode());
                data.put("refundAmount", Util.formatCurrency(refundRequest.getTransaction().getTotalPrice()));
                mailService.sendMail(TemplateService.TEMPLATE_REFUND_NOTIFY_APPROVED, refundRequest.getTransaction().getUser().getEmail(), "Smashclub - Update Pengajuan Pengembalian Dana", data);
            } else {
                action = "rejected";

                Map<String, Object> data = new HashMap<>();
                data.put("transactionCode", refundRequest.getTransaction().getTransactionCode());
                data.put("fullName", refundRequest.getTransaction().getUser().getFullName());
                data.put("refundNotes", refundRequest.getRefundNotes());
                mailService.sendMail(TemplateService.TEMPLATE_REFUND_NOTIFY_REJECTED, refundRequest.getTransaction().getUser().getEmail(), "Smashclub - Update Pengajuan Pengembalian Dana", data);
            }
        } catch(Exception e) {
            Logging.handleException("AdminRefundRequestService", "process(long id, int refundStatus, String notes, HttpServletRequest request)", 59, AdminConstant.ADMIN_REFUND_REQUEST_SERVICE_PROCESS_EXCEPTION, e.getMessage());
            logService.writeErrorLog(AdminConstant.ADMIN_REFUND_REQUEST_SERVICE_PROCESS_EXCEPTION, "AdminRefundRequestService@process()", e.getMessage());
            return GlobalResponse.failed("Failed to get refund request list!", AdminConstant.ADMIN_REFUND_REQUEST_SERVICE_PROCESS_EXCEPTION, null, request);
        }

        return GlobalResponse.success(String.format("Successfully %s refund request!", action), null, request);
    }

    private Page<RefundRequest> refundRequestList(LocalDateTime startDate, LocalDateTime endDate, Integer status, Pageable pageable) {
        Page<RefundRequest> page = null;

        if( status != null ) {
            page = refundRequestRepo.findAllByCreatedAtBetweenAndRefundStatus(startDate, endDate, status.byteValue(), pageable);
        } else {
            page =refundRequestRepo.findAllByCreatedAtBetween(startDate, endDate, pageable);
        }

        return page;
    }

    private RespAdminRefundRequestListDTO mapListToDTO(RefundRequest refundRequest) {
        RespAdminRefundRequestListDTO result = modelMapper.map(refundRequest, RespAdminRefundRequestListDTO.class);

        if( refundRequest.getCreatedAt() != null ) {
            result.setCreatedAt(DatetimeFormatting.getDatetimeFormat(refundRequest.getCreatedAt()));
        }

        result.setRefundStatusDesc(TransactionConstant.getRefundStatus(refundRequest.getRefundStatus()));

        return result;
    }
}
