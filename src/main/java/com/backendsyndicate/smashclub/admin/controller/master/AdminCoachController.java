package com.backendsyndicate.smashclub.admin.controller.master;

import com.backendsyndicate.smashclub.admin.dto.request.ReqAdminCoachSaveDTO;
import com.backendsyndicate.smashclub.admin.service.master.AdminCoachService;
import com.backendsyndicate.smashclub.booking.model.Coach;
import com.backendsyndicate.smashclub.common.constant.PermissionConstant;
import com.backendsyndicate.smashclub.common.util.GlobalResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@RestController
@RequestMapping("api/v1/admin/coach")
public class AdminCoachController {
    @Autowired
    private AdminCoachService adminCoachService;
    private ModelMapper modelMapper = new ModelMapper();

    @PreAuthorize("hasAuthority('" + PermissionConstant.COACH_READ_CODE + "')")
    @GetMapping
    public ResponseEntity<Object> coachList(@RequestParam String keyword, @RequestParam(required = false) Integer status, @RequestParam int page, @RequestParam int size, HttpServletRequest request) {
        Pageable pageable = PageRequest.of(page, size);
        return adminCoachService.findAll(keyword, status, pageable, request);
    }

    @PreAuthorize("hasAuthority('" + PermissionConstant.COACH_EDIT_CODE + "')")
    @GetMapping("{coachId}")
    public ResponseEntity<Object> coachDetail(@PathVariable Long coachId, HttpServletRequest request) {
        return adminCoachService.findById(coachId, request);
    }

    @PreAuthorize("hasAuthority('" + PermissionConstant.COACH_CREATE_CODE + "')")
    @PostMapping("save")
    public ResponseEntity<Object> coachSave(
//            @RequestBody ReqAdminCoachSaveDTO dto,
            @RequestParam MultipartFile coachImgLink,
            @RequestParam String coachCode,
            @RequestParam String coachName,
            @RequestParam BigDecimal pricePerHour,
            @RequestParam byte status,
            HttpServletRequest request
    ) {
        ReqAdminCoachSaveDTO dto = new ReqAdminCoachSaveDTO();
        dto.setCoachCode(coachCode);
        dto.setCoachName(coachName);
        dto.setPricePerHour(pricePerHour);
        dto.setStatus(status);
        dto.validate();

        if( !dto.isValidated() ) {
            return GlobalResponse.failed("Format tidak valid!", "X01001", dto.getValidation(), request);
        }

        Coach coach = modelMapper.map(dto, Coach.class);
        return adminCoachService.save(coach, coachImgLink, request);
    }

    @PreAuthorize("hasAuthority('" + PermissionConstant.COACH_EDIT_CODE + "')")
    @PutMapping("update/{coachId}")
    public ResponseEntity<Object> coachUpdate(
            @PathVariable Long coachId,
//            @RequestBody ReqAdminCoachSaveDTO dto,
            @RequestParam(required = false) MultipartFile coachImgLink,
            @RequestParam String coachCode,
            @RequestParam String coachName,
            @RequestParam BigDecimal pricePerHour,
            @RequestParam byte status,
            HttpServletRequest request
    ) {
        ReqAdminCoachSaveDTO dto = new ReqAdminCoachSaveDTO();
        dto.setCoachCode(coachCode);
        dto.setCoachName(coachName);
        dto.setPricePerHour(pricePerHour);
        dto.setStatus(status);
        dto.validate();

        if( !dto.isValidated() ) {
            return GlobalResponse.failed("Format tidak valid!", "X01001", dto.getValidation(), request);
        }

        Coach coach = modelMapper.map(dto, Coach.class);
        return adminCoachService.update(coachId, coach, coachImgLink, request);
    }

    @PreAuthorize("hasAuthority('" + PermissionConstant.COACH_DELETE_CODE + "')")
    @DeleteMapping("delete/{coachId}")
    public ResponseEntity<Object> coachDelete(@PathVariable Long coachId, HttpServletRequest request) {
        return adminCoachService.delete(coachId, request);
    }
}
