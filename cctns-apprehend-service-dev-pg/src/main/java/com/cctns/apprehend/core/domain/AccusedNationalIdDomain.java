package com.cctns.apprehend.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccusedNationalIdDomain {
    private Long id;
    private Integer nationalIdTypeCd;
    private String nationalIdType;
    private String nationalIdNum;
    private String passportIssueDt;
    private String passportIssuePlc;
}
