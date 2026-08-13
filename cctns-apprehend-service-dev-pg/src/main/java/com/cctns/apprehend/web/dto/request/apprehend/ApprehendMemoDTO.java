package com.cctns.apprehend.web.dto.request.apprehend;

import com.cctns.apprehend.web.dto.request.CommonParamsDTO;
import com.cctns.apprehend.web.dto.response.ActSectionDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApprehendMemoDTO extends CommonParamsDTO {

    private Long apprehendSrno;
    private Long accusedVid;
    private Long firRegNum;
//    @NotNull(message = "apprehendYear is required")
    private Integer apprehendYear;
    private Integer apprehendTypeCd;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime apprehendDt;
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
    private Integer ageMnths;
    private Integer yob;
  //  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dob;
    private Integer ageFromYrs;
    private Integer ageToYrs;
    private Integer apprehendBeatCd;
    private Integer apprehendActionTakenCd;
    private String apprehendReason;
    private String apprehendByOthers;
    private Boolean isRelatIntimated;
    private Integer intimateRelTypeCd;
    private String intimateRelType;
    private String intimateRelName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime intimateDt;
    private Integer intimateModeCd;
    private String intimationRemarks;
    private Boolean isApprGrndComm;
    private Long intimateMobNum;
    private String firCopyGivenTo;
    private String apprehendPlace;
    private Integer custodyTypeCd;
    private String custodyTypeName;
    private String jcwoName;
    private String apprehendCircumstances;
    private Long jcwoParentMobileNum;
    private String jcwoParentAddress;
    private String intimatePostAddr;
    private Long apprFromStateId;
    private Long apprFromDistrictId;
    private Long apprFromPsId;
    private String apprFromState;
    private String apprFromDistrict;
    private String apprFromPs;
    private String ps;
    private String district;
    private String state;
    private String firDisplayNum;
    private String gdDisplayNum;
    private String ciclGdDisplayNum;
    private String firRegDt;

    //newly added fields
    private Integer religionCd;
    private Integer nationalityCd;
    private Integer categoryCd;
    private Integer occupationCd;
    private String injuryDetails;
    private String nameRnkWlfOfcr;
    private String ciclGdNum;
 //   @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime ciclGdDt;

    @NotNull(message = "isFromGd flag is required")
    private Boolean isFromGd;
    @NotNull(message = "isExistingAccused flag is required")
    private Boolean isExistingAccused;
    private String gdNum;
    @JsonProperty("gdDt")
    private LocalDateTime gtDt;
    @NotNull(message = "isNew flag is required")
    private Boolean isNew;
    private Long existingAccSrno;
    private Long existingAccFirRegNum;

   // private Map<String, Object> alias;
    private List<AliasJsonDTO> aliases;

    private List<ApprehendAddressDTO> apprehendAddress;
    private List<ApprehendNationalIdDTO> idList;
    private List<ApprehendWitnessDTO> apprehendWitness;
    private List<ApprehendIntimateAddrDTO> intimateAddress;
    private List<ApprehendFilesDTO> fileList;
//    private List<ApprehendActSectionDTO>actSectionList;
    private List<ActSectionDTO>actSectionList;

    private String recordStatus;
    private LocalDateTime recordCreatedOn;
    private Long recordCreatedBy;
//    private LocalDateTime recordUpdatedOn;
//    private Long recordUpdatedBy;
//    private String recordSyncFrom;
//    private LocalDateTime recordSyncOn;

    // private String[] eduQualCd;
//    private Integer juvenileVid;
//    private Long juvenileSrno;
//    private Long firJuvenileSrno;
//    private Long juvenileSrnoMigr;
//    private Integer regSrno;
//    private Integer apprehendYear;
//    private Long firRegNum;
//    private Boolean isFromGd;
//    private String gdNum;
//    private LocalDateTime gtDt;
//    private String apprehendAlphanum;
//    private Boolean isReArrested;
//    private Long reApprehendSrno;
//    private String relativeAlias;
//    private String relTelephone;
//    private Integer othrRelTypeCd;
//    private String othrRelName;
//    private String othrRelNameEng;
//    private Long othrRelMobileNum;
//    private Integer religionCd;
//    private Integer nationalIdTypeCd;
//    private Integer categoryCd;
//    private Integer livingStatusCd;
//    private Integer maritalStatusCd;
//    private Integer occupationCd;
//    private Integer ageProofTypeCd;
//    private Integer langDialectCd;
//    private Boolean isAgeProofReq;
//    private Long mobileNum;
//    private String telephone;
//    private String email;
//    private Integer nationalityCd;
//    private Integer languageDialectCd;
//    private String othIdentificationMarks;
//    private Integer bloodGroupCd;
//    private Integer incomeGroupCd;
//    private String heightFromCm;
//    private String heightToCm;
//    private Integer weightKg;
//    private String eprisonPid;
//    private String apprehendByOthers;
//    private String apprehendByPisCd;
//    private Long apprFromStateId;
//    private Long apprFromDistrictId;
//    private Long apprFromPsId;
//    private String custodyActionOther;
//    private Integer apprehendStatusCd;
//    private String courtOrderDetails;
//    private Boolean isApprehendPhotoTaken;
//    private Boolean belongToCrimeGang;
//    private Integer criminalGangCd;
//    private String injuryDetails;
//    private Boolean isMedExamReq;
//    private String ioCd;
//    private String intimationGivenTo;
//    private Boolean isIntMobVerif;
//    private String intimateModeDetail;
//    private Long presIntRelAddrMigr;
//    private Long permIntRelAddrMigr;
//    private Boolean isFpTaken;
//    private Boolean isFpChance;
//    private Boolean isDangerous;
//    private Boolean isJuvenileDisable;
//    private Integer physicalCondCd;
//    private Boolean hasPrevJumpBail;
//    private String prevJumpBailDetails;
//    private Boolean isGenerallyArmed;
//    private Boolean operatesWithAccomplish;
//    private Boolean isKnownCriminal;
//    private String knownCriminalDetails;
//    private Boolean isRecidivist;
//    private Boolean likelyJumpsBail;
//    private Boolean afterBailCommitsCrime;
//    private Boolean wantedOtherCase;
//    private String wantedCaseDetails;
//    private String prevConvictionDetail;
//    private Long presentAddress1CdMigr;
//    private Long presentAddress2CdMigr;
//    private Long presentAddress3CdMigr;
//    private Long permanentAddressCdMigr;
//    private LocalDateTime psIntimateDt;
//    private Boolean isPhotoTaken;
//    private Boolean isApprPermTaken;
//    private Boolean isInfoVerified;
//    private String courtEstblCd;
//    private String courtName;
//    private Integer evidenceTypeCd;
//    private Integer idTypeCd;
//    private Boolean isProclaimedOffender;
//    private Boolean isDressForMF;
//    private String otherLivingStatus;
//    private String otherOccupation;
//    private String anyOtherDetails;
//    private String offenderDetails;
//    private String apprehendCircumstances;
}

