package com.backendsyndicate.smashclub.admin.service.internal;

import com.backendsyndicate.smashclub.admin.dto.relation.RelAdminPermissionMenuDTO;
import com.backendsyndicate.smashclub.admin.dto.response.RespAdminPermissionListDTO;
import com.backendsyndicate.smashclub.admin.model.AdminPermission;
import com.backendsyndicate.smashclub.admin.repo.AdminPermissionRepo;
import com.backendsyndicate.smashclub.admin.service.log.LogService;
import com.backendsyndicate.smashclub.common.constant.AdminConstant;
import com.backendsyndicate.smashclub.common.constant.CommonConstant;
import com.backendsyndicate.smashclub.common.util.GlobalResponse;
import com.backendsyndicate.smashclub.common.util.Logging;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class AdminPermissionService {
    @Autowired
    private AdminPermissionRepo adminPermissionRepo;
    @Autowired
    private LogService logService;

    private ModelMapper modelMapper = new ModelMapper();

    public ResponseEntity<Object> findAll(HttpServletRequest request) {
        List<RespAdminPermissionListDTO> response = new ArrayList<RespAdminPermissionListDTO>();

        try {
            List<AdminPermission> permissions = adminPermissionRepo.findAllByStatus(CommonConstant.STATUS_ACTIVE);
            if( permissions.isEmpty() ) {
                return GlobalResponse.failed("Permission list is empty!", AdminConstant.ADMIN_PERMISSION_SERVICE_LIST_EMPTY, null, request);
            }

            for( int i = 0; i < permissions.size(); i++ ) {
                AdminPermission item = permissions.get(i);
                RespAdminPermissionListDTO dest = new RespAdminPermissionListDTO();
                dest.setId(item.getId());
                dest.setPermissionName(item.getPermissionName());
                dest.setMenu(modelMapper.map(item.getMenu(), RelAdminPermissionMenuDTO.class));
                response.add(dest);
            }
        } catch(Exception e) {
            Logging.handleException("AdminPermissionService", "findAll(HttpServletRequest request)", 33, AdminConstant.ADMIN_PERMISSION_SERVICE_LIST_EXCEPTION, e.getMessage());
            logService.writeErrorLog(AdminConstant.ADMIN_PERMISSION_SERVICE_LIST_EXCEPTION, "AdminPermissionService@findAll()", e.getMessage());
            return GlobalResponse.failed("Failed to get permission list!", AdminConstant.ADMIN_PERMISSION_SERVICE_LIST_EXCEPTION, null, request);
        }

        return GlobalResponse.success("Successfully get permission list!", response, request);
    }
}
