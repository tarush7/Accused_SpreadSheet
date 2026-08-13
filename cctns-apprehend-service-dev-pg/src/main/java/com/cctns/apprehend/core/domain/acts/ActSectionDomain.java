package com.cctns.apprehend.core.domain.acts;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActSectionDomain {

    private String id;
    private String gdNum;
    private Integer actCd;
    private String actLong;
    private String actShort;
    private String sectionCd;
    private String section;
    private String sectionDesc;

}
