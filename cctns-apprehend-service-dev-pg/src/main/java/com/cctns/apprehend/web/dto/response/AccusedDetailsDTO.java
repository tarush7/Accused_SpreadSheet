package com.cctns.apprehend.web.dto.response;

import com.cctns.apprehend.core.domain.AccusedNationalIdDomain;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccusedDetailsDTO {
    private Long apprehendSrno;
    private Long accusedSrno;
    private Long accusedVid;
    private String firstName;
    private String middleName;
    private String lastName;
    private String firstNameEng;
    private String middleNameEng;
    private String lastNameEng;
    private Integer relationTypeCd;
    private String relativeName;
    private String relativeNameEng;
    private Long relMobileNum;
    private Integer genderCd;
    private Integer ageTypeCd;
    private Integer ageYrs;
    private Integer ageMonths;
    private Integer yob;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime dob;
    private Integer ageFromYrs;
    private Integer ageToYrs;
    private Integer nationalityCd;
    private Integer categoryCd;
    private Integer religionCd;
    private Integer occupationCd;
    private String injuryDetails;

    @JsonProperty("addressGrid")
    private List<AccusedAddressDTO> accusedAddress;
    private List<AccusedNationalIdDTO> idList;
}
