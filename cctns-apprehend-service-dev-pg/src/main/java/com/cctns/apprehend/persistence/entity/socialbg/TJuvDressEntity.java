
package com.cctns.apprehend.persistence.entity.socialbg;

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

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "t_juv_dress", schema = "apprehend")
public class TJuvDressEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "juv_dress_srno")
    private Long id;

    @Column(name = "lang_cd")
    private Integer langCd;

//    @Column(name = "bg_report_srno")
//    private Long bgReportSrno;

    @Column(name = "fir_reg_num")
    private Long firRegNum;

//    @Column(name = "reg_type_cd")
//    private Integer regTypeCd;

//    @Column(name = "crm_detail_srno")
//    private Long crmDetailSrno;
//
//    @Column(name = "crm_seq_num")
//    private Integer crmSeqNum;

    @Column(name = "dress_for_cd")
    private Integer dressForCd;

    @Column(name = "dress_type_cd")
    private Integer dressTypeCd;

    @Column(name = "dress_type")
    private String dressType;

    @Column(name = "dress_subtype_cd")
    private Integer dressSubtypeCd;

    @Column(name = "dress_subtype")
    private String dressSubtype;

    @Column(name = "othr_dress_dtls")
    private String othrDressDtls;

    @Column(name = "record_status")
    private String recordStatus;

    @Column(name = "record_created_on")
    private LocalDateTime recordCreatedOn;

    @Column(name = "record_created_by")
    private Long recordCreatedBy;

    @Column(name = "record_updated_on")
    private LocalDateTime recordUpdatedOn;

    @Column(name = "record_updated_by")
    private Long recordUpdatedBy;

    @Column(name = "record_sync_from")
    private String recordSyncFrom;

    @Column(name = "record_sync_on")
    private LocalDateTime recordSyncOn;

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