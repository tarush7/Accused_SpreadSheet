package com.cctns.apprehend.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActSectionDTO {
    private String id;
    private Integer actCd;
    private String actLong;
    private String actShort;
    private String sectionCd;
    private String section;
    private String sectionDesc;
}
