package com.backendsyndicate.smashclub.admin.controller.report;

import com.backendsyndicate.smashclub.admin.dto.request.ReqAdminPlayerSaveDTO;
import com.backendsyndicate.smashclub.admin.service.report.PlayerService;
import com.backendsyndicate.smashclub.auth.model.User;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/admin/player")
public class PlayerController {
    @Autowired
    private PlayerService playerService;
    private ModelMapper modelMapper = new ModelMapper();

    @GetMapping
    public ResponseEntity<Object> playerList(@RequestParam String keyword, @RequestParam int page, @RequestParam int size, HttpServletRequest request) {
        Pageable pageable = PageRequest.of(page, size);
        return playerService.findAll(keyword, pageable, request);
    }

    @GetMapping("{playerId}")
    public ResponseEntity<Object> playerDetail(@PathVariable String playerId, HttpServletRequest request) {
        return playerService.findById(playerId, request);
    }

    @PutMapping("update/{playerId}")
    public ResponseEntity<Object> playerUpdate(@PathVariable String playerId, @RequestBody ReqAdminPlayerSaveDTO dto, HttpServletRequest request) {
        User player = modelMapper.map(dto, User.class);
        return playerService.update(playerId, player, request);
    }

    @DeleteMapping("delete/{playerId}")
    public ResponseEntity<Object> playerDelete(@PathVariable String playerId, HttpServletRequest request) {
        return playerService.delete(playerId, request);
    }
}
