package com.cctns.apprehend.core.domain.apprehend;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ApprehendBankDtlsDomain{

    private Long apprBankSrno;
    private Integer langCd;
    private Long bankcardIdSrnoMigr;
    private Long apprehendSrno;
    private Integer bankCd;
    private Integer accountTypeCd;
    private String accountNum;
    private String bankOtherInfo;

    private String recordStatus;
    private LocalDateTime recordCreatedOn;
    private Long recordCreatedBy;
    private LocalDateTime recordUpdatedOn;
    private Long recordUpdatedBy;
    private String recordSyncFrom;
    private LocalDateTime recordSyncOn;

}

