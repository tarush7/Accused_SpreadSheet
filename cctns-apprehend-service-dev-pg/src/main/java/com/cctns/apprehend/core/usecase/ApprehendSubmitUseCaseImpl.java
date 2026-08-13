package com.cctns.apprehend.core.usecase;

import com.cctns.apprehend.constants.Constants;
import com.cctns.apprehend.core.domain.AccusedInfoSaveResultDomain;
import com.cctns.apprehend.core.domain.ApprehendResponseDomain;
import com.cctns.apprehend.core.domain.FileProcessDataDomainApprehend;
import com.cctns.apprehend.core.domain.FileSubmitDataDomain;
import com.cctns.apprehend.core.domain.FileSubmitDomain;
import com.cctns.apprehend.core.domain.FirAccusedInfoUpdateDomain;
import com.cctns.apprehend.core.domain.accused.FirAccusedInfoDomain;
import com.cctns.apprehend.core.domain.accused.FirMultiAccusedDomain;
import com.cctns.apprehend.core.domain.apprehend.ApprehendFilesDomain;
import com.cctns.apprehend.core.domain.apprehend.ApprehendMemoDomain;
import com.cctns.apprehend.core.exception.NotFoundException;
import com.cctns.apprehend.core.extport.MsComms;
import com.cctns.apprehend.core.repository.AccusedInfoRepository;
import com.cctns.apprehend.core.repository.ApprehendSubmitRepository;
import com.cctns.apprehend.core.repository.SequenceGeneratorRepoService;
import com.cctns.apprehend.core.repository.SrNoRepository;
import com.cctns.apprehend.mapper.EntityDomainMapper;
import com.cctns.apprehend.mapper.FileUploadMapper;
import com.cctns.apprehend.persistence.projection.NextSeqNumberProjection;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ApprehendSubmitUseCaseImpl implements ApprehendSubmitUseCase {
    private final ApprehendSubmitRepository apprehendSubmitRepository;
    private final AccusedInfoRepository accusedInfoRepository;
    private final SrNoRepository srNoRepository;
    private final SequenceGeneratorRepoService sequenceGeneratorRepoService;
    private final EntityDomainMapper entityDomainMapper;
    private final FileUploadMapper fileUploadMapper;
    private final MsComms msComms;

    public ApprehendSubmitUseCaseImpl(ApprehendSubmitRepository apprehendSubmitRepository, AccusedInfoRepository accusedInfoRepository,
                                      SrNoRepository srNoRepository, SequenceGeneratorRepoService sequenceGeneratorRepoService,
                                      EntityDomainMapper entityDomainMapper, FileUploadMapper fileUploadMapper, MsComms msComms) {
        this.apprehendSubmitRepository = apprehendSubmitRepository;
        this.accusedInfoRepository = accusedInfoRepository;
        this.srNoRepository = srNoRepository;
        this.sequenceGeneratorRepoService = sequenceGeneratorRepoService;
        this.entityDomainMapper = entityDomainMapper;
        this.fileUploadMapper = fileUploadMapper;
        this.msComms = msComms;
    }

    @Transactional
    @Override
    public ApprehendResponseDomain submitApprehendMemo(ApprehendMemoDomain request) {

        LocalDateTime now = LocalDateTime.now();
        Integer psCd = accusedInfoRepository.getPsCdById(request.getPsId());
        request.setApprehendYear(Year.now().getValue());

        // Generate apprehend memo sequence number
        NextSeqNumberProjection arrestMemoCurrentSrNo =
                srNoRepository.getAndUpdateSrNo(
                        request.getPsId(),
                        request.getApprehendYear(),
                        Constants.APPREHEND_REG_TYPE_CD
                );

        Long nextSrNo = arrestMemoCurrentSrNo.getNextSeqNum().longValue();
        nextSrNo++;

        Long apprehendSrNo = generateApprehendMemoRegNum(
                nextSrNo.intValue(),
                arrestMemoCurrentSrNo.getPsCd()
        );

        // Prepare main request details
        prepareMainRequest(request, apprehendSrNo, now);

        // Prepare child entities
     //   prepareActSections(request, apprehendSrNo);
        prepareAddresses(request, apprehendSrNo);
        prepareIntimateAddresses(request, apprehendSrNo);
        prepareWitnessDetails(request, apprehendSrNo);
        prepareNationalId(request,apprehendSrNo);

        // Upload files
        uploadDisposalFiles(request);

        // Handle new accused not from GD
        if (Boolean.TRUE.equals(request.getIsNew())
                && Boolean.FALSE.equals(request.getIsFromGd())) {

            saveNewAccused(request, psCd);

            if(Boolean.TRUE.equals(request.getIsExistingAccused())){
                submitAccusedInMultiAccused(request);
            }
            apprehendSubmitRepository.submitApprehendMemo(request);
        }

        // Handle existing accused not from GD
        else if (Boolean.FALSE.equals(request.getIsNew())
                && Boolean.FALSE.equals(request.getIsFromGd())) {

            updateExistingAccused(request);
            apprehendSubmitRepository.submitApprehendMemo(request);
        }

        // Handle accused from GD
        else if (Boolean.TRUE.equals(request.getIsNew())
                && Boolean.TRUE.equals(request.getIsFromGd())) {

            apprehendSubmitRepository.submitApprehendMemo(request);
        }
        else {
            throw new IllegalArgumentException(
                    "Invalid combination of flags"
            );
        }
        // Updating is_gd_used flag in t_gd_entry all successful submits
        if (request.getGdNum() != null) {
            apprehendSubmitRepository.updateGdStatus(
                    request.getGdNum(),
                    request.getStaffId()
            );
        }

        return ApprehendResponseDomain.builder()
                .apprehendSrno(apprehendSrNo)
                .apprehendDisplay(nextSrNo + "/" + request.getApprehendYear())
                .build();
    }

    /**
     * Prepare main request details
     */
    private void prepareMainRequest(
            ApprehendMemoDomain request,
            Long apprehendSrNo,
            LocalDateTime now
    ) {

        request.setId(apprehendSrNo);
        request.setApprehendSrno(apprehendSrNo);

        request.setRecordCreatedOn(now);
        request.setRecordCreatedBy(request.getStaffId());
        request.setRecordStatus(Constants.RECORD_STATUS_CREATED);
    }

    /**
     * Prepare act section details
     */
//    private void prepareActSections(
//            ApprehendMemoDomain request,
//            Long apprehendSrNo
//    ) {

//        if (request.getActSectionList() != null) {
//
//            request.getActSectionList().forEach(obj -> {
//
//                obj.setApprehendActSrno(null);
//                obj.setApprehendSrno(apprehendSrNo);
//                obj.setLangCd(request.getLangCd());
//
//                obj.setRecordCreatedOn(LocalDateTime.now());
//                obj.setRecordCreatedBy(request.getStaffId());
//                obj.setRecordStatus(Constants.RECORD_STATUS_CREATED);
//            });
//        }
//    }

    /**
     * Prepare apprehend address details
     */
    private void prepareAddresses(
            ApprehendMemoDomain request,
            Long apprehendSrNo
    ) {

        if (request.getApprehendAddress() != null) {

            request.getApprehendAddress().forEach(obj -> {
                obj.setApprAddrSrno(null);
                obj.setApprehendSrno(apprehendSrNo);
                obj.setLangCd(request.getLangCd());

                obj.setRecordCreatedOn(LocalDateTime.now());
                obj.setRecordCreatedBy(request.getStaffId());
                obj.setRecordStatus(Constants.RECORD_STATUS_CREATED);
            });
        }
    }

    /**
     * Prepare intimate address details
     */
    private void prepareIntimateAddresses(
            ApprehendMemoDomain request,
            Long apprehendSrNo
    ) {

        if (request.getIntimateAddress() != null) {

            request.getIntimateAddress().forEach(obj -> {

                obj.setIntmtAddrSrno(null);
                obj.setApprehendSrno(apprehendSrNo);
                obj.setLangCd(request.getLangCd());

                obj.setRecordCreatedOn(LocalDateTime.now());
                obj.setRecordCreatedBy(request.getStaffId());
                obj.setRecordStatus(Constants.RECORD_STATUS_CREATED);
            });
        }
    }

    /**
     * Prepare apprehend address details
     */
    private void prepareNationalId(
            ApprehendMemoDomain request,
            Long apprehendSrNo
    ) {

        if (request.getIdList() != null) {

            request.getIdList().forEach(obj -> {
                obj.setNationalIdSrno(null);
                obj.setApprehendSrno(apprehendSrNo);
                obj.setLangCd(request.getLangCd());

                obj.setRecordCreatedOn(LocalDateTime.now());
                obj.setRecordCreatedBy(request.getStaffId());
                obj.setRecordStatus(Constants.RECORD_STATUS_CREATED);
            });
        }
    }

    /**
     * Prepare witness details
     */
    private void prepareWitnessDetails(
            ApprehendMemoDomain request,
            Long apprehendSrNo
    ) {

        if (request.getApprehendWitness() != null) {

            request.getApprehendWitness().forEach(obj -> {

                obj.setApprWitnsSrno(null);
                obj.setApprehendSrno(apprehendSrNo);
                obj.setLangCd(request.getLangCd());

                obj.setRecordCreatedOn(LocalDateTime.now());
                obj.setRecordCreatedBy(request.getStaffId());
                obj.setRecordStatus(Constants.RECORD_STATUS_CREATED);

                if (obj.getWitnessAddress() != null) {

                    obj.getWitnessAddress().forEach(addr -> {

                        addr.setApprWitnAddrSrno(null);
                        addr.setLangCd(request.getLangCd());
                        addr.setRecordCreatedOn(LocalDateTime.now());
                        addr.setRecordCreatedBy(request.getStaffId());
                        addr.setRecordStatus(Constants.RECORD_STATUS_CREATED);
                    });
                }
                if (obj.getIdList() != null) {

                    obj.getIdList().forEach(id -> {

                    //    id.setApprehendWitness(obj);
                        id.setApprWitnNatSrno(null);
                        id.setLangCd(request.getLangCd());
                        id.setRecordCreatedOn(LocalDateTime.now());
                        id.setRecordCreatedBy(request.getStaffId());
                        id.setRecordStatus(Constants.RECORD_STATUS_CREATED);
                    });
                }
            });
        }
    }

    /**
     * Save new accused details
     */
    private void saveNewAccused(
            ApprehendMemoDomain request,
            Integer psCd
    ) {

        request.setAccusedSrno(getAccusedSequence(psCd));

        FirAccusedInfoDomain firDomain =
                entityDomainMapper.toFirAccusedDomain(request);

        setParentReference(firDomain);

        firDomain.setRegTypeCd(Constants.REG_TYPE_ARREST);

        AccusedInfoSaveResultDomain persistedAccusedInfo =
                accusedInfoRepository.submitAccusedInFir(firDomain);

        request.setJuvenileVid(persistedAccusedInfo.getAccusedVid());
        request.setJuvenileSrno(persistedAccusedInfo.getAccusedSrno());
    }

    /**
     * Update existing accused details
     */
    private void updateExistingAccused(ApprehendMemoDomain request) {

        FirAccusedInfoDomain infoDomain =
                accusedInfoRepository.getDetailsById(request.getAccusedVid());

        FirAccusedInfoUpdateDomain updateDomain =
                entityDomainMapper.toFirAccusedUpdateDomain(infoDomain);

        updateRecordStatus(updateDomain);

        entityDomainMapper.updateFirAccusedFromMemo(request, infoDomain);

        resetChildPrimaryKeys(infoDomain);

        infoDomain.setAccusedVid(null);
        infoDomain.setCrmSeqNum(null);
        infoDomain.setRegTypeCd(Constants.REG_TYPE_ARREST);

        AccusedInfoSaveResultDomain persistedAccusedInfo =
                accusedInfoRepository.submitAccusedInFir(infoDomain);

        request.setJuvenileVid(persistedAccusedInfo.getAccusedVid());
        request.setJuvenileSrno(persistedAccusedInfo.getAccusedSrno());
    }

    /**
     * Set parent reference in child entities
     */
    private void setParentReference(FirAccusedInfoDomain firDomain) {

        if (firDomain.getFirAccusedAddressList() != null) {

            firDomain.getFirAccusedAddressList()
                    .forEach(obj -> obj.setAccused(firDomain));
        }

        if (firDomain.getFirAccusedFilesList() != null) {

            firDomain.getFirAccusedFilesList()
                    .forEach(obj -> obj.setAccused(firDomain));
        }

        if (firDomain.getFirAccusedNationalityList() != null) {

            firDomain.getFirAccusedNationalityList()
                    .forEach(obj -> obj.setAccused(firDomain));
        }
    }

    /**
     * Reset child entity primary keys
     */
    private void resetChildPrimaryKeys(FirAccusedInfoDomain infoDomain) {

        if (infoDomain.getFirAccusedAddressList() != null) {

            infoDomain.getFirAccusedAddressList().forEach(obj -> {
                obj.setAccused(infoDomain);
                obj.setFirAccAddrSrno(null);
            });
        }

        if (infoDomain.getFirAccusedFilesList() != null) {

            infoDomain.getFirAccusedFilesList().forEach(obj -> {
                obj.setAccused(infoDomain);
                obj.setAccusedFileSrno(null);
            });
        }

        if (infoDomain.getFirAccusedBankcardDetailList() != null) {

            infoDomain.getFirAccusedBankcardDetailList().forEach(obj -> {
                obj.setAccused(infoDomain);
                obj.setBankcardIdSrno(null);
            });
        }

        if (infoDomain.getFirAccusedNationalityList() != null) {

            infoDomain.getFirAccusedNationalityList().forEach(obj -> {
                obj.setAccused(infoDomain);
                obj.setNationalIdSrno(null);
            });
        }

        if (infoDomain.getFirAccusedPhyFeatureList() != null) {

            infoDomain.getFirAccusedPhyFeatureList().forEach(obj -> {
                obj.setAccused(infoDomain);
                obj.setAccPhyFeatSrno(null);
            });
        }

        if (infoDomain.getFirAccusedIdMarkList() != null) {

            infoDomain.getFirAccusedIdMarkList().forEach(obj -> {
                obj.setAccused(infoDomain);
                obj.setFirAccIdMarksSrno(null);
            });
        }
    }

    private void uploadDisposalFiles(ApprehendMemoDomain request) {

        FileProcessDataDomainApprehend fileUploadData =
                FileProcessDataDomainApprehend.builder()
                        .documents(request.getFileList())
                        .loginParams(request.getLoginparams())
                        .moduleNumber(String.valueOf(request.getApprehendSrno()))
                        .fileBelongsToSrno(request.getApprehendSrno())
                        .fileBelongsTo(Constants.MODULE_NAME)
                        .langCd(request.getLangCd())
                        .staffId(request.getStaffId())
                        .build();

        List<ApprehendFilesDomain> files =
                processFileUpload(fileUploadData);

 //      files.forEach(file->file.setApprehendMemo(request));
        request.setFileList(files);
    }

    //File Upload function
    private List<ApprehendFilesDomain> processFileUpload(FileProcessDataDomainApprehend fileProcessDataDomainApprehend) {
        if (fileProcessDataDomainApprehend.getDocuments() == null || fileProcessDataDomainApprehend.getDocuments().isEmpty()) {
            return Collections.emptyList();
        }
        // Step 1: Map to FileSubmitDataDomain
        List<FileSubmitDataDomain> finalFileDataList = fileProcessDataDomainApprehend.getDocuments().stream()
                .map(upload -> {
                    FileSubmitDataDomain fileData = fileUploadMapper.mapFileDomainToDataDomain(upload);
                    fileData.setModuleNumber(fileProcessDataDomainApprehend.getModuleNumber());
                    fileData.setModuleName(Constants.MODULE_NAME);
                    return fileData;
                })
                .toList();
        // Step 2: Call file upload microservice
        FileSubmitDomain fileUploadRestCall = new FileSubmitDomain();
        fileUploadRestCall.setFileUploadDataList(finalFileDataList);
        finalFileDataList = msComms.submitFiles(fileProcessDataDomainApprehend.getLoginParams(), fileUploadRestCall);
        // Step 3: Map back to entity & enrich data
        AtomicInteger counter = new AtomicInteger(1);
        return finalFileDataList.stream()
                .map(fileData -> {
                    ApprehendFilesDomain fileDomain = fileUploadMapper.mapFileDataDomainToFileDomain(fileData);
                    fileDomain.setApprehendSrno(Long.valueOf(fileProcessDataDomainApprehend.getModuleNumber()));
                    fileDomain.setLangCd(fileProcessDataDomainApprehend.getLangCd());
                    fileDomain.setFileBelongsTo(fileProcessDataDomainApprehend.getFileBelongsTo());
                    fileDomain.setFileBelongsToSrno(fileProcessDataDomainApprehend.getFileBelongsToSrno());
                    fileDomain.setRecordStatus(Constants.RECORD_STATUS_CREATED);
                    fileDomain.setRecordCreatedOn(LocalDateTime.now());
                    fileDomain.setRecordCreatedBy(fileProcessDataDomainApprehend.getStaffId());
                    fileDomain.setFileSrno(counter.getAndIncrement());
                    return fileDomain;
                })
                .toList();
    }

    @Override
    public void updateRecordStatus(FirAccusedInfoUpdateDomain request) {
        FirAccusedInfoUpdateDomain infoDomain = accusedInfoRepository.getUpdateDetailsById(request.getAccusedVid());
        if (infoDomain == null) {
            throw new NotFoundException("No record found for AccusedDisposalSrno: " + request.getAccusedVid());
        }
        infoDomain.setRecordStatus(Constants.RECORD_STATUS_DELETED);
        infoDomain.setRecordUpdatedOn(LocalDateTime.now());
        infoDomain.setRecordUpdatedBy(request.getStaffId());  //yet to fix

        //save
        accusedInfoRepository.save(infoDomain);
    }

    public Long generateApprehendMemoRegNum(Integer srNo, Integer psCd) {

        StringBuilder builder = new StringBuilder();
        String formattedYear = LocalDate.now().format(DateTimeFormatter.ofPattern("yy"));
        builder.append(psCd).append(formattedYear).append(String.format("%06d", srNo));

        return Long.parseLong(builder.toString());
    }

    private Long getAccusedSequence(Integer psCd) {
        Integer regYear = LocalDateTime.now().getYear();
        return sequenceGeneratorRepoService.getAccusedNextSequence(psCd, regYear, Constants.REG_TYPE_FIR);
    }

    private void submitAccusedInMultiAccused(ApprehendMemoDomain request) {
        FirMultiAccusedDomain multiAccusedDomain = new FirMultiAccusedDomain();

        multiAccusedDomain.setLangCd(request.getLangCd());
        multiAccusedDomain.setAccusedVid(request.getJuvenileVid());  //accusedVid getting generated in t_fir_accused_info
        multiAccusedDomain.setAccusedSrno(request.getJuvenileSrno());  //accusedSrno which generating for this accused
        multiAccusedDomain.setExistAccusedSrno(request.getExistingAccSrno()); //accusedSrno coming from add from existing api
        multiAccusedDomain.setExistFirRegNum(request.getExistingAccFirRegNum()); //firRegNum coming from add from existing api
        multiAccusedDomain.setFirRegNum(request.getFirRegNum());                 //firRegNum for current request
        multiAccusedDomain.setFullName(buildFullName(request.getFirstName(), request.getMiddleName(), request.getLastName()));
        multiAccusedDomain.setRecordStatus(Constants.RECORD_STATUS_CREATED);
        multiAccusedDomain.setRecordCreatedOn(LocalDateTime.now());
        multiAccusedDomain.setRecordCreatedBy(request.getStaffId());

        accusedInfoRepository.submitMultiAccused(multiAccusedDomain);
    }

    private String buildFullName(String first, String middle, String last) {
        return java.util.stream.Stream.of(first, middle, last)
                .filter(java.util.Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(java.util.stream.Collectors.joining(" "));
    }

}
