package com.cctns.apprehend.web.dto.request.apprehend;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApprehendEducQualifDTO {

    private Long apprEduQualSrno;
    private Integer langCd;
    private Long apprEduQualSrnoMigr;
    private Long apprehendSrno;
    private Integer educationQualCd;
}
