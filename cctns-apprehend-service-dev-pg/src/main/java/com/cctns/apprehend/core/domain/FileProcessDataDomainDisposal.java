package com.cctns.apprehend.core.domain;

import com.cctns.apprehend.core.domain.disposal.JuvDisposalFilesDomain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileProcessDataDomainDisposal {
    private List<JuvDisposalFilesDomain> documents;
    private String loginParams;
    private String moduleNumber;
    private String fileBelongsTo;
    private Long fileBelongsToSrno;
    private Integer langCd;
    private Long staffId;
}
