package com.cctns.apprehend.web.dto.request.apprehend;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApprehendIntimateAddrDTO {

    @JsonProperty("id")
    private String intmtAddrSrno;
    private Integer langCd;
    private Long apprehendSrno;
    private Integer addressTypeCd;
    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    private Integer subDistrictCd;
    private Long villageCd;
    private String village;
    private String tehsil;
    private Integer countryCd;
    private Integer lgDistrictCd;
    private Long psId;
    private Long stateId;
    private Integer pincode;
    private Boolean isCommAddr;
    private String outsideIndiaAddr;
    private String addressEng;
    private Boolean isPermAddrSame;

    private String addressType;
    private String subDistrict;
    private String country;
    private String state;
    private String district;
    private String ps;

 //   private ApprehendMemoDTO apprehendMemo;

}
