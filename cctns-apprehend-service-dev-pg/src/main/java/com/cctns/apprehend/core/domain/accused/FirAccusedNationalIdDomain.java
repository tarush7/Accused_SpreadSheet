package com.cctns.apprehend.core.domain.accused;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class FirAccusedNationalIdDomain {

    private Long nationalIdSrno;
    private Integer langCd;
    private Long accusedVid;
    private Integer nationalIdTypeCd;
    private String nationalIdNum;
    private LocalDate passportIssueDt;
    private String passportIssuePlc;
    private String recordStatus;
    private LocalDateTime recordCreatedOn;
    private Long recordCreatedBy;
    private LocalDateTime recordUpdatedOn;
    private Long recordUpdatedBy;

    private FirAccusedInfoDomain accused;
}