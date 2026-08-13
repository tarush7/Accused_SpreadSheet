package com.cctns.apprehend.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FirAccusedInfoUpdateDomain extends CommonParamsDomain {
    private Long accusedVid;
    private String recordStatus;
    private LocalDateTime recordUpdatedOn;
    private Long recordUpdatedBy;
}
