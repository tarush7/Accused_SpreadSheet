package com.cctns.apprehend.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ComplainantDomain {

    private Long id;
    private Long complSrno;
    private Integer langCd;
    private Long firRegNum;
    private String firstName;
    private String middleName;
    private String lastName;
    private String fullName;
    private List<AliasDomain> alias;
    private String firstNameEng;
    private String middleNameEng;
    private String lastNameEng;
    private String fullNameEng;
    private Integer relationTypeCd;
    private String relationType;
    private String relativeName;
    private String relativeNameEng;
    private Integer othrRelTypeCd;
    private String othrRelType;
    private String othrRelName;
    private String othrRelNameEng;
    private Integer ageTypeCd;
    private String ageType;
    private Integer ageYrs;
    private Integer ageMnths;
    private Integer yob;
    private LocalDate dob;
    private Integer ageFromYrs;
    private Integer ageToYrs;
    private Long mobileNum;
    private Boolean isMobVerified;
    private String telephone;
    private String email;
    private Integer nationalityCd;
    private String nationality;
    private Integer genderCd;
    private String gender;
    private Boolean isComplDisable;
    private Boolean isComplVictimSame;
    private List<SocialMediaDomain> socialMedia;
    private List<AddressDomain> addresses;
}
