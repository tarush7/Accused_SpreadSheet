package com.cctns.apprehend.core.usecase;

import com.cctns.apprehend.core.domain.AccusedDetailsDomain;
import com.cctns.apprehend.core.domain.acts.ActSectionDomain;
import com.cctns.apprehend.core.domain.socialbg.JuvBackgroundReportDomain;

import java.util.List;

public interface SocialBackgroundViewUseCase {
    JuvBackgroundReportDomain getBgReport(JuvBackgroundReportDomain juvBackgroundReportDomain);

   // PageDomain<List<FirListDomain>> fetchFirListView(FirListDomain request);
   AccusedDetailsDomain fetchAccusedDetails(Long apprehendSrno);

   List<ActSectionDomain> getActSection(Long firRegNum);
}
