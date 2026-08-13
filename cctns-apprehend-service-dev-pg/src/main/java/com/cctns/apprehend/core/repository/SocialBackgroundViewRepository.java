package com.cctns.apprehend.core.repository;

import com.cctns.apprehend.core.domain.AccusedDetailsDomain;
import com.cctns.apprehend.core.domain.CourtDataDomain;
import com.cctns.apprehend.core.domain.FirListBgDomain;
import com.cctns.apprehend.core.domain.PageDomain;
import com.cctns.apprehend.core.domain.acts.ActSectionDomain;
import com.cctns.apprehend.core.domain.socialbg.JuvBackgroundReportDomain;

import java.util.List;

public interface SocialBackgroundViewRepository {
    JuvBackgroundReportDomain getBgReport(JuvBackgroundReportDomain juvBackgroundReportDomain);

    PageDomain<List<FirListBgDomain>> fetchFirListView(FirListBgDomain request);

    PageDomain<List<FirListBgDomain>> fetchGdListView(FirListBgDomain request);

    CourtDataDomain getCourtTypeAndName(String srcCourtTypeCd);

    AccusedDetailsDomain fetchAccusedDetails(Long apprehendSrno);

    List<ActSectionDomain> getActSection(Long firRegNum);

}
