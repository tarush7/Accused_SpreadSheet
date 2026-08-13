package com.cctns.apprehend.core.domain;


import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Data
@ToString
public class WitnessDomain {
    private String existingWitness;
    private Long id;
    private Integer langCd;
    private String firstName;
    private String middleName;
    private String lastName;
    private String fullName;
    private List<AliasDomain> alias;
    private Integer relationTypeCd;
    private String relationType;
    private String relativeName;
    private Integer ageYrs;
    private Integer ageMnths;
    private Integer yob;
    private LocalDate dob;
    private Integer ageFromYrs;
    private Integer ageToYrs;
    private Long mobileNum;
    private String telephone;
    private String email;
    private Integer nationalityCd;
    private String nationality;
    private Integer occupationCd;
    private String occupation;
    private Integer genderCd;
    private String gender;
    private Integer maritalStatusCd;
    private String maritalStatus;
    private String otherOccupation;
    private Integer evidenceTypeCd;
    private String evidenceType;
    private Integer witnEvidTenderCd;
    private String witnEvidTender;
    private String witnExaminStatus;
    private String witnExaminType;
    private String witnExaminRemk;
    private String witnessStatement;
    private String firstNameEng;
    private String middleNameEng;
    private String lastNameEng;
    private String fullNameEng;
    private String relativeNameEng;
    private List<SocialMediaDomain> socialMedia;
    private Collection<AddressDomain> addresses;
}

