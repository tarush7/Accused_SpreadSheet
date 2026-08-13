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
public class FirMultiAccusedDomain {
    private Long firMultiAccSrno;
    private Integer langCd;
    private Long crmMultiAccSrnoMigr;
    private Long firRegNum;
    private Long accusedVid;
    private Long accusedSrno;
    private Long existFirRegNum;
    private Long existAccusedSrno;
    private Long existAccusedUniqNum;
    private String fullName;
    private String recordStatus;
    private LocalDateTime recordCreatedOn;
    private Long recordCreatedBy;
    private LocalDateTime recordUpdatedOn;
    private Long recordUpdatedBy;
}