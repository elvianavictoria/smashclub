package com.backendsyndicate.smashclub.admin.controller.master;

import com.backendsyndicate.smashclub.admin.dto.request.ReqEquipmentSaveDTO;
import com.backendsyndicate.smashclub.admin.service.master.AdminEquipmentCategoryService;
import com.backendsyndicate.smashclub.booking.model.EquipmentCategory;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("admin/equipment-category")
public class AdminEquipmentCategoryController {
    @Autowired
    private AdminEquipmentCategoryService adminEquipmentCategoryService;
    private ModelMapper modelMapper = new ModelMapper();

    @GetMapping
    public ResponseEntity<Object> equipmentList(@RequestParam String keyword, @RequestParam int page, @RequestParam int size, HttpServletRequest request) {
        Pageable pageable = PageRequest.of(page, size);
        return adminEquipmentCategoryService.findAll(keyword, pageable, request);
    }

    @GetMapping("{equipmentId}")
    public ResponseEntity<Object> equipmentDetail(@PathVariable Long equipmentId, HttpServletRequest request) {
        return adminEquipmentCategoryService.findById(equipmentId, request);
    }

    @PostMapping("save")
    public ResponseEntity<Object> equipmentSave(@RequestBody ReqEquipmentSaveDTO dto, HttpServletRequest request) {
        EquipmentCategory equipmentCategory = modelMapper.map(dto, EquipmentCategory.class);
        return adminEquipmentCategoryService.save(equipmentCategory, request);
    }

    @PutMapping("update/{equipmentId}")
    public ResponseEntity<Object> equipmentUpdate(@PathVariable Long equipmentId, @RequestBody ReqEquipmentSaveDTO dto, HttpServletRequest request) {
        EquipmentCategory equipmentCategory = modelMapper.map(dto, EquipmentCategory.class);
        return adminEquipmentCategoryService.update(equipmentId, equipmentCategory, request);
    }

    @DeleteMapping("delete/{equipmentId}")
    public ResponseEntity<Object> equipmentDelete(@PathVariable Long equipmentId, HttpServletRequest request) {
        return adminEquipmentCategoryService.delete(equipmentId, request);
    }
}
