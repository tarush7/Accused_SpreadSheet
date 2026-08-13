package com.cctns.apprehend.web.controller;

import com.cctns.apprehend.constants.Constants;
import com.cctns.apprehend.core.domain.socialbg.JuvBackgroundReportDomain;
import com.cctns.apprehend.core.usecase.SocialBackgroundViewUseCase;
import com.cctns.apprehend.mapper.DomainWebMapper;
import com.cctns.apprehend.mapper.WebDomainMapper;
import com.cctns.apprehend.web.dto.request.socialBackground.JuvBackgroundReportDTO;
import com.cctns.apprehend.web.dto.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/social-bg/view")
public class SocialBackgroundViewController {
    private final WebDomainMapper webDomainMapper;
    private final DomainWebMapper domainWebMapper;
    private final SocialBackgroundViewUseCase socialBackgroundViewUseCase;

    public SocialBackgroundViewController(WebDomainMapper webDomainMapper, DomainWebMapper domainWebMapper, SocialBackgroundViewUseCase socialBackgroundViewUseCase) {
        this.webDomainMapper = webDomainMapper;
        this.domainWebMapper = domainWebMapper;
        this.socialBackgroundViewUseCase = socialBackgroundViewUseCase;
    }

    @PostMapping("/view-bg-report")
    public ResponseEntity<ApiResponse<JuvBackgroundReportDTO>> getBgReport(
            @Valid @RequestBody JuvBackgroundReportDTO juvBackgroundReportDTO){
        //Map DTO to domain
        JuvBackgroundReportDomain juvBackgroundReportDomain=webDomainMapper.toDomain(juvBackgroundReportDTO);
        //fetch data from use case
        JuvBackgroundReportDomain juvDetails= socialBackgroundViewUseCase.getBgReport(juvBackgroundReportDomain);

        //Map domain to DTO
        JuvBackgroundReportDTO response= domainWebMapper.toDTO(juvDetails);

        // Create and return the API response
        ApiResponse<JuvBackgroundReportDTO> responseDTO = ApiResponse.<JuvBackgroundReportDTO>builder()
                .message(Constants.DETAILS_SUCCESS)
                .status(HttpStatus.OK.name())
                .statusCode(HttpStatus.OK.value())
                .data(response)
                .errors(null)
                .build();

        return ResponseEntity.ok(responseDTO);
    }

}