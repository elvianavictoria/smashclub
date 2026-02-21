package com.backendsyndicate.smashclub.admin.service.report;

import com.backendsyndicate.smashclub.admin.core.IRUD;
import com.backendsyndicate.smashclub.admin.dto.response.RespAdminPlayerListDTO;
import com.backendsyndicate.smashclub.admin.service.log.LogService;
import com.backendsyndicate.smashclub.auth.model.User;
import com.backendsyndicate.smashclub.auth.repository.UserRepository;
import com.backendsyndicate.smashclub.common.constant.AdminConstant;
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
public class AdminPlayerService implements IRUD<User, String> {
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private LogService logService;

    private ModelMapper modelMapper = new ModelMapper();
    
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
                return GlobalResponse.failed("Player list is empty!", AdminConstant.ADMIN_PLAYER_SERVICE_LIST_EMPTY, null, request);
            }

            page = page.map(new Function<User, RespAdminPlayerListDTO>() {
                @Override
                public RespAdminPlayerListDTO apply(User player) {
                    return mapListToDTO(player);
                }
            });
        } catch(Exception e) {
            Logging.handleException("AdminPlayerService", "findAll(String keyword, Pageable pageable, HttpServletRequest request)", 39, AdminConstant.ADMIN_PLAYER_SERVICE_LIST_EXCEPTION, e.getMessage());
            logService.writeErrorLog(AdminConstant.ADMIN_PLAYER_SERVICE_LIST_EXCEPTION, "AdminPlayerService@findAll()", e.getMessage());
            return GlobalResponse.failed("Failed to get player list!", AdminConstant.ADMIN_PLAYER_SERVICE_LIST_EXCEPTION, null, request);
        }

        return GlobalResponse.success("Successfully get player list!", page, request);
    }

    @Override
    public ResponseEntity<Object> findById(String id, HttpServletRequest request) {
        User user = null;

        if( id == null ) {
            return GlobalResponse.failed("Player ID is required!", AdminConstant.ADMIN_PLAYER_SERVICE_DETAIL_ID_REQUIRED, null, request);
        }

        try {
            Optional<User> optionalUser = userRepo.findById(id);
            if( optionalUser.isEmpty() ) {
                return GlobalResponse.failed("Player not found!", AdminConstant.ADMIN_PLAYER_SERVICE_DETAIL_NOT_FOUND, null, request);
            }

            user = optionalUser.get();
        } catch(Exception e) {
            Logging.handleException("AdminPlayerService", "findById(String id, HttpServletRequest request)", 72, AdminConstant.ADMIN_PLAYER_SERVICE_DETAIL_EXCEPTION, e.getMessage());
            logService.writeErrorLog(AdminConstant.ADMIN_PLAYER_SERVICE_DETAIL_EXCEPTION, "AdminPlayerService@findById()", e.getMessage());
            return GlobalResponse.failed("Failed to get player data!", AdminConstant.ADMIN_PLAYER_SERVICE_DETAIL_EXCEPTION, null, request);
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
            return GlobalResponse.failed("Player ID is required!", AdminConstant.ADMIN_PLAYER_SERVICE_DELETE_ID_REQUIRED, null, request);
        }

        try {
            Optional<User> optionalPlayer = userRepo.findById(id);
            if( optionalPlayer.isEmpty() ) {
                return GlobalResponse.failed("Player data not found!", AdminConstant.ADMIN_PLAYER_SERVICE_DELETE_NOT_FOUND, null, request);
            }

            userRepo.deleteById(id);
        } catch(Exception e) {
            Logging.handleException("AdminPlayerService", "delete(String id, HttpServletRequest request)", 98, AdminConstant.ADMIN_PLAYER_SERVICE_DELETE_EXCEPTION, e.getMessage());
            logService.writeErrorLog(AdminConstant.ADMIN_PLAYER_SERVICE_DELETE_EXCEPTION, "AdminPlayerService@delete()", e.getMessage());
            return GlobalResponse.failed("Failed to delete player data!", AdminConstant.ADMIN_PLAYER_SERVICE_DELETE_EXCEPTION, null, request);
        }

        return GlobalResponse.success("Successfully deleted player data!", null, request);
    }

    private RespAdminPlayerListDTO mapListToDTO(User player) {
        RespAdminPlayerListDTO result = modelMapper.map(player, RespAdminPlayerListDTO.class);
        if( player.getCreatedDate() != null ) {
            result.setCreatedAt(DatetimeFormatting.getDatetimeFormat(player.getCreatedDate()));
        }
        if( player.getLockedUntil() != null ) {
            result.setLockedUntil(DatetimeFormatting.getDatetimeFormat(player.getLockedUntil()));
        }

        if( player.getUpdatedDate() != null ) {
            result.setUpdatedAt(DatetimeFormatting.getDatetimeFormat(player.getUpdatedDate()));
        }

        return result;
    }
}
