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
@Table(name = "t_juv_identity_marks", schema = "apprehend")
@Getter
@Setter
public class TJuvIdentityMarksEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "juv_identity_srno")
    private Long id;

    @Column(name = "lang_cd", nullable = false)
    private Integer langCd;

//    @Column(name = "appr_identity_srno_migr")
//    private Long apprIdentitySrnoMigr;

//    @Column(name = "bg_report_srno", nullable = false)
//    private Long bgReportSrno;

    @Column(name = "id_marks_type_cd")
    private Integer idMarksTypeCd;

    @Column(name = "body_part_loc_cd")
    private Integer bodyPartLocCd;

    @Column(name = "tattoo_type_cd")
    private Integer tattooTypeCd;

    @Column(name = "tattoo_mark_desc", length = 1000)
    private String tattooMarkDesc;

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