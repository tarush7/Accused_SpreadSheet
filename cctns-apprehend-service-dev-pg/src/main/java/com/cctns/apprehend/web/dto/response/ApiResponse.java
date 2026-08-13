package com.cctns.apprehend.web.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Builder
@ToString
public class ApiResponse<T> {
    String message;
    String status;
    Integer statusCode;
    List<String> errors;
    T data;
}