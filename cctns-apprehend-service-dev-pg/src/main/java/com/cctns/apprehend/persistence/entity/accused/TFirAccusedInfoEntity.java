package com.cctns.apprehend.persistence.entity.accused;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "t_fir_accused_info", schema = "fir")
public class TFirAccusedInfoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "accused_vid")
    private Long accusedVid;

    @Column(name = "accused_srno", nullable = false)
    private Long accusedSrno;

    @Column(name = "lang_cd", nullable = false)
    private Integer langCd;

    @Column(name = "fir_reg_num", nullable = false)
    private Long firRegNum;

    @Column(name = "crm_detail_srno")
    private Long crmDetailSrno;

    @Column(name = "crm_seq_num")
    private Integer crmSeqNum;

    @Column(name = "reg_type_cd")
    private Integer regTypeCd;

    @Column(name = "accused_rcn", length = 20)
    private String accusedRcn;

    @Column(name = "accused_ncn", length = 100)
    private String accusedNcn;

    @Column(name = "accused_known")
    private Boolean accusedKnown;

    @Column(name = "first_name", length = 160)
    private String firstName;

    @Column(name = "middle_name", length = 160)
    private String middleName;

    @Column(name = "last_name", length = 160)
    private String lastName;

    @Column(name = "first_name_eng", length = 160)
    private String firstNameEng;

    @Column(name = "middle_name_eng", length = 160)
    private String middleNameEng;

    @Column(name = "last_name_eng", length = 160)
    private String lastNameEng;

    @Column(name = "relation_type_cd")
    private Integer relationTypeCd;

    @Column(name = "relative_name", length = 250)
    private String relativeName;

    @Column(name = "relative_name_eng", length = 250)
    private String relativeNameEng;

    @Column(name = "othr_rel_type_cd")
    private Integer othrRelTypeCd;

    @Column(name = "othr_rel_name", length = 250)
    private String othrRelName;

    @Column(name = "othr_rel_name_eng", length = 250)
    private String othrRelNameEng;

    @Column(name = "othr_rel_mobile_num")
    private Long othrRelMobileNum;

    @Column(name = "age_type_cd")
    private Integer ageTypeCd;

    @Column(name = "age_yrs")
    private Integer ageYrs;

    @Column(name = "age_mnths")
    private Integer ageMnths;

    @Column(name = "yob")
    private Integer yob;

    @Column(name = "dob")
    private LocalDate dob;

    @Column(name = "age_from_yrs")
    private Integer ageFromYrs;

    @Column(name = "age_to_yrs")
    private Integer ageToYrs;

    @Column(name = "mobile_num")
    private Long mobileNum;

    @Column(name = "telephone", length = 25)
    private String telephone;

    @Column(name = "email", length = 50)
    private String email;

    @Column(name = "nationality_cd")
    private Integer nationalityCd;

    @Column(name = "category_cd")
    private Integer categoryCd;

    @Column(name = "occupation_cd")
    private Integer occupationCd;

    @Column(name = "age_proof_type_cd")
    private Integer ageProofTypeCd;

    @Column(name = "income_group_cd")
    private Integer incomeGroupCd;

    @Column(name = "gender_cd")
    private Integer genderCd;

    @Column(name = "religion_cd")
    private Integer religionCd;

    @Column(name = "marital_status_cd")
    private Integer maritalStatusCd;

    @Column(name = "is_accused_police")
    private Boolean isAccusedPolice;

    @Column(name = "is_acc_pol_witnin_ps")
    private Boolean isAccPolWitninPs;

    @Column(name = "police_gpf_num")
    private Long policeGpfNum;

    @Column(name = "police_office_name", length = 100)
    private String policeOfficeName;

    @Column(name = "acc_police_cd")
    private Long accPoliceCd;

    @Column(name = "is_proclaimed_offender")
    private Boolean isProclaimedOffender;

    @Column(name = "is_accused_infirm")
    private Boolean isAccusedInfirm;

    @Column(name = "has_dysp_apprv_taken")
    private Boolean hasDyspApprvTaken;

    @Column(name = "has_dysp_apprv")
    private Boolean hasDyspApprv;

    @Column(name = "dysp_apprv_dt")
    private LocalDateTime dyspApprvDt;

    @Column(name = "sent_to_dysp_dt")
    private LocalDateTime sentToDyspDt;

    @Column(name = "dysp_sending_remrk", length = 400)
    private String dyspSendingRemrk;

    @Column(name = "dysp_login_id")
    private Long dyspLoginId;

    @Column(name = "dysp_app_rej_remrk", length = 400)
    private String dyspAppRejRemrk;

    @Column(name = "accused_priority")
    private Integer accusedPriority;

    @Column(name = "is_juvenile")
    private Boolean isJuvenile;

    @Column(name = "is_accused_disable")
    private Boolean isAccusedDisable;

    @Column(name = "physical_cond_cd")
    private Integer physicalCondCd;

    @Column(name = "is_age_proof_req")
    private Boolean isAgeProofReq;

    @Column(name = "is_med_exam_req")
    private Boolean isMedExamReq;

    @Column(name = "rel_mobile_num")
    private Long relMobileNum;

    @Column(name = "rel_telephone", length = 25)
    private String relTelephone;

    @Column(name = "height_from_cm", length = 100)
    private String heightFromCm;

    @Column(name = "height_to_cm", length = 100)
    private String heightToCm;

    @Column(name = "is_photo_taken")
    private Boolean isPhotoTaken;

    @Column(name = "criminal_gang_cd")
    private Integer criminalGangCd;

    @Column(name = "injury_details", length = 1500)
    private String injuryDetails;

    @Column(name = "arrest_type_cd")
    private Integer arrestTypeCd;

    @Column(name = "arr_surr_srno")
    private Long arrSurrSrno;

    @Column(name = "surrenderd_estbl_cd", length = 10)
    private String surrenderdEstblCd;

    @Column(name = "arrest_surr_stat_id")
    private Integer arrestSurrStatId;

    @Column(name = "arrest_surr_dist_id")
    private Integer arrestSurrDistId;

    @Column(name = "arrest_surr_ps_id")
    private Integer arrestSurrPsId;

    @Column(name = "acc_surr_magistrate", length = 260)
    private String accSurrMagistrate;

    @Column(name = "arrest_surr_dt")
    private LocalDateTime arrestSurrDt;

    @Column(name = "arrest_action_taken_cd")
    private Integer arrestActionTakenCd;

    @Column(name = "is_re_arrested")
    private Boolean isReArrested;

    @Column(name = "re_arr_surr_srno")
    private Long reArrSurrSrno;

    @Column(name = "custody_action_other", length = 1000)
    private String custodyActionOther;

    @Column(name = "arr_intimate_rel_cd")
    private Integer arrIntimateRelCd;

    @Column(name = "arr_intimate_rel_name", length = 250)
    private String arrIntimateRelName;

    @Column(name = "arr_intimate_dt")
    private LocalDateTime arrIntimateDt;

    @Column(name = "arr_intimate_mode_cd")
    private Integer arrIntimateModeCd;

    @Column(name = "is_acc_fp_taken")
    private Boolean isAccFpTaken;

    @Column(name = "is_acc_fp_chance")
    private Boolean isAccFpChance;

    @Column(name = "is_acc_dangerous")
    private Boolean isAccDangerous;

    @Column(name = "has_acc_prev_jump_bail")
    private Boolean hasAccPrevJumpBail;

    @Column(name = "is_acc_gen_armed")
    private Boolean isAccGenArmed;

    @Column(name = "is_acc_operates_accompl")
    private Boolean isAccOperatesAccompl;

    @Column(name = "is_acc_known_criminal")
    private Boolean isAccKnownCriminal;

    @Column(name = "is_acc_recidivist")
    private Boolean isAccRecidivist;

    @Column(name = "does_acc_likely_jump_bail")
    private Boolean doesAccLikelyJumpBail;

    @Column(name = "after_bail_commits_crime")
    private Boolean afterBailCommitsCrime;

    @Column(name = "is_wanted_other_case")
    private Boolean isWantedOtherCase;

    @Column(name = "accused_status_cd")
    private Integer accusedStatusCd;

    @Column(name = "crpi_id", length = 30)
    private String crpiId;

    @Column(name = "eprison_id", length = 30)
    private String eprisonId;

