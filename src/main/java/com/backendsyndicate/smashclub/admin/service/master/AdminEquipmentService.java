package com.backendsyndicate.smashclub.admin.service.master;

import com.backendsyndicate.smashclub.admin.core.ICRUD;
import com.backendsyndicate.smashclub.admin.core.IUpload;
import com.backendsyndicate.smashclub.admin.dto.response.RespAdminEquipmentDetailDTO;
import com.backendsyndicate.smashclub.admin.dto.response.RespAdminEquipmentListDTO;
import com.backendsyndicate.smashclub.admin.service.log.LogService;
import com.backendsyndicate.smashclub.booking.model.Equipment;
import com.backendsyndicate.smashclub.booking.repository.EquipmentRepository;
import com.backendsyndicate.smashclub.common.constant.AdminConstant;
import com.backendsyndicate.smashclub.common.util.DatetimeFormatting;
import com.backendsyndicate.smashclub.common.util.GlobalResponse;
import com.backendsyndicate.smashclub.common.util.Logging;
import com.backendsyndicate.smashclub.external.service.storage.CloudinaryService;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.function.Function;

@Service
@Transactional
public class AdminEquipmentService implements ICRUD<Equipment, Long>, IUpload<Equipment, Long> {
    @Autowired
    private EquipmentRepository equipmentRepo;
    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private LogService logService;

    private ModelMapper modelMapper = new ModelMapper();

    @Override
    public ResponseEntity<Object> findAll(String keyword, Integer status, Pageable pageable, HttpServletRequest request) {
        Page page = null;

        try {
            if( !keyword.isEmpty() ) {
                page = equipmentRepo.findAllByEquipmentNameContainsIgnoreCase(keyword, pageable);
            } else {
                page = equipmentRepo.findAll(pageable);
            }
            if( page.isEmpty() ) {
                return GlobalResponse.failed("Equipment list is empty!", AdminConstant.ADMIN_EQUIPMENT_SERVICE_LIST_EMPTY, null, request);
            }

            page = page.map(new Function<Equipment, RespAdminEquipmentListDTO>() {
                @Override
                public RespAdminEquipmentListDTO apply(Equipment equipment) {
                    return mapListToDTO(equipment);
                }
            });
        } catch(Exception e) {
            Logging.handleException("EquipmentService", "findAll(Pageable pageable, HttpServletRequest request)", 31, AdminConstant.ADMIN_EQUIPMENT_SERVICE_LIST_EXCEPTION, e.getMessage());
            logService.writeErrorLog(AdminConstant.ADMIN_EQUIPMENT_SERVICE_LIST_EXCEPTION, "AdminEquipmentService@findAll()", e.getMessage());
            return GlobalResponse.failed("Failed to get equipment list!", AdminConstant.ADMIN_EQUIPMENT_SERVICE_LIST_EXCEPTION, null, request);
        }

        return GlobalResponse.success("Successfully get equipment list!", page, request);
    }

    @Override
    public ResponseEntity<Object> findById(Long id, HttpServletRequest request) {
        RespAdminEquipmentDetailDTO response = null;

        if( id == null ) {
            return GlobalResponse.failed("Equipment ID is required!", AdminConstant.ADMIN_EQUIPMENT_SERVICE_DETAIL_ID_REQUIRED, null, request);
        }

        try {
            Optional<Equipment> optionalEquipment = equipmentRepo.findById(id);
            if( optionalEquipment.isEmpty() ) {
                return GlobalResponse.failed("Equipment not found!", AdminConstant.ADMIN_EQUIPMENT_SERVICE_DETAIL_NOT_FOUND, null, request);
            }

            Equipment equipment = optionalEquipment.get();
            response = modelMapper.map(equipment, RespAdminEquipmentDetailDTO.class);
        } catch(Exception e) {
            Logging.handleException("EquipmentService", "findAll(Pageable pageable, HttpServletRequest request)", 79, AdminConstant.ADMIN_EQUIPMENT_SERVICE_DETAIL_EXCEPTION, e.getMessage());
            logService.writeErrorLog(AdminConstant.ADMIN_EQUIPMENT_SERVICE_DETAIL_EXCEPTION, "AdminEquipmentService@findById()", e.getMessage());
            return GlobalResponse.failed("Failed to get equipment data!", AdminConstant.ADMIN_EQUIPMENT_SERVICE_DETAIL_EXCEPTION, null, request);
        }

        return GlobalResponse.success("Equipment data found!", response, request);
    }

