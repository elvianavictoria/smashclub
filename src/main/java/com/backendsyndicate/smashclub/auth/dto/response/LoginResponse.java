package com.backendsyndicate.smashclub.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponse {
    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("token_type")
    private String tokenType;  // Usually "Bearer"

    @JsonProperty("expires_in")
    private Long expiresIn;    // In seconds

    @JsonProperty("user_id")
    private String userId;

    private String email;

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("scope")
    private String scope;      // "read write admin" etc

    @JsonProperty("created_at")
    private Long createdAt;    // Unix timestamp

    @JsonProperty("refresh_token_expires_in")
    private Long refreshTokenExpiresIn;
}