//    @Column(name = "is_dress_for_m_f")
//    private Boolean isDressForMF;

    @Column(name = "other_occupation", length = 300)
    private String otherOccupation;

    @Column(name = "other_living_status", length = 300)
    private String otherLivingStatus;

    @Column(name = "interro_srno")
    private Long interroSrno;

    @Column(name = "blood_group_cd")
    private Integer bloodGroupCd;

    @Column(name = "intimation_given_to", length = 200)
    private String intimationGivenTo;

    @Column(name = "oth_identification_marks", length = 600)
    private String othIdentificationMarks;

    @Column(name = "bg_report_srno")
    private Long bgReportSrno;

    @Column(name = "other_reg_type_cd")
    private Integer otherRegTypeCd;

    @Column(name = "other_reg_num")
    private Long otherRegNum;

//    @Column(name = "arrest_surr_ps_id")
//    private Long arrestSurrPsId;

    @Column(name = "is_med_assis_req")
    private Boolean isMedAssisReq;

    @Column(name = "med_mode_of_transport")
    private Integer medModeOfTransport;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "social_media", columnDefinition = "jsonb")
    private List<SocialMediaJsonEntity> socialMedia;

//    @JdbcTypeCode(SqlTypes.JSON)
//    @Column(name = "alias", columnDefinition = "jsonb")
//    private List<AliasJsonEntity> alias;

