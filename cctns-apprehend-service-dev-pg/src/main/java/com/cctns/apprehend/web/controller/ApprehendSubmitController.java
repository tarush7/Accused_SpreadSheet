package com.cctns.apprehend.web.controller;

import com.cctns.apprehend.constants.Constants;
import com.cctns.apprehend.core.domain.ApprehendResponseDomain;
import com.cctns.apprehend.core.domain.apprehend.ApprehendMemoDomain;
import com.cctns.apprehend.core.usecase.ApprehendSubmitUseCase;
import com.cctns.apprehend.mapper.WebDomainMapper;
import com.cctns.apprehend.web.dto.request.apprehend.ApprehendMemoDTO;
import com.cctns.apprehend.web.dto.response.ApiResponse;
import com.cctns.apprehend.web.dto.response.ApprehendResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/apprehend/submit")
public class ApprehendSubmitController {
    private final ApprehendSubmitUseCase apprehendSubmitUseCase;
    private final WebDomainMapper webDomainMapper;

    public ApprehendSubmitController(ApprehendSubmitUseCase apprehendSubmitUseCase, WebDomainMapper webDomainMapper){
        this.apprehendSubmitUseCase=apprehendSubmitUseCase;
        this.webDomainMapper = webDomainMapper;
    }
    @PostMapping("/submit-apprehend-memo")
    public ResponseEntity<ApiResponse<ApprehendResponseDTO>> submitApprehendMemo(@RequestBody @Valid ApprehendMemoDTO requestDTO){
        //DTO-->Domain
        ApprehendMemoDomain requestDomain=webDomainMapper.toDomain(requestDTO);

        ApprehendResponseDomain responseDomain=apprehendSubmitUseCase.submitApprehendMemo(requestDomain);

        //ResponseDomain-->DTO
        ApprehendResponseDTO responseDTO=webDomainMapper.toResponseDTO(responseDomain);

        ApiResponse<ApprehendResponseDTO> apiResponse = ApiResponse.<ApprehendResponseDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK.name())
                .message(Constants.SUCCESS)
                .data(responseDTO)
                .build();

        return ResponseEntity.ok(apiResponse);

    }
}
