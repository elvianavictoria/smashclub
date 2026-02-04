package com.backendsyndicate.smashclub.admin.service;

import com.backendsyndicate.smashclub.admin.core.IAuth;
import com.backendsyndicate.smashclub.admin.dto.response.RespLoginDTO;
import com.backendsyndicate.smashclub.admin.dto.response.RespLogoutDTO;
import com.backendsyndicate.smashclub.admin.model.AdminSession;
import com.backendsyndicate.smashclub.admin.model.AdminUser;
import com.backendsyndicate.smashclub.admin.repo.AdminSessionRepo;
import com.backendsyndicate.smashclub.admin.repo.AdminUserRepo;
import com.backendsyndicate.smashclub.common.constant.CommonConstant;
import com.backendsyndicate.smashclub.common.security.PasswordHasher;
import com.backendsyndicate.smashclub.common.util.GlobalResponse;
import com.backendsyndicate.smashclub.common.util.Logging;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * User can only be created to existing user, so no REGISTRATION
 */
@Service
@Transactional
public class AdminAuthService implements IAuth {
    @Autowired
    private AdminUserRepo adminUserRepo;
    @Autowired
    private AdminSessionRepo adminSessionRepo;

    private ModelMapper modelMapper = new ModelMapper();
    private PasswordHasher passwordHasher = new PasswordHasher();

    private String generateErrorCode(String methodNo, String errorNo) {
        return "ADM-AUTH" + "-" + methodNo + "-" + errorNo;
    }

    @Override
    public ResponseEntity<Object> login(String username, String password, HttpServletRequest request) {
        if( username == null || password == null ) {
            return GlobalResponse.failed("Credential is empty!", generateErrorCode("01", "001"), null, request);
        }

        RespLoginDTO response = null;

        try {
            Optional<AdminUser> opt = adminUserRepo.findByUsername(username);
            if( opt.isEmpty() ) {
                return GlobalResponse.failed("Login invalid!", generateErrorCode("01", "002"), null, request);
            }

            AdminUser user = opt.get();
            if( user.getPassword() != passwordHasher.hash(password)) {
                return GlobalResponse.failed("Login invalid!", generateErrorCode("01", "003"), null, request);
            }

            if( user.getStatus() != CommonConstant.STATUS_ACTIVE) {
                return GlobalResponse.failed("Account is locked!", generateErrorCode("01", "004"), null, request);
            }

            String accessToken = saveSession(user.getId());
            if( accessToken.isEmpty() ) {
                return GlobalResponse.failed("Failed to login!", generateErrorCode("01", "005"), null, request);
            }

            response = modelMapper.map(user, RespLoginDTO.class);
            response.setAccessToken(accessToken);
        } catch(Exception e) {
            Logging.handleException("AuthService", "login(String username, String password, HttpServletRequest request)", 35, generateErrorCode("01", "010"), e.getMessage());
            return GlobalResponse.failed("Login failed!", generateErrorCode("01", "010"), null, request);
        }

        return GlobalResponse.success("Successfully login admin page!", response, request);
    }

    @Override
    public ResponseEntity<Object> logout(String authToken, HttpServletRequest request) {
        RespLogoutDTO response = new RespLogoutDTO();
        response.setLoggedOut(false);

        try {
            boolean invalidated = invalidateToken(authToken);
            response.setLoggedOut(invalidated);

            if( !response.isLoggedOut() ) {
                return GlobalResponse.failed("Logout failed!", generateErrorCode("02", "001"), response, request);
            }

        } catch(Exception e) {
            Logging.handleException("AuthService", "logout(String authToken, HttpServletRequest request)", 89, generateErrorCode("02", "010"), e.getMessage());
            return GlobalResponse.failed("Logout failed!", generateErrorCode("02", "010"), response, request);
        }

        return GlobalResponse.success("Logout success!", response, request);
    }

    /**
     * 1. Extract token
     * 2. Get user ID
     * 3. Check active session with claimed user ID
     *
     * @param authToken
     * @return
     */
    @Override
    public ResponseEntity<Object> isAuthenticated(String authToken, HttpServletRequest request) {
        /**
         * return adminSessionRepo.countByAdminUser_IdAndStatus(userId, CommonConstant.STATUS_ACTIVE) > 0;
         */

        return GlobalResponse.success("This user is authenticated!", Map.of("isAuthenticated", true), request);
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
    private String saveSession(long userId) {
        LocalDateTime currentDatetime = LocalDateTime.now();
        List<AdminSession> activeSessions = adminSessionRepo.findAllByAdminUser_Id(userId);
        if( activeSessions.isEmpty() ) {
            return "";
        }

        for( int i = 0; i < activeSessions.size(); i++ ) {
            AdminSession session = activeSessions.get(i);
            session.setStatus(CommonConstant.STATUS_INACTIVE);
        }

        // Generate token
        String accessToken = "";

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
    }

    /**
     * 1. Find session with input token
     * 2. Invalidate that session
     * 3. Return true if success
     *
     * @param token
     * @return
     */
    private boolean invalidateToken(String token) {
        Optional<AdminSession> currentSession = adminSessionRepo.findByLoginToken(token);
        if( currentSession.isEmpty() ) {
            Logging.handleException("AuthService", "invalidateToken(String token)", 155, generateErrorCode("INV-TKN", "001"), "Session token " + token + " not found!");
            return false;
        }

        AdminSession session = currentSession.get();
        session.setStatus(CommonConstant.STATUS_INACTIVE);

        return true;
    }
}
