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
public class CiclGdAccusedListReqDto extends CommonParamsDTO {
    @NotNull(message = "flag APPR or BG is mandatory")
    private String flag;
    @NotNull(message = "ciclGdNum is mandatory")
    private String ciclGdNum;
    @NotNull(message = "reqPageable is mandatory")
    private Pageable pageable;
}
