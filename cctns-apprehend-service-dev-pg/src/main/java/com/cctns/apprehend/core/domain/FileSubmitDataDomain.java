package com.cctns.apprehend.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FileSubmitDataDomain {


    //These are the fields used by the other modules to identify unique files :
    private Long key;
    private String keyValue;
    private String fileBelongsTo;
    private Long fileBelongsToSrno;

    //Fields For Mapping With Frontend Response Of File Upload : (To Prevent Fail On Unknown Properties)
    private Integer fileTypeCd;
    private Integer fileSubtypeCd;

    //Mandatory Fields :
    private String moduleNumber;
  //  @JsonProperty("module")
    private String moduleName;
    private String filePath;
    private String fileGuid;
    private String fileName;
    private Long fileSize;
    private String fileDesc;
    private String contentType;
    private LocalDateTime fileUploadedOn;

    //These fields are added to identify the re-registration files :
    private Boolean isReregistered;
   // private CourtDisposalDomain courtDisposal;
}
