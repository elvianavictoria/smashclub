package com.backendsyndicate.smashclub.auth.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ProfilePictureRequest {

    @NotNull(message = "File foto harus diisi")
    private MultipartFile profilePicture;
}