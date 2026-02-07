package com.backendsyndicate.smashclub.external.dto;

import com.backendsyndicate.smashclub.external.dto.cloudinary.CloudinaryImageMetadataDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Getter
@Setter
public class CloudinaryResponseDTO {
    @JsonProperty("asset_id")
    private String assetId;
    @JsonProperty("public_id")
    private String publicId;
    private long version;
    @JsonProperty("version_id")
    private String versionId;
    private String signature;
    private int width;
    private int height;
    private String format;
    @JsonProperty("resource_type")
    private String resourceType;
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    private List<Object> tags;
    private int pages;
    private long bytes;
    private String type;
    private String etag;
    private boolean placeholder;
    private String url;
    @JsonProperty("secure_url")
    private String secureUrl;
    @JsonProperty("asset_folder")
    private String assetFolder;
    @JsonProperty("display_name")
    private String displayName;
    @JsonProperty("image_metadata")
    private CloudinaryImageMetadataDTO imageMetadata;
    @JsonProperty("illustration_score")
    private double illustrationScore;
    @JsonProperty("semi_transparent")
    private boolean semiTransparent;
    private boolean grayscale;
    @JsonProperty("original_filename")
    private String originalFilename;
//    private List<CloudinaryEagerDTO> eager;
    @JsonProperty("api_key")
    private String apiKey;
}
