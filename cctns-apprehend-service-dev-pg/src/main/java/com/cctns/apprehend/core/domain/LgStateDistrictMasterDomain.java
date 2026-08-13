package com.cctns.apprehend.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LgStateDistrictMasterDomain {
    private String country;
    private String state;
    private String distrct;
    private String subDistrict;
    private String village;
    private String ps;
    private String addressType;
    private String migratedFlag;


}
