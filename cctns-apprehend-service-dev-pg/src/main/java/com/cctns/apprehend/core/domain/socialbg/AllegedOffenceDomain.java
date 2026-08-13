package com.cctns.apprehend.core.domain.socialbg;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AllegedOffenceDomain {
    private String id;
    private Integer allegedOffenceCd;
    private String allegedOffence;
    private String allegedOffenceRemarks;
}
