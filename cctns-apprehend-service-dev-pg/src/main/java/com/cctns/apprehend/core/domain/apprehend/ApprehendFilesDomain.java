package com.cctns.apprehend.core.domain.apprehend;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApprehendFilesDomain {

    private Long apprFileSrno;
    private Integer langCd;
    private Long juvenileFileSrnoMigr;
    private Long apprehendSrno;
    private Integer fileSrno;
    private Integer fileTypeCd;
    private Integer fileSubtypeCd;
    private String fileBelongsTo;
    private Long fileBelongsToSrno;
    private String fileName;
    private String filePath;
    private String fileDesc;
    private Integer fileSize;
    private String fileGuid;
    private String fileType;
    private String fileSubtype;
    private String contentType;
    private LocalDateTime fileUploadedOn;
    private String moduleName;

    private String previewUrl;
    private String imageBase64;

    private ApprehendMemoDomain apprehendMemo;



    private String recordStatus;
    private LocalDateTime recordCreatedOn;
    private Long recordCreatedBy;
    private LocalDateTime recordUpdatedOn;
    private Long recordUpdatedBy;

}

