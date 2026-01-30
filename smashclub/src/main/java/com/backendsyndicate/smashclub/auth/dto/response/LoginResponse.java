package com.backendsyndicate.smashclub.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    @JsonProperty("session_token")
    private String sessionToken;

    @JsonProperty("user_id")
    private String userId;

    private String email;

    @JsonProperty("full_name")
    private String fullName;
}