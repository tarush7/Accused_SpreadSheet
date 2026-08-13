package com.cctns.apprehend.core.domain;

import com.cctns.apprehend.core.domain.accused.FirAccusedNationalIdDomain;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccusedDetailsDomain {
    private Long accusedSrno;
    private Long accusedVid;
    private Long apprehendSrno;
    private String firstName;
    private String middleName;
    private String lastName;
    private String firstNameEng;
    private String middleNameEng;
    private String lastNameEng;
    private Integer relationTypeCd;
    private String relativeName;
    private String relativeNameEng;
    private Long relMobileNum;
    private Integer genderCd;
    private Integer ageTypeCd;
    private Integer ageYrs;
    private Integer ageMonths;
    private Integer yob;
    private LocalDateTime dob;
    private Integer ageFromYrs;
    private Integer ageToYrs;
    private Integer nationalityCd;
    private Integer categoryCd;
    private Integer religionCd;
    private Integer occupationCd;
    private String injuryDetails;

    private List<AccusedAddressDomain> accusedAddress;
    private List<AccusedNationalIdDomain> idList;
}
