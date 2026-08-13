package com.cctns.apprehend.web.dto.request.apprehend;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApprehendNationalIdDTO {
    @JsonProperty("id")
    private String nationalIdSrno;
    private Integer nationalIdTypeCd;
    private String nationalIdType;
    private String nationalIdNum;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate passportIssueDt;
    private String passportIssuePlc;
}
