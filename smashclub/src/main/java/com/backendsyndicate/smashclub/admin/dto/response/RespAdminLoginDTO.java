package com.backendsyndicate.smashclub.admin.dto.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class RespAdminLoginDTO {
    private String accessToken;
    private String username;
    private String fullname;
}
