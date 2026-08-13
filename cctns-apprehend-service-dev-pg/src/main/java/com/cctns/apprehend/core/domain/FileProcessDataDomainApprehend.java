package com.cctns.apprehend.core.domain;

import com.cctns.apprehend.core.domain.apprehend.ApprehendFilesDomain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileProcessDataDomainApprehend {

  private List<ApprehendFilesDomain> documents;
  private String loginParams;
  private String moduleNumber;
  private String fileBelongsTo;
  private Long fileBelongsToSrno;
  private Integer langCd;
  private Long staffId;
}