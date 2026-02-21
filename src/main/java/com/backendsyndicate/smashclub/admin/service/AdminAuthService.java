package com.backendsyndicate.smashclub.admin.service;

import com.backendsyndicate.smashclub.admin.core.IAuth;
import com.backendsyndicate.smashclub.admin.dto.response.RespAdminLoginDTO;
import com.backendsyndicate.smashclub.admin.dto.response.RespAdminLogoutDTO;
import com.backendsyndicate.smashclub.admin.dto.response.RespAdminUserDetailDTO;
import com.backendsyndicate.smashclub.admin.model.AdminSession;
import com.backendsyndicate.smashclub.admin.model.AdminUser;
import com.backendsyndicate.smashclub.admin.repo.AdminSessionRepo;
import com.backendsyndicate.smashclub.admin.repo.AdminUserRepo;
import com.backendsyndicate.smashclub.admin.security.jwt.AdminJwtModel;
import com.backendsyndicate.smashclub.admin.security.jwt.AdminJwtUtility;
import com.backendsyndicate.smashclub.admin.service.log.LogService;
import com.backendsyndicate.smashclub.common.config.AdminJwtConfig;
import com.backendsyndicate.smashclub.common.constant.AdminConstant;
import com.backendsyndicate.smashclub.common.constant.CommonConstant;
import com.backendsyndicate.smashclub.common.security.Crypto;
import com.backendsyndicate.smashclub.common.security.PasswordHasher;
import com.backendsyndicate.smashclub.common.util.GlobalResponse;
import com.backendsyndicate.smashclub.common.util.Logging;
import com.backendsyndicate.smashclub.external.dto.CloudinaryResponseDTO;
import com.backendsyndicate.smashclub.external.service.storage.CloudinaryService;
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
import org.springframework.web.multipart.MultipartFile;

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
    @Autowired
    private AdminSessionService adminSessionService;
    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private LogService logService;

    private ModelMapper modelMapper = new ModelMapper();
    private PasswordHasher passwordHasher = new PasswordHasher();

    @Override
    public ResponseEntity<Object> login(String username, String password, HttpServletRequest request) {
        if( username == null || password == null ) {
            return GlobalResponse.failed("Credential is empty!", AdminConstant.ADMIN_AUTH_SERVICE_LOGIN_REQUEST_INVALID, null, request);
        }

        RespAdminLoginDTO response = null;

        try {
            AdminUser user = getUser(username);
            if( user == null ) {
                return GlobalResponse.failed("Login invalid!", AdminConstant.ADMIN_AUTH_SERVICE_LOGIN_NOT_FOUND, null, request);
            }

            if( user.getPassword() == null || !passwordHasher.verify(password, user.getPassword()) ) {
                return GlobalResponse.failed("Login invalid!", AdminConstant.ADMIN_AUTH_SERVICE_LOGIN_PASSWORD_INVALID, null, request);
            }

            if( user.getStatus() != CommonConstant.STATUS_ACTIVE) {
                return GlobalResponse.failed("Account is locked!", AdminConstant.ADMIN_AUTH_SERVICE_LOGIN_STATUS_INACTIVE, null, request);
            }

            String accessToken = adminSessionService.saveSession(user.getId(), user.getUsername(), user.getFullName());
            if( accessToken.isEmpty() ) {
                return GlobalResponse.failed("Failed to login!", AdminConstant.ADMIN_AUTH_SERVICE_LOGIN_TOKEN_INVALID, null, request);
            }

            response = modelMapper.map(user, RespAdminLoginDTO.class);
            response.setAccessToken(accessToken);
        } catch(Exception e) {
            Logging.handleException("AuthService", "login(String username, String password, HttpServletRequest request)", 67, AdminConstant.ADMIN_AUTH_SERVICE_LOGIN_EXCEPTION, e.getMessage());
            logService.writeErrorLog(AdminConstant.ADMIN_AUTH_SERVICE_LOGIN_EXCEPTION, "AdminAuthService@login()", e.getMessage());
            return GlobalResponse.failed("Login failed!", AdminConstant.ADMIN_AUTH_SERVICE_LOGIN_EXCEPTION, null, request);
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
                return GlobalResponse.failed("Logout failed!", AdminConstant.ADMIN_AUTH_SERVICE_LOGOUT_FAILED, response, request);
            }

        } catch(Exception e) {
            Logging.handleException("AuthService", "logout(String accessToken, HttpServletRequest request)", 89, AdminConstant.ADMIN_AUTH_SERVICE_LOGOUT_EXCEPTION, e.getMessage());
            logService.writeErrorLog(AdminConstant.ADMIN_AUTH_SERVICE_LOGOUT_EXCEPTION, "AdminAuthService@logout()", e.getMessage());
            return GlobalResponse.failed("Logout failed!", AdminConstant.ADMIN_AUTH_SERVICE_LOGOUT_EXCEPTION, response, request);
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
            return GlobalResponse.unauthorized("Unauthorized access!", AdminConstant.ADMIN_AUTH_SERVICE_AUTHENTICATED_TOKEN_REQUIRED, request);
        }

        RespAdminLoginDTO response = null;

        try {
            AdminJwtModel data = adminSessionService.getSessionData(accessToken);
            if( data == null ) {
                return GlobalResponse.unauthorized("Unauthorized access!", AdminConstant.ADMIN_AUTH_SERVICE_AUTHENTICATED_TOKEN_INVALID, request);
            }

            AdminUser user = getUser(data.getUsername());
            if( user == null ) {
                return GlobalResponse.unauthorized("Unauthorized access!", AdminConstant.ADMIN_AUTH_SERVICE_AUTHENTICATED_NOT_FOUND, request);
            }

            if( user.getStatus() != CommonConstant.STATUS_ACTIVE) {
                return GlobalResponse.failed("Account is locked!", AdminConstant.ADMIN_AUTH_SERVICE_AUTHENTICATED_STATUS_INACTIVE, null, request);
            }

            response = modelMapper.map(user, RespAdminLoginDTO.class);
            response.setAccessToken(accessToken);
        } catch(Exception e) {
            Logging.handleException("AuthService", "isAuthenticated(String accessToken, HttpServletRequest request)", 130, AdminConstant.ADMIN_AUTH_SERVICE_AUTHENTICATED_EXCEPTION, e.getMessage());
            logService.writeErrorLog(AdminConstant.ADMIN_AUTH_SERVICE_AUTHENTICATED_EXCEPTION, "AdminAuthService@isAuthenticated()", e.getMessage());
            return GlobalResponse.unauthorized("Unauthenticated!", AdminConstant.ADMIN_AUTH_SERVICE_AUTHENTICATED_EXCEPTION, request);
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
            Logging.handleException("AuthService", "getUser(String username)", 155, AdminConstant.ADMIN_AUTH_SERVICE_GET_EXCEPTION, e.getMessage());
            logService.writeErrorLog(AdminConstant.ADMIN_AUTH_SERVICE_GET_EXCEPTION, "AdminAuthService@getUser()", e.getMessage());
            return null;
        }

        return user;
    }

    /**
     * 1. Find userId from session token
     * 2.
     *
     * @param accessToken
     * @param user
     * @param profilePicture
     * @param request
     * @return
     */
    public ResponseEntity<Object> update(String accessToken, AdminUser user, MultipartFile profilePicture, HttpServletRequest request) {
        if( accessToken.isEmpty() ) {
            return GlobalResponse.unauthorized("Failed to update user profile!", AdminConstant.ADMIN_AUTH_SERVICE_UPDATE_TOKEN_REQUIRED, request);
        }

        RespAdminUserDetailDTO response = null;

        try {
            AdminJwtModel data = adminSessionService.getSessionData(accessToken);
            if( data == null ) {
                return GlobalResponse.failed("Failed to update user profile!", AdminConstant.ADMIN_AUTH_SERVICE_UPDATE_TOKEN_INVALID, null, request);
            }

            String profilePictureLink = "";
            if( profilePicture != null ) {
                profilePictureLink = cloudinaryService.uploadImageGetUrl("admin/user", profilePicture);
            }

            AdminUser userDB = getUser(data.getUsername());
            if( userDB == null ) {
                return GlobalResponse.failed("Failed to update user profile!", AdminConstant.ADMIN_AUTH_SERVICE_UPDATE_NOT_FOUND, null, request);
            }

            userDB.setUsername(user.getUsername());
            userDB.setFullName(user.getFullName());
            if( user.getPassword() != null && !user.getPassword().isEmpty() ) userDB.setPassword(Crypto.performEncrypt(user.getPassword()));
            if( profilePictureLink != null && !profilePictureLink.isEmpty() ) userDB.setProfilePicture(profilePictureLink);

            response = modelMapper.map(userDB, RespAdminUserDetailDTO.class);
        } catch(Exception e) {
            Logging.handleException("AuthService", "update(String authToken, AdminUser user, MultipartFile profilePicture, HttpServletRequest request)", 190, AdminConstant.ADMIN_AUTH_SERVICE_UPDATE_EXCEPTION, e.getMessage());
            logService.writeErrorLog(AdminConstant.ADMIN_AUTH_SERVICE_UPDATE_EXCEPTION, "AdminAuthService@update()", e.getMessage());
            return GlobalResponse.failed("Failed to update user profile!", AdminConstant.ADMIN_AUTH_SERVICE_UPDATE_EXCEPTION, null, request);
        }

        return GlobalResponse.success("Success", response, request);
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
