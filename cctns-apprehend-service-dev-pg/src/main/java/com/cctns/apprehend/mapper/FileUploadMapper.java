package com.cctns.apprehend.mapper;

import com.cctns.apprehend.core.domain.FileSubmitDataDomain;
import com.cctns.apprehend.core.domain.apprehend.ApprehendFilesDomain;
import com.cctns.apprehend.core.domain.disposal.JuvDisposalFilesDomain;
import com.cctns.apprehend.core.domain.socialbg.JclBackgroundFilesDomain;
import com.cctns.apprehend.persistence.entity.apprehend.TApprehendFilesEntity;
import org.mapstruct.Mapper;


@Mapper(config = GlobalMapperConfig.class )
public interface FileUploadMapper {

  FileSubmitDataDomain mapFileDomainToDataDomain(ApprehendFilesDomain apprehendFilesDomain);

  FileSubmitDataDomain mapFileDomainToDataDomain(JclBackgroundFilesDomain bgFilesDomain);

  FileSubmitDataDomain mapFileDomainToDataDomain(JuvDisposalFilesDomain apprehendFilesDomain);

  ApprehendFilesDomain mapFileDataDomainToFileDomain(FileSubmitDataDomain fileSubmitDataDomain);

  JclBackgroundFilesDomain mapFileDataDomainToFileDomainBg(FileSubmitDataDomain fileSubmitDataDomain);

  JuvDisposalFilesDomain mapFileDataDomainToFileDomainDisposal(FileSubmitDataDomain fileSubmitDataDomain);

  ApprehendFilesDomain mapFileEntityToDomain(TApprehendFilesEntity fileUploadsEntity);
}