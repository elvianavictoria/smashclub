package com.backendsyndicate.smashclub.admin.service.master;

import com.backendsyndicate.smashclub.admin.core.ICRUD;
import com.backendsyndicate.smashclub.admin.service.log.LogService;
import com.backendsyndicate.smashclub.booking.model.EquipmentCategory;
import com.backendsyndicate.smashclub.booking.repository.EquipmentCategoryRepository;
import com.backendsyndicate.smashclub.booking.repository.EquipmentRepository;
import com.backendsyndicate.smashclub.common.constant.AdminConstant;
import com.backendsyndicate.smashclub.common.util.GlobalResponse;
import com.backendsyndicate.smashclub.common.util.Logging;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class AdminEquipmentCategoryService implements ICRUD<EquipmentCategory, Long> {
    @Autowired
    private EquipmentCategoryRepository equipmentCategoryRepo;
    @Autowired
    private EquipmentRepository equipmentRepo;
    @Autowired
    private LogService logService;

    private ModelMapper modelMapper = new ModelMapper();

    @Override
    public ResponseEntity<Object> findAll(String keyword, Integer status, Pageable pageable, HttpServletRequest request) {
        Page page = null;

        try {
            if( !keyword.isEmpty() ) {
                page = equipmentCategoryRepo.findAllByCategoryNameContainsIgnoreCase(keyword, pageable);
            } else {
                page = equipmentCategoryRepo.findAll(pageable);
            }
            if( page.isEmpty() ) {
                return GlobalResponse.failed("Equipment category list is empty!", AdminConstant.ADMIN_EQUIPMENT_CATEGORY_SERVICE_LIST_EMPTY, null, request);
            }
        } catch(Exception e) {
            Logging.handleException("EquipmentCategoryService", "findAll(Pageable pageable, HttpServletRequest request)", 39, AdminConstant.ADMIN_EQUIPMENT_CATEGORY_SERVICE_LIST_EXCEPTION, e.getMessage());
            logService.writeErrorLog(AdminConstant.ADMIN_EQUIPMENT_CATEGORY_SERVICE_LIST_EXCEPTION, "AdminEquipmentCategoryService@findAll()", e.getMessage());
            return GlobalResponse.failed("Failed to get equipment category list!", AdminConstant.ADMIN_EQUIPMENT_CATEGORY_SERVICE_LIST_EXCEPTION, null, request);
        }

        return GlobalResponse.success("Successfully get equipment category list!", page, request);
    }

    @Override
    public ResponseEntity<Object> findById(Long id, HttpServletRequest request) {
        EquipmentCategory equipmentCategory = null;

        if( id == null ) {
            return GlobalResponse.failed("Equipment category ID is required!", AdminConstant.ADMIN_EQUIPMENT_CATEGORY_SERVICE_DETAIL_ID_REQUIRED, null, request);
        }

        try {
            Optional<EquipmentCategory> optionalEquipmentCategory = equipmentCategoryRepo.findById(id);
            if( optionalEquipmentCategory.isEmpty() ) {
                return GlobalResponse.failed("Equipment category not found!", AdminConstant.ADMIN_EQUIPMENT_CATEGORY_SERVICE_DETAIL_NOT_FOUND, null, request);
            }

            equipmentCategory = optionalEquipmentCategory.get();
        } catch(Exception e) {
            Logging.handleException("AdminEquipmentCategoryService", "findById(Long id, Court court, MultipartFile file, HttpServletRequest request)", 65, AdminConstant.ADMIN_EQUIPMENT_CATEGORY_SERVICE_DETAIL_EXCEPTION, e.getMessage());
            logService.writeErrorLog(AdminConstant.ADMIN_EQUIPMENT_CATEGORY_SERVICE_DETAIL_EXCEPTION, "AdminEquipmentCategoryService@findById()", e.getMessage());
            return GlobalResponse.failed("Failed to get equipment category data!", AdminConstant.ADMIN_EQUIPMENT_CATEGORY_SERVICE_DETAIL_EXCEPTION, null, request);
        }

        return GlobalResponse.success("Equipment category data found!", equipmentCategory, request);
    }

    @Override
    public ResponseEntity<Object> save(EquipmentCategory equipmentCategory, HttpServletRequest request) {
        if( equipmentCategory == null ) {
            return GlobalResponse.failed("Equipment category data is required!", AdminConstant.ADMIN_EQUIPMENT_CATEGORY_SERVICE_SAVE_REQUEST_INVALID, null, request);
        }

        try {
            equipmentCategoryRepo.save(equipmentCategory);
        } catch(Exception e) {
            Logging.handleException("EquipmentCategoryService", "save(EquipmentCategory equipmentCategory, HttpServletRequest request)", 72, AdminConstant.ADMIN_EQUIPMENT_CATEGORY_SERVICE_SAVE_EXCEPTION, e.getMessage());
            logService.writeErrorLog(AdminConstant.ADMIN_EQUIPMENT_CATEGORY_SERVICE_SAVE_EXCEPTION, "AdminEquipmentCategoryService@save()", e.getMessage());
            return GlobalResponse.failed("Failed to save equipment category data!",AdminConstant.ADMIN_EQUIPMENT_CATEGORY_SERVICE_SAVE_EXCEPTION, null, request);
        }

        return GlobalResponse.success("Successfully save equipment category data!", null, request);
    }

    @Override
    public ResponseEntity<Object> update(Long id, EquipmentCategory equipmentCategory, HttpServletRequest request) {
        if( id == null ) {
            return GlobalResponse.failed("Equipment category ID is required!", AdminConstant.ADMIN_EQUIPMENT_CATEGORY_SERVICE_UPDATE_ID_REQUIRED, null, request);
        }

        if( equipmentCategory == null ) {
            return GlobalResponse.failed("Equipment category data is required!", AdminConstant.ADMIN_EQUIPMENT_CATEGORY_SERVICE_UPDATE_REQUEST_INVALID, null, request);
        }

        try {
            Optional<EquipmentCategory> optionalEquipmentCategory = equipmentCategoryRepo.findById(id);
            if( optionalEquipmentCategory.isEmpty() ) {
                return GlobalResponse.failed("Equipment category data not found!", AdminConstant.ADMIN_EQUIPMENT_CATEGORY_SERVICE_UPDATE_NOT_FOUND, null, request);
            }

            EquipmentCategory equipmentCategoryDB = optionalEquipmentCategory.get();
            equipmentCategoryDB.setCategoryName(equipmentCategory.getCategoryName());
            equipmentCategoryDB.setStatus(equipmentCategory.getStatus());
        } catch(Exception e) {
            Logging.handleException("EquipmentCategoryService", "update(Long id, EquipmentCategory equipmentCategory, HttpServletRequest request)", 92, AdminConstant.ADMIN_EQUIPMENT_CATEGORY_SERVICE_UPDATE_EXCEPTION, e.getMessage());
            logService.writeErrorLog(AdminConstant.ADMIN_EQUIPMENT_CATEGORY_SERVICE_UPDATE_EXCEPTION, "AdminEquipmentCategoryService@update()", e.getMessage());
            return GlobalResponse.failed("Failed to update equipment category data!", AdminConstant.ADMIN_EQUIPMENT_CATEGORY_SERVICE_UPDATE_EXCEPTION, null, request);
        }

        return GlobalResponse.success("Successfully updated equipment category data!", null, request);
    }

    @Override
    public ResponseEntity<Object> delete(Long id, HttpServletRequest request) {
        if( id == null ) {
            return GlobalResponse.failed("Equipment category ID is required!", AdminConstant.ADMIN_EQUIPMENT_CATEGORY_SERVICE_DELETE_ID_REQUIRED, null, request);
        }

        try {
            Optional<EquipmentCategory> optionalEquipmentCategory = equipmentCategoryRepo.findById(id);
            if( optionalEquipmentCategory.isEmpty() ) {
                return GlobalResponse.failed("Equipment category data not found!", AdminConstant.ADMIN_EQUIPMENT_CATEGORY_SERVICE_DELETE_NOT_FOUND, null, request);
            }

            equipmentRepo.deleteByEquipmentCategory_Id(id);
            equipmentCategoryRepo.deleteById(id);
        } catch(Exception e) {
            Logging.handleException("EquipmentCategoryService", "delete(Long id, HttpServletRequest request)", 110, AdminConstant.ADMIN_EQUIPMENT_CATEGORY_SERVICE_DELETE_EXCEPTION, e.getMessage());
            logService.writeErrorLog(AdminConstant.ADMIN_EQUIPMENT_CATEGORY_SERVICE_DELETE_EXCEPTION, "AdminEquipmentCategoryService@delete()", e.getMessage());
            return GlobalResponse.failed("Failed to delete equipment category data!", AdminConstant.ADMIN_EQUIPMENT_CATEGORY_SERVICE_DELETE_EXCEPTION, null, request);
        }

        return GlobalResponse.success("Successfully deleted equipment category data!", null, request);
    }
}
