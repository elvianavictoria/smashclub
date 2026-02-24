package com.backendsyndicate.smashclub.admin.dto.relation;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class RelAdminBookingCourtDTO {
    private String courtCode;
    private String courtName;
    private String courtImgLink;
}
