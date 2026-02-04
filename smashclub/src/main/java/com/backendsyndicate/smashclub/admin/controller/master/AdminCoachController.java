package com.backendsyndicate.smashclub.admin.controller.master;

import com.backendsyndicate.smashclub.admin.dto.request.ReqCoachSaveDTO;
import com.backendsyndicate.smashclub.admin.service.master.AdminCoachService;
import com.backendsyndicate.smashclub.booking.model.Coach;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("admin/coach")
public class AdminCoachController {
    @Autowired
    private AdminCoachService adminCoachService;
    private ModelMapper modelMapper = new ModelMapper();

    @GetMapping
    public ResponseEntity<Object> coachList(@RequestParam String keyword, @RequestParam int page, @RequestParam int size, HttpServletRequest request) {
        Pageable pageable = PageRequest.of(page, size);
        return adminCoachService.findAll(keyword, pageable, request);
    }

    @GetMapping("{coachId}")
    public ResponseEntity<Object> coachDetail(@PathVariable Long coachId, HttpServletRequest request) {
        return adminCoachService.findById(coachId, request);
    }

    @PostMapping("save")
    public ResponseEntity<Object> coachSave(@RequestBody ReqCoachSaveDTO dto, HttpServletRequest request) {
        Coach coach = modelMapper.map(dto, Coach.class);
        return adminCoachService.save(coach, request);
    }

    @PutMapping("update/{coachId}")
    public ResponseEntity<Object> coachUpdate(@PathVariable Long coachId, @RequestBody ReqCoachSaveDTO dto, HttpServletRequest request) {
        Coach coach = modelMapper.map(dto, Coach.class);
        return adminCoachService.update(coachId, coach, request);
    }

    @DeleteMapping("delete/{coachId}")
    public ResponseEntity<Object> coachDelete(@PathVariable Long coachId, HttpServletRequest request) {
        return adminCoachService.delete(coachId, request);
    }
}
