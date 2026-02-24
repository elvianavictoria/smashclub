package com.backendsyndicate.smashclub.admin.dto.relation;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class RelAdminBookingPlayerDTO {
    private String id;
    private String fullName;
    private String email;
}
