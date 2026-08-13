package com.cctns.apprehend.persistence.entity.socialbg;

import com.cctns.apprehend.persistence.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "t_juv_family_dtls", schema = "apprehend")
public class TJuvFamilyDtlsEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "juv_family_srno")
    private Long id;

    @Column(name = "lang_cd", nullable = false)
    private Integer langCd;

    @Column(name = "juvenile_vid")
    private Integer juvenileVid;

    @Column(name = "juvenile_srno")
    private Long juvenileSrno;

    @Column(name = "apprehend_srno")
    private Long apprehendSrno;

//    @Column(name = "bg_report_srno")
//    private Long bgReportSrno;

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

    @Column(name = "age_type_cd")
    private Integer ageTypeCd;

    @Column(name = "age_yrs")
    private Integer ageYrs;

    @Column(name = "age_months")
    private Integer ageMonths;

    @Column(name = "yob")
    private Integer yob;

    @Column(name = "dob")
    private LocalDate dob;

    @Column(name = "age_from_yrs")
    private Integer ageFromYrs;

    @Column(name = "age_to_yrs")
    private Integer ageToYrs;

    @Column(name = "gender_cd")
    private Integer genderCd;

    @Column(name = "edu_qual_cd")
    private Integer eduQualCd;

    @Column(name = "occupation_cd")
    private Integer occupationCd;

    @Column(name = "health_status")
    private String healthStatus;

    @Column(name = "mental_health_hist")
    private String mentalHealthHist;

    @Column(name = "addiction_cd")
    private Integer addictionCd;

    @Column(name = "income_type_cd")
    private Integer incomeCd;

    @Column(name = "crime_nature")
    private String crimeNature;

    @Column(name = "legal_status")
    private String legalStatus;

    @Column(name = "arrest_dtls")
    private String arrestDtls;

    @Column(name = "punishment_awarded")
    private String punishmentAwarded;

    @Column(name = "confine_period")
    private String confinePeriod;

    // Foreign Key Mapping
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "bg_report_srno",
            referencedColumnName = "bg_report_srno"
//            insertable = false,
//            updatable = false
    )
    private TJuvBackgroundReportEntity juvBackgroundReport;
}

