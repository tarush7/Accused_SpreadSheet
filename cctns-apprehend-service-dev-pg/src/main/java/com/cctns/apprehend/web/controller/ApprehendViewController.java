package com.cctns.apprehend.web.controller;

import com.cctns.apprehend.constants.Constants;
import com.cctns.apprehend.core.domain.ApprehendViewReqDomain;
import com.cctns.apprehend.core.domain.CiclGdAccusedDomain;
import com.cctns.apprehend.core.domain.PageDomain;
import com.cctns.apprehend.core.domain.apprehend.ApprehendMemoDomain;
import com.cctns.apprehend.core.usecase.ApprehendViewUseCase;
import com.cctns.apprehend.mapper.DomainWebMapper;
import com.cctns.apprehend.mapper.WebDomainMapper;
import com.cctns.apprehend.web.dto.request.ApprehendViewReqDTO;
import com.cctns.apprehend.web.dto.request.CiclGdAccusedListReqDto;
import com.cctns.apprehend.web.dto.request.apprehend.ApprehendMemoDTO;
import com.cctns.apprehend.web.dto.response.ApiResponse;
import com.cctns.apprehend.web.dto.response.CiclGdAccusedListResDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/apprehend/view")
public class ApprehendViewController {
    private final WebDomainMapper webDomainMapper;
    private final DomainWebMapper domainWebMapper;
    private final ApprehendViewUseCase apprehendViewUseCase;

    public ApprehendViewController(WebDomainMapper webDomainMapper, DomainWebMapper domainWebMapper, ApprehendViewUseCase apprehendViewUseCase) {
        this.webDomainMapper = webDomainMapper;
        this.domainWebMapper = domainWebMapper;
        this.apprehendViewUseCase = apprehendViewUseCase;
    }

    @PostMapping("/view-apprehend-memo")
    public ResponseEntity<ApiResponse<ApprehendMemoDTO>> getApprehendMemo(
            @Valid @RequestBody ApprehendViewReqDTO reqDTO){
        //Map DTO to domain
        ApprehendViewReqDomain request=webDomainMapper.toDomain(reqDTO);
        //fetch data from use case
        ApprehendMemoDomain apprehendDetails= apprehendViewUseCase.getApprehendMemo(request);

        //Map domain to DTO
        ApprehendMemoDTO response= domainWebMapper.toDTO(apprehendDetails);

        // Create and return the API response
        ApiResponse<ApprehendMemoDTO> responseDTO = ApiResponse.<ApprehendMemoDTO>builder()
                .message(Constants.DETAILS_SUCCESS)
                .status(HttpStatus.OK.name())
                .statusCode(HttpStatus.OK.value())
                .data(response)
                .errors(null)
                .build();

        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/get-cicl-gd-accused-list")
    public ResponseEntity<ApiResponse<PageDomain<List<CiclGdAccusedListResDTO>>>> getFirList(
            @Valid @RequestBody CiclGdAccusedListReqDto requestDto) {

        // DTO → Domain
        CiclGdAccusedDomain domainRequest =
                webDomainMapper.toCiclGdDomain(requestDto);

        // Use case call
        PageDomain<List<CiclGdAccusedDomain>> firPage =
                apprehendViewUseCase.fetchCiclGdAccusedList(domainRequest);

        // Domain → DTO (MapStruct handles list)
        List<CiclGdAccusedListResDTO> responseList =
                (firPage.getList() != null && !firPage.getList().isEmpty())
                        ? webDomainMapper.toCiclGdResponseDtoList(firPage.getList())
                        : List.of();

        // Build response page
        PageDomain<List<CiclGdAccusedListResDTO>> responsePage =
                PageDomain.<List<CiclGdAccusedListResDTO>>builder()
                        .list(responseList)
                        .totalSize(firPage.getTotalSize())
                        .pageCount(firPage.getPageCount())
                        .build();

        // API response
        ApiResponse<PageDomain<List<CiclGdAccusedListResDTO>>> apiResponse =
                ApiResponse.<PageDomain<List<CiclGdAccusedListResDTO>>>builder()
                        .message(Constants.SUCCESS)
                        .status(HttpStatus.OK.name())
                        .statusCode(HttpStatus.OK.value())
                        .data(responsePage)
                        .errors(null)
                        .build();

        return ResponseEntity.ok(apiResponse);
    }

}
