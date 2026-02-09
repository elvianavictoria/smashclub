package com.backendsyndicate.smashclub.admin.controller.report;

import com.backendsyndicate.smashclub.admin.dto.request.ReqAdminPlayerSaveDTO;
import com.backendsyndicate.smashclub.admin.service.report.AdminPlayerService;
import com.backendsyndicate.smashclub.auth.model.User;
import com.backendsyndicate.smashclub.common.constant.PermissionConstant;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/admin/player")
public class AdminPlayerController {
    @Autowired
    private AdminPlayerService adminPlayerService;
    private ModelMapper modelMapper = new ModelMapper();

    @PreAuthorize("hasAuthority('" + PermissionConstant.PLAYER_READ_CODE + "')")
    @GetMapping
    public ResponseEntity<Object> playerList(@RequestParam String keyword, @RequestParam int page, @RequestParam int size, HttpServletRequest request) {
        Pageable pageable = PageRequest.of(page, size);
        return adminPlayerService.findAll(keyword, pageable, request);
    }

    @PreAuthorize("hasAuthority('" + PermissionConstant.PLAYER_EDIT_CODE + "')")
    @GetMapping("{playerId}")
    public ResponseEntity<Object> playerDetail(@PathVariable String playerId, HttpServletRequest request) {
        return adminPlayerService.findById(playerId, request);
    }

    @PreAuthorize("hasAuthority('" + PermissionConstant.PLAYER_EDIT_CODE + "')")
    @PutMapping("update/{playerId}")
    public ResponseEntity<Object> playerUpdate(@PathVariable String playerId, @RequestBody ReqAdminPlayerSaveDTO dto, HttpServletRequest request) {
        User player = modelMapper.map(dto, User.class);
        return adminPlayerService.update(playerId, player, request);
    }

    @PreAuthorize("hasAuthority('" + PermissionConstant.PLAYER_DELETE_CODE + "')")
    @DeleteMapping("delete/{playerId}")
    public ResponseEntity<Object> playerDelete(@PathVariable String playerId, HttpServletRequest request) {
        return adminPlayerService.delete(playerId, request);
    }
}
