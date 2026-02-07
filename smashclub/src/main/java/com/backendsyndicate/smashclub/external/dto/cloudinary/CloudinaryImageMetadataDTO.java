package com.backendsyndicate.smashclub.external.dto.cloudinary;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class CloudinaryImageMetadataDTO {
    @JsonProperty("JFIFVersion")
    private String jfifVersion;
    @JsonProperty("ResolutionUnit")
    private String resolutionUnit;
    @JsonProperty("XResolution")
    private String xResolution;
    @JsonProperty("YResolution")
    private String yResolution;
    @JsonProperty("Colorspace")
    private String colorSpace;
    @JsonProperty("DPI")
    private String dpi;
}
