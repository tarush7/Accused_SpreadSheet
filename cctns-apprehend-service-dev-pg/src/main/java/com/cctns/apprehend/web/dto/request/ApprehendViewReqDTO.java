package com.cctns.apprehend.web.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApprehendViewReqDTO extends CommonParamsDTO{
    @NotNull(message = "apprehendSrno is required")
    private Long apprehendSrno;
}
