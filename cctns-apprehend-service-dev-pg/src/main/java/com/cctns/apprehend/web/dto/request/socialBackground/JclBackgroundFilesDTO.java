package com.cctns.apprehend.web.dto.request.socialBackground;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JclBackgroundFilesDTO{

    private Long fileUploadSrno;
    private Integer langCd;
    private Long bgReportSrno;
    private Integer fileSrno;
    private Integer fileTypeCd;
    private Integer fileSubtypeCd;
    private String fileType;
    private String fileSubtype;
    private String fileName;
    private String filePath;
    private String fileBelongsTo;
    private Long fileBelongsToSrno;
    private String fileGuid;
    private String fileDesc;
    private Integer fileSize;
    private String contentType;
  //  @JsonProperty("module")
    private String moduleName;
    private LocalDateTime fileUploadedOn;

}
