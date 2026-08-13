package com.cctns.apprehend.core.domain.socialbg;

import com.cctns.apprehend.core.domain.AccusedDetailsDomain;
import com.cctns.apprehend.core.domain.CommonParamsDomain;
import com.cctns.apprehend.core.domain.acts.ActSectionDomain;
import com.cctns.apprehend.core.domain.apprehend.ApprehendMemoDomain;
import jakarta.persistence.Column;
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
public class    JuvBackgroundReportDomain extends CommonParamsDomain {

    private Long bgReportSrno;
    private Integer langCd;
    private Long firRegNum;
    private Long apprehendSrno;
    private Long juvenileSrno;
    private Integer ageVers;
    private Integer ageSourceCd;
    private Boolean isMarried;
    private String spouseName;
    private Integer spouseAge;
    private String childrenDtls;
    private Integer childAge;
    private String religionAttitude;
    private String presentLivingCond;
    private String homeDisciplineDtls;
    private String otherFactors;
    private String employmentDtls;
    private String incomeUtilManner;
    private String workRecord;
    private Integer eduQualCd;
    private String familyLeaveReason;
    private String lastSchoolStud;
    private String vocationalTraining;
    private String attTowardFriend;
    private String attitudeOfFriend;
    private String attitudeOfTeacher;
    private String neighbourObserv;
    private String onbservAbtNeighbour;
    private Boolean isVictimOffence;
    private String victimOffenceDtls;
    private Boolean isDrugPeddlerUsed;
    private String drugPeddlerUsedDtls;
    private Boolean hasTendRunAway;
    private String runAwayDtls;
    private Boolean isEarlierApprehend;
    private String prevApprehendDtls;
    private String prevCaseHistory;
    private String healthConditions;
    private String mentalCondition;
    private String anyOthrRemarks;
    private String emotionalFactors;
    private String intelligenceDtls;
    private String suggestiveProblems;
    private String socioEcoStatus;
    private String enquiryAnalysis;
    private String expertOpinion;
    private String rehabRecommend;
    private Integer physicalCondCd;

    private Integer bodyBuildTypeCd;
    private Integer bodyComplexionTypeCd;
    private String bodyBuildType;
    private String bodyComplexionType;
    private String otherPhysicalDetails;
    private String heightFromCm;
    private String heightToCm;
    private Boolean isOffenceHeinous;
    private Boolean isJuvDifferAble;
    private Integer differAbleCd;
    private String courtEstblCd;
    private String courtOrderNum;
    private String otherDifferAble;
    private String othrAgeSrc;
    private LocalDate jjbOrderDt;
    private String jjbName;
    private String ciclGdNum;

    private Long courtComplexCd;
    private String courtComplexName;
    private String establishmentName;
    private Integer cisDistrictCd;
    private String cisDistrictName;

    private String recordStatus;
    private LocalDateTime recordCreatedOn;
    private Long recordCreatedBy;
    private LocalDateTime recordUpdatedOn;
    private Long recordUpdatedBy;
    private String recordSyncFrom;
    private LocalDateTime recordSyncOn;

    private List<AllegedOffenceDomain>allegedOffenceDtls;
    private List<JuvFamilyDtlsDomain> familyDtls;
    private List<JuvPhyAbuseDomain> phyAbuse;
    private List<JuvPhyFeatureDomain> physicalFeaturesList;
    private List<JuvIdentityMarksDomain> identityMarkList;
    private List<JuvDressDomain> dressTypeList;
    private List<JclBackgroundFilesDomain> bgFiles;
    private AccusedDetailsDomain accusedDetails;
    private List<ActSectionDomain> actSectionList;
    private ApprehendMemoDomain apprehendMemo;

    private PhysicalFeatureDescDomain physicalDescription;

}
