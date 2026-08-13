package com.cctns.apprehend.persistence.projection;

import java.time.LocalDateTime;

public interface AccusedListProjection {
     String getJuvenileName();
     Integer getRelationTypeCd();
     String getRelationType();
     String getRelativeName();
     Integer getAge();
     Long getAccusedSrno();
     Long getAccusedVid();
     String getApprehendSrno();
     LocalDateTime getApprehendDt();
     String getJuvDisposalSrno();
      String getFirDisplayNum();
      String getFirRegDt();
      String getIoName();
     String getPs();
     String getDistrict();
     String getBgReportSrno();
     String getCiclGdNum();
}
