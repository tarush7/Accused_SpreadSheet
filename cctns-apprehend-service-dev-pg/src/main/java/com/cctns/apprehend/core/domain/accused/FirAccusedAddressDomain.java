package com.cctns.apprehend.core.domain.accused;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FirAccusedAddressDomain {

    private Long firAccAddrSrno;
    private Integer langCd;
    private Long accusedVid;
    private Integer addressTypeCd;
    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    private Integer subDistrictCd;
    private Long villageCd;
    private String village;
    private String tehsil;
    private Integer countryCd;
    private Integer stateCd;
    private Integer districtCd;
    private Integer psCd;
    private Integer pincode;
    private Boolean isPermAddrSame;
    private Boolean isCommAddr;
    private String addressEng;
    private String outsideIndiaAddr;
    private Integer lgDistrictCd;
    private Long psId;
    private Long stateId;
    private String recordStatus;
    private LocalDateTime recordCreatedOn;
    private Long recordCreatedBy;
    private LocalDateTime recordUpdatedOn;
    private Long recordUpdatedBy;

    private FirAccusedInfoDomain accused;

}