package com.backendsyndicate.smashclub.admin.controller.internal;

import com.backendsyndicate.smashclub.admin.dto.request.ReqAdminRoleSaveDTO;
import com.backendsyndicate.smashclub.admin.model.AdminRole;
import com.backendsyndicate.smashclub.admin.service.internal.AdminRoleService;
import com.backendsyndicate.smashclub.common.constant.PermissionConstant;
import com.backendsyndicate.smashclub.common.util.Logging;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/admin/roles")
public class AdminRoleController {
    @Autowired
    private AdminRoleService adminRoleService;
    private ModelMapper modelMapper = new ModelMapper();

    @PreAuthorize("hasAuthority('" + PermissionConstant.ROLES_READ_CODE + "')")
    @GetMapping
    public ResponseEntity<Object> adminRoleList(@RequestParam String keyword, @RequestParam(required = false) Integer status, @RequestParam int page, @RequestParam int size, HttpServletRequest request) {
        Pageable pageable = PageRequest.of(page, size);
        return adminRoleService.findAll(keyword, status, pageable, request);
    }

    @PreAuthorize("hasAuthority('" + PermissionConstant.ROLES_EDIT_CODE + "')")
    @GetMapping("{roleId}")
    public ResponseEntity<Object> adminRoleDetail(@PathVariable int roleId, HttpServletRequest request) {
        return adminRoleService.findById(roleId, request);
    }

    @PreAuthorize("hasAuthority('" + PermissionConstant.ROLES_CREATE_CODE + "')")
    @PostMapping("save")
    public ResponseEntity<Object> adminRoleSave(@RequestBody ReqAdminRoleSaveDTO dto, HttpServletRequest request) {
        AdminRole adminRole = modelMapper.map(dto, AdminRole.class);
        return adminRoleService.save(adminRole, request);
    }

    @PreAuthorize("hasAuthority('" + PermissionConstant.ROLES_EDIT_CODE + "')")
    @PutMapping("update/{roleId}")
    public ResponseEntity<Object> adminRoleUpdate(@PathVariable int roleId, @RequestBody ReqAdminRoleSaveDTO dto, HttpServletRequest request) {
        AdminRole adminRole = modelMapper.map(dto, AdminRole.class);
        return adminRoleService.update(roleId, adminRole, request);
    }

    @PreAuthorize("hasAuthority('" + PermissionConstant.ROLES_DELETE_CODE + "')")
    @DeleteMapping("delete/{roleId}")
    public ResponseEntity<Object> adminRoleDelete(@PathVariable int roleId, HttpServletRequest request) {
        return adminRoleService.delete(roleId, request);
    }
}
