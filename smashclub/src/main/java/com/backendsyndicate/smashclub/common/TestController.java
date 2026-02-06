package com.backendsyndicate.smashclub.common;

import com.backendsyndicate.smashclub.common.util.GlobalResponse;
import com.backendsyndicate.smashclub.common.util.Logging;
import com.backendsyndicate.smashclub.external.dto.CloudinaryResponseDTO;
import com.backendsyndicate.smashclub.external.service.storage.CloudinaryService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Profile("dev")
@RequestMapping("api/v1/test")
public class TestController {
    @Autowired
    private CloudinaryService cloudinaryService;

    @PostMapping
    public ResponseEntity<Object> testUpload(@RequestBody MultipartFile file, HttpServletRequest request) {
        try {
            Logging.printConsole(file.getContentType());
            Logging.printConsole(file.getOriginalFilename());
            Logging.printConsole(file.getName());
            Logging.printConsole(file.getSize() + "");

            CloudinaryResponseDTO dto = cloudinaryService.uploadImage("test", file);
            if( dto == null ) return GlobalResponse.failed("Failed to upload image!", "CMNTESTE001", dto, request);
        } catch(Exception e) {
            Logging.printConsole(e.getMessage());
        }

        return GlobalResponse.success("Success!", null, request);
    }
}
