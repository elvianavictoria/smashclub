package com.backendsyndicate.smashclub.admin.controller.master;

import com.backendsyndicate.smashclub.admin.dto.request.ReqAdminCourtSaveDTO;
import com.backendsyndicate.smashclub.admin.service.master.AdminCourtService;
import com.backendsyndicate.smashclub.booking.model.Court;
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

import java.time.LocalTime;

@RestController
@RequestMapping("api/v1/admin/court")
public class AdminCourtController {
    @Autowired
    private AdminCourtService adminCourtService;
    private ModelMapper modelMapper = new ModelMapper();

    @PreAuthorize("hasAuthority('" + PermissionConstant.COURT_READ_CODE + "')")
    @GetMapping
    public ResponseEntity<Object> courtList(@RequestParam String keyword, @RequestParam(required = false) Integer status, @RequestParam int page, @RequestParam int size, HttpServletRequest request) {
        Pageable pageable = PageRequest.of(page, size);
        return adminCourtService.findAll(keyword, status, pageable, request);
    }

    @PreAuthorize("hasAuthority('" + PermissionConstant.COURT_EDIT_CODE + "')")
    @GetMapping("{courtId}")
    public ResponseEntity<Object> courtDetail(@PathVariable Long courtId, HttpServletRequest request) {
        return adminCourtService.findById(courtId, request);
    }

    @PreAuthorize("hasAuthority('" + PermissionConstant.COURT_CREATE_CODE + "')")
    @PostMapping("save")
    public ResponseEntity<Object> courtSave(
//            @RequestBody ReqAdminCourtSaveDTO dto,
            @RequestParam MultipartFile courtImgLink,
            @RequestParam String courtCode,
            @RequestParam String courtName,
            @RequestParam LocalTime openTime,
            @RequestParam LocalTime closeTime,
            @RequestParam byte status,
            HttpServletRequest request
    ) {
        ReqAdminCourtSaveDTO dto = new ReqAdminCourtSaveDTO();
        dto.setCourtCode(courtCode);
        dto.setCourtName(courtName);
        dto.setOpenTime(openTime);
        dto.setCloseTime(closeTime);
        dto.setStatus(status);
        dto.validate();

        if( !dto.isValidated() ) {
            return GlobalResponse.failed("Format tidak valid!", "X01001", dto.getValidation(), request);
        }

        Court court = modelMapper.map(dto, Court.class);
        return adminCourtService.save(court, request);
    }

    @PreAuthorize("hasAuthority('" + PermissionConstant.COURT_EDIT_CODE + "')")
    @PutMapping("update/{courtId}")
    public ResponseEntity<Object> courtUpdate(
            @PathVariable Long courtId,
//            @RequestBody ReqAdminCourtSaveDTO dto,
            @RequestParam(required = false) MultipartFile courtImgLink,
            @RequestParam String courtCode,
            @RequestParam String courtName,
            @RequestParam LocalTime openTime,
            @RequestParam LocalTime closeTime,
            @RequestParam byte status,
            HttpServletRequest request
    ) {
        ReqAdminCourtSaveDTO dto = new ReqAdminCourtSaveDTO();
        dto.setCourtCode(courtCode);
        dto.setCourtName(courtName);
        dto.setOpenTime(openTime);
        dto.setCloseTime(closeTime);
        dto.setStatus(status);
        dto.validate();

        if( !dto.isValidated() ) {
            return GlobalResponse.failed("Format tidak valid!", "X01001", dto.getValidation(), request);
        }

        Court court = modelMapper.map(dto, Court.class);
        return adminCourtService.update(courtId, court, request);
    }

    @PreAuthorize("hasAuthority('" + PermissionConstant.COURT_DELETE_CODE + "')")
    @DeleteMapping("delete/{courtId}")
    public ResponseEntity<Object> courtDelete(@PathVariable Long courtId, HttpServletRequest request) {
        return adminCourtService.delete(courtId, request);
    }
}
