package com.backendsyndicate.smashclub.admin.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ReqAdminLoginDTO {
    private String username;
    private String password;
}
