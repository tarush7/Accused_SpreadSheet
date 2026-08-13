package com.cctns.apprehend.web.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FirListRequestDTO extends CommonParamsDTO {
    private String firSrno;
    private Integer year;
    private LocalDate fromDate;
    private LocalDate toDate;
    private List<Integer> psIdList;
    @NotNull(message = "reqPageable is mandatory")
    private Pageable pageable;

    private String gridFlag;
    private String firTypeFlag;
}
