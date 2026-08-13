package com.cctns.apprehend.core.domain;

import lombok.Data;

@Data
public class SocialMediaDomain {
    private String id;
    private Integer socialMediaTypeCd;
    private String socialMediaType; //view
    private String socialMediaUrl;
}
