package com.backendsyndicate.smashclub.admin.service.report;

import com.backendsyndicate.smashclub.admin.core.IRUD;
import com.backendsyndicate.smashclub.admin.dto.response.RespAdminPlayerListDTO;
import com.backendsyndicate.smashclub.auth.model.User;
import com.backendsyndicate.smashclub.auth.repository.UserRepository;
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
public class PlayerService implements IRUD<User, String> {
    @Autowired
    private UserRepository userRepo;
    private ModelMapper modelMapper = new ModelMapper();

    private String generateErrorCode(String methodNo, String errorNo) {
        return "ADM-PLYR" + "-" + methodNo + "-" + errorNo;
    }
    
    @Override
    public ResponseEntity<Object> findAll(String keyword, Pageable pageable, HttpServletRequest request) {
        Page page = null;

        try {
            if( !keyword.isEmpty() ) {
                page = userRepo.findAllByFullNameContainsOrEmailContains(keyword, keyword, pageable);
            } else {
                page = userRepo.findAll(pageable);
            }

            if( page.isEmpty() ) {
                return GlobalResponse.failed("Player list is empty!", generateErrorCode("01", "001"), null, request);
            }

            page = page.map(new Function<User, RespAdminPlayerListDTO>() {
                @Override
                public RespAdminPlayerListDTO apply(User player) {
                    return mapListToDTO(player);
                }
            });
        } catch(Exception e) {
            Logging.handleException("PlayerService", "findAll(String keyword, Pageable pageable, HttpServletRequest request)", 39, generateErrorCode("01", "010"), e.getMessage());
            return GlobalResponse.failed("Failed to get player list!", generateErrorCode("01", "010"), null, request);
        }

        return GlobalResponse.success("Successfully get player list!", page, request);
    }

    @Override
    public ResponseEntity<Object> findById(String id, HttpServletRequest request) {
        User user = null;

        if( id == null ) {
            return GlobalResponse.failed("Player ID is required!", generateErrorCode("02", "001"), null, request);
        }

        try {
            Optional<User> optionalUser = userRepo.findById(id);
            if( optionalUser.isEmpty() ) {
                return GlobalResponse.failed("Player not found!", generateErrorCode("02", "002"), null, request);
            }

            user = optionalUser.get();
        } catch(Exception e) {
            Logging.handleException("PlayerService", "findById(String id, HttpServletRequest request)", 72, generateErrorCode("02", "010"), e.getMessage());
            return GlobalResponse.failed("Failed to get player data!", generateErrorCode("02", "010"), null, request);
        }

        return GlobalResponse.success("Player data found!", user, request);
    }

    @Override
    public ResponseEntity<Object> update(String id, User user, HttpServletRequest request) {
        return null; // Need discussion on what can be updated in this data
    }

    @Override
    public ResponseEntity<Object> delete(String id, HttpServletRequest request) {
        if( id == null ) {
            return GlobalResponse.failed("Player ID is required!", generateErrorCode("04", "001"), null, request);
        }

        try {
            Optional<User> optionalPlayer = userRepo.findById(id);
            if( optionalPlayer.isEmpty() ) {
                return GlobalResponse.failed("Player data not found!", generateErrorCode("04", "002"), null, request);
            }

            userRepo.deleteById(id);
        } catch(Exception e) {
            Logging.handleException("PlayerService", "delete(String id, HttpServletRequest request)", 98, generateErrorCode("04", "010"), e.getMessage());
            return GlobalResponse.failed("Failed to delete player data!", generateErrorCode("04", "010"), null, request);
        }

        return GlobalResponse.success("Successfully deleted player data!", null, request);
    }

    private RespAdminPlayerListDTO mapListToDTO(User player) {
        RespAdminPlayerListDTO result = modelMapper.map(player, RespAdminPlayerListDTO.class);
        if( player.getCreatedDate() != null ) {
            result.setCreatedAt(DatetimeFormatting.getDatetimeFormat(player.getCreatedDate()));
        }
        if( player.getUpdatedDate() != null ) {
            result.setLockedUntil(DatetimeFormatting.getDatetimeFormat(player.getLockedUntil()));
        }

        return result;
    }
}
