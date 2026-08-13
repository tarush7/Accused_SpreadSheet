package com.cctns.apprehend.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddressDomain {

    private Long id;
    private Integer langCd;
    private Integer addressTypeCd;
    private String addressType;
    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    private String village;
    private String tehsil;
    private Integer countryCd;
    private String country;
    private Integer stateCd;
    private Long stateId;
    private String state;
    private Integer districtCd;
    private Long districtId;
    private String district;
    private Integer psCd;
    private Long psId;
    private String ps;
    private Integer pincode;
    private Boolean isPermAddrSame;
    private Boolean isCommAddr;
    private Boolean outsideIndiaAddr;
    private Integer subDistrictCd;
    private Long villageCd;
    private String addressEng;
}