package com.cctns.apprehend.web.dto.request.apprehend;

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
public class ApprehendFilesDTO {

    @JsonProperty("id")
    private Long apprFileSrno;
    private Integer langCd;
    private Long apprehendSrno;
    private Integer fileSrno;
    private Integer fileTypeCd;
    private Integer fileSubtypeCd;
    private String fileType;
    private String fileSubtype;
    private String fileBelongsTo;
    private Long fileBelongsToSrno;
    private String fileName;
    private String filePath;
    private String fileDesc;
    private Integer fileSize;
    private String fileGuid;
    private String contentType;
 //   @JsonProperty("module")
    private String moduleName;
    private LocalDateTime fileUploadedOn;

 //   private ApprehendMemoDTO apprehendMemo;

}

