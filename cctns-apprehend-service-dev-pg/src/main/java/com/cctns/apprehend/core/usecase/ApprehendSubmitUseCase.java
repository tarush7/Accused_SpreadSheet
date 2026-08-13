package com.cctns.apprehend.core.usecase;

import com.cctns.apprehend.core.domain.ApprehendResponseDomain;
import com.cctns.apprehend.core.domain.FirAccusedInfoUpdateDomain;
import com.cctns.apprehend.core.domain.apprehend.ApprehendMemoDomain;

public interface ApprehendSubmitUseCase {

    ApprehendResponseDomain submitApprehendMemo(ApprehendMemoDomain request);

    void updateRecordStatus(FirAccusedInfoUpdateDomain request);

}
