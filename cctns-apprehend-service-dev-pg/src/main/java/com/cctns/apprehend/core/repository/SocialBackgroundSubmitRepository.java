package com.cctns.apprehend.core.repository;

import com.cctns.apprehend.core.domain.SocialBackgroundResponseDomain;
import com.cctns.apprehend.core.domain.socialbg.JuvBackgroundReportDomain;

public interface SocialBackgroundSubmitRepository {
    public SocialBackgroundResponseDomain submitSocialBgReport(JuvBackgroundReportDomain request);

}
