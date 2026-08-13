package com.cctns.apprehend.persistence.projection;

public interface AccusedNationalIdProjection {
     Long getId();
     Integer getNationalIdTypeCd();
     String getNationalIdType();
     String getNationalIdNum();
     String getPassportIssueDt();
     String getPassportIssuePlc();
}
