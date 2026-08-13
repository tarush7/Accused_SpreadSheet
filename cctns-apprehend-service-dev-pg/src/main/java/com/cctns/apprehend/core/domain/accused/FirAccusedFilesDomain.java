package com.cctns.apprehend.core.domain.accused;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FirAccusedFilesDomain {
    private Long accusedFileSrno;
    private Integer langCd;
    private Long accusedFileSrnoMigr;
    private Long accusedSrnoMigr;
    private Long accusedVid;
    private Long crmDetailSrno;
    private Integer crmSeqNum;
    private Integer fileSrno;
    private Integer fileTypeCd;
    private Integer fileSubtypeCd;
    private String fileName;
    private String filePath;
    private String fileBelongsTo;
    private Long fileBelongsToSrno;
    private String fileGuid;
    private String fileDesc;
    private Integer fileSize;
    private String recordStatus;
    private LocalDateTime recordCreatedOn;
    private Long recordCreatedBy;
    private LocalDateTime recordUpdatedOn;
    private Long recordUpdatedBy;
    private String recordSyncFrom;
    private LocalDateTime recordSyncOn;

    private FirAccusedInfoDomain accused;
}
