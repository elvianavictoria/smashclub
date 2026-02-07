package com.backendsyndicate.smashclub.admin.service;

import com.backendsyndicate.smashclub.admin.core.IAuth;
import com.backendsyndicate.smashclub.admin.dto.response.RespAdminLoginDTO;
import com.backendsyndicate.smashclub.admin.dto.response.RespAdminLogoutDTO;
import com.backendsyndicate.smashclub.admin.model.AdminSession;
import com.backendsyndicate.smashclub.admin.model.AdminUser;
import com.backendsyndicate.smashclub.admin.repo.AdminSessionRepo;
import com.backendsyndicate.smashclub.admin.repo.AdminUserRepo;
import com.backendsyndicate.smashclub.admin.security.jwt.AdminJwtModel;
import com.backendsyndicate.smashclub.admin.security.jwt.AdminJwtUtility;
import com.backendsyndicate.smashclub.common.config.AdminJwtConfig;
import com.backendsyndicate.smashclub.common.constant.CommonConstant;
import com.backendsyndicate.smashclub.common.security.Crypto;
import com.backendsyndicate.smashclub.common.security.PasswordHasher;
import com.backendsyndicate.smashclub.common.util.GlobalResponse;
import com.backendsyndicate.smashclub.common.util.Logging;
import jakarta.servlet.http.HttpServletRequest;
import org.hibernate.Hibernate;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
public class AdminAuthService implements IAuth, UserDetailsService {
    @Autowired
    private AdminUserRepo adminUserRepo;
    @Autowired
    private AdminSessionRepo adminSessionRepo;
    @Autowired
    private AdminSessionService adminSessionService;

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

        RespAdminLoginDTO response = null;

        try {
            AdminUser user = getUser(username);
            if( user == null ) {
                return GlobalResponse.failed("Login invalid!", generateErrorCode("01", "002"), null, request);
            }

            if( user.getPassword() == null || !passwordHasher.verify(password, user.getPassword()) ) {
                return GlobalResponse.failed("Login invalid!", generateErrorCode("01", "003"), null, request);
            }

            if( user.getStatus() != CommonConstant.STATUS_ACTIVE) {
                return GlobalResponse.failed("Account is locked!", generateErrorCode("01", "004"), null, request);
            }

            String accessToken = adminSessionService.saveSession(user.getId(), user.getUsername(), user.getFullName());
            if( accessToken.isEmpty() ) {
                return GlobalResponse.failed("Failed to login!", generateErrorCode("01", "005"), null, request);
            }

            response = modelMapper.map(user, RespAdminLoginDTO.class);
            response.setAccessToken(accessToken);
        } catch(Exception e) {
            Logging.handleException("AuthService", "login(String username, String password, HttpServletRequest request)", 35, generateErrorCode("01", "010"), e.getMessage());
            return GlobalResponse.failed("Login failed!", generateErrorCode("01", "010"), null, request);
        }

        return GlobalResponse.success("Successfully login admin page!", response, request);
    }

    @Override
    public ResponseEntity<Object> logout(String accessToken, HttpServletRequest request) {
        RespAdminLogoutDTO response = new RespAdminLogoutDTO();
        response.setLoggedOut(false);

        try {
            boolean invalidated = adminSessionService.invalidateToken(accessToken);
            response.setLoggedOut(invalidated);

            if( !response.isLoggedOut() ) {
                return GlobalResponse.failed("Logout failed!", generateErrorCode("02", "001"), response, request);
            }

        } catch(Exception e) {
            Logging.handleException("AuthService", "logout(String accessToken, HttpServletRequest request)", 89, generateErrorCode("02", "010"), e.getMessage());
            return GlobalResponse.failed("Logout failed!", generateErrorCode("02", "010"), response, request);
        }

        return GlobalResponse.success("Logout success!", response, request);
    }

    /**
     * 1. Extract token
     * 2. Get user ID
     * 3. Check active session with claimed user ID
     * 4. If still active, check user with user ID
     *
     * @param accessToken
     * @return
     */
    @Override
    public ResponseEntity<Object> isAuthenticated(String accessToken, HttpServletRequest request) {
        if( accessToken.isEmpty() ) {
            return GlobalResponse.unauthorized("Unauthorized access!", generateErrorCode("03", "001"), request);
        }

        RespAdminLoginDTO response = null;

        try {
            AdminJwtModel data = adminSessionService.getSessionData(accessToken);
            if( data == null ) {
                return GlobalResponse.unauthorized("Unauthorized access!", generateErrorCode("03", "002"), request);
            }

            AdminUser user = getUser(data.getUsername());
            if( user == null ) {
                return GlobalResponse.unauthorized("Unauthorized access!", generateErrorCode("03", "003"), request);
            }

            if( user.getStatus() != CommonConstant.STATUS_ACTIVE) {
                return GlobalResponse.failed("Account is locked!", generateErrorCode("03", "004"), null, request);
            }

            response = modelMapper.map(user, RespAdminLoginDTO.class);
            response.setAccessToken(accessToken);
        } catch(Exception e) {
            Logging.handleException("AuthService", "isAuthenticated(String accessToken, HttpServletRequest request)", 130, generateErrorCode("03", "010"), e.getMessage());
            return GlobalResponse.unauthorized("Unauthenticated!", generateErrorCode("03", "010"), request);
        }

        return GlobalResponse.success("This user is authenticated!", response, request);
    }

    private AdminUser getUser(String username) {
        AdminUser user = null;
        try {
            Optional<AdminUser> opt = adminUserRepo.findByUsername(username);
            if( opt.isEmpty() ) {
                return null;
            }

            user = opt.get();

            if( user.getAdminRole() != null ) {
                Logging.printConsole("User role is loaded!");
                Hibernate.initialize(user.getAdminRole());

                if( user.getAdminRole().getMenuSet() != null ) {
                    Logging.printConsole("Role menu is loaded!");
                    Hibernate.initialize(user.getAdminRole().getMenuSet());
                }

                if( user.getAdminRole().getPermissionSet() != null ) {
                    Logging.printConsole("Role permission is loaded!");
                    Hibernate.initialize(user.getAdminRole().getPermissionSet());
                }
            }
        } catch(Exception e) {
            Logging.handleException("AuthService", "getUser(String username)", 155, generateErrorCode("04", "010"), e.getMessage());
            return null;
        }

        return user;
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<AdminUser> opt = adminUserRepo.findByUsernameAndStatus(username, CommonConstant.STATUS_ACTIVE);
        if( opt.isEmpty() ) {
            throw new UsernameNotFoundException("Invalid credentials!");
        }
        AdminUser adminUser = opt.get();
        return new User(adminUser.getUsername(), adminUser.getPassword(), adminUser.getAuthorities());
    }
}
