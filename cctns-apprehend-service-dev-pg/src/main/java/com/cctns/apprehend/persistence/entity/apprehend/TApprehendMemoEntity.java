package com.cctns.apprehend.persistence.entity.apprehend;

import com.cctns.apprehend.persistence.entity.BaseEntity;
import com.cctns.apprehend.persistence.entity.accused.AliasJsonEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "t_apprehend_memo", schema = "apprehend")
public class TApprehendMemoEntity extends BaseEntity {

    @Id
    @Column(name = "apprehend_srno")
    private Long apprehendSrno;

    @Column(name = "lang_cd", nullable = false)
    private Integer langCd;

    @Column(name = "juvenile_vid")
    private Long juvenileVid;

    @Column(name = "juvenile_srno")
    private Long juvenileSrno;

    @Column(name = "fir_juvenile_srno")
    private Long firJuvenileSrno;

    @Column(name = "juvenile_srno_migr")
    private Long juvenileSrnoMigr;

    @Column(name = "state_id", nullable = false)
    private Long stateId;

    @Column(name = "district_id", nullable = false)
    private Long districtId;

    @Column(name = "ps_id", nullable = false)
    private Long psId;

    @Column(name = "reg_srno")
    private Integer regSrno;

    @Column(name = "apprehend_year")
    private Integer apprehendYear;

    @Column(name = "fir_reg_num")
    private Long firRegNum;

    @Column(name = "is_from_gd")
    private Boolean isFromGd;

    @Column(name = "gd_num")
    private String gdNum;

    @Column(name = "gt_dt")
    private LocalDateTime gtDt;

    @Column(name = "apprehend_type_cd")
    private Integer apprehendTypeCd;

    @Column(name = "apprehend_dt")
    private LocalDateTime apprehendDt;

