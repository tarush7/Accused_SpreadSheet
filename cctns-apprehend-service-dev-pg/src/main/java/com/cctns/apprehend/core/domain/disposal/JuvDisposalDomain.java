package com.cctns.apprehend.core.domain.disposal;

import com.cctns.apprehend.core.domain.CommonParamsDomain;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JuvDisposalDomain extends CommonParamsDomain {

    private Long juvDisposalSrno;
    private Integer langCd;
    private Long firRegNum;
    private Long apprehendSrno;
    private Long juvenileSrno;
    private String jjbName;
    private String jjbAddress;
    private String jjbMagistrateName;
    private String finalOrderDtls;
    private LocalDate finalOrderDt;
    private String finalOrderNum;
    private String jjbEstblName;

    private String recordStatus;
    private LocalDateTime recordCreatedOn;
    private Long recordCreatedBy;
    private LocalDateTime recordUpdatedOn;
    private Long recordUpdatedBy;
    private String recordSyncFrom;
    private LocalDateTime recordSyncOn;

    private List<JuvDisposalFilesDomain> fileList;
}