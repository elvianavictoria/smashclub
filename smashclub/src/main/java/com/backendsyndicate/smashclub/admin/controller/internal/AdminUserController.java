package com.backendsyndicate.smashclub.admin.controller.internal;

import com.backendsyndicate.smashclub.admin.dto.request.ReqAdminUserSaveDTO;
import com.backendsyndicate.smashclub.admin.model.AdminRole;
import com.backendsyndicate.smashclub.admin.model.AdminUser;
import com.backendsyndicate.smashclub.admin.service.internal.AdminUserService;
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
@RequestMapping("api/v1/admin/users")
public class AdminUserController {
    @Autowired
    private AdminUserService adminUserService;
    private ModelMapper modelMapper = new ModelMapper();

    @PreAuthorize("hasAuthority('" + PermissionConstant.USERS_READ_CODE + "')")
    @GetMapping
    public ResponseEntity<Object> adminUserList(@RequestParam String keyword, @RequestParam int page, @RequestParam int size, HttpServletRequest request) {
        Pageable pageable = PageRequest.of(page, size);
        return adminUserService.findAll(keyword, pageable, request);
    }

    @PreAuthorize("hasAuthority('" + PermissionConstant.USERS_EDIT_CODE + "')")
    @GetMapping("{userId}")
    public ResponseEntity<Object> adminUserDetail(@PathVariable Long userId, HttpServletRequest request) {
        return adminUserService.findById(userId, request);
    }

    @PreAuthorize("hasAuthority('" + PermissionConstant.USERS_CREATE_CODE + "')")
    @PostMapping("save")
    public ResponseEntity<Object> adminUserSave(@RequestBody ReqAdminUserSaveDTO dto, HttpServletRequest request) {
        AdminUser adminUser = modelMapper.map(dto, AdminUser.class);
        return adminUserService.save(adminUser, request);
    }

    @PreAuthorize("hasAuthority('" + PermissionConstant.USERS_EDIT_CODE + "')")
    @PutMapping("update/{userId}")
    public ResponseEntity<Object> adminUserUpdate(@PathVariable Long userId, @RequestBody ReqAdminUserSaveDTO dto, HttpServletRequest request) {
        AdminUser adminUser = modelMapper.map(dto, AdminUser.class);
//        AdminRole adminRole = modelMapper.map(dto.getAdminRole(), AdminRole.class);
//        adminUser.setAdminRole(adminRole);
        return adminUserService.update(userId, adminUser, request);
    }

    @PreAuthorize("hasAuthority('" + PermissionConstant.USERS_DELETE_CODE + "')")
    @DeleteMapping("delete/{userId}")
    public ResponseEntity<Object> adminUserDelete(@PathVariable Long userId, HttpServletRequest request) {
        return adminUserService.delete(userId, request);
    }
}
