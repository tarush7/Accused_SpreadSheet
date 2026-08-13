package com.cctns.apprehend.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FirListDisposalResponseDto {
    private String regDt;
    private String firDisplayNum;
    private String firRegNum;
    private Long apprehendSrno;
    private Long juvDisposalSrno;
    private String ciclGdNum;
    private String ciclGdDisplayNum;
    private String ciclGdDt;
}
