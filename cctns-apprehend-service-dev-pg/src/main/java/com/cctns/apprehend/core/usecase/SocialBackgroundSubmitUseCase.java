package com.cctns.apprehend.core.usecase;

import com.cctns.apprehend.core.domain.SocialBackgroundResponseDomain;
import com.cctns.apprehend.core.domain.socialbg.JuvBackgroundReportDomain;

public interface SocialBackgroundSubmitUseCase {
    SocialBackgroundResponseDomain submitSocialBgReport(JuvBackgroundReportDomain request);
}
