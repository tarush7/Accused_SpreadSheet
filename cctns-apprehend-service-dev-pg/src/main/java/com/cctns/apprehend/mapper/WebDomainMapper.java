package com.cctns.apprehend.mapper;

import com.cctns.apprehend.core.domain.AccusedDetailsDomain;
import com.cctns.apprehend.core.domain.AccusedProfileDomain;
import com.cctns.apprehend.core.domain.ApprehendResponseDomain;
import com.cctns.apprehend.core.domain.ApprehendViewReqDomain;
import com.cctns.apprehend.core.domain.CiclGdAccusedDomain;
import com.cctns.apprehend.core.domain.FirListBgDomain;
import com.cctns.apprehend.core.domain.FirListDisposalDomain;
import com.cctns.apprehend.core.domain.FirListDomain;
import com.cctns.apprehend.core.domain.JuvDisposalReqDomain;
import com.cctns.apprehend.core.domain.JuvDisposalResponseDomain;
import com.cctns.apprehend.core.domain.JuvenileProfileDomain;
import com.cctns.apprehend.core.domain.SocialBackgroundResponseDomain;
import com.cctns.apprehend.core.domain.apprehend.ApprehendAddressDomain;
import com.cctns.apprehend.core.domain.apprehend.ApprehendFilesDomain;
import com.cctns.apprehend.core.domain.apprehend.ApprehendMemoDomain;
import com.cctns.apprehend.core.domain.apprehend.ApprehendWitnessAddrDomain;
import com.cctns.apprehend.core.domain.disposal.JuvDisposalDomain;
import com.cctns.apprehend.core.domain.socialbg.JclBackgroundFilesDomain;
import com.cctns.apprehend.core.domain.socialbg.JuvBackgroundReportDomain;
import com.cctns.apprehend.core.domain.socialbg.JuvFamilyDtlsDomain;
import com.cctns.apprehend.core.domain.socialbg.JuvIdentityMarksDomain;
import com.cctns.apprehend.core.domain.socialbg.JuvPhyAbuseDomain;
import com.cctns.apprehend.core.domain.socialbg.JuvPhyFeatureDomain;
import com.cctns.apprehend.web.dto.request.ApprehendViewReqDTO;
import com.cctns.apprehend.web.dto.request.CiclGdAccusedListReqDto;
import com.cctns.apprehend.web.dto.request.FirListBgRequestDTO;
import com.cctns.apprehend.web.dto.request.FirListRequestDTO;
import com.cctns.apprehend.web.dto.request.JuvDisposalReqDTO;
import com.cctns.apprehend.web.dto.request.apprehend.ApprehendAddressDTO;
import com.cctns.apprehend.web.dto.request.apprehend.ApprehendFilesDTO;
import com.cctns.apprehend.web.dto.request.apprehend.ApprehendMemoDTO;
import com.cctns.apprehend.web.dto.request.apprehend.ApprehendWitnessAddrDTO;
import com.cctns.apprehend.web.dto.request.disposal.JuvDisposalDTO;
import com.cctns.apprehend.web.dto.request.socialBackground.JclBackgroundFilesDTO;
import com.cctns.apprehend.web.dto.request.socialBackground.JuvBackgroundReportDTO;
import com.cctns.apprehend.web.dto.request.socialBackground.JuvFamilyDtlsDTO;
import com.cctns.apprehend.web.dto.request.socialBackground.JuvIdentityMarksDTO;
import com.cctns.apprehend.web.dto.request.socialBackground.JuvPhyAbuseDTO;
import com.cctns.apprehend.web.dto.request.socialBackground.JuvPhyFeatureDTO;
import com.cctns.apprehend.web.dto.response.AccusedDetailsDTO;
import com.cctns.apprehend.web.dto.response.AccusedProfileDTO;
import com.cctns.apprehend.web.dto.response.ApprehendResponseDTO;
import com.cctns.apprehend.web.dto.response.CiclGdAccusedListResDTO;
import com.cctns.apprehend.web.dto.response.FirListBgResponseDTO;
import com.cctns.apprehend.web.dto.response.FirListDisposalResponseDto;
import com.cctns.apprehend.web.dto.response.FirListResponseDTO;
import com.cctns.apprehend.web.dto.response.JuvDisposalResponseDTO;
import com.cctns.apprehend.web.dto.response.JuvenileProfileDTO;
import com.cctns.apprehend.web.dto.response.SocialBackgroundResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper( config = GlobalMapperConfig.class )
public interface WebDomainMapper {
    ApprehendMemoDomain toDomain(ApprehendMemoDTO dto);

    ApprehendFilesDomain toDomain(ApprehendFilesDTO dto);

    ApprehendAddressDomain toDomain(ApprehendAddressDTO dto);

    ApprehendWitnessAddrDomain toDomain(ApprehendWitnessAddrDTO dto);

    JuvBackgroundReportDomain toDomain(JuvBackgroundReportDTO dto);

    JclBackgroundFilesDomain toDomain(JclBackgroundFilesDTO dto);

    JuvPhyAbuseDomain toDomain(JuvPhyAbuseDTO dto);

    JuvFamilyDtlsDomain toDomain(JuvFamilyDtlsDTO dto);

    JuvPhyFeatureDomain toDomain(JuvPhyFeatureDTO dto);

    JuvIdentityMarksDomain toDomain(JuvIdentityMarksDTO dto);

    @Mapping(target = "pageable.page", source = "pageable.page")
    @Mapping(target = "pageable.pageSize", source = "pageable.pageSize")
    FirListDomain toDomain(FirListRequestDTO dto);

    FirListBgDomain toDomain(FirListBgRequestDTO dto);

    FirListDisposalDomain toDisposalDomain(FirListBgRequestDTO dto);

    ApprehendResponseDTO toResponseDTO(ApprehendResponseDomain domain);

    SocialBackgroundResponseDTO toResponseDTO(SocialBackgroundResponseDomain domain);

    JuvDisposalResponseDTO toResponseDTO(JuvDisposalResponseDomain domain);

    FirListResponseDTO toResponseDTO(FirListDomain domain);

    List<FirListResponseDTO> toResponseDtoList(List<FirListDomain> domain);

    List<CiclGdAccusedListResDTO> toCiclGdResponseDtoList(List<CiclGdAccusedDomain> domain);

    List<FirListBgResponseDTO> toResponseDtoBgList(List<FirListBgDomain> domain);

    List<FirListDisposalResponseDto> toResponseDtoDisposalList(List<FirListDisposalDomain> domain);

    AccusedProfileDomain toDomain(AccusedProfileDTO dto);

    AccusedDetailsDomain toDomain(AccusedDetailsDTO dto);

    AccusedProfileDTO toResponseDTO(AccusedProfileDomain domain);

    AccusedDetailsDTO toResponseDTO(AccusedDetailsDomain domain);

    ApprehendViewReqDomain toDomain(ApprehendViewReqDTO dto);

    JuvDisposalDomain toDisposalDomain(JuvDisposalDTO dto);

    JuvDisposalReqDomain toDomain(JuvDisposalReqDTO dto);

    JuvenileProfileDomain toJuvDomain(JuvenileProfileDTO dto);

    JuvenileProfileDTO toJuvResponseDTO(JuvenileProfileDomain domain);

    CiclGdAccusedDomain toCiclGdDomain( CiclGdAccusedListReqDto dto);

}
