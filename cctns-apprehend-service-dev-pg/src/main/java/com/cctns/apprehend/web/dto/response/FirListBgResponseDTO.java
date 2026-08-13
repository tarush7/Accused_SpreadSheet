package com.cctns.apprehend.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FirListBgResponseDTO {
    private String regDt;
    private String firDisplayNum;
    private String firRegNum;
    private Long apprehendSrno;
    private Long bgReportSrno;
    private String juvenileName;
    private Integer relationTypeCd;
    private String relationType;
    private String relativeName;
    private String ps;
    private String district;
    private String ciclGdNum;
    private String ciclGdDisplayNum;
    private String ciclGdDt;
}
