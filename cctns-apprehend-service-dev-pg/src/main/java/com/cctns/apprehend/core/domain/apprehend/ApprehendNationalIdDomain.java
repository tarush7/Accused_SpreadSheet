package com.cctns.apprehend.core.domain.apprehend;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class ApprehendNationalIdDomain {

    private String nationalIdSrno;
    private Integer langCd;
    private Long nationalIdSrnoMigr;
    private Long apprehendSrno;
    private Integer nationalIdTypeCd;
    private String nationalIdType;
    private String nationalIdNum;
    private LocalDate passportIssueDt;
    private String passportIssuePlc;

    private String recordStatus;
    private LocalDateTime recordCreatedOn;
    private Long recordCreatedBy;
    private LocalDateTime recordUpdatedOn;
    private Long recordUpdatedBy;
    private String recordSyncFrom;
    private LocalDateTime recordSyncOn;

    private ApprehendMemoDomain apprehendMemo;
}
