package com.backendsyndicate.smashclub.admin.service.internal;

import com.backendsyndicate.smashclub.admin.core.ICRUD;
import com.backendsyndicate.smashclub.admin.dto.relation.RelRoleMenuDTO;
import com.backendsyndicate.smashclub.admin.dto.relation.RelRolePermissionDTO;
import com.backendsyndicate.smashclub.admin.dto.response.RespAdminRoleDetailDTO;
import com.backendsyndicate.smashclub.admin.dto.response.RespAdminRoleListDTO;
import com.backendsyndicate.smashclub.admin.model.AdminRole;
import com.backendsyndicate.smashclub.admin.repo.AdminRoleRepo;
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
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminRoleService implements ICRUD<AdminRole, Integer> {
    @Autowired
    private AdminRoleRepo adminRoleRepo;
    private ModelMapper modelMapper = new ModelMapper();

    private String generateErrorCode(String methodNo, String errorNo) {
        return "ADM-ROLE" + "-" + methodNo + "-" + errorNo;
    }

    @Override
    public ResponseEntity<Object> findAll(String keyword, Pageable pageable, HttpServletRequest request) {
        Page page = null;

        try {
            if( keyword != "" ) {
                page = adminRoleRepo.findAllByRoleCodeContainsOrRoleNameContains(keyword, keyword, pageable);
            } else {
                page = adminRoleRepo.findAll(pageable);
            }

            if( page.isEmpty() ) {
                return GlobalResponse.failed("Role list is empty!", generateErrorCode("01", "001"), null, request);
            }

            page = page.map(adminRole -> { return modelMapper.map(adminRole, RespAdminRoleListDTO.class); });

        } catch(Exception e) {
            Logging.handleException("AdminRoleService", "findAll(Pageable pageable, HttpServletRequest request)", 33, generateErrorCode("01", "010"), e.getMessage());
            return GlobalResponse.failed("Failed to get role list!", generateErrorCode("01", "010"), null, request);
        }

        return GlobalResponse.success("Successfully get admin role list!", page, request);
    }

    @Override
    public ResponseEntity<Object> findById(Integer id, HttpServletRequest request) {
        RespAdminRoleDetailDTO response = null;

        if( id == null ) {
            return GlobalResponse.failed("Role ID is required!", generateErrorCode("02", "001"), null, request);
        }

        try {
            Optional<AdminRole> optionalAdminRole = adminRoleRepo.findById(id);
            if( optionalAdminRole.isEmpty() ) {
                return GlobalResponse.failed("Role not found!", generateErrorCode("02", "002"), null, request);
            }

            AdminRole adminRole = optionalAdminRole.get();
            response = modelMapper.map(adminRole, RespAdminRoleDetailDTO.class);
            response.setMenuSet(adminRole.getMenuSet().stream().map(rowSet -> modelMapper.map(rowSet, RelRoleMenuDTO.class)).collect(Collectors.toSet()));
            response.setPermissionSet(adminRole.getPermissionSet().stream().map(rowSet -> modelMapper.map(rowSet, RelRolePermissionDTO.class)).collect(Collectors.toSet()));
        } catch(Exception e) {
            return GlobalResponse.failed("Failed to get role data!", generateErrorCode("02", "010"), null, request);
        }

        return GlobalResponse.success("Role data found!", response, request);
    }

    @Override
    public ResponseEntity<Object> save(AdminRole adminRole, HttpServletRequest request) {
        if( adminRole == null ) {
            return GlobalResponse.failed("Role data is required!", generateErrorCode("03", "001"), null, request);
        }

        try {
            adminRoleRepo.save(adminRole);
        } catch(Exception e) {
            Logging.handleException("AdminRoleService", "save(AdminRole adminRole, HttpServletRequest request)", 73, generateErrorCode("03", "010"), e.getMessage());
            return GlobalResponse.failed("Failed to save role data!", generateErrorCode("03", "010"), null, request);
        }

        return GlobalResponse.success("Successfully save role data!", null, request);
    }

    @Override
    public ResponseEntity<Object> update(Integer id, AdminRole adminRole, HttpServletRequest request) {
        if( id == null ) {
            return GlobalResponse.failed("Role ID is required!", generateErrorCode("04", "001"), null, request);
        }

        if( adminRole == null ) {
            return GlobalResponse.failed("Role data is required!", generateErrorCode("04", "002"), null, request);
        }

        try {
            Optional<AdminRole> optionalAdminRole = adminRoleRepo.findById(id);
            if( optionalAdminRole.isEmpty() ) {
                return GlobalResponse.failed("Role data not found!", generateErrorCode("04", "003"), null, request);
            }

            AdminRole adminRoleDB = optionalAdminRole.get();
            adminRoleDB.setRoleCode(adminRole.getRoleCode());
            adminRoleDB.setRoleName(adminRole.getRoleName());
            adminRoleDB.setStatus(adminRole.getStatus());
            adminRoleDB.setMenuSet(adminRole.getMenuSet());
            adminRoleDB.setPermissionSet(adminRole.getPermissionSet());
        } catch(Exception e) {
            Logging.handleException("AdminRoleService", "update(Long id, AdminRole adminRole, HttpServletRequest request)", 94, generateErrorCode("04", "010"), e.getMessage());
            return GlobalResponse.failed("Failed to update role data!", generateErrorCode("04", "010"), null, request);
        }

        return GlobalResponse.success("Successfully updated role data!", null, request);
    }

    @Override
    public ResponseEntity<Object> delete(Integer id, HttpServletRequest request) {
        if( id == null ) {
            return GlobalResponse.failed("Role ID is required!", generateErrorCode("05", "001"), null, request);
        }

        try {
            Optional<AdminRole> optionalAdminRole = adminRoleRepo.findById(id);
            if( optionalAdminRole.isEmpty() ) {
                return GlobalResponse.failed("Role data not found!", generateErrorCode("05", "002"), null, request);
            }

            adminRoleRepo.deleteById(id);
        } catch(Exception e) {
            Logging.handleException("AdminRoleService", "delete(Long id, HttpServletRequest request)", 120, generateErrorCode("05", "010"), e.getMessage());
            return GlobalResponse.failed("Failed to delete role data!", generateErrorCode("05", "010"), null, request);
        }

        return GlobalResponse.success("Successfully deleted role data!", null, request);
    }
}
