package com.backendsyndicate.smashclub.admin.service.report;

import com.backendsyndicate.smashclub.admin.dto.extra.ExtAdminTransactionDTO;
import com.backendsyndicate.smashclub.admin.dto.response.RespAdminDashboardDTO;
import com.backendsyndicate.smashclub.admin.service.log.LogService;
import com.backendsyndicate.smashclub.booking.repository.CoachRepository;
import com.backendsyndicate.smashclub.booking.repository.CourtRepository;
import com.backendsyndicate.smashclub.common.constant.AdminConstant;
import com.backendsyndicate.smashclub.common.constant.TransactionConstant;
import com.backendsyndicate.smashclub.common.util.DatetimeFormatting;
import com.backendsyndicate.smashclub.common.util.GlobalResponse;
import com.backendsyndicate.smashclub.common.util.Logging;
import com.backendsyndicate.smashclub.payment.model.Transaction;
import com.backendsyndicate.smashclub.payment.repo.TransactionRepo;
import jakarta.servlet.http.HttpServletRequest;
import org.hibernate.Hibernate;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class AdminDashboardService {
    @Autowired
    private CourtRepository courtRepo;
    @Autowired
    private CoachRepository coachRepo;
    @Autowired
    private TransactionRepo transactionRepo;
    @Autowired
    private LogService logService;

    private ModelMapper modelMapper = new ModelMapper();

    /**
     * Dashboard:
     * 1. Court count
     * 2. Coach count
     * 3. Transaction Count
     * 4. 7 recent days transaction count
     * 5. Few recent transactions
     *
     * @return
     */
    public ResponseEntity<Object> dashboard(HttpServletRequest request) {
        RespAdminDashboardDTO response = new RespAdminDashboardDTO();

        try {
            LocalDateTime endDate = LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0));
            LocalDateTime startDate = endDate.minusDays(7);

            long courtCount = courtRepo.count();
            long coachCount = coachRepo.count();
            long trxCount = transactionRepo.count();
            Page<Transaction> recentTransactions = transactionRepo.findAllByCreatedAtBetween(startDate, endDate, PageRequest.of(0, 5));

            List<Transaction> recentTrxList = recentTransactions.getContent();

            response.setCourtCount(courtCount);
            response.setCoachCount(coachCount);
            response.setTransactionCount(trxCount);
            response.setDailyTransaction(recentTrxList.stream().map( trx -> {
                Hibernate.initialize(trx.getUser());

                ExtAdminTransactionDTO dto = modelMapper.map(trx, ExtAdminTransactionDTO.class);
                dto.setFullName(trx.getUser().getFullName());
                dto.setStatusDesc(TransactionConstant.getStatus(trx.getStatus()) );
                dto.setStartTime(DatetimeFormatting.getDatetimeFormat(trx.getCreatedAt()));
                return dto;
            } ).toList());

        } catch(Exception e) {
            Logging.handleException("AdminDashboardService", "dashboard(HttpServletRequest request)", 48, AdminConstant.ADMIN_DASHBOARD_SERVICE_DASHBOARD_EXCEPTION, e.getMessage());
            logService.writeErrorLog(AdminConstant.ADMIN_DASHBOARD_SERVICE_DASHBOARD_EXCEPTION, "AdminDashboardService@dashboard()", e.getMessage());
            return GlobalResponse.failed("Failed to get dashboard statistic!", AdminConstant.ADMIN_DASHBOARD_SERVICE_DASHBOARD_EXCEPTION, null, request);
        }

        return GlobalResponse.success("Successfully get dashboard statistic!", response, request);
    }
}
