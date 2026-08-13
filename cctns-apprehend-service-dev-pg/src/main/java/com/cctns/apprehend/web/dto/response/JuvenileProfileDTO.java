package com.cctns.apprehend.web.dto.response;

import com.cctns.apprehend.core.domain.AccusedListDomain;
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
public class JuvenileProfileDTO {

    private Long firRegNum;
    private String ciclGdNum;
    private String firTypeFlag;
//    private String firDisplayNum;
//    private String firRegDt;
//    private String ioName;
    private List<AccusedListDTO> accList;
    private List<ActSectionDTO> actSectionList;

    private String gridFlag;
}
