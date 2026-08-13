package com.cctns.apprehend.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CiclGdAccusedListResDTO {
    private String apprehendSrno;
    private String bgReportSrno;
    private String juvenileName;
    private Integer relationTypeCd;
    private String relationType;
    private String relativeName;
    private Integer age;
}
