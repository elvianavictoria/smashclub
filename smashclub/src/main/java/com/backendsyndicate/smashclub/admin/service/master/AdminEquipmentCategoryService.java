package com.backendsyndicate.smashclub.admin.service.master;

import com.backendsyndicate.smashclub.admin.core.ICRUD;
import com.backendsyndicate.smashclub.booking.model.EquipmentCategory;
import com.backendsyndicate.smashclub.booking.repo.EquipmentCategoryRepo;
import com.backendsyndicate.smashclub.booking.repo.EquipmentRepo;
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
    private EquipmentCategoryRepo equipmentCategoryRepo;
    @Autowired
    private EquipmentRepo equipmentRepo;
    private ModelMapper modelMapper = new ModelMapper();

    private String generateErrorCode(String methodNo, String errorNo) {
        return "ADM-EQPCAT" + "-" + methodNo + "-" + errorNo;
    }

    @Override
    public ResponseEntity<Object> findAll(String keyword, Pageable pageable, HttpServletRequest request) {
        Page page = null;

        try {
            if( !keyword.isEmpty() ) {
                page = equipmentCategoryRepo.findAllByCategoryNameContainsIgnoreCase(keyword, pageable);
            } else {
                page = equipmentCategoryRepo.findAll(pageable);
            }
            if( page.isEmpty() ) {
                return GlobalResponse.failed("Equipment category list is empty!", generateErrorCode("01", "001"), null, request);
            }
        } catch(Exception e) {
            Logging.handleException("EquipmentCategoryService", "findAll(Pageable pageable, HttpServletRequest request)", 31, generateErrorCode("01", "010"), e.getMessage());
            return GlobalResponse.failed("Failed to get equipment category list!", generateErrorCode("01", "010"), null, request);
        }

        return GlobalResponse.success("Successfully get equipment category list!", page, request);
    }

    @Override
    public ResponseEntity<Object> findById(Long id, HttpServletRequest request) {
        EquipmentCategory equipmentCategory = null;

        if( id == null ) {
            return GlobalResponse.failed("Equipment category ID is required!", generateErrorCode("02", "001"), null, request);
        }

        try {
            Optional<EquipmentCategory> optionalEquipmentCategory = equipmentCategoryRepo.findById(id);
            if( optionalEquipmentCategory.isEmpty() ) {
                return GlobalResponse.failed("Equipment category not found!", generateErrorCode("02", "002"), null, request);
            }

            equipmentCategory = optionalEquipmentCategory.get();
        } catch(Exception e) {
            return GlobalResponse.failed("Failed to get equipment category data!", generateErrorCode("02", "010"), null, request);
        }

        return GlobalResponse.success("Equipment category data found!", equipmentCategory, request);
    }

    @Override
    public ResponseEntity<Object> save(EquipmentCategory equipmentCategory, HttpServletRequest request) {
        if( equipmentCategory == null ) {
            return GlobalResponse.failed("Equipment category data is required!", generateErrorCode("03", "001"), null, request);
        }

        try {
            equipmentCategoryRepo.save(equipmentCategory);
        } catch(Exception e) {
            Logging.handleException("EquipmentCategoryService", "save(EquipmentCategory equipmentCategory, HttpServletRequest request)", 72, generateErrorCode("03", "010"), e.getMessage());
            return GlobalResponse.failed("Failed to save equipment category data!", generateErrorCode("03", "010"), null, request);
        }

        return GlobalResponse.success("Successfully save equipment category data!", null, request);
    }

    @Override
    public ResponseEntity<Object> update(Long id, EquipmentCategory equipmentCategory, HttpServletRequest request) {
        if( id == null ) {
            return GlobalResponse.failed("Equipment category ID is required!", generateErrorCode("04", "001"), null, request);
        }

        if( equipmentCategory == null ) {
            return GlobalResponse.failed("Equipment category data is required!", generateErrorCode("04", "002"), null, request);
        }

        try {
            Optional<EquipmentCategory> optionalEquipmentCategory = equipmentCategoryRepo.findById(id);
            if( optionalEquipmentCategory.isEmpty() ) {
                return GlobalResponse.failed("Equipment category data not found!", generateErrorCode("04", "003"), null, request);
            }

            EquipmentCategory equipmentCategoryDB = optionalEquipmentCategory.get();
            equipmentCategoryDB.setCategoryName(equipmentCategory.getCategoryName());
            equipmentCategoryDB.setStatus(equipmentCategory.getStatus());
        } catch(Exception e) {
            Logging.handleException("EquipmentCategoryService", "update(Long id, EquipmentCategory equipmentCategory, HttpServletRequest request)", 92, generateErrorCode("04", "010"), e.getMessage());
            return GlobalResponse.failed("Failed to update equipment category data!", generateErrorCode("04", "010"), null, request);
        }

        return GlobalResponse.success("Successfully updated equipment category data!", null, request);
    }

    @Override
    public ResponseEntity<Object> delete(Long id, HttpServletRequest request) {
        if( id == null ) {
            return GlobalResponse.failed("Equipment category ID is required!", generateErrorCode("05", "001"), null, request);
        }

        try {
            Optional<EquipmentCategory> optionalEquipmentCategory = equipmentCategoryRepo.findById(id);
            if( optionalEquipmentCategory.isEmpty() ) {
                return GlobalResponse.failed("Equipment category data not found!", generateErrorCode("05", "002"), null, request);
            }

            equipmentRepo.deleteByEquipmentCategory_Id(id);
            equipmentCategoryRepo.deleteById(id);
        } catch(Exception e) {
            Logging.handleException("EquipmentCategoryService", "delete(Long id, HttpServletRequest request)", 110, generateErrorCode("05", "010"), e.getMessage());
            return GlobalResponse.failed("Failed to delete equipment category data!", generateErrorCode("05", "010"), null, request);
        }

        return GlobalResponse.success("Successfully deleted equipment category data!", null, request);
    }
}
