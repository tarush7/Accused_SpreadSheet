package com.cctns.apprehend.core.domain;

import com.cctns.apprehend.core.domain.acts.ActSectionDomain;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JuvenileProfileDomain {

    private String firTypeFlag;
    private String ciclGdNum;
    private Long firRegNum;
    private String firDisplayNum;
    private String firRegDt;
    private String ioName;
  //  private String ciclGdNum;
    private List<AccusedListDomain> accList;
    private List<ActSectionDomain> actSectionList;

    private String gridFlag;
}
