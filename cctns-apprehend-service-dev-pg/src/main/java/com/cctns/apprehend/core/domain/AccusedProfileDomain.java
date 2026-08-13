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
public class AccusedProfileDomain {
    private Long firRegNum;
    private List<AccusedListDomain> accList;
    private List<ActSectionDomain> actSectionList;

    private String gridFlag;
}
