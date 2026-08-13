package com.cctns.apprehend.persistence.entity.accused;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SocialMediaJsonEntity {
    private String id;
    private Integer socialMediaTypeCd;
    private String socialMediaType;
    private String socialMediaUrl;
}
