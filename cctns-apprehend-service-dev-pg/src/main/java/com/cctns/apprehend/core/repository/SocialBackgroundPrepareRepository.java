package com.cctns.apprehend.core.repository;

import com.cctns.apprehend.core.domain.AccusedDetailsDomain;
import com.cctns.apprehend.core.domain.AccusedProfileDomain;
import com.cctns.apprehend.core.domain.FirListBgDomain;
import com.cctns.apprehend.core.domain.PageDomain;

import java.util.List;
public interface SocialBackgroundPrepareRepository {
    PageDomain<List<FirListBgDomain>> fetchFirListPrepare(FirListBgDomain request);

    PageDomain<List<FirListBgDomain>> fetchGdListPrepare(FirListBgDomain request);

    AccusedDetailsDomain fetchAccusedDetails(AccusedDetailsDomain request);

    AccusedProfileDomain fetchDetailsForBgPrepare(AccusedProfileDomain request);

    AccusedProfileDomain fetchDetailsForBgView(AccusedProfileDomain request);
}