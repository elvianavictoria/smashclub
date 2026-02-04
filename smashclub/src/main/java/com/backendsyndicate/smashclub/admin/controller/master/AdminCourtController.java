package com.backendsyndicate.smashclub.admin.controller.master;

import com.backendsyndicate.smashclub.admin.dto.request.ReqAdminCourtSaveDTO;
import com.backendsyndicate.smashclub.admin.service.master.AdminCourtService;
import com.backendsyndicate.smashclub.booking.model.Court;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/admin/court")
public class AdminCourtController {
    @Autowired
    private AdminCourtService adminCourtService;
    private ModelMapper modelMapper = new ModelMapper();

    @GetMapping
    public ResponseEntity<Object> courtList(@RequestParam String keyword, @RequestParam int page, @RequestParam int size, HttpServletRequest request) {
        Pageable pageable = PageRequest.of(page, size);
        return adminCourtService.findAll(keyword, pageable, request);
    }

    @GetMapping("{courtId}")
    public ResponseEntity<Object> courtDetail(@PathVariable Long courtId, HttpServletRequest request) {
        return adminCourtService.findById(courtId, request);
    }

    @PostMapping("save")
    public ResponseEntity<Object> courtSave(@RequestBody ReqAdminCourtSaveDTO dto, HttpServletRequest request) {
        Court court = modelMapper.map(dto, Court.class);
        return adminCourtService.save(court, request);
    }

    @PutMapping("update/{courtId}")
    public ResponseEntity<Object> courtUpdate(@PathVariable Long courtId, @RequestBody ReqAdminCourtSaveDTO dto, HttpServletRequest request) {
        Court court = modelMapper.map(dto, Court.class);
        return adminCourtService.update(courtId, court, request);
    }

    @DeleteMapping("delete/{courtId}")
    public ResponseEntity<Object> courtDelete(@PathVariable Long courtId, HttpServletRequest request) {
        return adminCourtService.delete(courtId, request);
    }
}
