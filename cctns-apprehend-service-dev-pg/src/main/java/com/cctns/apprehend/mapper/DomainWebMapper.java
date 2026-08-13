package com.cctns.apprehend.mapper;

import com.cctns.apprehend.core.domain.apprehend.ApprehendMemoDomain;
import com.cctns.apprehend.core.domain.disposal.JuvDisposalDomain;
import com.cctns.apprehend.core.domain.socialbg.JuvBackgroundReportDomain;
import com.cctns.apprehend.web.dto.request.apprehend.ApprehendMemoDTO;
import com.cctns.apprehend.web.dto.request.disposal.JuvDisposalDTO;
import com.cctns.apprehend.web.dto.request.socialBackground.JuvBackgroundReportDTO;
import org.mapstruct.Mapper;

@Mapper( config = GlobalMapperConfig.class )
public interface DomainWebMapper {

    ApprehendMemoDTO toDTO(ApprehendMemoDomain domain);

    JuvDisposalDTO toDTO(JuvDisposalDomain domain);

    JuvBackgroundReportDTO toDTO(JuvBackgroundReportDomain domain);

}