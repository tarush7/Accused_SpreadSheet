package com.cctns.apprehend.core.usecase;

import com.cctns.apprehend.core.domain.AccusedDetailsDomain;
import com.cctns.apprehend.core.domain.AccusedProfileDomain;
import com.cctns.apprehend.core.domain.FirListDomain;
import com.cctns.apprehend.core.domain.PageDomain;

import java.util.List;

public interface ApprehendPrepareUseCase {

    PageDomain<List<FirListDomain>> fetchFirList(FirListDomain request);

    AccusedProfileDomain fetchDetailsForApprehendPrepare(AccusedProfileDomain request);

    AccusedDetailsDomain fetchAccusedDetails(AccusedDetailsDomain request);
}
