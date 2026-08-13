package com.cctns.apprehend.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccusedNationalIdDTO {
    private Long id;
    private Integer nationalIdTypeCd;
    private String nationalIdType;
    private String nationalIdNum;
    private String passportIssueDt;
    private String passportIssuePlc;
}
