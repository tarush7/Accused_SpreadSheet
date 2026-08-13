package com.cctns.apprehend.web.dto.request.apprehend;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApprehendWitnessDTO {

    @JsonProperty("id")
    private String apprWitnsSrno;
    private Integer langCd;
    private String firstName;
    private String middleName;
    private String lastName;
    private String firstNameEng;
    private String middleNameEng;
    private String lastNameEng;
//    @JsonProperty("aliases")
//    private String witnAlias;
    private Integer relationTypeCd;
    private String relativeName;
    private Integer ageTypeCd;
    private Integer ageYrs;
    private Integer ageMnths;
    private Integer yob;
  //  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dob;
    private Integer ageFrmYrs;
    private Integer ageToYrs;
    private Long mobileNum;
    private String email;
    private Integer nationalityCd;
    private Integer occupationCd;
    private Integer genderCd;
    private Integer maritalStatusCd;
    private Integer witnEvidTenderCd;
    private String witnEvidTender;
    private String witnessStatement;
    @JsonProperty("isMobVerified")
    private Boolean isWitnMobileVerf;
    private Integer witnCategoryCd;
    private List<AliasJsonDTO> aliases;

    @JsonProperty("addressGrid")
    private List<ApprehendWitnessAddrDTO> witnessAddress;
    private List<ApprehendWitnessNationalityDto> idList;
 //   private ApprehendMemoDTO apprehendMemo;

//    private String otherOccupation;
//    private Long apprWitnsSrnoMigr;
//    private Long apprehendSrno;
//    private Long personCodeMigr;
//    private String witnAliasEng;
//    private String telephone;
}
