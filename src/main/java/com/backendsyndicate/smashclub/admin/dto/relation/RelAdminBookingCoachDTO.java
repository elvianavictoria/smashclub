package com.backendsyndicate.smashclub.admin.dto.relation;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class RelAdminBookingCoachDTO {
    private String coachCode;
    private String coachName;
    private String coachImgLink;
}
