package com.cctns.apprehend.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocialBackgroundResponseDTO {
    private Long bgReportSrno;
    private String bgDisplay;
}
