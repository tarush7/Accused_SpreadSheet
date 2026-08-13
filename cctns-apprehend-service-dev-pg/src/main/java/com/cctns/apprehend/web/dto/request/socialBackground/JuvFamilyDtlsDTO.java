package com.cctns.apprehend.web.dto.request.socialBackground;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JuvFamilyDtlsDTO {

//    private Long juvFamilySrno;
//    private Integer langCd;
//    private Integer juvenileVid;
//    private Long juvenileSrno;
//    private Long apprehendSrno;
//    private Long bgReportSrno;
    private String id;
    private Integer relationTypeCd;
    private String relationType;
    private String relativeName;
    private String relativeNameEng;
//    private String relativeAlias;
//    private Long relMobileNum;
//    private String relTelephone;
    private Integer ageTypeCd;
    private Integer ageYrs;
    @JsonProperty("ageMnths")
    private Integer ageMonths;
    private Integer yob;
    private LocalDate dob;
    private Integer ageFromYrs;
    private Integer ageToYrs;
    private Integer genderCd;
    private Integer eduQualCd;
    private Integer occupationCd;
    private String occupation;
    private String healthStatus;
    private String mentalHealthHist;
    private Integer addictionCd;
    private Integer incomeCd;
    private String crimeNature;
    private String legalStatus;
    private String arrestDtls;
    private String punishmentAwarded;
    private String confinePeriod;


    //fields to add
    // incomeCd
}
