package com.cctns.apprehend.core.domain.apprehend;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ApprehendSocialMediaDomain {

    private Long apprSocMedSrno;
    private Integer langCd;
    private Long socialmediaIdSrnoMigr;
    private Long apprehendSrno;
    private Integer socialmediaTypeCd;
    private String socialmediaUrl;

    private String recordStatus;
    private LocalDateTime recordCreatedOn;
    private Long recordCreatedBy;
    private LocalDateTime recordUpdatedOn;
    private Long recordUpdatedBy;
    private String recordSyncFrom;
    private LocalDateTime recordSyncOn;
}
