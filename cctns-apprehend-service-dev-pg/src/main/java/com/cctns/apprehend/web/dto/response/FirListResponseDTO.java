package com.cctns.apprehend.web.dto.response;

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
public class FirListResponseDTO {
    private String regDt;
    private String complainantName;
    private String juvenileName;
    private String relationType;
    private String relativeName;
    private String firDisplayNum;
    private String firRegNum;
    private String apprehendSrno;
    private String ciclGdNum;
    private String ciclGdDt;
    private String ciclGdDisplayNum;

    private List<ActSectionDTO> gdActSectionList;

}
