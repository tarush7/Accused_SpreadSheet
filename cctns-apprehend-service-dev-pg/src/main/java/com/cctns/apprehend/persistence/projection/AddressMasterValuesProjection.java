package com.cctns.apprehend.persistence.projection;

public interface AddressMasterValuesProjection {

    String getAddressType();
    String getCountry();
    String getState();
    String getDistrict();
    String getSubDistrict();
    String getVillage();
    String getPs();
}