package com.backendsyndicate.smashclub.admin.controller.internal;

import com.backendsyndicate.smashclub.admin.dto.request.ReqAdminRoleSaveDTO;
import com.backendsyndicate.smashclub.admin.model.AdminRole;
import com.backendsyndicate.smashclub.admin.service.internal.AdminRoleService;
import com.backendsyndicate.smashclub.common.util.Logging;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("admin/roles")
public class AdminRoleController {
    @Autowired
    private AdminRoleService adminRoleService;
    private ModelMapper modelMapper = new ModelMapper();

    @GetMapping
    public ResponseEntity<Object> adminRoleList(@RequestParam String keyword, @RequestParam int page, @RequestParam int size, HttpServletRequest request) {
        Pageable pageable = PageRequest.of(page, size);
        return adminRoleService.findAll(keyword, pageable, request);
    }

    @GetMapping("{roleId}")
    public ResponseEntity<Object> adminRoleDetail(@PathVariable int roleId, HttpServletRequest request) {
        return adminRoleService.findById(roleId, request);
    }

    @PostMapping("save")
    public ResponseEntity<Object> adminRoleSave(@RequestBody ReqAdminRoleSaveDTO dto, HttpServletRequest request) {
        AdminRole adminRole = modelMapper.map(dto, AdminRole.class);
        return adminRoleService.save(adminRole, request);
    }

    @PutMapping("update/{roleId}")
    public ResponseEntity<Object> adminRoleUpdate(@PathVariable int roleId, @RequestBody ReqAdminRoleSaveDTO dto, HttpServletRequest request) {
        AdminRole adminRole = modelMapper.map(dto, AdminRole.class);
        return adminRoleService.update(roleId, adminRole, request);
    }

    @DeleteMapping("delete/{roleId}")
    public ResponseEntity<Object> adminRoleDelete(@PathVariable int roleId, HttpServletRequest request) {
        return adminRoleService.delete(roleId, request);
    }
}
