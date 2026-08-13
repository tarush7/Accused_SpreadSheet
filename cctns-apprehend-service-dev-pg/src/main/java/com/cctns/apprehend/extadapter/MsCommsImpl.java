package com.cctns.apprehend.extadapter;

import com.cctns.apprehend.core.domain.FileSubmitDataDomain;
import com.cctns.apprehend.core.domain.FileSubmitDomain;
import com.cctns.apprehend.core.extport.MsComms;
import com.cctns.apprehend.web.dto.response.ApiResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MsCommsImpl implements MsComms {

    private final FileUploadServiceClient fileUploadServiceClient;

    public MsCommsImpl(FileUploadServiceClient fileUploadServiceClient) {
        this.fileUploadServiceClient = fileUploadServiceClient;
    }


    @Override
    public List<FileSubmitDataDomain> submitFiles(String loginParams,FileSubmitDomain fileSubmitDomain){
        ApiResponse<List<FileSubmitDataDomain>> apiResponse=fileUploadServiceClient.submitFile(loginParams,fileSubmitDomain);
        return apiResponse.getData();
    }

}
