package com.cctns.apprehend.web.controller;

import com.cctns.apprehend.constants.Constants;
import com.cctns.apprehend.core.domain.FirListDisposalDomain;
import com.cctns.apprehend.core.domain.JuvDisposalReqDomain;
import com.cctns.apprehend.core.domain.JuvDisposalResponseDomain;
import com.cctns.apprehend.core.domain.JuvenileProfileDomain;
import com.cctns.apprehend.core.domain.PageDomain;
import com.cctns.apprehend.core.domain.disposal.JuvDisposalDomain;
import com.cctns.apprehend.core.usecase.JuvenileDisposalUseCase;
import com.cctns.apprehend.mapper.DomainWebMapper;
import com.cctns.apprehend.mapper.WebDomainMapper;
import com.cctns.apprehend.web.dto.request.FirListBgRequestDTO;
import com.cctns.apprehend.web.dto.request.JuvDisposalReqDTO;
import com.cctns.apprehend.web.dto.request.disposal.JuvDisposalDTO;
import com.cctns.apprehend.web.dto.response.ApiResponse;
import com.cctns.apprehend.web.dto.response.FirListDisposalResponseDto;
import com.cctns.apprehend.web.dto.response.JuvDisposalResponseDTO;
import com.cctns.apprehend.web.dto.response.JuvenileProfileDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/juv-disposal")
public class JuvenileDisposalController {
    private final JuvenileDisposalUseCase juvenileDisposalUseCase;
    private final WebDomainMapper webDomainMapper;
    private final DomainWebMapper domainWebMapper;

    public JuvenileDisposalController(JuvenileDisposalUseCase juvenileDisposalUseCase, WebDomainMapper webDomainMapper, DomainWebMapper domainWebMapper) {
        this.juvenileDisposalUseCase = juvenileDisposalUseCase;
        this.webDomainMapper = webDomainMapper;
        this.domainWebMapper = domainWebMapper;
    }


    @PostMapping("/get-fir-list-juv")
    public ResponseEntity<ApiResponse<PageDomain<List<FirListDisposalResponseDto>>>> getFirList(
            @Valid @RequestBody FirListBgRequestDTO requestDto) {

        // DTO → Domain
        FirListDisposalDomain domainRequest = webDomainMapper.toDisposalDomain(requestDto);

        // Use case call
        PageDomain<List<FirListDisposalDomain>> firPage = juvenileDisposalUseCase.fetchDisposalFirList(domainRequest);

        // Domain → DTO (MapStruct handles list)
        List<FirListDisposalResponseDto> responseList =
                (firPage.getList() != null && !firPage.getList().isEmpty())
                        ? webDomainMapper.toResponseDtoDisposalList(firPage.getList())
                        : List.of();

        // Build response page
        PageDomain<List<FirListDisposalResponseDto>> responsePage =
                PageDomain.<List<FirListDisposalResponseDto>>builder()
                        .list(responseList)
                        .totalSize(firPage.getTotalSize())
                        .pageCount(firPage.getPageCount())
                        .build();

        // API response
        ApiResponse<PageDomain<List<FirListDisposalResponseDto>>> apiResponse =
                ApiResponse.<PageDomain<List<FirListDisposalResponseDto>>>builder()
                        .message(Constants.SUCCESS)
                        .status(HttpStatus.OK.name())
                        .statusCode(HttpStatus.OK.value())
                        .data(responsePage)
                        .errors(null)
                        .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/submit-juv-disposal")
    public ResponseEntity<ApiResponse<JuvDisposalResponseDTO>> submitJuvDisposal(@RequestBody JuvDisposalDTO requestDTO){
        //DTO-->Domain
        JuvDisposalDomain requestDomain=webDomainMapper.toDisposalDomain(requestDTO);

        JuvDisposalResponseDomain responseDomain=juvenileDisposalUseCase.submitJuvDisposal(requestDomain);

        //ResponseDomain-->DTO
        JuvDisposalResponseDTO responseDTO=webDomainMapper.toResponseDTO(responseDomain);

        ApiResponse<JuvDisposalResponseDTO> apiResponse = ApiResponse.<JuvDisposalResponseDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK.name())
                .message(Constants.SUCCESS)
                .data(responseDTO)
                .build();

        return ResponseEntity.ok(apiResponse);

    }

    @PostMapping("/get-juv-disposal-details")
    public ResponseEntity<ApiResponse<JuvenileProfileDTO>> getJuvDisposalDetails(
            @Valid @RequestBody JuvenileProfileDTO requestDto) {

        // DTO → Domain
        JuvenileProfileDomain domainRequest = webDomainMapper.toJuvDomain(requestDto);

        // Use Case call
        JuvenileProfileDomain domainResponse = juvenileDisposalUseCase.fetchDetailsForDisposalPrepare(domainRequest);

        // Domain → Response DTO
        JuvenileProfileDTO responseDto = webDomainMapper.toJuvResponseDTO(domainResponse);

        // Build API response
        ApiResponse<JuvenileProfileDTO> apiResponse =
                ApiResponse.<JuvenileProfileDTO>builder()
                        .statusCode(HttpStatus.OK.value())
                        .status(HttpStatus.OK.name())
                        .message(Constants.SUCCESS)
                        .data(responseDto)
                        .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/view-juv-disposal")
    public ResponseEntity<ApiResponse<JuvDisposalDTO>> getBgReport(
            @Valid @RequestBody JuvDisposalReqDTO reqDTO){
        //Map DTO to domain
        JuvDisposalReqDomain reqDomain=webDomainMapper.toDomain(reqDTO);
        //fetch data from use case
        JuvDisposalDomain juvDetails= juvenileDisposalUseCase.getJuvDisposal(reqDomain);

        //Map domain to DTO
        JuvDisposalDTO response= domainWebMapper.toDTO(juvDetails);

        // Create and return the API response
        ApiResponse<JuvDisposalDTO> responseDTO = ApiResponse.<JuvDisposalDTO>builder()
                .message(Constants.DETAILS_SUCCESS)
                .status(HttpStatus.OK.name())
                .statusCode(HttpStatus.OK.value())
                .data(response)
                .errors(null)
                .build();

        return ResponseEntity.ok(responseDTO);
}

}
