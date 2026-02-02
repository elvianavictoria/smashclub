package com.backendsyndicate.smashclub.admin.dto.request;

import jakarta.persistence.Column;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Getter
@Setter
public class ReqCourtSaveDTO {
    private Long id;
    private String courtCode;
    private String courtName;
    private LocalTime openTime;
    private LocalTime closeTime;
    private byte status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
