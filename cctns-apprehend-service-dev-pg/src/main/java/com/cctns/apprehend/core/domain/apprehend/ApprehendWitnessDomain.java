package com.cctns.apprehend.core.domain.apprehend;

import com.cctns.apprehend.core.domain.accused.AliasJsonDomain;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ApprehendWitnessDomain {

    private String apprWitnsSrno;
    private Integer langCd;
    private Long apprWitnsSrnoMigr;
    private Long apprehendSrno;
    private Long personCodeMigr;
    private String firstName;
    private String middleName;
    private String lastName;
    private String firstNameEng;
    private String middleNameEng;
    private String lastNameEng;
 //   private String witnAlias;
    private String witnAliasEng;
    private Integer relationTypeCd;
    private String relativeName;
    private Integer ageTypeCd;
    private Integer ageYrs;
    private Integer ageMnths;
    private Integer yob;
    private LocalDate dob;
    private Integer ageFrmYrs;
    private Integer ageToYrs;
    private Long mobileNum;
    private String telephone;
    private String email;
    private Integer nationalityCd;
    private Integer occupationCd;
    private Integer genderCd;
    private Integer maritalStatusCd;
    private String otherOccupation;
    private Integer witnEvidTenderCd;
    private String witnEvidTender;
    private String witnessStatement;
    private Boolean isWitnMobileVerf;
    private Integer witnCategoryCd;
    private List<AliasJsonDomain> aliases;

    private String recordStatus;
    private LocalDateTime recordCreatedOn;
    private Long recordCreatedBy;
    private LocalDateTime recordUpdatedOn;
    private Long recordUpdatedBy;
    private String recordSyncFrom;
    private LocalDateTime recordSyncOn;

    private List<ApprehendWitnessAddrDomain> witnessAddress;
    private List<ApprehendWitnessNationalityDomain> idList;
    private ApprehendMemoDomain apprehendMemo;

}
