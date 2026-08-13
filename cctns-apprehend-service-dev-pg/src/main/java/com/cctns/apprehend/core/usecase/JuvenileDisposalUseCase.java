package com.cctns.apprehend.core.usecase;

import com.cctns.apprehend.core.domain.AccusedProfileDomain;
import com.cctns.apprehend.core.domain.FirListDisposalDomain;
import com.cctns.apprehend.core.domain.JuvDisposalReqDomain;
import com.cctns.apprehend.core.domain.JuvDisposalResponseDomain;
import com.cctns.apprehend.core.domain.JuvenileProfileDomain;
import com.cctns.apprehend.core.domain.PageDomain;
import com.cctns.apprehend.core.domain.disposal.JuvDisposalDomain;

import java.util.List;

public interface JuvenileDisposalUseCase {

    PageDomain<List<FirListDisposalDomain>> fetchDisposalFirList(FirListDisposalDomain request);

    JuvDisposalResponseDomain submitJuvDisposal(JuvDisposalDomain request);

    JuvDisposalDomain getJuvDisposal(JuvDisposalReqDomain request);

    JuvenileProfileDomain fetchDetailsForDisposalPrepare(JuvenileProfileDomain request);
}
