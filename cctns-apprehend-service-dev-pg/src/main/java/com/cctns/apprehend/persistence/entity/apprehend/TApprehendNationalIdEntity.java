package com.cctns.apprehend.persistence.entity.apprehend;
import com.cctns.apprehend.persistence.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "t_apprehend_national_id", schema = "apprehend")
public class TApprehendNationalIdEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "national_id_srno")
    private Long nationalIdSrno;

    @Column(name = "lang_cd", nullable = false)
    private Integer langCd;

    @Column(name = "national_id_srno_migr")
    private Long nationalIdSrnoMigr;

    @Column(name = "apprehend_srno", nullable = false, insertable = true, updatable = false)
    private Long apprehendSrno;

    @Column(name = "nationality_id_type_cd")
    private Integer nationalIdTypeCd;

    @Column(name = "national_id_num")
    private String nationalIdNum;

    @Column(name = "passport_issue_dt")
    private LocalDate passportIssueDt;

    @Column(name = "passport_issue_plc")
    private String passportIssuePlc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apprehend_srno",referencedColumnName = "apprehend_srno", insertable = false, updatable = false)
    private TApprehendMemoEntity apprehendMemo;

}
