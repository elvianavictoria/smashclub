package com.backendsyndicate.smashclub.admin.service.master;

import com.backendsyndicate.smashclub.admin.core.ICRUD;
import com.backendsyndicate.smashclub.admin.dto.response.RespAdminCoachListDTO;
import com.backendsyndicate.smashclub.admin.dto.response.RespAdminCourtListDTO;
import com.backendsyndicate.smashclub.booking.model.Coach;
import com.backendsyndicate.smashclub.booking.model.Court;
import com.backendsyndicate.smashclub.booking.repo.CoachRepo;
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
public class AdminCoachService implements ICRUD<Coach, Long> {
    @Autowired
    private CoachRepo coachRepo;
    private ModelMapper modelMapper = new ModelMapper();

    private String generateErrorCode(String methodNo, String errorNo) {
        return "ADM-CCH" + "-" + methodNo + "-" + errorNo;
    }

    @Override
    public ResponseEntity<Object> findAll(String keyword, Pageable pageable, HttpServletRequest request) {
        Page page = null;

        try {
            if( !keyword.isEmpty() ) {
                page = coachRepo.findAllByCoachCodeContainsOrCoachNameContainsIgnoreCase(keyword, keyword, pageable);
            } else {
                page = coachRepo.findAll(pageable);
            }
            if( page.isEmpty() ) {
                return GlobalResponse.failed("Coach list is empty!", generateErrorCode("01", "001"), null, request);
            }

            page = page.map(new Function<Coach, RespAdminCoachListDTO>() {
                @Override
                public RespAdminCoachListDTO apply(Coach coach) {
                    return mapListToDTO(coach);
                }
            });
        } catch(Exception e) {
            Logging.handleException("CoachService", "findAll(Pageable pageable, HttpServletRequest request)", 35, generateErrorCode("01", "010"), e.getMessage());
            return GlobalResponse.failed("Failed to get coach list!", generateErrorCode("01", "010"), null, request);
        }

        return GlobalResponse.success("Successfully get coach list!", page, request);
    }

    @Override
    public ResponseEntity<Object> findById(Long id, HttpServletRequest request) {
        Coach coach = null;

        if( id == null ) {
            return GlobalResponse.failed("Coach ID is required!", generateErrorCode("02", "001"), null, request);
        }

        try {
            Optional<Coach> optionalCoach = coachRepo.findById(id);
            if( optionalCoach.isEmpty() ) {
                return GlobalResponse.failed("Coach not found!", generateErrorCode("02", "002"), null, request);
            }

            coach = optionalCoach.get();
        } catch(Exception e) {
            return GlobalResponse.failed("Failed to get coach data!", generateErrorCode("02", "010"), null, request);
        }

        return GlobalResponse.success("Coach data found!", coach, request);
    }

    @Override
    public ResponseEntity<Object> save(Coach coach, HttpServletRequest request) {
        if( coach == null ) {
            return GlobalResponse.failed("Coach data is required!", generateErrorCode("03", "001"), null, request);
        }

        try {
            coachRepo.save(coach);
        } catch(Exception e) {
            Logging.handleException("CoachService", "save(Coach coach, HttpServletRequest request)", 75, generateErrorCode("03", "010"), e.getMessage());
            return GlobalResponse.failed("Failed to save coach data!", generateErrorCode("03", "010"), null, request);
        }

        return GlobalResponse.success("Successfully save coach data!", null, request);
    }

    @Override
    public ResponseEntity<Object> update(Long id, Coach coach, HttpServletRequest request) {
        if( id == null ) {
            return GlobalResponse.failed("Coach ID is required!", generateErrorCode("04", "001"), null, request);
        }

        if( coach == null ) {
            return GlobalResponse.failed("Coach data is required!", generateErrorCode("04", "002"), null, request);
        }

        try {
            Optional<Coach> optionalCoach = coachRepo.findById(id);
            if( optionalCoach.isEmpty() ) {
                return GlobalResponse.failed("Coach data not found!", generateErrorCode("04", "003"), null, request);
            }

            Coach coachDB = optionalCoach.get();
            coachDB.setCoachCode(coach.getCoachCode());
            coachDB.setCoachName(coach.getCoachName());
            coachDB.setStatus(coach.getStatus());
            coachDB.setPricePerHour(coach.getPricePerHour());
        } catch(Exception e) {
            Logging.handleException("CoachService", "update(Long id, Coach coach, HttpServletRequest request)", 94, generateErrorCode("04", "010"), e.getMessage());
            return GlobalResponse.failed("Failed to update coach data!", generateErrorCode("04", "010"), null, request);
        }

        return GlobalResponse.success("Successfully updated coach data!", null, request);
    }

    @Override
    public ResponseEntity<Object> delete(Long id, HttpServletRequest request) {
        if( id == null ) {
            return GlobalResponse.failed("Coach ID is required!", generateErrorCode("05", "001"), null, request);
        }

        try {
            Optional<Coach> optionalCoach = coachRepo.findById(id);
            if( optionalCoach.isEmpty() ) {
                return GlobalResponse.failed("Coach data not found!", generateErrorCode("05", "002"), null, request);
            }

            coachRepo.deleteById(id);
        } catch(Exception e) {
            Logging.handleException("CoachService", "delete(Long id, HttpServletRequest request)", 122, generateErrorCode("05", "010"), e.getMessage());
            return GlobalResponse.failed("Failed to delete coach data!", generateErrorCode("05", "010"), null, request);
        }

        return GlobalResponse.success("Successfully deleted coach data!", null, request);
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
