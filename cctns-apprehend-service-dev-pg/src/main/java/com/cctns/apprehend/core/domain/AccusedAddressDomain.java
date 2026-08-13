package com.cctns.apprehend.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccusedAddressDomain {
    private Long id;
    private Long apprehendSrno;
    private Integer addressTypeCd;
    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    private Long subDistrictCd;
    private Long villageCd;
    private String village;
    private String tehsil;
    private Integer countryCd;
    private Integer pincode;
    private Boolean isCommAddr;
    private String outsideIndiaAddr;
    private String addressEng;
    private Boolean isPermAddrSame;
    private Long  psId;
    private Long stateId;
    private Integer lgDistrictCd;

    private String subDistrict;
    private String country;
    private String state;
    private String district;
    private String ps;
}
