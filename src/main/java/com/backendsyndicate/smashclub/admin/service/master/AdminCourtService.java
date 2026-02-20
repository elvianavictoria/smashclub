package com.backendsyndicate.smashclub.admin.service.master;

import com.backendsyndicate.smashclub.admin.core.ICRUD;
import com.backendsyndicate.smashclub.admin.core.IUpload;
import com.backendsyndicate.smashclub.admin.dto.response.RespAdminCourtListDTO;
import com.backendsyndicate.smashclub.admin.service.log.LogService;
import com.backendsyndicate.smashclub.booking.model.Court;
import com.backendsyndicate.smashclub.booking.repo.CourtRepo;
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

import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.function.Function;

@Service
@Transactional
public class AdminCourtService implements ICRUD<Court, Long>, IUpload<Court, Long> {
    @Autowired
    private CourtRepo courtRepo;
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
                page = courtRepo.findAllByCourtCodeContainsOrCourtNameContainsIgnoreCase(keyword, keyword, pageable);
            } else {
                page = courtRepo.findAll(pageable);
            }

            if( page.isEmpty() ) {
                return GlobalResponse.failed("Court list is empty!", AdminConstant.ADMIN_COURT_SERVICE_LIST_EMPTY, null, request);
            }

            page = page.map(new Function<Court, RespAdminCourtListDTO>() {
                @Override
                public RespAdminCourtListDTO apply(Court court) {
                    return mapListToDTO(court);
                }
            });
        } catch(Exception e) {
            Logging.handleException("AdminCourtService", "findAll(Pageable pageable, HttpServletRequest request)", 33, AdminConstant.ADMIN_COURT_SERVICE_LIST_EXCEPTION, e.getMessage());
            logService.writeErrorLog(AdminConstant.ADMIN_COURT_SERVICE_LIST_EXCEPTION, "AdminCourtService@findAll()", e.getMessage());
            return GlobalResponse.failed("Failed to get court list!", AdminConstant.ADMIN_COURT_SERVICE_LIST_EXCEPTION, null, request);
        }

        return GlobalResponse.success("Successfully get court list!", page, request);
    }

    @Override
    public ResponseEntity<Object> findById(Long id, HttpServletRequest request) {
        Court court = null;

        if( id == null ) {
            return GlobalResponse.failed("Court ID is required!", AdminConstant.ADMIN_COURT_SERVICE_DETAIL_ID_REQUIRED, null, request);
        }

        try {
            Optional<Court> optionalCourt = courtRepo.findById(id);
            if( optionalCourt.isEmpty() ) {
                return GlobalResponse.failed("Court not found!", AdminConstant.ADMIN_COURT_SERVICE_DETAIL_NOT_FOUND, null, request);
            }

            court = optionalCourt.get();
        } catch(Exception e) {
            Logging.handleException("AdminCourtService", "findById(Long id, HttpServletRequest request)", 33, AdminConstant.ADMIN_COURT_SERVICE_DETAIL_EXCEPTION, e.getMessage());
            logService.writeErrorLog(AdminConstant.ADMIN_COURT_SERVICE_DETAIL_EXCEPTION, "AdminCourtService@findById()", e.getMessage());
            return GlobalResponse.failed("Failed to get court data!", AdminConstant.ADMIN_COURT_SERVICE_DETAIL_EXCEPTION, null, request);
        }

        return GlobalResponse.success("Court data found!", court, request);
    }

    @Override
    public ResponseEntity<Object> save(Court court, HttpServletRequest request) {
        if( court == null ) {
            return GlobalResponse.failed("Court data is required!", AdminConstant.ADMIN_COURT_SERVICE_SAVE_REQUEST_INVALID, null, request);
        }

        try {
            courtRepo.save(court);
        } catch(Exception e) {
            Logging.handleException("AdminCourtService", "save(Court court, HttpServletRequest request)", 73, AdminConstant.ADMIN_COURT_SERVICE_SAVE_EXCEPTION, e.getMessage());
            logService.writeErrorLog(AdminConstant.ADMIN_COURT_SERVICE_SAVE_EXCEPTION, "AdminCourtService@save()", e.getMessage());
            return GlobalResponse.failed("Failed to save court data!", AdminConstant.ADMIN_COURT_SERVICE_SAVE_EXCEPTION, null, request);
        }

        return GlobalResponse.success("Successfully save court data!", null, request);
    }

    @Override
    public ResponseEntity<Object> update(Long id, Court court, HttpServletRequest request) {
        if( id == null ) {
            return GlobalResponse.failed("Court ID is required!", AdminConstant.ADMIN_COURT_SERVICE_UPDATE_ID_REQUIRED, null, request);
        }

        if( court == null ) {
            return GlobalResponse.failed("Court data is required!", AdminConstant.ADMIN_COURT_SERVICE_UPDATE_REQUEST_INVALID, null, request);
        }

        try {
            Optional<Court> optionalCourt = courtRepo.findById(id);
            if( optionalCourt.isEmpty() ) {
                return GlobalResponse.failed("Court data not found!", AdminConstant.ADMIN_COURT_SERVICE_UPDATE_NOT_FOUND, null, request);
            }

            Court courtDB = optionalCourt.get();
            courtDB.setCourtCode(court.getCourtCode());
            courtDB.setCourtName(court.getCourtName());
            courtDB.setOpenTime(court.getOpenTime());
            courtDB.setCloseTime(court.getCloseTime());
            courtDB.setPricePerHour(court.getPricePerHour());
            if( court.getCourtImgLink() != null ) courtDB.setCourtImgLink(court.getCourtImgLink());
            courtDB.setStatus(court.getStatus());
        } catch(Exception e) {
            Logging.handleException("AdminCourtService", "update(Long id, Court court, HttpServletRequest request)", 94, AdminConstant.ADMIN_COURT_SERVICE_UPDATE_EXCEPTION, e.getMessage());
            logService.writeErrorLog(AdminConstant.ADMIN_COURT_SERVICE_UPDATE_EXCEPTION, "AdminCourtService@update()", e.getMessage());
            return GlobalResponse.failed("Failed to update court data!", AdminConstant.ADMIN_COURT_SERVICE_UPDATE_EXCEPTION, null, request);
        }

        return GlobalResponse.success("Successfully updated court data!", null, request);
    }

    @Override
    public ResponseEntity<Object> delete(Long id, HttpServletRequest request) {
        if( id == null ) {
            return GlobalResponse.failed("Court ID is required!", AdminConstant.ADMIN_COURT_SERVICE_DELETE_ID_REQUIRED, null, request);
        }

        try {
            Optional<Court> optionalCourt = courtRepo.findById(id);
            if( optionalCourt.isEmpty() ) {
                return GlobalResponse.failed("Court data not found!", AdminConstant.ADMIN_COURT_SERVICE_DELETE_NOT_FOUND, null, request);
            }

            courtRepo.deleteById(id);
        } catch(Exception e) {
            Logging.handleException("AdminCourtService", "delete(Long id, HttpServletRequest request)", 120, AdminConstant.ADMIN_COURT_SERVICE_DELETE_EXCEPTION, e.getMessage());
            logService.writeErrorLog(AdminConstant.ADMIN_COURT_SERVICE_DELETE_EXCEPTION, "AdminCourtService@delete()", e.getMessage());
            return GlobalResponse.failed("Failed to delete court data!", AdminConstant.ADMIN_COURT_SERVICE_DELETE_EXCEPTION, null, request);
        }

        return GlobalResponse.success("Successfully deleted court data!", null, request);
    }

    @Override
    public ResponseEntity<Object> save(Court court, MultipartFile file, HttpServletRequest request) {
        if( court == null ) {
            return GlobalResponse.failed("Court data is required!", AdminConstant.ADMIN_COURT_SERVICE_SAVE_FILE_REQUEST_INVALID, null, request);
        }

        String courtImgLink = cloudinaryService.uploadImageGetUrl("court", file);
        if( courtImgLink == null || courtImgLink.isEmpty() ) {
            Logging.handleException("AdminCourtService", "save(Court court, MultipartFile file, HttpServletRequest request)", 175, AdminConstant.ADMIN_COURT_SERVICE_SAVE_FILE_IMAGE_ERROR, "Failed to upload image!");
            logService.writeErrorLog(AdminConstant.ADMIN_COURT_SERVICE_SAVE_FILE_IMAGE_ERROR, "AdminCourtService@update()", "Failed to upload image!");
            return GlobalResponse.failed("Failed to upload court image!", AdminConstant.ADMIN_COURT_SERVICE_SAVE_FILE_IMAGE_ERROR, null, request);
        }

        court.setCourtImgLink(courtImgLink);

        ResponseEntity<Object> response = save(court, request);

        return response;
    }

    @Override
    public ResponseEntity<Object> update(Long id, Court court, MultipartFile file, HttpServletRequest request) {
        if( id == null ) {
            return GlobalResponse.failed("Court ID is required!", AdminConstant.ADMIN_COURT_SERVICE_UPDATE_FILE_ID_REQUIRED, null, request);
        }

        if( court == null ) {
            return GlobalResponse.failed("Court data is required!", AdminConstant.ADMIN_COURT_SERVICE_UPDATE_FILE_REQUEST_INVALID, null, request);
        }

        if( file != null ) {
            String courtImgLink = cloudinaryService.uploadImageGetUrl("court", file);
            if( courtImgLink == null || courtImgLink.isEmpty() ) {
                Logging.handleException("AdminCourtService", "update(Long id, Court court, MultipartFile file, HttpServletRequest request)", 184, AdminConstant.ADMIN_COURT_SERVICE_UPDATE_FILE_IMAGE_ERROR, "Failed to upload image!");
                logService.writeErrorLog(AdminConstant.ADMIN_COURT_SERVICE_UPDATE_FILE_IMAGE_ERROR, "AdminCourtService@update()", "Failed to upload image!");
                return GlobalResponse.failed("Failed to upload court image!", AdminConstant.ADMIN_COURT_SERVICE_UPDATE_FILE_IMAGE_ERROR, null, request);
            }

            court.setCourtImgLink(courtImgLink);
        }

        ResponseEntity<Object> response = update(id, court, request);

        return response;
    }

    private RespAdminCourtListDTO mapListToDTO(Court court) {
        RespAdminCourtListDTO result = modelMapper.map(court, RespAdminCourtListDTO.class);
        if( court.getOpenTime() != null ) {
            result.setOpenTime(DatetimeFormatting.getClockFormat(court.getOpenTime()));
        }
        if( court.getCloseTime() != null ) {
            result.setCloseTime(DatetimeFormatting.getClockFormat(court.getCloseTime()));
        }
        if( court.getCreatedAt() != null ) {
            result.setCreatedAt(DatetimeFormatting.getDatetimeFormat(court.getCreatedAt()));
        }
        if( court.getUpdatedAt() != null ) {
            result.setUpdatedAt(DatetimeFormatting.getDatetimeFormat(court.getUpdatedAt()));
        }

        return result;
    }
}
