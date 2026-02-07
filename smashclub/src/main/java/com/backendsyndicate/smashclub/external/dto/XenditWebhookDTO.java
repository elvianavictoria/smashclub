package com.backendsyndicate.smashclub.external.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
public class XenditWebhookDTO {
    private String uuid;
    private String title;
    private String amount;
    private String status;
    @JsonProperty("paid_at")
    private LocalDateTime paidAt;
    @JsonProperty("settle_at")
    private LocalDateTime settleAt;
    @JsonProperty("channel_id")
    private String channelId;
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    @JsonProperty("expires_at")
    private LocalDateTime expiresAt;
    @JsonProperty("return_url")
    private String returnUrl;
    @JsonProperty("description")
    private String description;
    @JsonProperty("external_id")
    private String externalId;
    @JsonProperty("payment_link_url")
    private String paymentLinkUrl;
}
