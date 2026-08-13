package com.cctns.apprehend.web.dto.request.disposal;

import com.cctns.apprehend.web.dto.request.CommonParamsDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JuvDisposalDTO extends CommonParamsDTO {

    private Long juvDisposalSrno;
    private Integer langCd;
    private Long firRegNum;
    private Long apprehendSrno;
    private Long juvenileSrno;
    private String jjbName;
    private String jjbAddress;
    private String jjbMagistrateName;
    private String finalOrderDtls;
    private String finalOrderNum;
    private LocalDate finalOrderDt;
    private String jjbEstblName;

    private List<JuvDisposalFilesDTO> fileList;
}