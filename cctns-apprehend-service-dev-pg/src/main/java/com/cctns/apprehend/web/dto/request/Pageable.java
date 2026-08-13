package com.cctns.apprehend.web.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Pageable {

    @NotNull(message = "Page Number is mandatory")
    @Min(0)
    private Integer page;

    @NotNull(message = "Page size is mandatory")
    @Min(1)
    private Integer pageSize;

}