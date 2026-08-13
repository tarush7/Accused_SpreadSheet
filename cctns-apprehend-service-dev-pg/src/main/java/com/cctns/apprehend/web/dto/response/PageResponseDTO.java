package com.cctns.apprehend.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Builder
@ToString
public class PageResponseDTO<T> {
    T list;
    Long totalSize;
    Integer pageCount;
}