    @Override
    public ResponseEntity<Object> save(Equipment equipment, HttpServletRequest request) {
        if( equipment == null ) {
            return GlobalResponse.failed("Equipment data is required!", AdminConstant.ADMIN_EQUIPMENT_SERVICE_SAVE_REQUEST_INVALID, null, request);
        }

        try {
            equipmentRepo.save(equipment);
        } catch(Exception e) {
            Logging.handleException("EquipmentService", "save(Equipment equipment, HttpServletRequest request)", 72, AdminConstant.ADMIN_EQUIPMENT_SERVICE_SAVE_EXCEPTION, e.getMessage());
            logService.writeErrorLog(AdminConstant.ADMIN_EQUIPMENT_SERVICE_SAVE_EXCEPTION, "AdminEquipmentService@save()", e.getMessage());
            return GlobalResponse.failed("Failed to save equipment data!", AdminConstant.ADMIN_EQUIPMENT_SERVICE_SAVE_EXCEPTION, null, request);
        }

        return GlobalResponse.success("Successfully save equipment data!", null, request);
    }

    @Override
    public ResponseEntity<Object> update(Long id, Equipment equipment, HttpServletRequest request) {
        if( id == null ) {
            return GlobalResponse.failed("Equipment ID is required!", AdminConstant.ADMIN_EQUIPMENT_SERVICE_UPDATE_ID_REQUIRED, null, request);
        }

        if( equipment == null ) {
            return GlobalResponse.failed("Equipment data is required!", AdminConstant.ADMIN_EQUIPMENT_SERVICE_UPDATE_REQUEST_INVALID, null, request);
        }

        try {
            Optional<Equipment> optionalEquipment = equipmentRepo.findById(id);
            if( optionalEquipment.isEmpty() ) {
                return GlobalResponse.failed("Equipment data not found!", AdminConstant.ADMIN_EQUIPMENT_SERVICE_UPDATE_NOT_FOUND, null, request);
            }

            Equipment equipmentDB = optionalEquipment.get();
            equipmentDB.setEquipmentCategory(equipment.getEquipmentCategory());
            equipmentDB.setEquipmentName(equipment.getEquipmentName());
            equipmentDB.setBrand(equipment.getBrand());
            equipmentDB.setPrice(equipment.getPrice());
            equipmentDB.setDescription(equipment.getDescription());
            equipmentDB.setStock(equipment.getStock());
            equipmentDB.setType(equipment.getType());
            if( equipment.getEquipmentImgLink() != null ) {
                cloudinaryService.deleteImage(equipmentDB.getEquipmentImgLink());
                equipmentDB.setEquipmentImgLink(equipment.getEquipmentImgLink());
            }
            equipmentDB.setStatus(equipment.getStatus());
        } catch(Exception e) {
            Logging.handleException("EquipmentService", "update(Long id, Equipment equipment, HttpServletRequest request)", 92, AdminConstant.ADMIN_EQUIPMENT_SERVICE_UPDATE_EXCEPTION, e.getMessage());
            logService.writeErrorLog(AdminConstant.ADMIN_EQUIPMENT_SERVICE_UPDATE_EXCEPTION, "AdminEquipmentService@update()", e.getMessage());

            return GlobalResponse.failed("Failed to update equipment data!", AdminConstant.ADMIN_EQUIPMENT_SERVICE_UPDATE_EXCEPTION, null, request);
        }

        return GlobalResponse.success("Successfully updated equipment data!", null, request);
    }

