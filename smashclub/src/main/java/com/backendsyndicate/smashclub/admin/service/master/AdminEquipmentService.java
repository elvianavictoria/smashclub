package com.backendsyndicate.smashclub.admin.service.master;

import com.backendsyndicate.smashclub.admin.core.ICRUD;
import com.backendsyndicate.smashclub.admin.dto.response.RespAdminCourtListDTO;
import com.backendsyndicate.smashclub.admin.dto.response.RespAdminEquipmentListDTO;
import com.backendsyndicate.smashclub.booking.model.Court;
import com.backendsyndicate.smashclub.booking.model.Equipment;
import com.backendsyndicate.smashclub.booking.repo.EquipmentRepo;
import com.backendsyndicate.smashclub.common.util.DatetimeFormatting;
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
import java.util.function.Function;

@Service
@Transactional
public class AdminEquipmentService implements ICRUD<Equipment, Long> {
    @Autowired
    private EquipmentRepo equipmentRepo;
    private ModelMapper modelMapper = new ModelMapper();

    private String generateErrorCode(String methodNo, String errorNo) {
        return "ADM-EQP" + "-" + methodNo + "-" + errorNo;
    }

    @Override
    public ResponseEntity<Object> findAll(String keyword, Pageable pageable, HttpServletRequest request) {
        Page page = null;

        try {
            if( !keyword.isEmpty() ) {
                page = equipmentRepo.findAllByEquipmentNameContains(keyword, pageable);
            } else {
                page = equipmentRepo.findAll(pageable);
            }
            if( page.isEmpty() ) {
                return GlobalResponse.failed("Equipment list is empty!", generateErrorCode("01", "001"), null, request);
            }

            page = page.map(new Function<Equipment, RespAdminEquipmentListDTO>() {
                @Override
                public RespAdminEquipmentListDTO apply(Equipment equipment) {
                    return mapListToDTO(equipment);
                }
            });
        } catch(Exception e) {
            Logging.handleException("EquipmentService", "findAll(Pageable pageable, HttpServletRequest request)", 31, generateErrorCode("01", "010"), e.getMessage());
            return GlobalResponse.failed("Failed to get equipment list!", generateErrorCode("01", "010"), null, request);
        }

        return GlobalResponse.success("Successfully get equipment list!", page, request);
    }

    @Override
    public ResponseEntity<Object> findById(Long id, HttpServletRequest request) {
        Equipment equipment = null;

        if( id == null ) {
            return GlobalResponse.failed("Equipment ID is required!", generateErrorCode("02", "001"), null, request);
        }

        try {
            Optional<Equipment> optionalEquipment = equipmentRepo.findById(id);
            if( optionalEquipment.isEmpty() ) {
                return GlobalResponse.failed("Equipment not found!", generateErrorCode("02", "002"), null, request);
            }

            equipment = optionalEquipment.get();
        } catch(Exception e) {
            return GlobalResponse.failed("Failed to get equipment data!", generateErrorCode("02", "010"), null, request);
        }

        return GlobalResponse.success("Equipment data found!", equipment, request);
    }

    @Override
    public ResponseEntity<Object> save(Equipment equipment, HttpServletRequest request) {
        if( equipment == null ) {
            return GlobalResponse.failed("Equipment data is required!", generateErrorCode("03", "001"), null, request);
        }

        try {
            equipmentRepo.save(equipment);
        } catch(Exception e) {
            Logging.handleException("EquipmentService", "save(Equipment equipment, HttpServletRequest request)", 72, generateErrorCode("03", "010"), e.getMessage());
            return GlobalResponse.failed("Failed to save equipment data!", generateErrorCode("03", "010"), null, request);
        }

        return GlobalResponse.success("Successfully save equipment data!", null, request);
    }

    @Override
    public ResponseEntity<Object> update(Long id, Equipment equipment, HttpServletRequest request) {
        if( id == null ) {
            return GlobalResponse.failed("Equipment ID is required!", generateErrorCode("04", "001"), null, request);
        }

        if( equipment == null ) {
            return GlobalResponse.failed("Equipment data is required!", generateErrorCode("04", "002"), null, request);
        }

        try {
            Optional<Equipment> optionalEquipment = equipmentRepo.findById(id);
            if( optionalEquipment.isEmpty() ) {
                return GlobalResponse.failed("Equipment data not found!", generateErrorCode("04", "003"), null, request);
            }

            Equipment equipmentDB = optionalEquipment.get();
            equipmentDB.setEquipmentCategory(equipment.getEquipmentCategory());
            equipmentDB.setEquipmentName(equipment.getEquipmentName());
            equipmentDB.setBrand(equipment.getBrand());
            equipmentDB.setPrice(equipment.getPrice());
            equipmentDB.setDescription(equipment.getDescription());
            equipmentDB.setStock(equipment.getStock());
            equipmentDB.setType(equipment.getType());
            equipmentDB.setStatus(equipment.getStatus());
        } catch(Exception e) {
            Logging.handleException("EquipmentService", "update(Long id, Equipment equipment, HttpServletRequest request)", 92, generateErrorCode("04", "010"), e.getMessage());
            return GlobalResponse.failed("Failed to update equipment data!", generateErrorCode("04", "010"), null, request);
        }

        return GlobalResponse.success("Successfully updated equipment data!", null, request);
    }

    @Override
    public ResponseEntity<Object> delete(Long id, HttpServletRequest request) {
        if( id == null ) {
            return GlobalResponse.failed("Equipment ID is required!", generateErrorCode("05", "001"), null, request);
        }

        try {
            Optional<Equipment> optionalEquipment = equipmentRepo.findById(id);
            if( optionalEquipment.isEmpty() ) {
                return GlobalResponse.failed("Equipment data not found!", generateErrorCode("05", "002"), null, request);
            }

            equipmentRepo.deleteById(id);
        } catch(Exception e) {
            Logging.handleException("EquipmentService", "delete(Long id, HttpServletRequest request)", 110, generateErrorCode("05", "010"), e.getMessage());
            return GlobalResponse.failed("Failed to delete equipment data!", generateErrorCode("05", "010"), null, request);
        }

        return GlobalResponse.success("Successfully deleted equipment data!", null, request);
    }

    private RespAdminEquipmentListDTO mapListToDTO(Equipment equipment) {
        RespAdminEquipmentListDTO result = modelMapper.map(equipment, RespAdminEquipmentListDTO.class);
        if( equipment.getCreatedAt() != null ) {
            result.setCreatedAt(DatetimeFormatting.getDatetimeFormat(equipment.getCreatedAt()));
        }

        return result;
    }
}
