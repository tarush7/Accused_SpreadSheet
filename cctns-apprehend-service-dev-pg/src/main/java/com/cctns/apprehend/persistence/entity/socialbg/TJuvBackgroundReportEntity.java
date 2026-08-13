package com.cctns.apprehend.persistence.entity.socialbg;

import com.cctns.apprehend.core.domain.SocialMediaDomain;
import com.cctns.apprehend.core.domain.socialbg.AllegedOffenceDomain;
import com.cctns.apprehend.persistence.entity.BaseEntity;
import com.cctns.apprehend.persistence.entity.apprehend.TApprehendMemoEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "t_juv_background_report", schema = "apprehend")
public class TJuvBackgroundReportEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bg_report_srno")
    private Long bgReportSrno;

    @Column(name = "lang_cd", nullable = false)
    private Integer langCd;

    @Column(name = "ps_id")
    private Long psId;

    @Column(name = "fir_reg_num")
    private Long firRegNum;

    @Column(name = "apprehend_srno")
    private Long apprehendSrno;

    @Column(name = "juvenile_srno")
    private Long juvenileSrno;

    @Column(name = "age_vers")
    private Integer ageVers;

    @Column(name = "age_source_cd")
    private Integer ageSourceCd;

    @Column(name = "is_married")
    private Boolean isMarried;

    @Column(name = "spouse_name")
    private String spouseName;

    @Column(name = "spouse_age")
    private Integer spouseAge;

    @Column(name = "children_dtls")
    private String childrenDtls;

    @Column(name = "child_age")
    private Integer childAge;

    @Column(name = "religion_attitude")
    private String religionAttitude;

    @Column(name = "present_living_cond")
    private String presentLivingCond;

    @Column(name = "home_discipline_dtls")
    private String homeDisciplineDtls;

    @Column(name = "other_factors")
    private String otherFactors;

    @Column(name = "employment_dtls")
    private String employmentDtls;

    @Column(name = "income_util_manner")
    private String incomeUtilManner;

    @Column(name = "work_record")
    private String workRecord;

    @Column(name = "edu_qual_cd")
    private Integer eduQualCd;

    @Column(name = "family_leave_reason")
    private String familyLeaveReason;

    @Column(name = "last_school_stud")
    private String lastSchoolStud;

    @Column(name = "vocational_training")
    private String vocationalTraining;

    @Column(name = "att_toward_friend")
    private String attTowardFriend;

    @Column(name = "attitude_of_friend")
    private String attitudeOfFriend;

    @Column(name = "attitude_of_teacher")
    private String attitudeOfTeacher;

    @Column(name = "neighbour_observ")
    private String neighbourObserv;

    @Column(name = "onbserv_abt_neighbour")
    private String onbservAbtNeighbour;

    @Column(name = "is_victim_offence")
    private Boolean isVictimOffence;

    @Column(name = "victim_offence_dtls")
    private String victimOffenceDtls;

    @Column(name = "is_drug_peddler_used")
    private Boolean isDrugPeddlerUsed;

    @Column(name = "drug_peddler_used_dtls")
    private String drugPeddlerUsedDtls;

    @Column(name = "has_tend_run_away")
    private Boolean hasTendRunAway;

    @Column(name = "run_away_dtls")
    private String runAwayDtls;

    @Column(name = "is_earlier_apprehend")
    private Boolean isEarlierApprehend;

    @Column(name = "prev_apprehend_dtls")
    private String prevApprehendDtls;

    @Column(name = "prev_case_history")
    private String prevCaseHistory;

    @Column(name = "health_conditions")
    private String healthConditions;

    @Column(name = "mental_condition")
    private String mentalCondition;

    @Column(name = "any_othr_remarks")
    private String anyOthrRemarks;

    @Column(name = "emotional_factors")
    private String emotionalFactors;

    @Column(name = "intelligence_dtls")
    private String intelligenceDtls;

    @Column(name = "suggestive_problems")
    private String suggestiveProblems;

    @Column(name = "socio_eco_status")
    private String socioEcoStatus;

    @Column(name = "enquiry_analysis")
    private String enquiryAnalysis;

    @Column(name = "expert_opinion")
    private String expertOpinion;

    @Column(name = "rehab_recommend")
    private String rehabRecommend;

    @Column(name = "physical_cond_cd")
    private Integer physicalCondCd;

    @Column(name = "build_type_cd")
    private Integer bodyBuildTypeCd;

    @Column(name = "complexion_type_cd")
    private Integer bodyComplexionTypeCd;

    @Column(name = "height_from", length = 30)
    private String heightFromCm;

    @Column(name = "height_to", length = 30)
    private String heightToCm;

    @Column(name = "other_physical_desc")
    private String otherPhysicalDetails;

    @Column(name = "is_offence_heinous")
    private Boolean isOffenceHeinous;

    @Column(name = "is_juv_differ_able")
    private Boolean isJuvDifferAble;

    @Column(name = "differ_able_cd")
    private Integer differAbleCd;

    @Column(name = "jjb_name", length = 200)
    private String jjbName;

    @Column(name = "court_order_num", length = 30)
    private String courtOrderNum;

    @Column(name = "differ_able_others", length = 200)
    private String otherDifferAble;

    @Column(name = "age_src_others", length = 200)
    private String othrAgeSrc;

    @Column(name = "jjb_order_dt")
    private LocalDate jjbOrderDt;

    @Column(name = "alleged_offence_dtls")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<AllegedOffenceDomain>allegedOffenceDtls;

    @OneToMany(mappedBy = "juvBackgroundReport", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TJclBackgroundFilesEntity> bgFiles;

    @OneToMany(mappedBy = "juvBackgroundReport", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TJuvFamilyDtlsEntity> familyDtls;

    @OneToMany(mappedBy = "juvBackgroundReport", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TJuvPhyAbuseEntity> phyAbuse;

    @OneToMany(mappedBy = "juvBackgroundReport", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TJuvPhyFeatureEntity> physicalFeaturesList;

    @OneToMany(mappedBy = "juvBackgroundReport", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TJuvIdentityMarksEntity> identityMarkList;

    @OneToMany(mappedBy = "juvBackgroundReport", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TJuvDressEntity> dressTypeList;

    // Foreign Key Mapping
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apprehend_srno", referencedColumnName = "apprehend_srno", insertable = false, updatable = false)
    private TApprehendMemoEntity apprehendMemo;
}
