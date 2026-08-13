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
public class JuvPhyFeatureDomain {

    private String id;
    private Integer langCd;
    private Long bgReportSrno;
    private Long juvenileSrno;
    private Integer phyFeatCategoryCd;
    private Integer phyFeatureMajCd;
    private Integer phyFeatureMinCd;
    private String phyFeatCategory;
    private String phyFeatureMaj;
    private String phyFeatureMin;
    private Integer juvenileVid;

    private String recordStatus;
    private LocalDateTime recordCreatedOn;
    private Long recordCreatedBy;
    private LocalDateTime recordUpdatedOn;
    private Long recordUpdatedBy;
    private String recordSyncFrom;
    private LocalDateTime recordSyncOn;

    private JuvBackgroundReportDomain juvBackgroundReport;
}