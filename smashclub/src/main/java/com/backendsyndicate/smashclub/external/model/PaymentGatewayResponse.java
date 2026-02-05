package com.backendsyndicate.smashclub.external.model;

import com.backendsyndicate.smashclub.common.util.Logging;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Data
@Getter
@Setter
public class PaymentGatewayResponse {
    private String externalId;

    // Invoice
    private String invoiceUrl;

    // Closed VA
    private String ownerId;
    private String accountNumber;
    private String bankCode;
    private String name;
    private Boolean isClosed;
    private Date expirationDate;
    private Boolean isSingleUse;
    private String status;
//    private Long expectedAmount;
//    private Long suggestedAmount;

    // EWallet
    private String referenceId;
//    private String chargeAmount;
//    private String captureAmount;
//    private String channelCode;
    private Map<String, String> channelProperties;
    private Map<String, String> actions;
    private Boolean isRedirectRequired;
    private String callbackUrl;
    private String created;
    private String updated;
    private String voidedAt;
    private String voidStatus;
    private Boolean captureNow;
    private String customerId;
    private String paymentMethodId;
    private String failureCode;

    // QRIS
//    private String currency;
//    private Number amount;
    private String qrString;
    private String webhookUrl;
    private String expiresAt;

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        Field[] fields = this.getClass().getDeclaredFields();

        for( Field field: fields ) {
            boolean accessible = field.isAccessible();

            try {
                field.setAccessible(true);

                String fieldName = field.getName();
                Object value = field.get(this);

                if( value != null ) {
                    map.put(fieldName, value);
                }

            } catch(IllegalAccessException ex) {
                Logging.printConsole(ex.getMessage());
            } finally {
                field.setAccessible(accessible);
            }
        }

        return map;
    }
}
