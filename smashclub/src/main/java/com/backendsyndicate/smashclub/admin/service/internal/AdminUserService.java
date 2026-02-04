package com.backendsyndicate.smashclub.admin.service.internal;

import com.backendsyndicate.smashclub.admin.core.ICRUD;
import com.backendsyndicate.smashclub.admin.dto.response.RespAdminUserDetailDTO;
import com.backendsyndicate.smashclub.admin.dto.response.RespAdminUserListDTO;
import com.backendsyndicate.smashclub.admin.model.AdminUser;
import com.backendsyndicate.smashclub.admin.repo.AdminUserRepo;
import com.backendsyndicate.smashclub.common.security.PasswordHasher;
import com.backendsyndicate.smashclub.common.util.DatetimeFormatting;
import com.backendsyndicate.smashclub.common.util.GlobalResponse;
import com.backendsyndicate.smashclub.common.util.Logging;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.function.Function;

@Service
@Transactional
public class AdminUserService implements ICRUD<AdminUser, Long> {
    @Autowired
    private AdminUserRepo adminUserRepo;
    private ModelMapper modelMapper = new ModelMapper();
    private PasswordHasher passwordHasher = new PasswordHasher();

    private String generateErrorCode(String methodNo, String errorNo) {
        return "ADM-USER" + "-" + methodNo + "-" + errorNo;
    }

    @Override
    public ResponseEntity<Object> findAll(String keyword, Pageable pageable, HttpServletRequest request) {
        Page page = null;

        try {
            if( !keyword.isEmpty() ) {
                page = adminUserRepo.findAll(pageable);
            } else {
                page = adminUserRepo.findAllByUsernameContainsOrFullNameContains(keyword, keyword, pageable);
            }

            if( page.isEmpty() ) {
                return GlobalResponse.failed("User list is empty!", generateErrorCode("01", "001"), null, request);
            }

            page = page.map(new Function<AdminUser, RespAdminUserListDTO>() {
                @Override
                public RespAdminUserListDTO apply(AdminUser adminUser) {
                    return mapListToDTO(adminUser);
                }
            });
        } catch(Exception e) {
            Logging.handleException("AdminUserService", "findAll(Pageable pageable, HttpServletRequest request)", 33, generateErrorCode("01", "010"), e.getMessage());
            return GlobalResponse.failed("Failed to get admin user list!", generateErrorCode("01", "010"), null, request);
        }

        return GlobalResponse.success("Successfully get admin user list!", page, request);
    }

    @Override
    public ResponseEntity<Object> findById(Long id, HttpServletRequest request) {
        RespAdminUserDetailDTO response = null;

        if( id == null ) {
            return GlobalResponse.failed("User ID is required!", generateErrorCode("02", "001"), null, request);
        }

        try {
            Optional<AdminUser> optionalAdminUser = adminUserRepo.findById(id);
            if( optionalAdminUser.isEmpty() ) {
                return GlobalResponse.failed("User not found!", generateErrorCode("02", "002"), null, request);
            }

            AdminUser adminUser = optionalAdminUser.get();
            response = modelMapper.map(adminUser, RespAdminUserDetailDTO.class);
        } catch(Exception e) {
            return GlobalResponse.failed("Failed to get user data!", generateErrorCode("02", "010"), null, request);
        }

        return GlobalResponse.success("User data found!", response, request);
    }

    @Override
    public ResponseEntity<Object> save(AdminUser adminUser, HttpServletRequest request) {
        if( adminUser == null ) {
            return GlobalResponse.failed("User data is required!", generateErrorCode("03", "001"), null, request);
        }

        if( adminUser.getPassword() == null || adminUser.getPassword().isEmpty() ) {
            return GlobalResponse.failed("Failed to save user data!", generateErrorCode("03", "002"), null, request);
        }

        try {
            adminUserRepo.save(adminUser);
        } catch(Exception e) {
            Logging.handleException("AdminUserService", "save(AdminUser adminUser, HttpServletRequest request)", 73, generateErrorCode("03", "010"), e.getMessage());
            return GlobalResponse.failed("Failed to save user data!", generateErrorCode("03", "010"), null, request);
        }

        return GlobalResponse.success("Successfully save user data!", null, request);
    }

    @Override
    public ResponseEntity<Object> update(Long id, AdminUser adminUser, HttpServletRequest request) {
        if( id == null ) {
            return GlobalResponse.failed("User ID is required!", generateErrorCode("04", "001"), null, request);
        }

        if( adminUser == null ) {
            return GlobalResponse.failed("User data is required!", generateErrorCode("04", "002"), null, request);
        }

        try {
            Optional<AdminUser> optionalAdminUser = adminUserRepo.findById(id);
            if( optionalAdminUser.isEmpty() ) {
                return GlobalResponse.failed("User data not found!", generateErrorCode("04", "003"), null, request);
            }

            Logging.printConsole("Admin Role: " + adminUser.getAdminRole());
            AdminUser adminUserDB = optionalAdminUser.get();
            adminUserDB.setAdminRole(adminUser.getAdminRole());
            adminUserDB.setUsername(adminUser.getUsername());
            adminUserDB.setFullName(adminUser.getFullName());
            if( adminUser.getPassword() != null && !adminUser.getPassword().isEmpty() ) {
                adminUserDB.setPassword(passwordHasher.hash(adminUser.getPassword()));
            }
            adminUserDB.setStatus(adminUser.getStatus());
        } catch(Exception e) {
            Logging.handleException("AdminUserService", "update(Long id, AdminUser adminUser, HttpServletRequest request)", 94, generateErrorCode("04", "010"), e.getMessage());
            return GlobalResponse.failed("Failed to update user data!", generateErrorCode("04", "010"), null, request);
        }

        return GlobalResponse.success("Successfully updated user data!", null, request);
    }

    @Override
    public ResponseEntity<Object> delete(Long id, HttpServletRequest request) {
        if( id == null ) {
            return GlobalResponse.failed("User ID is required!", generateErrorCode("05", "001"), null, request);
        }

        try {
            Optional<AdminUser> optionalAdminUser = adminUserRepo.findById(id);
            if( optionalAdminUser.isEmpty() ) {
                return GlobalResponse.failed("User data not found!", generateErrorCode("05", "002"), null, request);
            }

            adminUserRepo.deleteById(id);
        } catch(Exception e) {
            Logging.handleException("AdminUserService", "delete(Long id, HttpServletRequest request)", 120, generateErrorCode("05", "010"), e.getMessage());
            return GlobalResponse.failed("Failed to delete user data!", generateErrorCode("05", "010"), null, request);
        }

        return GlobalResponse.success("Successfully deleted user data!", null, request);
    }

    private RespAdminUserListDTO mapListToDTO(AdminUser adminUser) {
        RespAdminUserListDTO result = modelMapper.map(adminUser, RespAdminUserListDTO.class);
        if( adminUser.getCreatedAt() != null ) {
            result.setCreatedAt(DatetimeFormatting.getDatetimeFormat(adminUser.getCreatedAt()));
        }
        if( adminUser.getUpdatedAt() != null ) {
            result.setUpdatedAt(DatetimeFormatting.getDatetimeFormat(adminUser.getUpdatedAt()));
        }

        return result;
    }
}
