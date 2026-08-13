package com.cctns.apprehend.core.repository;

import com.cctns.apprehend.core.domain.AccusedProfileDomain;
import com.cctns.apprehend.core.domain.FirListDisposalDomain;
import com.cctns.apprehend.core.domain.JuvDisposalReqDomain;
import com.cctns.apprehend.core.domain.JuvDisposalResponseDomain;
import com.cctns.apprehend.core.domain.JuvenileProfileDomain;
import com.cctns.apprehend.core.domain.PageDomain;
import com.cctns.apprehend.core.domain.disposal.JuvDisposalDomain;

import java.util.List;

public interface JuvenileDisposalRepository {
    PageDomain<List<FirListDisposalDomain>> fetchFirListPrepare(FirListDisposalDomain request);

    PageDomain<List<FirListDisposalDomain>> fetchGdListPrepare(FirListDisposalDomain request);

    PageDomain<List<FirListDisposalDomain>> fetchFirListView(FirListDisposalDomain request);

    PageDomain<List<FirListDisposalDomain>> fetchGdListView(FirListDisposalDomain request);

    public JuvDisposalResponseDomain submitJuvDisposal(JuvDisposalDomain request);

    JuvDisposalDomain getJuvDisposal(JuvDisposalReqDomain request);

    JuvenileProfileDomain fetchDetailsForDisposalPrepare(JuvenileProfileDomain request);

    JuvenileProfileDomain fetchDetailsForDisposalView(JuvenileProfileDomain request);

    JuvenileProfileDomain fetchDetailsForGdPrepare(JuvenileProfileDomain request);

    JuvenileProfileDomain fetchDetailsForGdView(JuvenileProfileDomain request);


}
