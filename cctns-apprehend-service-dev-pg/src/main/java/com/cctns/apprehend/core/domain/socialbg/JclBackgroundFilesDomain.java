package com.cctns.apprehend.core.domain.socialbg;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JclBackgroundFilesDomain {

    private Long id;
    private Integer langCd;
    private Long bgReportSrno;
    private Long fileUploadSrnoMigr;
    private Integer fileSrno;
    private Integer fileTypeCd;
    private Integer fileSubtypeCd;
    private String fileType;
    private String fileSubtype;
    private String fileName;
    private String filePath;
    private String fileBelongsTo;
    private Long fileBelongsToSrno;
    private String fileGuid;
    private String fileDesc;
    private Integer fileSize;

    private String fileTypeValue;
    private String fileSubTypeValue;
    private String contentType;
    private LocalDateTime fileUploadedOn;
    private String moduleName;
    private String previewUrl;
    private String imageBase64;

    private String recordStatus;
    private LocalDateTime recordCreatedOn;
    private Long recordCreatedBy;
    private LocalDateTime recordUpdatedOn;
    private Long recordUpdatedBy;
    private String recordSyncFrom;
    private LocalDateTime recordSyncOn;

    private JuvBackgroundReportDomain juvBackgroundReport;

}
