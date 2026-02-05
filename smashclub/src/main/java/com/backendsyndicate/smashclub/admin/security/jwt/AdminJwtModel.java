package com.backendsyndicate.smashclub.admin.security.jwt;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Data
@Getter
@Setter
public class AdminJwtModel {
    private String username;

    public Map<String, Object> convertToMap() {
        Map<String, Object> data = new HashMap<>();
        data.put("username", username);

        return data;
    }
}
