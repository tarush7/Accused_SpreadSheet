package com.cctns.apprehend.persistence.entity.socialbg;

import com.cctns.apprehend.persistence.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "t_jcl_background_files", schema = "apprehend")
public class TJclBackgroundFilesEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_upload_srno")
    private Long id;

    @Column(name = "lang_cd", nullable = false)
    private Integer langCd;

//    @Column(name = "bg_report_srno")
//    private Long bgReportSrno;

    @Column(name = "file_upload_srno_migr")
    private Long fileUploadSrnoMigr;

    @Column(name = "file_srno")
    private Integer fileSrno;

    @Column(name = "file_type_cd")
    private Integer fileTypeCd;

    @Column(name = "file_subtype_cd")
    private Integer fileSubtypeCd;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "file_belongs_to")
    private String fileBelongsTo;

    @Column(name = "file_belongs_to_srno")
    private Long fileBelongsToSrno;

    @Column(name = "file_guid")
    private String fileGuid;

    @Column(name = "file_desc")
    private String fileDesc;

    @Column(name = "file_size")
    private Integer fileSize;

    // Foreign Key Mapping
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "bg_report_srno",
            referencedColumnName = "bg_report_srno"
//            insertable = false,
//            updatable = false
    )
    private TJuvBackgroundReportEntity juvBackgroundReport;
}
