package com.cctns.apprehend.web.controller;

import com.cctns.apprehend.constants.Constants;
import com.cctns.apprehend.core.domain.SocialBackgroundResponseDomain;
import com.cctns.apprehend.core.domain.socialbg.JuvBackgroundReportDomain;
import com.cctns.apprehend.core.usecase.SocialBackgroundSubmitUseCase;
import com.cctns.apprehend.mapper.WebDomainMapper;
import com.cctns.apprehend.web.dto.request.socialBackground.JuvBackgroundReportDTO;
import com.cctns.apprehend.web.dto.response.ApiResponse;
import com.cctns.apprehend.web.dto.response.SocialBackgroundResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/social-bg/submit")
public class SocialBackgroundSubmitController {
    private final SocialBackgroundSubmitUseCase socialBackgroundSubmitUseCase;
    private final WebDomainMapper webDomainMapper;

    public SocialBackgroundSubmitController(SocialBackgroundSubmitUseCase socialBackgroundSubmitUseCase, WebDomainMapper webDomainMapper){
        this.socialBackgroundSubmitUseCase=socialBackgroundSubmitUseCase;
        this.webDomainMapper = webDomainMapper;
    }
    @PostMapping("/submit-bg-report")
    public ResponseEntity<ApiResponse<SocialBackgroundResponseDTO>> submitApprehendMemo(@RequestBody JuvBackgroundReportDTO requestDTO){
        //DTO-->Domain
        JuvBackgroundReportDomain requestDomain=webDomainMapper.toDomain(requestDTO);

        SocialBackgroundResponseDomain responseDomain=socialBackgroundSubmitUseCase.submitSocialBgReport(requestDomain);

        //ResponseDomain-->DTO
        SocialBackgroundResponseDTO responseDTO=webDomainMapper.toResponseDTO(responseDomain);

        ApiResponse<SocialBackgroundResponseDTO> apiResponse = ApiResponse.<SocialBackgroundResponseDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK.name())
                .message(Constants.SUCCESS)
                .data(responseDTO)
                .build();

        return ResponseEntity.ok(apiResponse);

    }
}