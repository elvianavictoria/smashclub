package com.backendsyndicate.smashclub.admin.service;

import com.backendsyndicate.smashclub.admin.model.AdminSession;
import com.backendsyndicate.smashclub.admin.model.AdminUser;
import com.backendsyndicate.smashclub.admin.repo.AdminSessionRepo;
import com.backendsyndicate.smashclub.admin.security.jwt.AdminJwtModel;
import com.backendsyndicate.smashclub.admin.security.jwt.AdminJwtUtility;
import com.backendsyndicate.smashclub.common.config.AdminJwtConfig;
import com.backendsyndicate.smashclub.common.constant.CommonConstant;
import com.backendsyndicate.smashclub.common.security.Crypto;
import com.backendsyndicate.smashclub.common.util.GlobalResponse;
import com.backendsyndicate.smashclub.common.util.Logging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AdminSessionService {
    @Autowired
    private AdminSessionRepo adminSessionRepo;
    @Autowired
    private AdminJwtUtility adminJwtUtility;

    private String generateErrorCode(String methodNo, String errorNo) {
        return "ADM-SESS" + "-" + methodNo + "-" + errorNo;
    }

    protected AdminJwtModel getSessionData(String accessToken) {
        if( accessToken.isEmpty() ) {
            Logging.handleException("AuthService", "getSessionData(String accessToken)", 35, generateErrorCode("01", "001"), "Session token is empty!");
        }

        AdminJwtModel sessionData = null;

        try {
            boolean isValid = isSessionValid(accessToken);
            if( !isValid ) {
                Logging.handleException("AuthService", "getSessionData(String accessToken)", 43, generateErrorCode("01", "002"), "Invalid session!");
                return sessionData;
            }

            sessionData = adminJwtUtility.mapToken(accessToken);
        } catch(Exception e) {
            Logging.handleException("AuthService", "getSessionData(String accessToken)", 41, generateErrorCode("01", "010"), e.getMessage());
        }

        return sessionData;
    }

    /**
     *
     * 1. Invalidate all token related to this user
     * 2. Generate new token
     * 3. Save to session record
     * 4. Return token
     *
     * @param userId
     * @return
     */
    protected String saveSession(long userId, String username, String fullName) {
        try {
            LocalDateTime currentDatetime = LocalDateTime.now();
            List<AdminSession> activeSessions = adminSessionRepo.findAllByAdminUser_Id(userId);
            if( !activeSessions.isEmpty() ) {
                for( int i = 0; i < activeSessions.size(); i++ ) {
                    AdminSession session = activeSessions.get(i);
                    session.setStatus(CommonConstant.STATUS_INACTIVE);
                }
            }

            // Generate token
            AdminJwtModel claims = new AdminJwtModel();
            claims.setUsername(username);
            String accessToken = adminJwtUtility.doGenerateToken(claims.convertToMap(), username);
            if( accessToken.isBlank() ) {
                return accessToken;
            }

            if(AdminJwtConfig.getEnableEncrypt().equals("y")){
                accessToken = Crypto.performEncrypt(accessToken);
            }

            AdminUser user = new AdminUser();
            user.setId(userId);

            AdminSession newSession = new AdminSession();
            newSession.setLoginToken(accessToken);
            newSession.setLoginTime(currentDatetime);
            newSession.setExpiredTime(currentDatetime.plusHours(24));
            newSession.setStatus(CommonConstant.STATUS_ACTIVE);
            newSession.setAdminUser(user);

            adminSessionRepo.save(newSession);

            return accessToken;
        } catch(Exception e) {
            Logging.handleException("AuthService", "saveSession(long userId, String username, String fullName)", 61, generateErrorCode("02", "010"), e.getMessage());
            return "";
        }
    }

    /**
     * 1. Find session with input token
     * 2. Invalidate that session
     * 3. Return true if success
     *
     * @param token
     * @return
     */
    protected boolean invalidateToken(String token) {
        Optional<AdminSession> currentSession = adminSessionRepo.findByLoginToken(token);
        if( currentSession.isEmpty() ) {
            Logging.handleException("AuthService", "invalidateToken(String token)", 114, generateErrorCode("03", "010"), "Session token " + token + " not found!");
            return false;
        }

        AdminSession session = currentSession.get();
        session.setStatus(CommonConstant.STATUS_INACTIVE);

        return true;
    }

    public boolean isSessionValid(String accessToken) {
        if( accessToken.isEmpty() ) {
            Logging.handleException("AuthService", "getSessionData(String accessToken)", 126, generateErrorCode("04", "010"), "Access token is empty!");
        }

        boolean isValid = false;

        try {
            int activeSessionCount = adminSessionRepo.countByLoginTokenAndStatus(accessToken, CommonConstant.STATUS_ACTIVE);
            Logging.printConsole("Active session count found: " + activeSessionCount);
            isValid = activeSessionCount > 0;
        } catch(Exception e) {
            Logging.handleException("AuthService", "isSessionValid(String token)", 130, generateErrorCode("04", "010"), e.getMessage());
        }

        return isValid;
    }
}
