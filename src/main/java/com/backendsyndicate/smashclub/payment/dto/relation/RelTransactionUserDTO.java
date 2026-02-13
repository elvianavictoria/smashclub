package com.backendsyndicate.smashclub.payment.dto.relation;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class RelTransactionUserDTO {
    private String id;
    private String fullName;
    private String email;
}
