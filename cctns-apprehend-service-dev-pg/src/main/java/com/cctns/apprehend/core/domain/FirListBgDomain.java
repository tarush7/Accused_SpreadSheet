package com.cctns.apprehend.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FirListBgDomain extends CommonParamsDomain {
    private String firSrno;
    private String firDisplayNum;
    private String firRegNum;
    private String regDt;
    private String ciclGdNum;
    private String ciclGdDisplayNum;
    private String ciclGdDt;
    private String ps;
    private String district;
    private Long apprehendSrno;
    private Long bgReportSrno;
    private String juvenileName;
    private Integer relationTypeCd;
    private String relativeName;
    private String relationType;
    private Integer year;
    private LocalDate fromDate;
    private LocalDate toDate;
    private Integer[] psIdList;
    private PageableDomain pageable;

    private String gridFlag;
    private String firTypeFlag;
}
