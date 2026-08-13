package com.cctns.apprehend.core.domain.apprehend;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ApprehendActSectionDomain {

    private String apprehendActSrno;
    private Integer langCd;
    private Long apprehendActSrnoMigr;
    private Long apprehendSrno;
    private Integer actCd;
    private String sectionCd;
    private String section;
    private String actShort;
    private String actLong;
    private String sectionDesc;

    private String recordStatus;
    private LocalDateTime recordCreatedOn;
    private Long recordCreatedBy;
    private LocalDateTime recordUpdatedOn;
    private Long recordUpdatedBy;
    private String recordSyncFrom;
    private LocalDateTime recordSyncOn;

    private ApprehendMemoDomain apprehendMemo;

}
