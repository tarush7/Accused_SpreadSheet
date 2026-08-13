package com.cctns.apprehend.persistence.entity.disposal;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_juv_disposal_files", schema = "apprehend")
public class TJuvDisposalFilesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "disp_file_srno")
    private Long dispFileSrno;

    @Column(name = "lang_cd", nullable = false)
    private Integer langCd;

//    @Column(name = "juv_disposal_srno", nullable = false)
//    private Long juvDisposalSrno;

    @Column(name = "file_srno")
    private Integer fileSrno;

    @Column(name = "file_type_cd")
    private Integer fileTypeCd;

    @Column(name = "file_subtype_cd")
    private Integer fileSubtypeCd;

    @Column(name = "file_belongs_to", length = 40)
    private String fileBelongsTo;

    @Column(name = "file_belongs_to_srno")
    private Long fileBelongsToSrno;

    @Column(name = "file_name", length = 100)
    private String fileName;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "file_desc", length = 400)
    private String fileDesc;

    @Column(name = "file_size")
    private Integer fileSize;

    @Column(name = "file_guid", length = 120)
    private String fileGuid;

    @Column(name = "record_status", length = 1)
    private String recordStatus;

    @Column(name = "record_created_on")
    private LocalDateTime recordCreatedOn;

    @Column(name = "record_created_by")
    private Long recordCreatedBy;

    @Column(name = "record_updated_on")
    private LocalDateTime recordUpdatedOn;

    @Column(name = "record_updated_by")
    private Long recordUpdatedBy;

    @Column(name = "record_sync_from", length = 35)
    private String recordSyncFrom;

    @Column(name = "record_sync_on")
    private LocalDateTime recordSyncOn;

    // Foreign Key Mapping
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "juv_disposal_srno", referencedColumnName = "juv_disposal_srno")
    private TJuvDisposalEntity juvDisposal;
}
