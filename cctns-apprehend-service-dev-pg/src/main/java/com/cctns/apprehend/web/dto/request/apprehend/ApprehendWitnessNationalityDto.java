package com.cctns.apprehend.web.dto.request.apprehend;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApprehendWitnessNationalityDto {
    @JsonProperty("id")
    private String apprWitnNatSrno;
    private Integer langCd;
    private Long apprWitnsSrno;
    private Long apprWitnsSrnoMigr;
    private Integer nationalIdTypeCd;
    private String nationalIdType;
    private String nationalIdNum;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate passportIssueDt;
    private String passportIssuePlc;
//    private String recordStatus;
//    private LocalDateTime recordCreatedOn;
//    private Long recordCreatedBy;
//    private LocalDateTime recordUpdatedOn;
//    private Long recordUpdatedBy;
//    private String recordSyncFrom;
//    private LocalDateTime recordSyncOn;
}