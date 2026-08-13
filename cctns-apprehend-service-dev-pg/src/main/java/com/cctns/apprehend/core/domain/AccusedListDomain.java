package com.cctns.apprehend.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccusedListDomain {
    private String juvenileName;
    private Integer relationTypeCd;
    private String relationType;
    private String relativeName;
    private Integer age;
    private Long accusedSrno;
    private Long accusedVid;
    private String apprehendSrno;
    private LocalDateTime apprehendDt;
    private String juvDisposalSrno;
    private String bgReportSrno;
    private String firDisplayNum;
    private String firRegDt;
    private String ioName;
    private String ps;
    private String district;
    private String ciclGdNum;

}
