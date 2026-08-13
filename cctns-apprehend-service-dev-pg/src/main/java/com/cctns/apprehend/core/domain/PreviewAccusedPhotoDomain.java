package com.cctns.apprehend.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PreviewAccusedPhotoDomain {

    private String fileGuid;
    private String filePath;

    //For identification  :
    private String belongsTo;
    private Long belongsToSrno;
    private Long key;
    private String keyValue;

    //Response : The pre-signed url
    private String previewUrl;
    private String contentType;
    private String imageBase64;
}