//    @ElementCollection
//    @CollectionTable(name = "t_fir_accused_lang_used", joinColumns = @JoinColumn(name = "accused_vid"))
    @Column(name = "language_used")
    private List<String> languageUsed;

//    @ElementCollection
//    @CollectionTable(name = "t_fir_accused_edu", joinColumns = @JoinColumn(name = "accused_vid"))
    @Column(name = "edu_qual_cd")
    private List<String> eduQualCd;

    @Column(name = "record_status", length = 1)
    private String recordStatus;

    @CreationTimestamp
    @Column(name = "record_created_on", updatable = false)
    private LocalDateTime recordCreatedOn;

    @Column(name = "record_created_by")
    private Long recordCreatedBy;

    @UpdateTimestamp
    @Column(name = "record_updated_on")
    private LocalDateTime recordUpdatedOn;

    @Column(name = "record_updated_by")
    private Long recordUpdatedBy;


    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "accused_vid")
    private List<TFirAccusedAddressEntity> firAccusedAddressList;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "accused_vid")
    private List<TFirAccusedFilesEntity> firAccusedFilesList;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name="accused_vid")
    private List<TFirAccusedNationalIdEntity> firAccusedNationalityList;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name="accused_vid")
    private List<TFirAccusedPhysicalFeatureEntity> firAccusedPhyFeatureList;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name="accused_vid")
    private List<TFirAccusedBankDetailsEntity> firAccusedBankcardDetailList;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name="accused_vid")
    private List<TFirAccusedIdMarksEntity> firAccusedIdMarkList;


    //    @OneToMany(mappedBy = "accusedVid", cascade = CascadeType.ALL)
//    private List<TFirAccusedAddressEntity> firAccusedAddressList;

//    @OneToMany(mappedBy = "accusedVid", cascade = CascadeType.ALL)
//    private List<TFirAccusedNationalIdEntity> firAccusedNationalityList;
//
//    @OneToMany(mappedBy = "accusedVid", cascade = CascadeType.ALL)
//    private List<TFirAccusedPhysicalFeatureEntity> firAccusedPhyFeatureList;
//
//    @OneToMany(mappedBy = "accusedVid", cascade = CascadeType.ALL)
//    private List<TFirAccusedBankDetailsEntity> firAccusedBankcardDetailList;
//
//    @OneToMany(mappedBy = "accusedVid", cascade = CascadeType.ALL)
//    private List<TFirAccusedIdMarksEntity> firAccusedIdMarkList;



}