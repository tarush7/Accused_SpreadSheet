package com.cctns.apprehend.persistence.entity.apprehend;

import com.cctns.apprehend.persistence.entity.BaseEntity;
import com.cctns.apprehend.persistence.entity.accused.AliasJsonEntity;
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

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "t_apprehend_witness", schema = "apprehend")
public class TApprehendWitnessEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appr_witns_srno")
    private Long apprWitnsSrno;

    @Column(name = "lang_cd", nullable = false)
    private Integer langCd;

    @Column(name = "appr_witns_srno_migr")
    private Long apprWitnsSrnoMigr;

    @Column(name = "apprehend_srno", nullable = false, insertable = true, updatable = false)
    private Long apprehendSrno;

    @Column(name = "person_code_migr")
    private Long personCodeMigr;

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

//    @Column(name = "witn_alias")
//    private String witnAlias;

//    @Column(name = "witn_alias_eng")
//    private String witnAliasEng;

    @Column(name = "relation_type_cd")
    private Integer relationTypeCd;

    @Column(name = "relative_name")
    private String relativeName;

    @Column(name = "age_type_cd")
    private Integer ageTypeCd;

    @Column(name = "age_yrs")
    private Integer ageYrs;

    @Column(name = "age_mnths")
    private Integer ageMnths;

    @Column(name = "yob")
    private Integer yob;

    @Column(name = "dob")
    private LocalDateTime dob;

    @Column(name = "age_frm_yrs")
    private Integer ageFrmYrs;

    @Column(name = "age_to_yrs")
    private Integer ageToYrs;

    @Column(name = "mobile_num")
    private Long mobileNum;

    @Column(name = "telephone")
    private String telephone;

    @Column(name = "email")
    private String email;

    @Column(name = "nationality_cd")
    private Integer nationalityCd;

    @Column(name = "occupation_cd")
    private Integer occupationCd;

    @Column(name = "gender_cd")
    private Integer genderCd;

    @Column(name = "marital_status_cd")
    private Integer maritalStatusCd;

    @Column(name = "other_occupation")
    private String otherOccupation;

    @Column(name = "witn_evid_tender_cd")
    private Integer witnEvidTenderCd;

    @Column(name = "witness_statement")
    private String witnessStatement;

    @Column(name = "is_witn_mobile_verf")
    private Boolean isWitnMobileVerf;

    @Column(name = "witn_category_cd")
    private Integer witnCategoryCd;

    @Column(name = "alias",columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<AliasJsonEntity> aliases;

    @OneToMany(mappedBy = "apprehendWitness", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TApprehendWitnessAddrEntity> witnessAddress;

    @OneToMany(mappedBy = "apprehendWitness", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TApprehendWitnessNationalityEntity> idList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apprehend_srno",referencedColumnName = "apprehend_srno", insertable = false, updatable = false)
    private TApprehendMemoEntity apprehendMemo;

}

