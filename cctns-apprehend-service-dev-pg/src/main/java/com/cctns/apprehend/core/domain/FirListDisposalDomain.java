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
public class FirListDisposalDomain extends CommonParamsDomain {
    private String firSrno;
    private String firDisplayNum;
    private String firRegNum;
    private Long apprehendSrno;
    private Long juvDisposalSrno;
    private String regDt;
    private Integer year;
    private LocalDate fromDate;
    private LocalDate toDate;
    private Integer[] psIdList;
    private PageableDomain pageable;

    private String gridFlag;
    private String firTypeFlag;
    private String ciclGdNum;
    private String ciclGdDisplayNum;
    private String ciclGdDt;
}
