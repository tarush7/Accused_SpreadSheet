package com.cctns.apprehend.web.dto.request.socialBackground;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PhysicalFeatureDescDTO {

//    @JsonSerialize(using = ToStringSerializer.class)
//    private Long id;

    private Integer bodyBuildTypeCd;
    private Integer bodyComplexionTypeCd;
    private String bodyBuildType;
    private String bodyComplexionType;
    private String otherPhysicalDetails;
    private String heightFromCm;
    private String heightToCm;
    private List<JuvDressDTO> dressTypeList;
    private List<JuvIdentityMarksDTO> identityMarkList;
    @JsonProperty("physicalFeatureList")
    private List<JuvPhyFeatureDTO> physicalFeaturesList;

}
