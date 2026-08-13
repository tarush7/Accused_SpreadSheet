package com.cctns.apprehend.persistence.entity.accused;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "t_fir_accused_files", schema = "fir")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TFirAccusedFilesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "accused_file_srno")
    private Long accusedFileSrno;

    @Column(name = "lang_cd", nullable = false)
    private Integer langCd;

    @Column(name = "accused_file_srno_migr")
    private Long accusedFileSrnoMigr;

    @Column(name = "accused_srno_migr")
    private Long accusedSrnoMigr;

    @Column(name = "accused_vid",insertable = false,updatable = false)
    private Long accusedVid;

    @Column(name = "crm_detail_srno")
    private Long crmDetailSrno;

    @Column(name = "crm_seq_num")
    private Integer crmSeqNum;

    @Column(name = "file_srno")
    private Integer fileSrno;

    @Column(name = "file_type_cd")
    private Integer fileTypeCd;

    @Column(name = "file_subtype_cd")
    private Integer fileSubtypeCd;

    @Column(name = "file_name", length = 100)
    private String fileName;

    @Column(name = "file_path", columnDefinition = "text")
    private String filePath;

    @Column(name = "file_belongs_to", length = 50)
    private String fileBelongsTo;

    @Column(name = "file_belongs_to_srno")
    private Long fileBelongsToSrno;

    @Column(name = "file_guid", length = 100)
    private String fileGuid;

    @Column(name = "file_desc", length = 400)
    private String fileDesc;

    @Column(name = "file_size")
    private Integer fileSize;

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

    @ManyToOne
    @JoinColumn(name = "accused_vid")
    private TFirAccusedInfoEntity accused;

}