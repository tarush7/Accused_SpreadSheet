package com.cctns.apprehend.core.usecase;

import com.cctns.apprehend.core.domain.ApprehendViewReqDomain;
import com.cctns.apprehend.core.domain.CiclGdAccusedDomain;
import com.cctns.apprehend.core.domain.FirListDomain;
import com.cctns.apprehend.core.domain.PageDomain;
import com.cctns.apprehend.core.domain.apprehend.ApprehendMemoDomain;

import java.util.List;

public interface ApprehendViewUseCase {
    ApprehendMemoDomain getApprehendMemo(ApprehendViewReqDomain reqDomain);

    PageDomain<List<CiclGdAccusedDomain>> fetchCiclGdAccusedList(CiclGdAccusedDomain request);
}
