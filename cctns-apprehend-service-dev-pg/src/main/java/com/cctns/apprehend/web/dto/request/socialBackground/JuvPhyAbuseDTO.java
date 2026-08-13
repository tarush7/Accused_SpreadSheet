package com.cctns.apprehend.web.dto.request.socialBackground;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JuvPhyAbuseDTO{

    private String id;
    private Long juvAbuseSrno;
    private Integer langCd;
    private Integer juvenileVid;
    private Long juvenileSrno;
//    private Long apprehendSrno;
//    private Long bgReportSrno;
    private Integer abuseTypeCd;
    private String abuseType;
    private String abuseRemarks;

}
