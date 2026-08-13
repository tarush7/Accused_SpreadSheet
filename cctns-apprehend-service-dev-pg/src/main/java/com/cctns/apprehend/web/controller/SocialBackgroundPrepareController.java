package com.cctns.apprehend.web.controller;

import com.cctns.apprehend.constants.Constants;
import com.cctns.apprehend.core.domain.AccusedDetailsDomain;
import com.cctns.apprehend.core.domain.AccusedProfileDomain;
import com.cctns.apprehend.core.domain.FirListBgDomain;
import com.cctns.apprehend.core.domain.PageDomain;
import com.cctns.apprehend.core.usecase.SocialBackgroundPrepareUseCase;
import com.cctns.apprehend.mapper.WebDomainMapper;
import com.cctns.apprehend.web.dto.request.FirListBgRequestDTO;
import com.cctns.apprehend.web.dto.response.AccusedDetailsDTO;
import com.cctns.apprehend.web.dto.response.AccusedProfileDTO;
import com.cctns.apprehend.web.dto.response.ApiResponse;
import com.cctns.apprehend.web.dto.response.FirListBgResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/social-bg/prepare")
public class SocialBackgroundPrepareController {
    private final SocialBackgroundPrepareUseCase socialBackgroundPrepareUseCase;
    private final WebDomainMapper webDomainMapper;

    public SocialBackgroundPrepareController(SocialBackgroundPrepareUseCase socialBackgroundPrepareUseCase, WebDomainMapper webDomainMapper) {
        this.socialBackgroundPrepareUseCase = socialBackgroundPrepareUseCase;
        this.webDomainMapper = webDomainMapper;
    }

    @PostMapping("/get-fir-list-bg")
    public ResponseEntity<ApiResponse<PageDomain<List<FirListBgResponseDTO>>>> getFirList(
            @Valid @RequestBody FirListBgRequestDTO requestDto) {

        // DTO → Domain
        FirListBgDomain domainRequest =
                webDomainMapper.toDomain(requestDto);

        // Use case call
        PageDomain<List<FirListBgDomain>> firPage =
                socialBackgroundPrepareUseCase.fetchFirList(domainRequest);

        // Domain → DTO (MapStruct handles list)
        List<FirListBgResponseDTO> responseList =
                (firPage.getList() != null && !firPage.getList().isEmpty())
                        ? webDomainMapper.toResponseDtoBgList(firPage.getList())
                        : List.of();

        // Build response page
        PageDomain<List<FirListBgResponseDTO>> responsePage =
                PageDomain.<List<FirListBgResponseDTO>>builder()
                        .list(responseList)
                        .totalSize(firPage.getTotalSize())
                        .pageCount(firPage.getPageCount())
                        .build();

        // API response
        ApiResponse<PageDomain<List<FirListBgResponseDTO>>> apiResponse =
                ApiResponse.<PageDomain<List<FirListBgResponseDTO>>>builder()
                        .message(Constants.SUCCESS)
                        .status(HttpStatus.OK.name())
                        .statusCode(HttpStatus.OK.value())
                        .data(responsePage)
                        .errors(null)
                        .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/get-bg-details")
    public ResponseEntity<ApiResponse<AccusedProfileDTO>> getApprehendPrepareDetails(
            @Valid @RequestBody AccusedProfileDTO requestDto) {

        // DTO → Domain
        AccusedProfileDomain domainRequest = webDomainMapper.toDomain(requestDto);

        // Use Case call
        AccusedProfileDomain domainResponse =
                socialBackgroundPrepareUseCase.fetchDetailsForBgPrepare(domainRequest);

        // Domain → Response DTO
        AccusedProfileDTO responseDto =
                webDomainMapper.toResponseDTO(domainResponse);

        // Build API response
        ApiResponse<AccusedProfileDTO> apiResponse =
                ApiResponse.<AccusedProfileDTO>builder()
                        .statusCode(HttpStatus.OK.value())
                        .status(HttpStatus.OK.name())
                        .message(Constants.SUCCESS)
                        .data(responseDto)
                        .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/get-bg-accused-details-prepare")
    public ResponseEntity<ApiResponse<AccusedDetailsDTO>> getAccusedPrepareDetails(
            @Valid @RequestBody AccusedDetailsDTO requestDto) {

        // DTO → Domain
        AccusedDetailsDomain domainRequest = webDomainMapper.toDomain(requestDto);

        // Use Case call
        AccusedDetailsDomain domainResponse =
                socialBackgroundPrepareUseCase.fetchAccusedDetails(domainRequest);

        // Domain → Response DTO
        AccusedDetailsDTO responseDto =
                webDomainMapper.toResponseDTO(domainResponse);

        // Build API response
        ApiResponse<AccusedDetailsDTO> apiResponse =
                ApiResponse.<AccusedDetailsDTO>builder()
                        .statusCode(HttpStatus.OK.value())
                        .status(HttpStatus.OK.name())
                        .message(Constants.SUCCESS)
                        .data(responseDto)
                        .build();

        return ResponseEntity.ok(apiResponse);
    }
}