    @Column(name = "apprehend_alphanum")
    private String apprehendAlphanum;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "first_name_eng")
    private String firstNameEng;

    @Column(name = "middle_name_eng")
    private String middleNameEng;

    @Column(name = "last_name_eng")
    private String lastNameEng;

    @Column(name = "is_re_arrested")
    private Boolean isReArrested;

    @Column(name = "re_apprehend_srno")
    private Long reApprehendSrno;

    @Column(name = "relation_type_cd")
    private Integer relationTypeCd;

    @Column(name = "relative_name")
    private String relativeName;

    @Column(name = "relative_name_eng")
    private String relativeNameEng;

    @Column(name = "relative_alias")
    private String relativeAlias;

    @Column(name = "rel_mobile_num")
    private Long relMobileNum;

    @Column(name = "rel_telephone")
    private String relTelephone;

    @Column(name = "othr_rel_type_cd")
    private Integer othrRelTypeCd;

    @Column(name = "othr_rel_name")
    private String othrRelName;

    @Column(name = "othr_rel_name_eng")
    private String othrRelNameEng;

    @Column(name = "othr_rel_mobile_num")
    private Long othrRelMobileNum;

    @Column(name = "religion_cd")
    private Integer religionCd;

    @Column(name = "national_id_type_cd")
    private Integer nationalIdTypeCd;

    @Column(name = "gender_cd")
    private Integer genderCd;

    @Column(name = "category_cd")
    private Integer categoryCd;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "edu_qual_cd")
    private String[] eduQualCd;

    @Column(name = "living_status_cd")
    private Integer livingStatusCd;

    @Column(name = "marital_status_cd")
    private Integer maritalStatusCd;

    @Column(name = "occupation_cd")
    private Integer occupationCd;

    @Column(name = "age_proof_type_cd")
    private Integer ageProofTypeCd;

    @Column(name = "lang_dialect_cd")
    private Integer langDialectCd;

    @Column(name = "age_type_cd")
    private Integer ageTypeCd;

    @Column(name = "age_yrs")
    private Integer ageYrs;

    @Column(name = "age_months")
    private Integer ageMnths;

    @Column(name = "yob")
    private Integer yob;

    @Column(name = "dob")
    private LocalDate dob;

    @Column(name = "age_from_yrs")
    private Integer ageFromYrs;

    @Column(name = "age_to_yrs")
    private Integer ageToYrs;

    @Column(name = "is_age_proof_req")
    private Boolean isAgeProofReq;

    @Column(name = "mobile_num")
    private Long mobileNum;

    @Column(name = "telephone")
    private String telephone;

    @Column(name = "email")
    private String email;

    @Column(name = "nationality_cd")
    private Integer nationalityCd;

    @Column(name = "language_dialect_cd")
    private Integer languageDialectCd;

    @Column(name = "oth_identification_marks")
    private String othIdentificationMarks;

    @Column(name = "blood_group_cd")
    private Integer bloodGroupCd;

    @Column(name = "income_group_cd")
    private Integer incomeGroupCd;

    @Column(name = "height_from_cm")
    private String heightFromCm;

    @Column(name = "height_to_cm")
    private String heightToCm;

    @Column(name = "weight_kg")
    private Integer weightKg;

    @Column(name = "eprison_pid")
    private String eprisonPid;

    @Column(name = "apprehend_by_others")
    private String apprehendByOthers;

    @Column(name = "apprehend_by_pis_cd")
    private String apprehendByPisCd;

    @Column(name = "appr_from_state_id")
    private Long apprFromStateId;

    @Column(name = "appr_from_district_id")
    private Long apprFromDistrictId;

    @Column(name = "appr_from_ps_id")
    private Long apprFromPsId;

    @Column(name = "apprehend_beat_cd")
    private Integer apprehendBeatCd;

    @Column(name = "apprehend_action_taken_cd")
    private Integer apprehendActionTakenCd;

    @Column(name = "custody_action_other")
    private String custodyActionOther;

    @Column(name = "apprehend_status_cd")
    private Integer apprehendStatusCd;

    @Column(name = "court_order_details")
    private String courtOrderDetails;

    @Column(name = "is_apprehend_photo_taken")
    private Boolean isApprehendPhotoTaken;

    @Column(name = "belong_to_crime_gang")
    private Boolean belongToCrimeGang;

    @Column(name = "criminal_gang_cd")
    private Integer criminalGangCd;

    @Column(name = "injury_details")
    private String injuryDetails;

    @Column(name = "is_med_exam_req")
    private Boolean isMedExamReq;

    @Column(name = "apprehend_reason")
    private String apprehendReason;

    @Column(name = "io_cd")
    private String ioCd;

    @Column(name = "is_relat_intimated")
    private Boolean isRelatIntimated;

    @Column(name = "intimate_rel_type_cd")
    private Integer intimateRelTypeCd;

    @Column(name = "intimate_rel_name")
    private String intimateRelName;

    @Column(name = "intimate_dt")
    private LocalDateTime intimateDt;

    @Column(name = "intimation_given_to")
    private String intimationGivenTo;

    @Column(name = "intimate_mode_cd")
    private Integer intimateModeCd;

    @Column(name = "is_int_mob_verif")
    private Boolean isIntMobVerif;

    @Column(name = "intimate_mob_num")
    private Long intimateMobNum;

    @Column(name = "intimate_mode_detail")
    private String intimateModeDetail;

    @Column(name = "intimation_remarks")
    private String intimationRemarks;

    @Column(name = "is_appr_grnd_comm")
    private Boolean isApprGrndComm;

    @Column(name = "pres_int_rel_addr_migr")
    private Long presIntRelAddrMigr;

    @Column(name = "perm_int_rel_addr_migr")
    private Long permIntRelAddrMigr;

    @Column(name = "is_fp_taken")
    private Boolean isFpTaken;

    @Column(name = "is_fp_chance")
    private Boolean isFpChance;

    @Column(name = "is_dangerous")
    private Boolean isDangerous;

    @Column(name = "is_juvenile_disable")
    private Boolean isJuvenileDisable;

    @Column(name = "physical_cond_cd")
    private Integer physicalCondCd;

    @Column(name = "has_prev_jump_bail")
    private Boolean hasPrevJumpBail;

    @Column(name = "prev_jump_bail_details")
    private String prevJumpBailDetails;

    @Column(name = "is_generally_armed")
    private Boolean isGenerallyArmed;

    @Column(name = "operates_with_accomplish")
    private Boolean operatesWithAccomplish;

    @Column(name = "is_known_criminal")
    private Boolean isKnownCriminal;

    @Column(name = "known_criminal_details")
    private String knownCriminalDetails;

    @Column(name = "is_recidivist")
    private Boolean isRecidivist;

    @Column(name = "likely_jumps_bail")
    private Boolean likelyJumpsBail;

    @Column(name = "after_bail_commits_crime")
    private Boolean afterBailCommitsCrime;

    @Column(name = "wanted_other_case")
    private Boolean wantedOtherCase;

    @Column(name = "wanted_case_details")
    private String wantedCaseDetails;

    @Column(name = "prev_conviction_detail")
    private String prevConvictionDetail;

    @Column(name = "present_address_1_cd_migr")
    private Long presentAddress1CdMigr;

    @Column(name = "present_address_2_cd_migr")
    private Long presentAddress2CdMigr;

    @Column(name = "present_address_3_cd_migr")
    private Long presentAddress3CdMigr;

    @Column(name = "permanent_address_cd_migr")
    private Long permanentAddressCdMigr;

    @Column(name = "ps_intimate_dt")
    private LocalDateTime psIntimateDt;

    @Column(name = "is_photo_taken")
    private Boolean isPhotoTaken;

    @Column(name = "is_appr_perm_taken")
    private Boolean isApprPermTaken;

    @Column(name = "is_info_verified")
    private Boolean isInfoVerified;

    @Column(name = "court_estbl_cd")
    private String courtEstblCd;

    @Column(name = "court_name")
    private String courtName;

    @Column(name = "evidence_type_cd")
    private Integer evidenceTypeCd;

    @Column(name = "id_type_cd")
    private Integer idTypeCd;

    @Column(name = "is_proclaimed_offender")
    private Boolean isProclaimedOffender;

