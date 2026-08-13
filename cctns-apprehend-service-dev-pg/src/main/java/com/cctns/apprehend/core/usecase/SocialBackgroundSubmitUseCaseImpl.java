package com.cctns.apprehend.core.usecase;

import com.cctns.apprehend.constants.Constants;
import com.cctns.apprehend.core.domain.FileProcessDataDomainBg;
import com.cctns.apprehend.core.domain.FileSubmitDataDomain;
import com.cctns.apprehend.core.domain.FileSubmitDomain;
import com.cctns.apprehend.core.domain.SocialBackgroundResponseDomain;
import com.cctns.apprehend.core.domain.socialbg.JclBackgroundFilesDomain;
import com.cctns.apprehend.core.domain.socialbg.JuvBackgroundReportDomain;
import com.cctns.apprehend.core.domain.socialbg.PhysicalFeatureDescDomain;
import com.cctns.apprehend.core.extport.MsComms;
import com.cctns.apprehend.core.repository.SocialBackgroundSubmitRepository;
import com.cctns.apprehend.mapper.FileUploadMapper;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SocialBackgroundSubmitUseCaseImpl implements SocialBackgroundSubmitUseCase {

    private final SocialBackgroundSubmitRepository socialBackgroundSubmitRepository;
    private final FileUploadMapper fileUploadMapper;
    private final MsComms msComms;

    public SocialBackgroundSubmitUseCaseImpl(SocialBackgroundSubmitRepository socialBackgroundSubmitRepository, FileUploadMapper fileUploadMapper, MsComms msComms) {
        this.socialBackgroundSubmitRepository = socialBackgroundSubmitRepository;
        this.fileUploadMapper = fileUploadMapper;
        this.msComms = msComms;
    }

    @Transactional
    @Override
    public SocialBackgroundResponseDomain submitSocialBgReport(JuvBackgroundReportDomain request) {

        // Set parent audit fields
        request.setRecordCreatedOn(LocalDateTime.now());
        request.setRecordCreatedBy(request.getStaffId());
        request.setRecordStatus(Constants.RECORD_STATUS_CREATED);

        // Set child audit fields ONLY (NO FK SETTING)
        if (request.getBgFiles() != null) {
            request.getBgFiles().forEach(obj -> {
                obj.setLangCd(request.getLangCd());
                obj.setRecordCreatedOn(LocalDateTime.now());
                obj.setRecordCreatedBy(request.getStaffId());
                obj.setRecordStatus(Constants.RECORD_STATUS_CREATED);
            });
        }

        if (request.getPhyAbuse() != null) {
            request.getPhyAbuse().forEach(obj -> {
                obj.setId(null);
                obj.setLangCd(request.getLangCd());
                obj.setRecordCreatedOn(LocalDateTime.now());
                obj.setRecordCreatedBy(request.getStaffId());
                obj.setRecordStatus(Constants.RECORD_STATUS_CREATED);
            });
        }

        if (request.getFamilyDtls() != null) {
            request.getFamilyDtls().forEach(obj -> {
                obj.setId(null);
                obj.setLangCd(request.getLangCd());
                obj.setRecordCreatedOn(LocalDateTime.now());
                obj.setRecordCreatedBy(request.getStaffId());
                obj.setRecordStatus(Constants.RECORD_STATUS_CREATED);
            });
        }

        //file upload
        uploadDisposalFiles(request);

        // =========================
        // Physical Description Child Lists
        // =========================
        if (request.getPhysicalDescription() != null) {

            PhysicalFeatureDescDomain physicalDescription = request.getPhysicalDescription();

            request.setBodyBuildTypeCd(physicalDescription.getBodyBuildTypeCd());
            request.setBodyComplexionTypeCd(physicalDescription.getBodyComplexionTypeCd());
            request.setHeightFromCm(physicalDescription.getHeightFromCm());
            request.setHeightToCm(physicalDescription.getHeightToCm());
            request.setOtherPhysicalDetails(physicalDescription.getOtherPhysicalDetails());

            // Move child lists to parent request
            request.setPhysicalFeaturesList(physicalDescription.getPhysicalFeaturesList());
            request.setIdentityMarkList(physicalDescription.getIdentityMarkList());
            request.setDressTypeList(physicalDescription.getDressTypeList());

            // Physical Features
            if (physicalDescription.getPhysicalFeaturesList() != null) {
                physicalDescription.getPhysicalFeaturesList().forEach(obj -> {
                    obj.setId(null);
                    obj.setLangCd(request.getLangCd());
                    obj.setRecordCreatedOn(LocalDateTime.now());
                    obj.setRecordCreatedBy(request.getStaffId());
                    obj.setRecordStatus(Constants.RECORD_STATUS_CREATED);
                });
            }

            // Identity Marks
            if (physicalDescription.getIdentityMarkList() != null) {
                physicalDescription.getIdentityMarkList().forEach(obj -> {
                    obj.setId(null);
                    obj.setLangCd(request.getLangCd());
                    obj.setRecordCreatedOn(LocalDateTime.now());
                    obj.setRecordCreatedBy(request.getStaffId());
                    obj.setRecordStatus(Constants.RECORD_STATUS_CREATED);
                });
            }

            // Dress Details
            if (physicalDescription.getDressTypeList() != null) {
                physicalDescription.getDressTypeList().forEach(obj -> {
                    obj.setId(null);
                    obj.setLangCd(request.getLangCd());
                    obj.setFirRegNum(request.getFirRegNum());
                    obj.setRecordCreatedOn(LocalDateTime.now());
                    obj.setRecordCreatedBy(request.getStaffId());
                    obj.setRecordStatus(Constants.RECORD_STATUS_CREATED);
                });
            }
        }

        // Save and get response from repository
        SocialBackgroundResponseDomain response =
                socialBackgroundSubmitRepository.submitSocialBgReport(request);

        // Build final response using SAVED ID (not request)
        return SocialBackgroundResponseDomain.builder()
                .bgReportSrno(response.getBgReportSrno())
                .bgDisplay(response.getBgReportSrno() + "/" + Year.now().getValue())
                .build();
    }

    private void uploadDisposalFiles(JuvBackgroundReportDomain request) {

        FileProcessDataDomainBg fileUploadData =
                FileProcessDataDomainBg.builder()
                        .documents(request.getBgFiles())
                        .loginParams(request.getLoginparams())
                        .moduleNumber(String.valueOf(request.getApprehendSrno()))
                        .fileBelongsToSrno(request.getApprehendSrno())
                        .fileBelongsTo(Constants.MODULE_NAME)
                        .langCd(request.getLangCd())
                        .staffId(request.getStaffId())
                        .build();

        List<JclBackgroundFilesDomain> files = processFileUpload(fileUploadData);

        request.setBgFiles(files);
    }

    //File Upload function
    private List<JclBackgroundFilesDomain> processFileUpload(FileProcessDataDomainBg fileProcessDataDomainBg) {
        if (fileProcessDataDomainBg.getDocuments() == null || fileProcessDataDomainBg.getDocuments().isEmpty()) {
            return Collections.emptyList();
        }
        // Step 1: Map to FileSubmitDataDomain
        List<FileSubmitDataDomain> finalFileDataList = fileProcessDataDomainBg.getDocuments().stream()
                .map(upload -> {
                    FileSubmitDataDomain fileData = fileUploadMapper.mapFileDomainToDataDomain(upload);
                    fileData.setModuleNumber(fileProcessDataDomainBg.getModuleNumber());
                    fileData.setModuleName(Constants.MODULE_NAME);
                    return fileData;
                })
                .toList();
        // Step 2: Call file upload microservice
        FileSubmitDomain fileUploadRestCall = new FileSubmitDomain();
        fileUploadRestCall.setFileUploadDataList(finalFileDataList);
        finalFileDataList = msComms.submitFiles(fileProcessDataDomainBg.getLoginParams(), fileUploadRestCall);
        // Step 3: Map back to entity & enrich data
        AtomicInteger counter = new AtomicInteger(1);
        return finalFileDataList.stream()
                .map(fileData -> {
                    JclBackgroundFilesDomain fileDomain = fileUploadMapper.mapFileDataDomainToFileDomainBg(fileData);
                    fileDomain.setBgReportSrno(Long.valueOf(fileProcessDataDomainBg.getModuleNumber()));
                    fileDomain.setLangCd(fileProcessDataDomainBg.getLangCd());
                    fileDomain.setFileBelongsTo(fileProcessDataDomainBg.getFileBelongsTo());
                    fileDomain.setFileBelongsToSrno(fileProcessDataDomainBg.getFileBelongsToSrno());
                    fileDomain.setRecordStatus(Constants.RECORD_STATUS_CREATED);
                    fileDomain.setRecordCreatedOn(LocalDateTime.now());
                    fileDomain.setRecordCreatedBy(fileProcessDataDomainBg.getStaffId());
                    fileDomain.setFileSrno(counter.getAndIncrement());
                    return fileDomain;
                })
                .toList();
    }

}

