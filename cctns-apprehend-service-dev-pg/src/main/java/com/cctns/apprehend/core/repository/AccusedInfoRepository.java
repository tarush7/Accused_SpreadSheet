package com.cctns.apprehend.core.repository;

import com.cctns.apprehend.core.domain.AccusedInfoSaveResultDomain;
import com.cctns.apprehend.core.domain.FirAccusedInfoUpdateDomain;
import com.cctns.apprehend.core.domain.accused.FirAccusedInfoDomain;
import com.cctns.apprehend.core.domain.accused.FirMultiAccusedDomain;

public interface AccusedInfoRepository {

    AccusedInfoSaveResultDomain submitAccusedInFir(FirAccusedInfoDomain request);

    FirAccusedInfoDomain getDetailsById(Long accusedVid);

    FirAccusedInfoUpdateDomain getUpdateDetailsById(Long accusedVid);

    void save(FirAccusedInfoUpdateDomain request);

    void submitMultiAccused(FirMultiAccusedDomain request);

    Integer getPsCdById(Long psId);
}
