package com.cctns.apprehend.core.domain.socialbg;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JuvPhyAbuseDomain {

    private String id;
    private Integer langCd;
    private Integer juvenileVid;
    private Long juvenileSrno;
    private Long apprehendSrno;
    private Long bgReportSrno;
    private Integer abuseTypeCd;
    private String abuseRemarks;

    private String recordStatus;
    private LocalDateTime recordCreatedOn;
    private Long recordCreatedBy;
    private LocalDateTime recordUpdatedOn;
    private Long recordUpdatedBy;
    private String recordSyncFrom;
    private LocalDateTime recordSyncOn;

    private JuvBackgroundReportDomain juvBackgroundReport;

}