    @Override
    public ResponseEntity<Object> delete(Long id, HttpServletRequest request) {
        if( id == null ) {
            return GlobalResponse.failed("Equipment ID is required!", AdminConstant.ADMIN_EQUIPMENT_SERVICE_DELETE_ID_REQUIRED, null, request);
        }

        try {
            Optional<Equipment> optionalEquipment = equipmentRepo.findById(id);
            if( optionalEquipment.isEmpty() ) {
                return GlobalResponse.failed("Equipment data not found!", AdminConstant.ADMIN_EQUIPMENT_SERVICE_DELETE_NOT_FOUND, null, request);
            }

            cloudinaryService.deleteImage(optionalEquipment.get().getEquipmentImgLink());
            equipmentRepo.deleteById(id);
        } catch(Exception e) {
            Logging.handleException("EquipmentService", "delete(Long id, HttpServletRequest request)", 110, AdminConstant.ADMIN_EQUIPMENT_SERVICE_DELETE_EXCEPTION, e.getMessage());
            logService.writeErrorLog(AdminConstant.ADMIN_EQUIPMENT_SERVICE_DELETE_EXCEPTION, "AdminEquipmentService@delete()", e.getMessage());
            return GlobalResponse.failed("Failed to delete equipment data!", AdminConstant.ADMIN_EQUIPMENT_SERVICE_DELETE_EXCEPTION, null, request);
        }

        return GlobalResponse.success("Successfully deleted equipment data!", null, request);
    }

    @Override
    public ResponseEntity<Object> save(Equipment equipment, MultipartFile file, HttpServletRequest request) {
        if( equipment == null ) {
            return GlobalResponse.failed("Equipment data is required!", AdminConstant.ADMIN_EQUIPMENT_SERVICE_SAVE_FILE_REQUEST_INVALID, null, request);
        }

        String equipmentImgLink =  cloudinaryService.uploadImageGetUrl("equipment", file);
        if( equipmentImgLink == null || equipmentImgLink.isEmpty() ) {
            Logging.handleException("EquipmentService", "save(Equipment equipment, MultipartFile file, HttpServletRequest request)", 178, AdminConstant.ADMIN_EQUIPMENT_SERVICE_SAVE_FILE_IMAGE_ERROR, "Failed to upload image!");
            logService.writeErrorLog(AdminConstant.ADMIN_EQUIPMENT_SERVICE_SAVE_FILE_IMAGE_ERROR, "AdminEquipmentService@save()", "Failed to upload image!");
            return GlobalResponse.failed("Failed to upload equipment image!", AdminConstant.ADMIN_EQUIPMENT_SERVICE_SAVE_FILE_IMAGE_ERROR, null, request);
        }

        equipment.setEquipmentImgLink(equipmentImgLink);

        ResponseEntity<Object> response = save(equipment, request);

        return response;
    }

    @Override
    public ResponseEntity<Object> update(Long id, Equipment equipment, MultipartFile file, HttpServletRequest request) {
        if( id == null ) {
            return GlobalResponse.failed("Equipment ID is required!", AdminConstant.ADMIN_EQUIPMENT_SERVICE_UPDATE_FILE_ID_REQUIRED, null, request);
        }

        if( equipment == null ) {
            return GlobalResponse.failed("Equipment data is required!", AdminConstant.ADMIN_EQUIPMENT_SERVICE_UPDATE_FILE_REQUEST_INVALID, null, request);
        }

        if( file != null ) {
            String equipmentImgLink = cloudinaryService.uploadImageGetUrl("equipment", file);
            if( equipmentImgLink == null || equipmentImgLink.isEmpty() ) {
                Logging.handleException("EquipmentService", "save(Equipment equipment, MultipartFile file, HttpServletRequest request)", 178, AdminConstant.ADMIN_EQUIPMENT_SERVICE_UPDATE_FILE_IMAGE_ERROR, "Failed to upload image!");
                logService.writeErrorLog(AdminConstant.ADMIN_EQUIPMENT_SERVICE_UPDATE_FILE_IMAGE_ERROR, "AdminEquipmentService@update()", "Failed to upload image!");
                return GlobalResponse.failed("Failed to upload equipment image!", AdminConstant.ADMIN_EQUIPMENT_SERVICE_UPDATE_FILE_IMAGE_ERROR, null, request);
            }

            equipment.setEquipmentImgLink(equipmentImgLink);
        }

        ResponseEntity<Object> response = update(id, equipment, request);

        return response;
    }

    private RespAdminEquipmentListDTO mapListToDTO(Equipment equipment) {
        RespAdminEquipmentListDTO result = modelMapper.map(equipment, RespAdminEquipmentListDTO.class);
        if( equipment.getCreatedAt() != null ) {
            result.setCreatedAt(DatetimeFormatting.getDatetimeFormat(equipment.getCreatedAt()));
        }

        return result;
    }
}
