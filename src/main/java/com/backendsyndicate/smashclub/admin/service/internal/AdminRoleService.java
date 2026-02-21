package com.backendsyndicate.smashclub.admin.service.internal;

import com.backendsyndicate.smashclub.admin.core.ICRUD;
import com.backendsyndicate.smashclub.admin.dto.relation.RelAdminRoleMenuDTO;
import com.backendsyndicate.smashclub.admin.dto.relation.RelAdminRolePermissionDTO;
import com.backendsyndicate.smashclub.admin.dto.response.RespAdminRoleDetailDTO;
import com.backendsyndicate.smashclub.admin.dto.response.RespAdminRoleListDTO;
import com.backendsyndicate.smashclub.admin.model.AdminRole;
import com.backendsyndicate.smashclub.admin.repo.AdminRoleRepo;
import com.backendsyndicate.smashclub.admin.service.log.LogService;
import com.backendsyndicate.smashclub.common.constant.AdminConstant;
import com.backendsyndicate.smashclub.common.constant.CommonConstant;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminRoleService implements ICRUD<AdminRole, Integer> {
    @Autowired
    private AdminRoleRepo adminRoleRepo;
    @Autowired
    private LogService logService;

    private ModelMapper modelMapper = new ModelMapper();

    @Override
    public ResponseEntity<Object> findAll(String keyword, Integer status, Pageable pageable, HttpServletRequest request) {
        Page page = null;

        try {
            page = roleList(keyword, status, pageable);

            if( page.isEmpty() ) {
                return GlobalResponse.failed("Role list is empty!", AdminConstant.ADMIN_ROLE_SERVICE_LIST_EMPTY, null, request);
            }

            page = page.map(adminRole -> { return modelMapper.map(adminRole, RespAdminRoleListDTO.class); });

        } catch(Exception e) {
            Logging.handleException("AdminRoleService", "findAll(Pageable pageable, HttpServletRequest request)", 33, AdminConstant.ADMIN_ROLE_SERVICE_LIST_EXCEPTION, e.getMessage());
            logService.writeErrorLog(AdminConstant.ADMIN_ROLE_SERVICE_LIST_EXCEPTION, "AdminRoleService@findAll()", e.getMessage());
            return GlobalResponse.failed("Failed to get role list!", AdminConstant.ADMIN_ROLE_SERVICE_LIST_EXCEPTION, null, request);
        }

        return GlobalResponse.success("Successfully get admin role list!", page, request);
    }

    @Override
    public ResponseEntity<Object> findById(Integer id, HttpServletRequest request) {
        RespAdminRoleDetailDTO response = null;

        if( id == null ) {
            return GlobalResponse.failed("Role ID is required!", AdminConstant.ADMIN_ROLE_SERVICE_DETAIL_ID_REQUIRED, null, request);
        }

        try {
            Optional<AdminRole> optionalAdminRole = adminRoleRepo.findById(id);
            if( optionalAdminRole.isEmpty() ) {
                return GlobalResponse.failed("Role not found!", AdminConstant.ADMIN_ROLE_SERVICE_DETAIL_NOT_FOUND, null, request);
            }

            AdminRole adminRole = optionalAdminRole.get();
            response = modelMapper.map(adminRole, RespAdminRoleDetailDTO.class);
            response.setMenuSet(adminRole.getMenuSet().stream().map(rowSet -> modelMapper.map(rowSet, RelAdminRoleMenuDTO.class)).collect(Collectors.toSet()));
            response.setPermissionSet(adminRole.getPermissionSet().stream().map(rowSet -> modelMapper.map(rowSet, RelAdminRolePermissionDTO.class)).collect(Collectors.toSet()));
        } catch(Exception e) {
            Logging.handleException("AdminRoleService", "findById(Integer id, HttpServletRequest request)", 69, AdminConstant.ADMIN_ROLE_SERVICE_DETAIL_EXCEPTION, e.getMessage());
            logService.writeErrorLog(AdminConstant.ADMIN_ROLE_SERVICE_DETAIL_EXCEPTION, "AdminRoleService@findById()", e.getMessage());
            return GlobalResponse.failed("Failed to get role data!", AdminConstant.ADMIN_ROLE_SERVICE_DETAIL_EXCEPTION, null, request);
        }

        return GlobalResponse.success("Role data found!", response, request);
    }

    @Override
    public ResponseEntity<Object> save(AdminRole adminRole, HttpServletRequest request) {
        if( adminRole == null ) {
            return GlobalResponse.failed("Role data is required!", AdminConstant.ADMIN_ROLE_SERVICE_SAVE_REQUEST_INVALID, null, request);
        }

        try {
            adminRoleRepo.save(adminRole);
        } catch(Exception e) {
            Logging.handleException("AdminRoleService", "save(AdminRole adminRole, HttpServletRequest request)", 73, AdminConstant.ADMIN_ROLE_SERVICE_SAVE_EXCEPTION, e.getMessage());
            logService.writeErrorLog(AdminConstant.ADMIN_ROLE_SERVICE_SAVE_EXCEPTION, "AdminRoleService@save()", e.getMessage());
            return GlobalResponse.failed("Failed to save role data!", AdminConstant.ADMIN_ROLE_SERVICE_SAVE_EXCEPTION, null, request);
        }

        return GlobalResponse.success("Successfully save role data!", null, request);
    }

    @Override
    public ResponseEntity<Object> update(Integer id, AdminRole adminRole, HttpServletRequest request) {
        if( id == null ) {
            return GlobalResponse.failed("Role ID is required!", AdminConstant.ADMIN_ROLE_SERVICE_UPDATE_ID_REQUIRED, null, request);
        }

        if( adminRole == null ) {
            return GlobalResponse.failed("Role data is required!", AdminConstant.ADMIN_ROLE_SERVICE_UPDATE_REQUEST_INVALID, null, request);
        }

        try {
            Optional<AdminRole> optionalAdminRole = adminRoleRepo.findById(id);
            if( optionalAdminRole.isEmpty() ) {
                return GlobalResponse.failed("Role data not found!", AdminConstant.ADMIN_ROLE_SERVICE_UPDATE_NOT_FOUND, null, request);
            }

            AdminRole adminRoleDB = optionalAdminRole.get();
            adminRoleDB.setRoleCode(adminRole.getRoleCode());
            adminRoleDB.setRoleName(adminRole.getRoleName());
            adminRoleDB.setStatus(adminRole.getStatus());
            adminRoleDB.setMenuSet(adminRole.getMenuSet());
            adminRoleDB.setPermissionSet(adminRole.getPermissionSet());
        } catch(Exception e) {
            Logging.handleException("AdminRoleService", "update(Long id, AdminRole adminRole, HttpServletRequest request)", 94, AdminConstant.ADMIN_ROLE_SERVICE_UPDATE_EXCEPTION, e.getMessage());
            logService.writeErrorLog(AdminConstant.ADMIN_ROLE_SERVICE_UPDATE_EXCEPTION, "AdminRoleService@update()", e.getMessage());
            return GlobalResponse.failed("Failed to update role data!", AdminConstant.ADMIN_ROLE_SERVICE_UPDATE_EXCEPTION, null, request);
        }

        return GlobalResponse.success("Successfully updated role data!", null, request);
    }

    @Override
    public ResponseEntity<Object> delete(Integer id, HttpServletRequest request) {
        if( id == null ) {
            return GlobalResponse.failed("Role ID is required!", AdminConstant.ADMIN_ROLE_SERVICE_DELETE_ID_REQUIRED, null, request);
        }

        try {
            Optional<AdminRole> optionalAdminRole = adminRoleRepo.findById(id);
            if( optionalAdminRole.isEmpty() ) {
                return GlobalResponse.failed("Role data not found!", AdminConstant.ADMIN_ROLE_SERVICE_DELETE_NOT_FOUND, null, request);
            }

            adminRoleRepo.deleteById(id);
        } catch(Exception e) {
            Logging.handleException("AdminRoleService", "delete(Long id, HttpServletRequest request)", 120, AdminConstant.ADMIN_ROLE_SERVICE_DELETE_EXCEPTION, e.getMessage());
            logService.writeErrorLog(AdminConstant.ADMIN_ROLE_SERVICE_DELETE_EXCEPTION, "AdminRoleService@delete()", e.getMessage());
            return GlobalResponse.failed("Failed to delete role data!", AdminConstant.ADMIN_ROLE_SERVICE_DELETE_EXCEPTION, null, request);
        }

        return GlobalResponse.success("Successfully deleted role data!", null, request);
    }

    private Page roleList(String keyword, Integer status, Pageable pageable) throws Exception {
        Page page = null;
        boolean isKeywordPresent = !keyword.isEmpty();
        boolean isStatusPresent = status != null && (List.of(CommonConstant.STATUS_ACTIVE, CommonConstant.STATUS_INACTIVE).contains(status));

        if( isKeywordPresent && isStatusPresent ) {
            page = adminRoleRepo.findAllByRoleCodeContainsOrRoleNameContainsIgnoreCaseAndStatus(keyword, keyword, status.byteValue(), pageable);

        } else if( isKeywordPresent ) {
            page = adminRoleRepo.findAllByRoleCodeContainsOrRoleNameContainsIgnoreCase(keyword, keyword, pageable);
        } else if( isStatusPresent ) {
            page = adminRoleRepo.findAllByStatus(status.byteValue(), pageable);
        } else {
            page = adminRoleRepo.findAll(pageable);
        }

        return page;
    }
}
