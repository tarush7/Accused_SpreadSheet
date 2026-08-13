package com.cctns.apprehend.core.domain.apprehend;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApprehendWitnessNationalityDomain {

    private String apprWitnNatSrno;
    private Integer langCd;
    private Long apprWitnsSrno;
    private Long apprWitnsSrnoMigr;
    private Integer nationalIdTypeCd;
    private String nationalIdType;
    private String nationalIdNum;
    private LocalDateTime passportIssueDt;
    private String passportIssuePlc;
    private String recordStatus;
    private LocalDateTime recordCreatedOn;
    private Long recordCreatedBy;
    private LocalDateTime recordUpdatedOn;
    private Long recordUpdatedBy;
    private String recordSyncFrom;
    private LocalDateTime recordSyncOn;

    private ApprehendWitnessDomain apprehendWitness;
}