package com.cctns.apprehend.core.repository;

import com.cctns.apprehend.core.domain.ActSectionWrapper;
import com.cctns.apprehend.core.domain.ApprehendViewReqDomain;
import com.cctns.apprehend.core.domain.CiclGdAccusedDomain;
import com.cctns.apprehend.core.domain.FirListDomain;
import com.cctns.apprehend.core.domain.PageDomain;
import com.cctns.apprehend.core.domain.acts.ActSectionDomain;
import com.cctns.apprehend.core.domain.apprehend.ApprehendMemoDomain;

import java.util.List;
import java.util.Map;

public interface ApprehendViewRepository {
    ApprehendMemoDomain getApprehendMemo(ApprehendViewReqDomain reqDomain);

    Map<String, ActSectionWrapper> fetchActSectionMasterData(List<String> sectionCdList, Integer langCd);

     String getFirDisplayNum(Long firRegNum);

     String getGdDisplayNum(String gdNum);

     String getFirDate(Long firRegNum);

     List<ActSectionDomain> getCiclGdActSection(String ciclGdNum);

    PageDomain<List<CiclGdAccusedDomain>> fetchGdListView(CiclGdAccusedDomain request);

    PageDomain<List<CiclGdAccusedDomain>> fetchGdAccusedBg(CiclGdAccusedDomain request);
}
