package com.cctns.apprehend.web.controller;

import com.cctns.apprehend.constants.Constants;
import com.cctns.apprehend.core.exception.InvalidHeaderException;
import com.cctns.apprehend.web.dto.request.CommonParamsDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.NonNull;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import java.lang.reflect.Type;
import java.util.Optional;
import java.util.Set;
@ControllerAdvice
@Validated
public class CommonHeaderBinder extends RequestBodyAdviceAdapter {
    private final ObjectMapper objectMapper;
    private final Validator validator;

    public CommonHeaderBinder(ObjectMapper objectMapper, Validator validator) {
        this.objectMapper = objectMapper;
        this.validator = validator;
    }

    @Override
    public boolean supports(@NonNull MethodParameter methodParameter,
                            @NonNull Type targetType,
                            @NonNull Class<? extends HttpMessageConverter<?>> converterType) {

        return CommonParamsDTO.class.isAssignableFrom((Class<?>) targetType);
    }

    @NonNull
    @Override
    public Object afterBodyRead(@NonNull Object body,
                                @NonNull HttpInputMessage inputMessage,
                                @NonNull MethodParameter parameter,
                                @NonNull Type targetType,
                                @NonNull Class<? extends HttpMessageConverter<?>> converterType) {

        if (!(body instanceof CommonParamsDTO base)) {
            return body;
        }


        HttpServletRequest request =
                Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                        .filter(ServletRequestAttributes.class::isInstance)
                        .map(ServletRequestAttributes.class::cast)
                        .map(ServletRequestAttributes::getRequest)
                        .orElseThrow(() -> new InvalidHeaderException(Constants.INVALID_HEADER_MISSING_COMMON_PARAMS_EXCEPTION));

        String header = request.getHeader("loginparams");

        if (header == null) {
            throw new InvalidHeaderException(Constants.INVALID_HEADER_MISSING_COMMON_PARAMS_EXCEPTION);
        }

        try {
            CommonParamsDTO headerDto = objectMapper.readValue(header, CommonParamsDTO.class);

            // validation
            Set<ConstraintViolation<CommonParamsDTO>> violations = validator.validate(headerDto);

            if (!violations.isEmpty()) {
                throw new ConstraintViolationException(violations);
            }

            // copy values
            base.setStaffId(headerDto.getStaffId());
            base.setLoginId(headerDto.getLoginId());
            base.setLangCd(headerDto.getLangCd());
            base.setOfficeCd(headerDto.getOfficeCd());
            base.setStateId(headerDto.getStateId());
            base.setDistrictId(headerDto.getDistrictId());
            base.setPsId(headerDto.getPsId());
            base.setOfficeTypeCd(headerDto.getOfficeTypeCd());
            base.setRankCd(headerDto.getRankCd());
            base.setOfficeLevelCd(headerDto.getOfficeLevelCd());
            base.setAllowedRoleCd(headerDto.getAllowedRoleCd());
            base.setOicStaffId(headerDto.getOicStaffId());
            base.setOicLoginId(headerDto.getOicLoginId());
            base.setLoginparams(header);

        } catch (JsonProcessingException e) {
            throw new InvalidHeaderException(Constants.INVALID_HEADER_FORMAT_EXCEPTION);
        }

        return base;
    }

}