//    @Column(name = "is_dress_for_m_f")
//    private Boolean isDressForMF;

    @Column(name = "other_living_status")
    private String otherLivingStatus;

    @Column(name = "apprehend_place")
    private String apprehendPlace;

    @Column(name = "other_occupation")
    private String otherOccupation;

    @Column(name = "any_other_details")
    private String anyOtherDetails;

    @Column(name = "fir_copy_given_to")
    private String firCopyGivenTo;

    @Column(name = "offender_details")
    private String offenderDetails;

    @Column(name = "apprehend_circumstances")
    private String apprehendCircumstances;

    @Column(name = "alias",columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<AliasJsonEntity> aliases;

//    @Column(name = "alias")
//    @JdbcTypeCode(SqlTypes.JSON)
//    private List<AliasDomain> aliases;

    @Column(name = "custody_type_cd")
    private Integer custodyTypeCd;

    @Column(name = "custody_type_name")
    private String custodyTypeName;

    @Column(name = "jcwo_name")
    private String jcwoName;

    @Column(name = "jcwo_parent_mobile_num")
    private Long jcwoParentMobileNum;

    @Column(name = "jcwo_parent_address")
    private String jcwoParentAddress;

    @Column(name = "intimate_postal_addr")
    private String intimatePostAddr;

    @Column(name = "cicl_gd_num")
    private String ciclGdNum;

    @Column(name = "cicl_gt_dt")
    private LocalDateTime ciclGdDt;

    @Column(name = "record_created_on")
    private LocalDateTime recordCreatedOn;

    @OneToMany(mappedBy = "apprehendMemo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TApprehendAddressesEntity> apprehendAddress;

    @OneToMany(mappedBy = "apprehendMemo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TApprehendWitnessEntity> apprehendWitness;

    @OneToMany(mappedBy = "apprehendMemo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TApprehendIntimateAddrEntity> intimateAddress;

    @OneToMany(mappedBy = "apprehendMemo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TApprehendFilesEntity> fileList;

    @OneToMany(mappedBy = "apprehendMemo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TApprehendActSectionEntity> actSectionList;

    @OneToMany(mappedBy = "apprehendMemo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TApprehendNationalIdEntity> idList;



}
