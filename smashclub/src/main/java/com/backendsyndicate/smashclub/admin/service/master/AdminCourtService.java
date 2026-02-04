package com.backendsyndicate.smashclub.admin.service.master;

import com.backendsyndicate.smashclub.admin.core.ICRUD;
import com.backendsyndicate.smashclub.booking.model.Court;
import com.backendsyndicate.smashclub.booking.repo.CourtRepo;
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
public class AdminCourtService implements ICRUD<Court, Long> {
    @Autowired
    private CourtRepo courtRepo;
    private ModelMapper modelMapper = new ModelMapper();

    private String generateErrorCode(String methodNo, String errorNo) {
        return "ADM-CRT" + "-" + methodNo + "-" + errorNo;
    }

    @Override
    public ResponseEntity<Object> findAll(String keyword, Pageable pageable, HttpServletRequest request) {
        Page page = null;

        try {
            page = courtRepo.findAll(pageable);
            if( page.isEmpty() ) {
                return GlobalResponse.failed("Court list is empty!", generateErrorCode("01", "001"), null, request);
            }
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
}
