package com.cctns.apprehend.extadapter;

import com.cctns.apprehend.core.domain.FileSubmitDataDomain;
import com.cctns.apprehend.core.domain.FileSubmitDomain;
import com.cctns.apprehend.web.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "${fileUploadMsName}", url = "${fileUploadMsUrl}")
public interface FileUploadServiceClient {

    @PostMapping("${fileUploadUrl}")
    ApiResponse<List<FileSubmitDataDomain>> submitFile(@RequestHeader("loginparams") String loginParams, @RequestBody FileSubmitDomain fileUploadRestCall);
}

