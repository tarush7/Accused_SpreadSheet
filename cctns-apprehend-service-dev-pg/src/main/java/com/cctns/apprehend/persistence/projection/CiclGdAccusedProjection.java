package com.cctns.apprehend.persistence.projection;

import com.cctns.apprehend.core.domain.CommonParamsDomain;
import com.cctns.apprehend.core.domain.PageableDomain;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public interface CiclGdAccusedProjection {
    String getCiclGdNum();

    String getApprehendSrno();

    String getBgReportSrno();

    String getJuvenileName();

    Integer getRelationTypeCd();

    String getRelationType();

    String getRelativeName();

    Integer getAge();

}
