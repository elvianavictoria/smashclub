package com.backendsyndicate.smashclub.admin.controller.master;

import com.backendsyndicate.smashclub.admin.dto.request.ReqAdminCoachSaveDTO;
import com.backendsyndicate.smashclub.admin.service.master.AdminCoachService;
import com.backendsyndicate.smashclub.booking.model.Coach;
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
@RequestMapping("api/v1/admin/coach")
public class AdminCoachController {

    @Autowired
    private AdminCoachService adminCoachService;
    private ModelMapper modelMapper = new ModelMapper();

    @PreAuthorize("hasAuthority('" + PermissionConstant.COACH_READ_CODE + "')")
    @GetMapping
    public ResponseEntity<Object> coachList(@RequestParam String keyword, @RequestParam int page, @RequestParam int size, HttpServletRequest request) {
        Pageable pageable = PageRequest.of(page, size);
        return adminCoachService.findAll(keyword, pageable, request);
    }

    @PreAuthorize("hasAuthority('" + PermissionConstant.COACH_EDIT_CODE + "')")
    @GetMapping("{coachId}")
    public ResponseEntity<Object> coachDetail(@PathVariable Long coachId, HttpServletRequest request) {
        return adminCoachService.findById(coachId, request);
    }

    @PreAuthorize("hasAuthority('" + PermissionConstant.COACH_CREATE_CODE + "')")
    @PostMapping("save")
    public ResponseEntity<Object> coachSave(@RequestBody ReqAdminCoachSaveDTO dto, HttpServletRequest request) {
        Coach coach = modelMapper.map(dto, Coach.class);
        return adminCoachService.save(coach, request);
    }

    @PreAuthorize("hasAuthority('" + PermissionConstant.COACH_EDIT_CODE + "')")
    @PutMapping("update/{coachId}")
    public ResponseEntity<Object> coachUpdate(@PathVariable Long coachId, @RequestBody ReqAdminCoachSaveDTO dto, HttpServletRequest request) {
        Coach coach = modelMapper.map(dto, Coach.class);
        return adminCoachService.update(coachId, coach, request);
    }

    @PreAuthorize("hasAuthority('" + PermissionConstant.COACH_DELETE_CODE + "')")
    @DeleteMapping("delete/{coachId}")
    public ResponseEntity<Object> coachDelete(@PathVariable Long coachId, HttpServletRequest request) {
        return adminCoachService.delete(coachId, request);
    }
}
