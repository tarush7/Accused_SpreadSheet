package com.cctns.apprehend.web.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccusedListDTO {
    private String firDisplayNum;
    private String firRegDt;
    private String ps;
    private String district;
    private String ioName;
    private String juvenileName;
    private Integer relationTypeCd;
    private String relationType;
    private String relativeName;
    private Integer age;
//    private Long accusedSrno;
//    private Long accusedVid;
    LocalDateTime apprehendDt;
    private String apprehendSrno;
    private String juvDisposalSrno;
    private String bgReportSrno;
    private String ciclGdNum;
    public String id;

    @JsonProperty("id")
    public String getId(){return this.apprehendSrno;}

}
