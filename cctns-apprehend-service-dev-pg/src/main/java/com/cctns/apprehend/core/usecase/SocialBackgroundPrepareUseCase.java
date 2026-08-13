package com.cctns.apprehend.core.usecase;

import com.cctns.apprehend.core.domain.AccusedDetailsDomain;
import com.cctns.apprehend.core.domain.AccusedProfileDomain;
import com.cctns.apprehend.core.domain.FirListBgDomain;
import com.cctns.apprehend.core.domain.PageDomain;

import java.util.List;
public interface SocialBackgroundPrepareUseCase {
    PageDomain<List<FirListBgDomain>> fetchFirList(FirListBgDomain request);

    AccusedProfileDomain fetchDetailsForBgPrepare(AccusedProfileDomain request);

    AccusedDetailsDomain fetchAccusedDetails(AccusedDetailsDomain request);
}