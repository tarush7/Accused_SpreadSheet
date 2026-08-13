package com.cctns.apprehend.core.usecase;

import com.cache.service.CacheService;
import com.cctns.apprehend.constants.Constants;
import com.cctns.apprehend.core.domain.AccusedProfileDomain;
import com.cctns.apprehend.core.domain.FileProcessDataDomainDisposal;
import com.cctns.apprehend.core.domain.FileSubmitDataDomain;
import com.cctns.apprehend.core.domain.FileSubmitDomain;
import com.cctns.apprehend.core.domain.FirListDisposalDomain;
import com.cctns.apprehend.core.domain.JuvDisposalReqDomain;
import com.cctns.apprehend.core.domain.JuvDisposalResponseDomain;
import com.cctns.apprehend.core.domain.JuvenileProfileDomain;
import com.cctns.apprehend.core.domain.PageDomain;
import com.cctns.apprehend.core.domain.disposal.JuvDisposalDomain;
import com.cctns.apprehend.core.domain.disposal.JuvDisposalFilesDomain;
import com.cctns.apprehend.core.exception.InvalidFlagException;
import com.cctns.apprehend.core.extport.MsComms;
import com.cctns.apprehend.core.repository.JuvenileDisposalRepository;
import com.cctns.apprehend.mapper.FileUploadMapper;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.cctns.apprehend.constants.Constants.FIR;
import static com.cctns.apprehend.constants.Constants.GD;
import static com.cctns.apprehend.constants.Constants.SUBMIT_GRID;
import static com.cctns.apprehend.constants.Constants.VIEW_GRID;

public class JuvenileDisposalUseCaseImpl implements JuvenileDisposalUseCase {
    private final JuvenileDisposalRepository juvenileDisposalRepository;
    private final FileUploadMapper fileUploadMapper;
    private final MsComms msComms;
    private final CacheService cacheService;

    public JuvenileDisposalUseCaseImpl(JuvenileDisposalRepository juvenileDisposalRepository, FileUploadMapper fileUploadMapper, MsComms msComms, CacheService cacheService) {
        this.juvenileDisposalRepository = juvenileDisposalRepository;
        this.fileUploadMapper = fileUploadMapper;
        this.msComms = msComms;
        this.cacheService = cacheService;
    }

    @Override
    public PageDomain<List<FirListDisposalDomain>> fetchDisposalFirList(FirListDisposalDomain request) {
        String gridFlag = request.getGridFlag();
        String firTypeFlag = request.getFirTypeFlag();

        if (SUBMIT_GRID.equalsIgnoreCase(gridFlag)) {

            if (FIR.equalsIgnoreCase(firTypeFlag)) {
                return juvenileDisposalRepository.fetchFirListPrepare(request);
            } else if (GD.equalsIgnoreCase(firTypeFlag)) {
                return juvenileDisposalRepository.fetchGdListPrepare(request);
            }

        } else if (VIEW_GRID.equalsIgnoreCase(gridFlag)) {

            if (FIR.equalsIgnoreCase(firTypeFlag)) {
                return juvenileDisposalRepository.fetchFirListView(request);
            } else if (GD.equalsIgnoreCase(firTypeFlag)) {
                return juvenileDisposalRepository.fetchGdListView(request);
            }
        }

        throw new InvalidFlagException(
                "Invalid combination of gridFlag: " + gridFlag +
                        " and firTypeFlag: " + firTypeFlag
        );
    }

    @Override
    public JuvDisposalResponseDomain submitJuvDisposal(JuvDisposalDomain request) {

        request.setRecordCreatedOn(LocalDateTime.now());
        request.setRecordCreatedBy(request.getStaffId());
        request.setRecordStatus(Constants.RECORD_STATUS_CREATED);

        //file upload
        uploadDisposalFiles(request);

        // Save and get response from repository
        JuvDisposalResponseDomain response = juvenileDisposalRepository.submitJuvDisposal(request);

        // Build final response using SAVED ID (not request)
        return JuvDisposalResponseDomain.builder()
                .juvDisposalSrno(response.getJuvDisposalSrno())
                .juvDisplayNum(response.getJuvDisposalSrno() + "/" + Year.now().getValue())
                .build();
    }

    @Override
    public JuvDisposalDomain getJuvDisposal(JuvDisposalReqDomain request) {
        JuvDisposalDomain response = juvenileDisposalRepository.getJuvDisposal(request);
        List<JuvDisposalFilesDomain> files=response.getFileList();
        if (files != null && !files.isEmpty()) {
            files.forEach(fileDetails -> {
                fileDetails.setFileType(cacheService.get(Constants.FILE_TYPE_MASTER_CD, response.getLangCd(), fileDetails.getFileTypeCd(), null));
                fileDetails.setFileSubtype(cacheService.get(Constants.FILE_SUB_TYPE_MASTER_CD,response.getLangCd(),fileDetails.getFileSubtypeCd(),null));
               }
            );
        }
        return response;
    }


