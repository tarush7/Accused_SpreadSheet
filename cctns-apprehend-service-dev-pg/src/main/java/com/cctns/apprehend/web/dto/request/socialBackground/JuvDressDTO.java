
package com.cctns.apprehend.web.dto.request.socialBackground;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JuvDressDTO {

    private String id;
    private Integer langCd;
    private Long firAccusedSrnoMigr;
    private Long accusedVid;
    private Long accusedSrno;
    private Long firRegNum;
    private Integer regTypeCd;
    private Long crmDetailSrno;
    private Integer crmSeqNum;
    private Integer dressForCd;
    private String dressFor;
    private Integer dressTypeCd;
    private String dressType;
    private Integer dressSubtypeCd;
    private String dressSubtype;
    private String othrDressDtls;

}