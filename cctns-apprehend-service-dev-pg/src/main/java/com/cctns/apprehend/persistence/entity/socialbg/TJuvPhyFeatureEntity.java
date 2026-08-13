package com.cctns.apprehend.persistence.entity.socialbg;

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

@Entity
@Table(name = "t_juv_phy_feature", schema = "apprehend")
@Getter
@Setter
public class TJuvPhyFeatureEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "juv_phy_feat_srno")
    private Long id;

    @Column(name = "lang_cd", nullable = false)
    private Integer langCd;

    @Column(name = "juvenile_srno")
    private Long juvenileSrno;

    @Column(name = "juvenile_srno_migr")
    private Long juvenileSrnoMigr;

    @Column(name = "phy_feat_category_cd")
    private Integer phyFeatCategoryCd;

    @Column(name = "phy_feature_maj_cd")
    private Integer phyFeatureMajCd;

    @Column(name = "phy_feature_min_cd")
    private Integer phyFeatureMinCd;

    @Column(name = "phy_feat_category", length = 100)
    private String phyFeatCategory;

    @Column(name = "phy_feature_major", length = 100)
    private String phyFeatureMaj;

    @Column(name = "phy_feature_minor", length = 100)
    private String phyFeatureMin;

    @Column(name = "juvenile_vid")
    private Integer juvenileVid;

    /**
     * Foreign Key Mapping
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "bg_report_srno",
            referencedColumnName = "bg_report_srno"
    )
    private TJuvBackgroundReportEntity juvBackgroundReport;
}
