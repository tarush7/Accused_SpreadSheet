package com.cctns.apprehend.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommonParamsDomain {

    private Long staffId;
    private String loginId;
    private Integer langCd;
    private Long officeCd;
    private Long stateId;
    private Long districtId;
    private Long psId;
    private Integer officeTypeCd;
    private Integer rankCd;
    private Integer officeLevelCd;
    private List<Integer> allowedRoleCd; // all available role for that user
    private Long oicStaffId;
    private String oicLoginId;

    //New
    private String loginparams;   //Needed for all external calls (For passing header)
    private String requestId;     //Need for grafana and cross service logs
    private String authToken;     //Needed for Auth

}