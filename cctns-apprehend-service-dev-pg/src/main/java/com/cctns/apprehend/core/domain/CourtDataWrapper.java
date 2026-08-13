package com.cctns.apprehend.core.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourtDataWrapper {

    private Long courtComplexCd;
    private String courtComplexName;
    private String establishmentName;

}

