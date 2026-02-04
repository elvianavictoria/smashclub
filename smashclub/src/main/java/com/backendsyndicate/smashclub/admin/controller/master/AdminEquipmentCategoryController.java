package com.backendsyndicate.smashclub.admin.controller.master;

import com.backendsyndicate.smashclub.admin.dto.request.ReqAdminEquipmentCategorySaveDTO;
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
@RequestMapping("api/v1/admin/equipment-category")
public class AdminEquipmentCategoryController {
    @Autowired
    private AdminEquipmentCategoryService adminEquipmentCategoryService;
    private ModelMapper modelMapper = new ModelMapper();

    @GetMapping
    public ResponseEntity<Object> equipmentCategoryList(@RequestParam String keyword, @RequestParam int page, @RequestParam int size, HttpServletRequest request) {
        Pageable pageable = PageRequest.of(page, size);
        return adminEquipmentCategoryService.findAll(keyword, pageable, request);
    }

    @GetMapping("{categoryId}")
    public ResponseEntity<Object> equipmentCategoryDetail(@PathVariable Long categoryId, HttpServletRequest request) {
        return adminEquipmentCategoryService.findById(categoryId, request);
    }

    @PostMapping("save")
    public ResponseEntity<Object> equipmentCategorySave(@RequestBody ReqAdminEquipmentCategorySaveDTO dto, HttpServletRequest request) {
        EquipmentCategory equipmentCategory = modelMapper.map(dto, EquipmentCategory.class);
        return adminEquipmentCategoryService.save(equipmentCategory, request);
    }

    @PutMapping("update/{categoryId}")
    public ResponseEntity<Object> equipmentCategoryUpdate(@PathVariable Long categoryId, @RequestBody ReqAdminEquipmentCategorySaveDTO dto, HttpServletRequest request) {
        EquipmentCategory equipmentCategory = modelMapper.map(dto, EquipmentCategory.class);
        return adminEquipmentCategoryService.update(categoryId, equipmentCategory, request);
    }

    @DeleteMapping("delete/{categoryId}")
    public ResponseEntity<Object> equipmentCategoryDelete(@PathVariable Long categoryId, HttpServletRequest request) {
        return adminEquipmentCategoryService.delete(categoryId, request);
    }
}
