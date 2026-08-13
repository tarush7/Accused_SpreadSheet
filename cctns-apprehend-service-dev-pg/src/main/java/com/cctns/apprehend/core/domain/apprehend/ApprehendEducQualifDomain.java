package com.cctns.apprehend.core.domain.apprehend;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ApprehendEducQualifDomain {

    private Long apprEduQualSrno;
    private Integer langCd;
    private Long apprEduQualSrnoMigr;
    private Long apprehendSrno;
    private Integer educationQualCd;

    private String recordStatus;
    private LocalDateTime recordCreatedOn;
    private Long recordCreatedBy;
    private LocalDateTime recordUpdatedOn;
    private Long recordUpdatedBy;
    private String recordSyncFrom;
    private LocalDateTime recordSyncOn;
}
