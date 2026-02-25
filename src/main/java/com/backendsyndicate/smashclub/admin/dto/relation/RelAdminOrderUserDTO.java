package com.backendsyndicate.smashclub.admin.dto.relation;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class RelAdminOrderUserDTO {
    private String userId;
    private String email;
    private String fullName;
}
