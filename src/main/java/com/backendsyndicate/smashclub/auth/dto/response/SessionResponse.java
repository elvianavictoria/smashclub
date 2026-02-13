package com.backendsyndicate.smashclub.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SessionResponse {

    // ✅ WAJIB: Untuk basic functionality
    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("token_valid")
    private Boolean tokenValid;

    @JsonProperty("expires_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime expiresAt;

    @JsonProperty("remaining_minutes")
    private Long remainingMinutes;  // ⭐️ SANGAT PENTING!

    // ✅ RECOMMENDED: Untuk better UX
    @JsonProperty("requires_renewal")
    private Boolean requiresRenewal;

    @JsonProperty("last_accessed_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastAccessedAt;

    @JsonProperty("device_info")
    private String deviceInfo;

    // ✅ OPTIONAL: Jika butuh lebih detail
    private String email;

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("ip_address")
    private String ipAddress;
}