    @Override
    public JuvenileProfileDomain fetchDetailsForDisposalPrepare(JuvenileProfileDomain request){
        String gridFlag = request.getGridFlag();
        String firTypeFlag = request.getFirTypeFlag();
        if (gridFlag == null) {
            throw new InvalidFlagException("gridFlag cannot be null");
        }
        if (SUBMIT_GRID.equalsIgnoreCase(gridFlag)) {
          //  return juvenileDisposalRepository.fetchDetailsForDisposalPrepare(request);
            if (FIR.equalsIgnoreCase(firTypeFlag)) {
                return juvenileDisposalRepository.fetchDetailsForDisposalPrepare(request);
            } else if (GD.equalsIgnoreCase(firTypeFlag)) {
                return juvenileDisposalRepository.fetchDetailsForGdPrepare(request);
            }
        }
        else if (VIEW_GRID.equalsIgnoreCase(gridFlag)) {
            if (FIR.equalsIgnoreCase(firTypeFlag)) {
                return juvenileDisposalRepository.fetchDetailsForDisposalView(request);
            } else if (GD.equalsIgnoreCase(firTypeFlag)) {
                return juvenileDisposalRepository.fetchDetailsForGdView(request);
            }
        }
        throw new InvalidFlagException("Invalid gridFlag: " + gridFlag);
    }

    private void uploadDisposalFiles(JuvDisposalDomain request) {

        FileProcessDataDomainDisposal fileUploadData =
                FileProcessDataDomainDisposal.builder()
                        .documents(request.getFileList())
                        .loginParams(request.getLoginparams())
                      //  .moduleNumber(UUID.randomUUID().toString())
                        .moduleNumber(String.valueOf(request.getApprehendSrno()))
                        .fileBelongsToSrno(request.getJuvDisposalSrno())
                        .fileBelongsTo(Constants.MODULE_NAME)
                        .langCd(request.getLangCd())
                        .staffId(request.getStaffId())
                        .build();

        List<JuvDisposalFilesDomain> files = processFileUpload(fileUploadData);

        request.setFileList(files);
    }

    //File Upload function
    private List<JuvDisposalFilesDomain> processFileUpload(FileProcessDataDomainDisposal fileProcessDataDomainDisposal) {
        if (fileProcessDataDomainDisposal.getDocuments() == null || fileProcessDataDomainDisposal.getDocuments().isEmpty()) {
            return Collections.emptyList();
        }
        // Step 1: Map to FileSubmitDataDomain
        List<FileSubmitDataDomain> finalFileDataList = fileProcessDataDomainDisposal.getDocuments().stream()
                .map(upload -> {
                    FileSubmitDataDomain fileData = fileUploadMapper.mapFileDomainToDataDomain(upload);
                    fileData.setModuleNumber(fileProcessDataDomainDisposal.getModuleNumber());
                    fileData.setModuleName(Constants.MODULE_NAME);
                    return fileData;
                })
                .toList();
        // Step 2: Call file upload microservice
        FileSubmitDomain fileUploadRestCall = new FileSubmitDomain();
        fileUploadRestCall.setFileUploadDataList(finalFileDataList);
        finalFileDataList = msComms.submitFiles(fileProcessDataDomainDisposal.getLoginParams(), fileUploadRestCall);
        // Step 3: Map back to entity & enrich data
        AtomicInteger counter = new AtomicInteger(1);
        return finalFileDataList.stream()
                .map(fileData -> {
                    JuvDisposalFilesDomain fileDomain = fileUploadMapper.mapFileDataDomainToFileDomainDisposal(fileData);
                    fileDomain.setJuvDisposalSrno(Long.valueOf(fileProcessDataDomainDisposal.getModuleNumber()));
                    fileDomain.setLangCd(fileProcessDataDomainDisposal.getLangCd());
                    fileDomain.setFileBelongsTo(fileProcessDataDomainDisposal.getFileBelongsTo());
                    fileDomain.setFileBelongsToSrno(fileProcessDataDomainDisposal.getFileBelongsToSrno());
                    fileDomain.setRecordStatus(Constants.RECORD_STATUS_CREATED);
                    fileDomain.setRecordCreatedOn(LocalDateTime.now());
                    fileDomain.setRecordCreatedBy(fileProcessDataDomainDisposal.getStaffId());
                    fileDomain.setFileSrno(counter.getAndIncrement());
                    return fileDomain;
                })
                .toList();
    }

}
