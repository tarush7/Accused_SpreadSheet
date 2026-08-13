package com.cctns.apprehend.core.domain.socialbg;

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
public class PhysicalFeatureDescDomain {

    private Integer bodyBuildTypeCd;
    private Integer bodyComplexionTypeCd;
    private String bodyBuildType;
    private String bodyComplexionType;
    private String otherPhysicalDetails;
    private String heightFromCm;
    private String heightToCm;
    private List<JuvDressDomain> dressTypeList;
    private List<JuvIdentityMarksDomain> identityMarkList;
    private List<JuvPhyFeatureDomain> physicalFeaturesList;

}
