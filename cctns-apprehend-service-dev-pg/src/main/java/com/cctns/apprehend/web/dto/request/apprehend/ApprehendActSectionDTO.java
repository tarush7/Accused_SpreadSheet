package com.cctns.apprehend.web.dto.request.apprehend;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApprehendActSectionDTO  {

    @JsonProperty("id")
   private String apprehendActSrno;
   // private Integer langCd;
 //   private Long apprehendSrno;
    private Integer actCd;
    private String sectionCd;
    private String section;
    private String actShort;
    private String actLong;
    private String sectionDesc;

}
