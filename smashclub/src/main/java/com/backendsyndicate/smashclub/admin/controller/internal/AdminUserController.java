package com.backendsyndicate.smashclub.admin.controller.internal;

import com.backendsyndicate.smashclub.admin.dto.request.ReqAdminUserSaveDTO;
import com.backendsyndicate.smashclub.admin.model.AdminUser;
import com.backendsyndicate.smashclub.admin.service.internal.AdminUserService;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("admin/users")
public class AdminUserController {
    @Autowired
    private AdminUserService adminUserService;
    private ModelMapper modelMapper = new ModelMapper();

    @GetMapping
    public ResponseEntity<Object> adminUserList(@RequestParam String keyword, @RequestParam int page, @RequestParam int size, HttpServletRequest request) {
        Pageable pageable = PageRequest.of(page, size);
        return adminUserService.findAll(keyword, pageable, request);
    }

    @GetMapping("{userId}")
    public ResponseEntity<Object> adminUserDetail(@PathVariable Long userId, HttpServletRequest request) {
        return adminUserService.findById(userId, request);
    }

    @PostMapping("save")
    public ResponseEntity<Object> adminUserSave(@RequestBody ReqAdminUserSaveDTO dto, HttpServletRequest request) {
        AdminUser adminUser = modelMapper.map(dto, AdminUser.class);
        return adminUserService.save(adminUser, request);
    }

    @PutMapping("update/{userId}")
    public ResponseEntity<Object> adminUserUpdate(@PathVariable Long userId, @RequestBody ReqAdminUserSaveDTO dto, HttpServletRequest request) {
        AdminUser adminUser = modelMapper.map(dto, AdminUser.class);
        return adminUserService.update(userId, adminUser, request);
    }

    @DeleteMapping("delete/{userId}")
    public ResponseEntity<Object> adminUserDelete(@PathVariable Long userId, HttpServletRequest request) {
        return adminUserService.delete(userId, request);
    }
}
