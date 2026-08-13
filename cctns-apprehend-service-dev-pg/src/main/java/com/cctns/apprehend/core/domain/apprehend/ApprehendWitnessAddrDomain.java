package com.cctns.apprehend.core.domain.apprehend;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ApprehendWitnessAddrDomain {

    private String apprWitnAddrSrno;
    private Integer langCd;
    private Long addressCdMigr;
    private Long apprWitnsSrno;
    private Long apprWitnsSrnoMigr;
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
    private Boolean isPermAddrSame;
    private String outsideIndiaAddr;
    private String addressEng;

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

    private ApprehendWitnessDomain apprehendWitness;

}
