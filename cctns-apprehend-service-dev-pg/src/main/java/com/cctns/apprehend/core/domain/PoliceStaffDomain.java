package com.cctns.apprehend.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PoliceStaffDomain {
    private Long id;
    private String loginId;
    private Integer langCd;
    private String firstName;
    private String middleName;
    private String lastName;
    private String personFullName;
    private String beltno;
    private Integer rankCd;
    private String rankDesc;
    private LocalDate joiningDt;
    private LocalDate dob;
    private Integer genderCd;
    private String genderDesc;
    private Integer mobStdCd;
    private Long mobileNum;
    private String telephone;
    private String email;
    private String parichayId;
    private Integer stateCd;
    private Integer districtCd;
    private Integer psCd;
    private Integer officeLevelCd;
    private Integer officeTypeCd;
    private Long officeCd;
    private String officeName;
    private Integer beatCd;
    private String beatPlaceFrom;
    private String beatPlaceTo;
    private Integer userStatusCd;
    private String userStatus;
    private Date relieveDt;
    private String supervisorCd;
    private String pisStatus;
    private Long transfrdFrmOfficeCd;
    private Date releaseDt;
    private String uidVirtualNum;
    private String uidNumMasked;
    private String isEsignConsentPrvded;
    private String userPwd;
    private String activeStatus;
    private String recordStatus;
    private LocalDateTime recordCreatedOn;
    private Long recordCreatedBy;
    private LocalDateTime recordUpdatedOn;
    private Long recordUpdatedBy;
    private List<AddressDomain> addresses;
}