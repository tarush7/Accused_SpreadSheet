package com.cctns.apprehend.core.domain;

import com.cctns.apprehend.core.domain.acts.ActSectionDomain;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FirListDomain extends CommonParamsDomain {
    private String firSrno;
    private String firDisplayNum;
    private String firRegNum;
    private String regDt;
    private String ciclGdNum;
    private String ciclGdDisplayNum;
    private String ciclGdDt;
    private String complainantName;
    private String juvenileName;
    private String relationType;
    private String relativeName;
    private Integer year;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String apprehendSrno;

    private Integer[] psIdList;
    private PageableDomain pageable;

    private String gridFlag;
    private String firTypeFlag;

    private List<ActSectionDomain> gdActSectionList;
}
