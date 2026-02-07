package com.backendsyndicate.smashclub.admin.controller.master;

import com.backendsyndicate.smashclub.admin.dto.request.ReqAdminEquipmentSaveDTO;
import com.backendsyndicate.smashclub.admin.dto.validation.ValAdminEquipmentEquipmentCategoryDTO;
import com.backendsyndicate.smashclub.admin.service.master.AdminEquipmentService;
import com.backendsyndicate.smashclub.booking.model.Equipment;
import com.backendsyndicate.smashclub.common.constant.PermissionConstant;
import com.backendsyndicate.smashclub.common.util.GlobalResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Pattern;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

@RestController
@RequestMapping("api/v1/admin/equipment")
public class AdminEquipmentController {
    @Autowired
    private AdminEquipmentService adminEquipmentService;
    private ModelMapper modelMapper = new ModelMapper();
    private ObjectMapper objectMapper = new ObjectMapper();

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
    public ResponseEntity<Object> equipmentSave(
//            @RequestBody ReqAdminEquipmentSaveDTO dto,
            @RequestParam MultipartFile equipmentImgLink,
            @RequestParam String equipmentName,
            @RequestParam String brand,
            @RequestParam String type,
            @RequestParam BigDecimal price,
            @RequestParam int stock,
            @RequestParam(required = false) String description,
            @RequestParam int status,
            @RequestParam String equipmentCategory,
            HttpServletRequest request
    ) {
        ValAdminEquipmentEquipmentCategoryDTO equipmentCategoryDTO = objectMapper.readValue(equipmentCategory, ValAdminEquipmentEquipmentCategoryDTO.class);
        ReqAdminEquipmentSaveDTO dto = new ReqAdminEquipmentSaveDTO();
        dto.setEquipmentName(equipmentName);
        dto.setBrand(brand);
        dto.setType(type);
        dto.setPrice(price);
        dto.setStock(stock);
        dto.setDescription(description);
        dto.setStatus(status);
        dto.setEquipmentCategory(equipmentCategoryDTO);
        dto.validate();

        if( !dto.isValidated() ) {
            return GlobalResponse.failed("Format tidak valid!", "X01001", dto.getValidation(), request);
        }

        Equipment equipment = modelMapper.map(dto, Equipment.class);
        return adminEquipmentService.save(equipment, equipmentImgLink, request);
    }

    @PreAuthorize("hasAuthority('" + PermissionConstant.EQUIPMENT_EDIT_CODE + "')")
    @PutMapping("update/{equipmentId}")
    public ResponseEntity<Object> equipmentUpdate(
            @PathVariable Long equipmentId,
//            @RequestBody ReqAdminEquipmentSaveDTO dto,
            @RequestParam(required = false) MultipartFile equipmentImgLink,
            @RequestParam String equipmentName,
            @RequestParam String brand,
            @RequestParam String type,
            @RequestParam BigDecimal price,
            @RequestParam int stock,
            @RequestParam(required = false) String description,
            @RequestParam int status,
            @RequestParam String equipmentCategory,
            HttpServletRequest request
    ) {
        ValAdminEquipmentEquipmentCategoryDTO equipmentCategoryDTO = objectMapper.readValue(equipmentCategory, ValAdminEquipmentEquipmentCategoryDTO.class);
        ReqAdminEquipmentSaveDTO dto = new ReqAdminEquipmentSaveDTO();

        dto.setEquipmentName(equipmentName);
        dto.setBrand(brand);
        dto.setType(type);
        dto.setPrice(price);
        dto.setStock(stock);
        dto.setDescription(description);
        dto.setStatus(status);
        dto.setEquipmentCategory(equipmentCategoryDTO);
        dto.validate();

        if( !dto.isValidated() ) {
            return GlobalResponse.failed("Format tidak valid!", "X01001", dto.getValidation(), request);
        }

        Equipment equipment = modelMapper.map(dto, Equipment.class);
        return adminEquipmentService.update(equipmentId, equipment, equipmentImgLink, request);
    }

    @PreAuthorize("hasAuthority('" + PermissionConstant.EQUIPMENT_DELETE_CODE + "')")
    @DeleteMapping("delete/{equipmentId}")
    public ResponseEntity<Object> equipmentDelete(@PathVariable Long equipmentId, HttpServletRequest request) {
        return adminEquipmentService.delete(equipmentId, request);
    }
}
