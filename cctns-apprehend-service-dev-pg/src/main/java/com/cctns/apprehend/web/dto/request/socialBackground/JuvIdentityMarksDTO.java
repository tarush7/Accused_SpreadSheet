package com.cctns.apprehend.web.dto.request.socialBackground;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JuvIdentityMarksDTO {

    private String id;
    private Integer langCd;
    private Long bgReportSrno;
    private Integer idMarksTypeCd;
    private Integer bodyPartLocCd;
    private Integer tattooTypeCd;
    private String tattooMarkDesc;
    private String idMarksType;
    private String bodyPartLoc;

}