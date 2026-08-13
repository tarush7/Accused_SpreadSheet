package com.cctns.apprehend.persistence.entity.accused;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "t_fir_accused_phy_feature", schema = "fir")
@Getter
@Setter
public class TFirAccusedPhysicalFeatureEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "acc_phy_feat_srno")
    private Long accPhyFeatSrno;

    @Column(name = "lang_cd", nullable = false)
    private Integer langCd;

    @Column(name = "accused_vid",insertable = false,updatable = false)
    private Long accusedVid;

    @Column(name = "phy_feat_category_cd")
    private Integer phyFeatCategoryCd;

    @Column(name = "phy_feature_maj_cd")
    private Integer phyFeatureMajCd;

    @Column(name = "phy_feature_min_cd")
    private Integer phyFeatureMinCd;

//    @Column(name = "othr_dress_type", length = 100)
//    private String othrDressType;

    @Column(name = "phy_feat_category", length = 100)
    private String phyFeatCategory;

    @Column(name = "phy_feature_major", length = 100)
    private String phyFeatureMajor;

    @Column(name = "phy_feature_minor", length = 100)
    private String phtFeatureMinor;

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

    @ManyToOne
    @JoinColumn(name = "accused_vid")
    private TFirAccusedInfoEntity accused;
}
