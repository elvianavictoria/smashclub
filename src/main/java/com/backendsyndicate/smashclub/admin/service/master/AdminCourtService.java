package com.backendsyndicate.smashclub.admin.service.master;

import com.backendsyndicate.smashclub.admin.core.ICRUD;
import com.backendsyndicate.smashclub.admin.core.IUpload;
import com.backendsyndicate.smashclub.admin.dto.response.RespAdminCourtListDTO;
import com.backendsyndicate.smashclub.booking.model.Court;
import com.backendsyndicate.smashclub.booking.repo.CourtRepo;
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
    private ModelMapper modelMapper = new ModelMapper();

    private String generateErrorCode(String methodNo, String errorNo) {
        return "ADM-CRT" + "-" + methodNo + "-" + errorNo;
    }

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
                return GlobalResponse.failed("Court list is empty!", generateErrorCode("01", "001"), null, request);
            }

            page = page.map(new Function<Court, RespAdminCourtListDTO>() {
                @Override
                public RespAdminCourtListDTO apply(Court court) {
                    return mapListToDTO(court);
                }
            });
        } catch(Exception e) {
            Logging.handleException("CourtService", "findAll(Pageable pageable, HttpServletRequest request)", 33, generateErrorCode("01", "010"), e.getMessage());
            return GlobalResponse.failed("Failed to get court list!", generateErrorCode("01", "010"), null, request);
        }

        return GlobalResponse.success("Successfully get court list!", page, request);
    }

    @Override
    public ResponseEntity<Object> findById(Long id, HttpServletRequest request) {
        Court court = null;

        if( id == null ) {
            return GlobalResponse.failed("Court ID is required!", generateErrorCode("02", "001"), null, request);
        }

        try {
            Optional<Court> optionalCourt = courtRepo.findById(id);
            if( optionalCourt.isEmpty() ) {
                return GlobalResponse.failed("Court not found!", generateErrorCode("02", "002"), null, request);
            }

            court = optionalCourt.get();
        } catch(Exception e) {
            return GlobalResponse.failed("Failed to get court data!", generateErrorCode("02", "010"), null, request);
        }

        return GlobalResponse.success("Court data found!", court, request);
    }

    @Override
    public ResponseEntity<Object> save(Court court, HttpServletRequest request) {
        if( court == null ) {
            return GlobalResponse.failed("Court data is required!", generateErrorCode("03", "001"), null, request);
        }

        try {
            courtRepo.save(court);
        } catch(Exception e) {
            Logging.handleException("CourtService", "save(Court court, HttpServletRequest request)", 73, generateErrorCode("03", "010"), e.getMessage());
            return GlobalResponse.failed("Failed to save court data!", generateErrorCode("03", "010"), null, request);
        }

        return GlobalResponse.success("Successfully save court data!", null, request);
    }

    @Override
    public ResponseEntity<Object> update(Long id, Court court, HttpServletRequest request) {
        if( id == null ) {
            return GlobalResponse.failed("Court ID is required!", generateErrorCode("04", "001"), null, request);
        }

        if( court == null ) {
            return GlobalResponse.failed("Court data is required!", generateErrorCode("04", "002"), null, request);
        }

        try {
            Optional<Court> optionalCourt = courtRepo.findById(id);
            if( optionalCourt.isEmpty() ) {
                return GlobalResponse.failed("Court data not found!", generateErrorCode("04", "003"), null, request);
            }

            Court courtDB = optionalCourt.get();
            courtDB.setCourtCode(court.getCourtCode());
            courtDB.setCourtName(court.getCourtName());
            courtDB.setOpenTime(court.getOpenTime());
            courtDB.setCloseTime(court.getCloseTime());
//            if( court.getCourtImgLink() != null ) courtDB.setCourtImgLink(court.getCourtImgLink());
            courtDB.setStatus(court.getStatus());
        } catch(Exception e) {
            Logging.handleException("CourtService", "update(Long id, Court court, HttpServletRequest request)", 94, generateErrorCode("04", "010"), e.getMessage());
            return GlobalResponse.failed("Failed to update court data!", generateErrorCode("04", "010"), null, request);
        }

        return GlobalResponse.success("Successfully updated court data!", null, request);
    }

    @Override
    public ResponseEntity<Object> delete(Long id, HttpServletRequest request) {
        if( id == null ) {
            return GlobalResponse.failed("Court ID is required!", generateErrorCode("05", "001"), null, request);
        }

        try {
            Optional<Court> optionalCourt = courtRepo.findById(id);
            if( optionalCourt.isEmpty() ) {
                return GlobalResponse.failed("Court data not found!", generateErrorCode("05", "002"), null, request);
            }

            courtRepo.deleteById(id);
        } catch(Exception e) {
            Logging.handleException("CourtService", "delete(Long id, HttpServletRequest request)", 120, generateErrorCode("05", "010"), e.getMessage());
            return GlobalResponse.failed("Failed to delete court data!", generateErrorCode("05", "010"), null, request);
        }

        return GlobalResponse.success("Successfully deleted court data!", null, request);
    }

    @Override
    public ResponseEntity<Object> save(Court court, MultipartFile file, HttpServletRequest request) {
        if( court == null ) {
            return GlobalResponse.failed("Court data is required!", generateErrorCode("13", "001"), null, request);
        }

        String courtImgLink = cloudinaryService.uploadImageGetUrl("court", file);
        if( courtImgLink == null || courtImgLink.isEmpty() ) {
            return GlobalResponse.failed("Failed to upload court image!", generateErrorCode("13", "002"), null, request);
        }

//            court.setCourtImgLink(courtImgLink);

        ResponseEntity<Object> response = save(court, request);

        return response;
    }

    @Override
    public ResponseEntity<Object> update(Long id, Court court, MultipartFile file, HttpServletRequest request) {
        if( id == null ) {
            return GlobalResponse.failed("Court ID is required!", generateErrorCode("14", "001"), null, request);
        }

        if( court == null ) {
            return GlobalResponse.failed("Court data is required!", generateErrorCode("14", "002"), null, request);
        }

        if( file != null ) {
            String courtImgLink = cloudinaryService.uploadImageGetUrl("court", file);
            if( courtImgLink == null || courtImgLink.isEmpty() ) {
                return GlobalResponse.failed("Failed to upload court image!", generateErrorCode("14", "003"), null, request);
            }

//            court.setCourtImgLink(courtImgLink);
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
