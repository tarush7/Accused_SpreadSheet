package com.cctns.apprehend.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccusedAddressDTO {
    //   private Long apprehendSrno;
    private Long id;
    private Integer addressTypeCd;
    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    private Long subDistrictCd;
    private Long villageCd;
    private String village;
    private String tehsil;
    private Integer countryCd;
    private Long psId;
    private Long stateId;
    private Integer lgDistrictCd;
    private Integer pincode;
    private Boolean isCommAddr;
    private String outsideIndiaAddr;
    private String addressEng;
    private Boolean isPermAddrSame;

    private String subDistrict;
    private String country;
    private String state;
    private String district;
    private String ps;
}
