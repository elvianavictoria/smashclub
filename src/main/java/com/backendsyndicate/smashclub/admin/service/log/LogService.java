package com.backendsyndicate.smashclub.admin.service.log;

import com.backendsyndicate.smashclub.admin.model.ErrorLog;
import com.backendsyndicate.smashclub.admin.repo.ActivityLogRepo;
import com.backendsyndicate.smashclub.admin.repo.ErrorLogRepo;
import com.backendsyndicate.smashclub.common.constant.AdminConstant;
import com.backendsyndicate.smashclub.common.util.GlobalResponse;
import com.backendsyndicate.smashclub.common.util.Logging;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LogService {
    @Autowired
    private ActivityLogRepo activityLogRepo;
    @Autowired
    private ErrorLogRepo errorLogRepo;

    public void writeActivityLog() {}

    public ResponseEntity<Object> getErrorLogs(Pageable pageable, HttpServletRequest request) {
        Page<ErrorLog> response = null;

        try {
            response = errorLogRepo.findAll(pageable);
        } catch(Exception e) {
            Logging.handleException("LogService", "getErrorLogs(Pageable pageable, HttpServletRequest request)", 26, AdminConstant.ADMIN_LOG_SERVICE_LIST_EXCEPTION, e.getMessage());
        }

        return GlobalResponse.success("Successfully get error logs!", response, request);
    }

    public void writeErrorLog(String errorCode, String module, String description) {
        try {
            ErrorLog errorLog = new ErrorLog();
            errorLog.setErrorCode(errorCode);
            errorLog.setModule(module.substring(0, 255));
            errorLog.setDescription(description);
            errorLogRepo.save(errorLog);
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
