package com.cctns.apprehend.core.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourtDataDomain {

    private Long courtComplexCd;
    private String courtComplexName;
    private String establishmentName;
    private Integer cisDistrictCd;
    private String cisDistrictName;


}