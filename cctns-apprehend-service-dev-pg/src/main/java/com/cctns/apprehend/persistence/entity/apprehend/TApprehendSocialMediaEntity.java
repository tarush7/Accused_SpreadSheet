package com.cctns.apprehend.persistence.entity.apprehend;

import com.cctns.apprehend.persistence.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "t_apprehend_socialmedia", schema = "apprehend")
public class TApprehendSocialMediaEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appr_soc_med_srno")
    private Long apprSocMedSrno;

    @Column(name = "lang_cd", nullable = false)
    private Integer langCd;

    @Column(name = "socialmedia_id_srno_migr")
    private Long socialmediaIdSrnoMigr;

    @Column(name = "apprehend_srno", nullable = false, insertable = false, updatable = false)
    private Long apprehendSrno;

    @Column(name = "socialmedia_type_cd")
    private Integer socialmediaTypeCd;

    @Column(name = "socialmedia_url")
    private String socialmediaUrl;

}
