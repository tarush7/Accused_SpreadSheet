package com.cctns.apprehend.core.repository;

public interface LookUpMasterRepository {

    String fetchMasterValue(String apiMasterCd, Integer langCd, Integer lookUpCd, Integer lookUpParentCd);

}
