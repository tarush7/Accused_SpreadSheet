package com.cctns.apprehend.web.dto.request.socialBackground;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AllegedOffenceDTO {
    private String id;
    private Integer allegedOffenceCd;
    private String allegedOffence;
    private String allegedOffenceRemarks;
}