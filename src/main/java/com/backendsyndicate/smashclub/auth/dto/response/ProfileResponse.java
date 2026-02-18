package com.backendsyndicate.smashclub.auth.dto.response;

import com.backendsyndicate.smashclub.auth.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponse {

    private String userId;
    private String fullName;
    private String email;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String profilePicture;

    public static ProfileResponse fromUser(User user) {
        return ProfileResponse.builder()
                .userId(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .status(String.valueOf(user.getStatus()))
                .createdAt(user.getCreatedDate())
                .updatedAt(user.getUpdatedDate())
                .build();
    }
}