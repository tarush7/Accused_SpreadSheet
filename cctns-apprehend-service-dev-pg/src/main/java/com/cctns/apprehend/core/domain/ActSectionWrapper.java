package com.cctns.apprehend.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ActSectionWrapper {
    private String section;
    private String sectionCd;
    private String sectionDesc;
    private String actShort;
    private String actLong;
}
