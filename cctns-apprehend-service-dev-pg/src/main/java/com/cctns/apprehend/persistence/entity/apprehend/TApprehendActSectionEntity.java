package com.cctns.apprehend.persistence.entity.apprehend;

import com.cctns.apprehend.persistence.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "t_apprehend_act_section", schema = "apprehend")
public class TApprehendActSectionEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "apprehend_act_srno")
    private Long apprehendActSrno;

    @Column(name = "lang_cd", nullable = false)
    private Integer langCd;

    @Column(name = "apprehend_act_srno_migr")
    private Long apprehendActSrnoMigr;

    @Column(name = "apprehend_srno", nullable = false, insertable = true, updatable = false)
    private Long apprehendSrno;

    @Column(name = "act_cd")
    private Integer actCd;

    @Column(name = "section_cd")
    private String sectionCd;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "apprehend_srno")
//    private TApprehendMemoEntity apprehendMemo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apprehend_srno",referencedColumnName = "apprehend_srno", insertable = false, updatable = false)
    private TApprehendMemoEntity apprehendMemo;


}
