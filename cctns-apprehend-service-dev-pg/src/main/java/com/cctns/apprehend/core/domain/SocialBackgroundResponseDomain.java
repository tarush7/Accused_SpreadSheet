package com.cctns.apprehend.core.domain;

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
public class SocialBackgroundResponseDomain {
    private Long bgReportSrno;
    private String bgDisplay;
}
