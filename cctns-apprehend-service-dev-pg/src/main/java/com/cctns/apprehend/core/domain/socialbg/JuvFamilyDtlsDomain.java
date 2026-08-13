package com.cctns.apprehend.core.domain.socialbg;

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
public class JuvFamilyDtlsDomain {

    private String id;
    private Integer langCd;
    private Integer juvenileVid;
    private Long juvenileSrno;
    private Long apprehendSrno;
    private Long bgReportSrno;
    private Integer relationTypeCd;
    private String relationType;
    private String relativeName;
    private String relativeNameEng;
    private String relativeAlias;
    private Long relMobileNum;
    private String relTelephone;
    private Integer ageTypeCd;
    private Integer ageYrs;
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

    private String recordStatus;
    private LocalDateTime recordCreatedOn;
    private Long recordCreatedBy;
    private LocalDateTime recordUpdatedOn;
    private Long recordUpdatedBy;
    private String recordSyncFrom;
    private LocalDateTime recordSyncOn;

    private JuvBackgroundReportDomain juvBackgroundReport;

}
