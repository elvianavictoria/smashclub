package com.backendsyndicate.smashclub.external.dto.cloudinary;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class CloudinaryEagerDTO {
    private String transformation;
    private String width;
    private String height;
    private String format;
    private String url;
    private String secureUrl;
}
