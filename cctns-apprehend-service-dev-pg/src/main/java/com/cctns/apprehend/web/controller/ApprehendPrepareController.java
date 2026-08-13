package com.cctns.apprehend.web.controller;

import com.cctns.apprehend.constants.Constants;
import com.cctns.apprehend.core.domain.AccusedDetailsDomain;
import com.cctns.apprehend.core.domain.AccusedProfileDomain;
import com.cctns.apprehend.core.domain.FirListDomain;
import com.cctns.apprehend.core.domain.PageDomain;
import com.cctns.apprehend.core.usecase.ApprehendPrepareUseCase;
import com.cctns.apprehend.mapper.WebDomainMapper;
import com.cctns.apprehend.web.dto.request.FirListRequestDTO;
import com.cctns.apprehend.web.dto.response.AccusedDetailsDTO;
import com.cctns.apprehend.web.dto.response.AccusedProfileDTO;
import com.cctns.apprehend.web.dto.response.ApiResponse;
import com.cctns.apprehend.web.dto.response.FirListResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/apprehend/prepare")
public class ApprehendPrepareController {
    private final ApprehendPrepareUseCase apprehendPrepareUseCase;
    private final WebDomainMapper webDomainMapper;

    public ApprehendPrepareController(ApprehendPrepareUseCase apprehendPrepareUseCase, WebDomainMapper webDomainMapper) {
        this.apprehendPrepareUseCase = apprehendPrepareUseCase;
        this.webDomainMapper = webDomainMapper;
    }

    @PostMapping("/get-fir-list")
    public ResponseEntity<ApiResponse<PageDomain<List<FirListResponseDTO>>>> getFirList(
            @Valid @RequestBody FirListRequestDTO requestDto) {

        // DTO → Domain
        FirListDomain domainRequest =
                webDomainMapper.toDomain(requestDto);

        // Use case call
        PageDomain<List<FirListDomain>> firPage =
                apprehendPrepareUseCase.fetchFirList(domainRequest);

        // Domain → DTO (MapStruct handles list)
        List<FirListResponseDTO> responseList =
                (firPage.getList() != null && !firPage.getList().isEmpty())
                        ? webDomainMapper.toResponseDtoList(firPage.getList())
                        : List.of();

        // Build response page
        PageDomain<List<FirListResponseDTO>> responsePage =
                PageDomain.<List<FirListResponseDTO>>builder()
                        .list(responseList)
                        .totalSize(firPage.getTotalSize())
                        .pageCount(firPage.getPageCount())
                        .build();

        // API response
        ApiResponse<PageDomain<List<FirListResponseDTO>>> apiResponse =
                ApiResponse.<PageDomain<List<FirListResponseDTO>>>builder()
                        .message(Constants.SUCCESS)
                        .status(HttpStatus.OK.name())
                        .statusCode(HttpStatus.OK.value())
                        .data(responsePage)
                        .errors(null)
                        .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/get-apprehend-details")
    public ResponseEntity<ApiResponse<AccusedProfileDTO>> getApprehendPrepareDetails(
            @Valid @RequestBody AccusedProfileDTO requestDto) {

        // DTO → Domain
        AccusedProfileDomain domainRequest = webDomainMapper.toDomain(requestDto);

        // Use Case call
        AccusedProfileDomain domainResponse =
                apprehendPrepareUseCase.fetchDetailsForApprehendPrepare(domainRequest);

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

    @PostMapping("/get-accused-details-prepare")
    public ResponseEntity<ApiResponse<AccusedDetailsDTO>> getAccusedPrepareDetails(
            @Valid @RequestBody AccusedDetailsDTO requestDto) {

        // DTO → Domain
        AccusedDetailsDomain domainRequest = webDomainMapper.toDomain(requestDto);

        // Use Case call
        AccusedDetailsDomain domainResponse =
                apprehendPrepareUseCase.fetchAccusedDetails(domainRequest);

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
