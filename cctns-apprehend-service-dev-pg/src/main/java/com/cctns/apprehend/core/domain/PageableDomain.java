package com.cctns.apprehend.core.domain;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageableDomain {
    @NotNull(message = "Page Number is mandatory")
    private Integer page;

    @NotNull(message = "Page size is mandatory")
    private Integer pageSize;
}
