package com.cctns.apprehend.core.extport;

import com.cctns.apprehend.core.domain.FileSubmitDataDomain;
import com.cctns.apprehend.core.domain.FileSubmitDomain;

import java.util.List;

/**
 * This is the external port interface for external microservice communications :
 */
public interface MsComms {

    List<FileSubmitDataDomain> submitFiles(String loginParams,FileSubmitDomain fileSubmitDomain);

}



