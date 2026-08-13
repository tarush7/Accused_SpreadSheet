package com.cctns.apprehend.web.dto.request.socialBackground;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JuvPhyFeatureDTO {

    private String id;
    private Integer juvenileVid;
    private Integer langCd;
    private Long bgReportSrno;
    private Long juvenileSrno;
    private Integer phyFeatCategoryCd;
    private Integer phyFeatureMajCd;
    private Integer phyFeatureMinCd;
    private String phyFeatCategory;
    private String phyFeatureMaj;
    private String phyFeatureMin;
}