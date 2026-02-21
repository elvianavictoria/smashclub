package com.backendsyndicate.smashclub.admin.service.log;

import com.backendsyndicate.smashclub.admin.model.ErrorLog;
import com.backendsyndicate.smashclub.admin.repo.ActivityLogRepo;
import com.backendsyndicate.smashclub.admin.repo.ErrorLogRepo;
import org.springframework.beans.factory.annotation.Autowired;
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
