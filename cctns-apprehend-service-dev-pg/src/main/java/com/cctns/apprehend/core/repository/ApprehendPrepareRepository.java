package com.cctns.apprehend.core.repository;

import com.cctns.apprehend.core.domain.AccusedDetailsDomain;
import com.cctns.apprehend.core.domain.AccusedProfileDomain;
import com.cctns.apprehend.core.domain.FirListDomain;
import com.cctns.apprehend.core.domain.PageDomain;

import java.util.List;

public interface ApprehendPrepareRepository {
    PageDomain<List<FirListDomain>> fetchFirListPrepare(FirListDomain request);

    PageDomain<List<FirListDomain>> fetchGdListPrepare(FirListDomain request);

    PageDomain<List<FirListDomain>> fetchFirListView(FirListDomain request);

    PageDomain<List<FirListDomain>> fetchGdListView(FirListDomain request);

    AccusedProfileDomain fetchDetailsForApprehendPrepare(AccusedProfileDomain request);

    AccusedProfileDomain fetchDetailsForApprehendView(AccusedProfileDomain request);

    AccusedDetailsDomain fetchAccusedDetails(AccusedDetailsDomain request);

//    PoliceStaffDomain getPoliceStaffDetails(Long staffId, Integer langCd);
//
//    List<ComplainantDomain> getComplainantFromFir(Long firRegNum);
}
