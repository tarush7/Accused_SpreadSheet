package com.cctns.apprehend.core.domain.apprehend;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ApprehendAddressDomain {

    private String apprAddrSrno;
  //  private String id;
    private Integer langCd;
    private Long apprAddrSrnoMigr;
    private Long apprehendSrno;
    private Long juvenileSrno;
    private Integer juvenileVid;
    private Long juvenileSrnoMigr;
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

    private String recordStatus;
    private LocalDateTime recordCreatedOn;
    private Long recordCreatedBy;
    private LocalDateTime recordUpdatedOn;
    private Long recordUpdatedBy;
    private String recordSyncFrom;
    private LocalDateTime recordSyncOn;

    private ApprehendMemoDomain apprehendMemo;
}
