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
public class FirAccusedIdMarksDomain {

    private Long firAccIdMarksSrno;
    private Integer langCd;
    private Long accusedVid;
    private Integer idMarksTypeCd;
    private Integer bodyPartLocCd;
    private Integer tattooTypeCd;
    private String tattooMarkDesc;
    private String recordStatus;
    private LocalDateTime recordCreatedOn;
    private Long recordCreatedBy;
    private LocalDateTime recordUpdatedOn;
    private Long recordUpdatedBy;

    private FirAccusedInfoDomain accused;
}
