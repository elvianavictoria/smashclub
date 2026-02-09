package com.backendsyndicate.smashclub.external.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Getter
@Setter
public class CloudinaryResponseDTO {
    private String assetFolder;
    private String signature;
    private String format;
    private String resourceType;
    private String secureUrl;
    private String createdAt;
    private String assetId;
    private String versionId;
    private String type;
    private String displayName;
    private long version;
    private String url;
    private String publicId;
    private List<Object> tags;
    private String originalFilename;
    private String apiKey;
    private long bytes;
    private boolean overwritten;
    private int width;
    private int height;
    private String etag;
    private boolean placeholder;

    public void mapToDTO(Map map) {
        this.assetFolder = (String) map.get("asset_folder");
        this.signature = (String) map.get("signature");
        this.format = (String) map.get("format");
        this.resourceType = (String) map.get("resource_type");
        this.secureUrl = (String) map.get("secure_url");
        this.createdAt = (String) map.get("created_at");
        this.assetId = (String) map.get("asset_id");
        this.versionId = (String) map.get("version_id");
        this.type = (String) map.get("type");
        this.displayName = (String) map.get("display_name");
        this.version = (int) map.get("version");
        this.url = (String) map.get("url");
        this.publicId = (String) map.get("public_id");
        this.tags = (List<Object>) map.get("tags");
        this.originalFilename = (String) map.get("original_filename");
        this.apiKey = (String) map.get("api_key");
        this.bytes = (int) map.get("bytes");
        this.overwritten = (boolean) map.get("overwritten");
        this.width = (int) map.get("width");
        this.height = (int) map.get("height");
        this.etag = (String) map.get("etag");
        this.placeholder = (boolean) map.get("placeholder");
    }
}
