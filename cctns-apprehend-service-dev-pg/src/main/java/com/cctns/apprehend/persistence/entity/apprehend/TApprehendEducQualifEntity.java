package com.cctns.apprehend.persistence.entity.apprehend;

import com.cctns.apprehend.persistence.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "t_apprehend_educ_qualif", schema = "apprehend")
public class TApprehendEducQualifEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appr_edu_qual_srno")
    private Long apprEduQualSrno;

    @Column(name = "lang_cd", nullable = false)
    private Integer langCd;

    @Column(name = "appr_edu_qual_srno_migr")
    private Long apprEduQualSrnoMigr;

    @Column(name = "apprehend_srno", nullable = false, insertable = false, updatable = false)
    private Long apprehendSrno;

    @Column(name = "education_qual_cd")
    private Integer educationQualCd;

}

