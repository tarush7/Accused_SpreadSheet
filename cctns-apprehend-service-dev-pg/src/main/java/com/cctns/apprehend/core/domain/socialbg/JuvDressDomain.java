
package com.cctns.apprehend.core.domain.socialbg;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class JuvDressDomain {

    private String id;
    private Long bgReportSrno;
    private Integer langCd;
    private Long firAccusedSrnoMigr;
    private Long accusedVid;
    private Long accusedSrno;
    private Long firRegNum;
    private Integer regTypeCd;
    private Long crmDetailSrno;
    private Integer crmSeqNum;
    private Integer dressForCd;
    private String dressFor;
    private Integer dressTypeCd;
    private String dressType;
    private Integer dressSubtypeCd;
    private String dressSubtype;
    private String othrDressDtls;

    private String recordStatus;
    private LocalDateTime recordCreatedOn;
    private Long recordCreatedBy;
    private LocalDateTime recordUpdatedOn;
    private Long recordUpdatedBy;
    private String recordSyncFrom;
    private LocalDateTime recordSyncOn;

    private JuvBackgroundReportDomain juvBackgroundReport;

}