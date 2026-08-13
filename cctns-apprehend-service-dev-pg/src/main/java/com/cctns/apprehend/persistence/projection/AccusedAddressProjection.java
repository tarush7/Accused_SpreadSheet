package com.cctns.apprehend.persistence.projection;

public interface AccusedAddressProjection {
     Long getId();
     Integer getAddressTypeCd();
     String getAddressLine1();
     String getAddressLine2();
     String getAddressLine3();
     Long getSubDistrictCd();
     Long getVillageCd();
     String getVillage();
     String getTehsil();
     Integer getCountryCd();
     Integer getPincode();
     Boolean getIsCommAddr();
     String getOutsideIndiaAddr();
     String getAddressEng();
     Boolean getIsPermAddrSame();
     Integer getLgDistrictCd();
     Long  getPsId();
     Long getStateId();

      String getSubDistrict();
      String getCountry();
      String getState();
      String getDistrict();
      String getPs();
}
