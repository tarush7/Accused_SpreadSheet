package com.cctns.apprehend.core.repository;

import com.cctns.apprehend.core.domain.ApprehendResponseDomain;
import com.cctns.apprehend.core.domain.apprehend.ApprehendMemoDomain;

public interface ApprehendSubmitRepository {
    ApprehendResponseDomain submitApprehendMemo(ApprehendMemoDomain request);

    void updateGdStatus(String gdNum,Long staffId);
}
