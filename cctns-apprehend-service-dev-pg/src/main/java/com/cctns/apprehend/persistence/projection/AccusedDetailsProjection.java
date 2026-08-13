package com.cctns.apprehend.persistence.projection;

import java.time.LocalDateTime;

public interface AccusedDetailsProjection {
     Long getAccusedSrno();
     Long getApprehendSrno();
     String getFirstName();
     String getMiddleName();
     String getLastName();
     String getFirstNameEng();
     String getMiddleNameEng();
     String getLastNameEng();
     Integer getRelationTypeCd();
     String getRelativeName();
     String getRelativeNameEng();
     Long getRelMobileNum();
     Integer getGenderCd();
     Integer getAgeTypeCd();
     Integer getAgeYrs();
     Integer getAgeMonths();
     Integer getYob();
     LocalDateTime getDob();
     Integer getAgeFromYrs();
     Integer getAgeToYrs();
     Integer getNationalityCd();
     Integer getCategoryCd();
     Integer getReligionCd();
     Integer getOccupationCd();
     String getInjuryDetails();
}
