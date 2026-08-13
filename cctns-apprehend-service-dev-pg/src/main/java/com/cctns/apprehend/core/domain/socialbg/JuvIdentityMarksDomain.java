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
public class JuvIdentityMarksDomain {

    private String id;
    private Integer langCd;
    private Long bgReportSrno;
    private Integer idMarksTypeCd;
    private Integer bodyPartLocCd;
    private String idMarksType;
    private String bodyPartLoc;
    private Integer tattooTypeCd;
    private String tattooMarkDesc;

    private String recordStatus;
    private LocalDateTime recordCreatedOn;
    private Long recordCreatedBy;
    private LocalDateTime recordUpdatedOn;
    private Long recordUpdatedBy;
    private String recordSyncFrom;
    private LocalDateTime recordSyncOn;

    private JuvBackgroundReportDomain juvBackgroundReport;

}