package com.cctns.apprehend.core.domain.accused;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SocialMediaJsonDomain {
    private String id;
    private Integer socialMediaTypeCd;
    private String socialMediaType;
    private String socialMediaUrl;
}
