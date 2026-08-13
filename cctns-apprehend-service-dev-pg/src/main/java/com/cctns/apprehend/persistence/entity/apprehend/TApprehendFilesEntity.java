package com.cctns.apprehend.persistence.entity.apprehend;

import com.cctns.apprehend.persistence.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "t_apprehend_files", schema = "apprehend")
public class TApprehendFilesEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appr_file_srno")
    private Long apprFileSrno;

    @Column(name = "lang_cd", nullable = false)
    private Integer langCd;

    @Column(name = "juvenile_file_srno_migr")
    private Long juvenileFileSrnoMigr;

    @Column(name = "apprehend_srno", nullable = false, insertable = true, updatable = false)
    private Long apprehendSrno;

    @Column(name = "file_srno")
    private Integer fileSrno;

    @Column(name = "file_type_cd")
    private Integer fileTypeCd;

    @Column(name = "file_subtype_cd")
    private Integer fileSubtypeCd;

    @Column(name = "file_belongs_to")
    private String fileBelongsTo;

    @Column(name = "file_belongs_to_srno")
    private Long fileBelongsToSrno;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "file_desc")
    private String fileDesc;

    @Column(name = "file_size")
    private Integer fileSize;

    @Column(name = "file_guid")
    private String fileGuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apprehend_srno",insertable = false, updatable = false)
    private TApprehendMemoEntity apprehendMemo;

}
