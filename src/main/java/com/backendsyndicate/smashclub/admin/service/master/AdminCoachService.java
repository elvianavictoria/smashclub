package com.backendsyndicate.smashclub.admin.service.master;

import com.backendsyndicate.smashclub.admin.core.ICRUD;
import com.backendsyndicate.smashclub.admin.core.IUpload;
import com.backendsyndicate.smashclub.admin.dto.response.RespAdminCoachListDTO;
import com.backendsyndicate.smashclub.admin.service.log.LogService;
import com.backendsyndicate.smashclub.booking.model.Coach;
import com.backendsyndicate.smashclub.booking.repo.CoachRepo;
import com.backendsyndicate.smashclub.common.constant.AdminConstant;
import com.backendsyndicate.smashclub.common.util.DatetimeFormatting;
import com.backendsyndicate.smashclub.common.util.GlobalResponse;
import com.backendsyndicate.smashclub.common.util.Logging;
import com.backendsyndicate.smashclub.external.dto.CloudinaryResponseDTO;
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
public class AdminCoachService implements ICRUD<Coach, Long>, IUpload<Coach, Long> {
    @Autowired
    private CoachRepo coachRepo;
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
                page = coachRepo.findAllByCoachCodeContainsOrCoachNameContainsIgnoreCase(keyword, keyword, pageable);
            } else {
                page = coachRepo.findAll(pageable);
            }
            if( page.isEmpty() ) {
                return GlobalResponse.failed("Coach list is empty!", AdminConstant.ADMIN_COACH_SERVICE_LIST_EMPTY, null, request);
            }

            page = page.map(new Function<Coach, RespAdminCoachListDTO>() {
                @Override
                public RespAdminCoachListDTO apply(Coach coach) {
                    return mapListToDTO(coach);
                }
            });
        } catch(Exception e) {
            Logging.handleException("CoachService", "findAll(Pageable pageable, HttpServletRequest request)", 35, AdminConstant.ADMIN_COACH_SERVICE_LIST_EXCEPTION, e.getMessage());
            logService.writeErrorLog(AdminConstant.ADMIN_COACH_SERVICE_LIST_EXCEPTION, "AdminCoachService@findAll()", e.getMessage());
            return GlobalResponse.failed("Failed to get coach list!", AdminConstant.ADMIN_COACH_SERVICE_LIST_EXCEPTION, null, request);
        }

        return GlobalResponse.success("Successfully get coach list!", page, request);
    }

    @Override
    public ResponseEntity<Object> findById(Long id, HttpServletRequest request) {
        Coach coach = null;

        if( id == null ) {
            return GlobalResponse.failed("Coach ID is required!", AdminConstant.ADMIN_COACH_SERVICE_DETAIL_ID_REQUIRED, null, request);
        }

        try {
            Optional<Coach> optionalCoach = coachRepo.findById(id);
            if( optionalCoach.isEmpty() ) {
                return GlobalResponse.failed("Coach not found!", AdminConstant.ADMIN_COACH_SERVICE_DETAIL_NOT_FOUND, null, request);
            }

            coach = optionalCoach.get();
        } catch(Exception e) {
            Logging.handleException("CoachService", "findAll(Pageable pageable, HttpServletRequest request)", 35, AdminConstant.ADMIN_COACH_SERVICE_DETAIL_EXCEPTION, e.getMessage());
            logService.writeErrorLog(AdminConstant.ADMIN_COACH_SERVICE_DETAIL_EXCEPTION, "AdminCoachService@findById()", e.getMessage());
            return GlobalResponse.failed("Failed to get coach data!", AdminConstant.ADMIN_COACH_SERVICE_DETAIL_EXCEPTION, null, request);
        }

        return GlobalResponse.success("Coach data found!", coach, request);
    }

    @Override
    public ResponseEntity<Object> save(Coach coach, HttpServletRequest request) {
        if( coach == null ) {
            return GlobalResponse.failed("Coach data is required!", AdminConstant.ADMIN_COACH_SERVICE_SAVE_REQUEST_INVALID, null, request);
        }

        try {
            coachRepo.save(coach);
        } catch(Exception e) {
            Logging.handleException("CoachService", "save(Coach coach, HttpServletRequest request)", 75, AdminConstant.ADMIN_COACH_SERVICE_SAVE_EXCEPTION, e.getMessage());
            logService.writeErrorLog(AdminConstant.ADMIN_COACH_SERVICE_SAVE_EXCEPTION, "AdminCoachService@save()", e.getMessage());
            return GlobalResponse.failed("Failed to save coach data!", AdminConstant.ADMIN_COACH_SERVICE_SAVE_EXCEPTION, null, request);
        }

        return GlobalResponse.success("Successfully save coach data!", null, request);
    }

    @Override
    public ResponseEntity<Object> update(Long id, Coach coach, HttpServletRequest request) {
        if( id == null ) {
            return GlobalResponse.failed("Coach ID is required!", AdminConstant.ADMIN_COACH_SERVICE_UPDATE_ID_REQUIRED, null, request);
        }

        if( coach == null ) {
            return GlobalResponse.failed("Coach data is required!", AdminConstant.ADMIN_COACH_SERVICE_UPDATE_REQUEST_INVALID, null, request);
        }

        try {
            Optional<Coach> optionalCoach = coachRepo.findById(id);
            if( optionalCoach.isEmpty() ) {
                return GlobalResponse.failed("Coach data not found!", AdminConstant.ADMIN_COACH_SERVICE_UPDATE_NOT_FOUND, null, request);
            }

            Coach coachDB = optionalCoach.get();
            coachDB.setCoachCode(coach.getCoachCode());
            coachDB.setCoachName(coach.getCoachName());
            coachDB.setPricePerHour(coach.getPricePerHour());
            if( coach.getCoachImgLink() != null ) coachDB.setCoachImgLink(coach.getCoachImgLink());
            coachDB.setStatus(coach.getStatus());
        } catch(Exception e) {
            Logging.handleException("CoachService", "update(Long id, Coach coach, HttpServletRequest request)", 94, AdminConstant.ADMIN_COACH_SERVICE_UPDATE_EXCEPTION, e.getMessage());
            logService.writeErrorLog(AdminConstant.ADMIN_COACH_SERVICE_UPDATE_EXCEPTION, "AdminCoachService@update()", e.getMessage());
            return GlobalResponse.failed("Failed to update coach data!", AdminConstant.ADMIN_COACH_SERVICE_UPDATE_EXCEPTION, null, request);
        }

        return GlobalResponse.success("Successfully updated coach data!", null, request);
    }

    @Override
    public ResponseEntity<Object> delete(Long id, HttpServletRequest request) {
        if( id == null ) {
            return GlobalResponse.failed("Coach ID is required!", AdminConstant.ADMIN_COACH_SERVICE_DELETE_ID_REQUIRED, null, request);
        }

        try {
            Optional<Coach> optionalCoach = coachRepo.findById(id);
            if( optionalCoach.isEmpty() ) {
                return GlobalResponse.failed("Coach data not found!", AdminConstant.ADMIN_COACH_SERVICE_DELETE_NOT_FOUND, null, request);
            }

            coachRepo.deleteById(id);
        } catch(Exception e) {
            Logging.handleException("CoachService", "delete(Long id, HttpServletRequest request)", 122, AdminConstant.ADMIN_COACH_SERVICE_DELETE_EXCEPTION, e.getMessage());
            logService.writeErrorLog(AdminConstant.ADMIN_COACH_SERVICE_DELETE_EXCEPTION, "AdminCoachService@delete()", e.getMessage());
            return GlobalResponse.failed("Failed to delete coach data!", AdminConstant.ADMIN_COACH_SERVICE_DELETE_EXCEPTION, null, request);
        }

        return GlobalResponse.success("Successfully deleted coach data!", null, request);
    }

    @Override
    public ResponseEntity<Object> save(Coach coach, MultipartFile file, HttpServletRequest request) {
        if( coach == null ) {
            return GlobalResponse.failed("Coach data is required!", AdminConstant.ADMIN_COACH_SERVICE_SAVE_FILE_REQUEST_INVALID, null, request);
        }

        String coachImgLink = cloudinaryService.uploadImageGetUrl("coach", file);
        if( coachImgLink == null || coachImgLink.isEmpty() ) {
            Logging.handleException("AdminCoachService", "save(Coach coach, MultipartFile file, HttpServletRequest request)", 184, AdminConstant.ADMIN_COACH_SERVICE_SAVE_FILE_IMAGE_ERROR, "Failed to upload image!");
            logService.writeErrorLog(AdminConstant.ADMIN_COACH_SERVICE_SAVE_FILE_IMAGE_ERROR, "AdminCoachService@save()", "Failed to upload image!");
            return GlobalResponse.failed("Failed to upload coach image!", AdminConstant.ADMIN_COACH_SERVICE_SAVE_FILE_IMAGE_ERROR, null, request);
        }

        coach.setCoachImgLink(coachImgLink);

        ResponseEntity<Object> response = save(coach, request);

        return response;
    }

    @Override
    public ResponseEntity<Object> update(Long id, Coach coach, MultipartFile file, HttpServletRequest request) {
        if( id == null ) {
            return GlobalResponse.failed("Coach ID is required!", AdminConstant.ADMIN_COACH_SERVICE_UPDATE_FILE_ID_REQUIRED, null, request);
        }

        if( coach == null ) {
            return GlobalResponse.failed("Coach data is required!", AdminConstant.ADMIN_COACH_SERVICE_UPDATE_FILE_REQUEST_INVALID, null, request);
        }

        if( file != null ) {
            String coachImgLink = cloudinaryService.uploadImageGetUrl("coach", file);
            if( coachImgLink == null || coachImgLink.isEmpty() ) {
                Logging.handleException("AdminCoachService", "update(Long id, Coach coach, MultipartFile file, HttpServletRequest request)", 184, AdminConstant.ADMIN_COACH_SERVICE_UPDATE_FILE_IMAGE_ERROR, "Failed to upload image!");
                logService.writeErrorLog(AdminConstant.ADMIN_COACH_SERVICE_UPDATE_FILE_IMAGE_ERROR, "AdminCoachService@update()", "Failed to upload image!");
                return GlobalResponse.failed("Failed to upload coach image!", AdminConstant.ADMIN_COACH_SERVICE_UPDATE_FILE_IMAGE_ERROR, null, request);
            }

            coach.setCoachImgLink(coachImgLink);
        }

        ResponseEntity<Object> response = update(id, coach, request);

        return response;
    }

    private RespAdminCoachListDTO mapListToDTO(Coach coach) {
        RespAdminCoachListDTO result = modelMapper.map(coach, RespAdminCoachListDTO.class);
        if( coach.getCreatedAt() != null ) {
            result.setCreatedAt(DatetimeFormatting.getDatetimeFormat(coach.getCreatedAt()));
        }
        if( coach.getUpdatedAt() != null ) {
            result.setUpdatedAt(DatetimeFormatting.getDatetimeFormat(coach.getUpdatedAt()));
        }

        return result;
    }
}
