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

@Getter
@Setter
@Entity
@Table(name = "t_juv_phy_abuse", schema = "apprehend")
public class TJuvPhyAbuseEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "juv_abuse_srno")
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

    @Column(name = "abuse_type_cd")
    private Integer abuseTypeCd;

    @Column(name = "abuse_remarks", length = 250)
    private String abuseRemarks;


    // Foreign Key Mapping
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "bg_report_srno",
            referencedColumnName = "bg_report_srno"
    )
    private TJuvBackgroundReportEntity juvBackgroundReport;
}
