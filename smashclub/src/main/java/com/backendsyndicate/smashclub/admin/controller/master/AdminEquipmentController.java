package com.backendsyndicate.smashclub.admin.controller.master;

import com.backendsyndicate.smashclub.admin.dto.request.ReqAdminEquipmentSaveDTO;
import com.backendsyndicate.smashclub.admin.service.master.AdminEquipmentService;
import com.backendsyndicate.smashclub.booking.model.Equipment;
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
@RequestMapping("api/v1/admin/equipment")
public class AdminEquipmentController {
    @Autowired
    private AdminEquipmentService adminEquipmentService;
    private ModelMapper modelMapper = new ModelMapper();

    @PreAuthorize("hasAuthority('" + PermissionConstant.EQUIPMENT_READ_CODE + "')")
    @GetMapping
    public ResponseEntity<Object> equipmentList(@RequestParam String keyword, @RequestParam int page, @RequestParam int size, HttpServletRequest request) {
        Pageable pageable = PageRequest.of(page, size);
        return adminEquipmentService.findAll(keyword, pageable, request);
    }

    @PreAuthorize("hasAuthority('" + PermissionConstant.EQUIPMENT_EDIT_CODE + "')")
    @GetMapping("{equipmentId}")
    public ResponseEntity<Object> equipmentDetail(@PathVariable Long equipmentId, HttpServletRequest request) {
        return adminEquipmentService.findById(equipmentId, request);
    }

    @PreAuthorize("hasAuthority('" + PermissionConstant.EQUIPMENT_CREATE_CODE + "')")
    @PostMapping("save")
    public ResponseEntity<Object> equipmentSave(@RequestBody ReqAdminEquipmentSaveDTO dto, HttpServletRequest request) {
        Equipment equipment = modelMapper.map(dto, Equipment.class);
        return adminEquipmentService.save(equipment, request);
    }

    @PreAuthorize("hasAuthority('" + PermissionConstant.EQUIPMENT_EDIT_CODE + "')")
    @PutMapping("update/{equipmentId}")
    public ResponseEntity<Object> equipmentUpdate(@PathVariable Long equipmentId, @RequestBody ReqAdminEquipmentSaveDTO dto, HttpServletRequest request) {
        Equipment equipment = modelMapper.map(dto, Equipment.class);
        return adminEquipmentService.update(equipmentId, equipment, request);
    }

    @PreAuthorize("hasAuthority('" + PermissionConstant.EQUIPMENT_DELETE_CODE + "')")
    @DeleteMapping("delete/{equipmentId}")
    public ResponseEntity<Object> equipmentDelete(@PathVariable Long equipmentId, HttpServletRequest request) {
        return adminEquipmentService.delete(equipmentId, request);
    }
}
