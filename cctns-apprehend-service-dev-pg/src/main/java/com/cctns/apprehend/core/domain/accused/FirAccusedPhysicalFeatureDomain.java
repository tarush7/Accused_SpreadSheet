package com.cctns.apprehend.core.domain.accused;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FirAccusedPhysicalFeatureDomain {

    private Long accPhyFeatSrno;
    private Integer langCd;
    private Long accusedVid;
    private Integer phyFeatCategoryCd;
    private Integer phyFeatureMajCd;
    private Integer phyFeatureMinCd;
    private String othrDressType;
    private String phyFeatCategory;
    private String phyFeatureMajor;
    private String phyFeatureMinor;
    private String recordStatus;
    private LocalDateTime recordCreatedOn;
    private Long recordCreatedBy;
    private LocalDateTime recordUpdatedOn;
    private Long recordUpdatedBy;

    private FirAccusedInfoDomain accused;

}
