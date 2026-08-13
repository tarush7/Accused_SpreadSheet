package com.cctns.apprehend.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccusedProfileDTO {
    private Long firRegNum;
    private List<AccusedListDTO> accList;
    private List<ActSectionDTO> actSectionList;
    //   private List<WitnessDomain> witnessList;

    private String gridFlag;
}
