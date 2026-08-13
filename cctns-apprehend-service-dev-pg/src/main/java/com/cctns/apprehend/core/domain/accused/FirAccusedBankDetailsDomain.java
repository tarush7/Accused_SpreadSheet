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
public class FirAccusedBankDetailsDomain {

    private Long bankcardIdSrno;
    private Integer langCd;
    private Long accusedVid;
    private Integer bankCd;
    private Integer accountTypeCd;
    private String accountNum;
    private String bankotherInfo;
    private String recordStatus;
    private LocalDateTime recordCreatedOn;
    private Long recordCreatedBy;
    private LocalDateTime recordUpdatedOn;
    private Long recordUpdatedBy;

    private FirAccusedInfoDomain accused;
}
