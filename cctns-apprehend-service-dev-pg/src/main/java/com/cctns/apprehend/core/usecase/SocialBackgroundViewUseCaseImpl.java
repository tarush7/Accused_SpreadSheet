package com.cctns.apprehend.core.usecase;

import com.cache.service.CacheService;
import com.cctns.apprehend.constants.Constants;
import com.cctns.apprehend.core.domain.AccusedDetailsDomain;
import com.cctns.apprehend.core.domain.CourtDataDomain;
import com.cctns.apprehend.core.domain.acts.ActSectionDomain;
import com.cctns.apprehend.core.domain.socialbg.JclBackgroundFilesDomain;
import com.cctns.apprehend.core.domain.socialbg.JuvBackgroundReportDomain;
import com.cctns.apprehend.core.domain.socialbg.PhysicalFeatureDescDomain;
import com.cctns.apprehend.core.repository.ApprehendViewRepository;
import com.cctns.apprehend.core.repository.LookUpMasterRepository;
import com.cctns.apprehend.core.repository.SocialBackgroundViewRepository;

import java.util.List;

public class SocialBackgroundViewUseCaseImpl implements SocialBackgroundViewUseCase {
    private final SocialBackgroundViewRepository socialBackgroundViewRepository;
    private final ApprehendViewRepository apprehendViewRepository;
    private final LookUpMasterRepository lookUpMasterRepository;
    private final CacheService cacheService;

    public SocialBackgroundViewUseCaseImpl(SocialBackgroundViewRepository socialBackgroundViewRepository, ApprehendViewRepository apprehendViewRepository, LookUpMasterRepository lookUpMasterRepository, CacheService cacheService) {
        this.socialBackgroundViewRepository = socialBackgroundViewRepository;
        this.apprehendViewRepository = apprehendViewRepository;
        this.lookUpMasterRepository = lookUpMasterRepository;
        this.cacheService = cacheService;
    }

    public JuvBackgroundReportDomain getBgReport(JuvBackgroundReportDomain juvBackgroundReportDomain) {
        JuvBackgroundReportDomain response = socialBackgroundViewRepository.getBgReport(juvBackgroundReportDomain);
        setPhysicalDescription(response);
        setCourtDetails(response);
        setFileType(response);
        setFamilyMaster(response);
      //  response.setActSectionList(getActSection(response.getFirRegNum()));
        if (response.getFirRegNum() != null) {
            response.setActSectionList(getActSection(response.getFirRegNum()));
        } else {
            response.setActSectionList(getGdActSection(juvBackgroundReportDomain.getCiclGdNum()));
        }
        response.setAccusedDetails(socialBackgroundViewRepository.fetchAccusedDetails(response.getApprehendSrno()));
        return response;
    }


    private void setPhysicalDescription(JuvBackgroundReportDomain responseDomain) {

        PhysicalFeatureDescDomain physicalDescription = new PhysicalFeatureDescDomain();
        physicalDescription.setBodyBuildTypeCd(responseDomain.getBodyBuildTypeCd());
        physicalDescription.setBodyComplexionTypeCd(responseDomain.getBodyComplexionTypeCd());
        physicalDescription.setBodyBuildType(cacheService.get(Constants.PHY_FEAT,responseDomain.getLangCd(), responseDomain.getBodyBuildTypeCd(), null));
        physicalDescription.setBodyComplexionType(cacheService.get(Constants.PHY_FEAT,responseDomain.getLangCd(), responseDomain.getBodyComplexionTypeCd(), null));
        physicalDescription.setHeightFromCm(responseDomain.getHeightFromCm());
        physicalDescription.setHeightToCm(responseDomain.getHeightToCm());
        physicalDescription.setOtherPhysicalDetails(responseDomain.getOtherPhysicalDetails());
        physicalDescription.setDressTypeList(responseDomain.getDressTypeList());
        physicalDescription.setIdentityMarkList(responseDomain.getIdentityMarkList());
        physicalDescription.setPhysicalFeaturesList(responseDomain.getPhysicalFeaturesList());
        physicalDescription.getIdentityMarkList().forEach(idMarks -> idMarks.setIdMarksType(cacheService.get(Constants.IDEN_MARKS, responseDomain.getLangCd(), idMarks.getIdMarksTypeCd(), null)));
        physicalDescription.getIdentityMarkList().forEach(idMarks -> idMarks.setBodyPartLoc(cacheService.get(Constants.PHY_FEAT, responseDomain.getLangCd(), idMarks.getBodyPartLocCd(), null)));
        physicalDescription.getPhysicalFeaturesList().forEach(phyFeat -> phyFeat.setPhyFeatureMin(cacheService.get(Constants.PHY_FEAT, responseDomain.getLangCd(), phyFeat.getPhyFeatureMinCd(), null)));
        if (physicalDescription.getDressTypeList() != null) {
            physicalDescription.getDressTypeList().forEach(dress -> {
                if (dress.getDressForCd() != null) {
                    dress.setDressFor(cacheService.get(
                            Constants.PHY_MASTER_CODE, responseDomain.getLangCd(), dress.getDressForCd(), null));
                }
            });
        }

        responseDomain.setPhysicalDescription(physicalDescription);
    }

    private void setCourtDetails(JuvBackgroundReportDomain response) {
        var courtEstblCd = response.getCourtEstblCd();
        if (courtEstblCd == null)
            return;
        CourtDataDomain court = getCourtTypeAndName(courtEstblCd);
        if (court == null) return;
        response.setEstablishmentName(court.getEstablishmentName());
        response.setCourtComplexCd(court.getCourtComplexCd());
        response.setCourtComplexName(court.getCourtComplexName());
        response.setCisDistrictCd(court.getCisDistrictCd());
        response.setCisDistrictName(court.getCisDistrictName());
    }

    private void setFileType(JuvBackgroundReportDomain response){
        List<JclBackgroundFilesDomain> files=response.getBgFiles();
        if (files != null && !files.isEmpty()) {
            files.forEach(fileDetails -> {
                        fileDetails.setFileType(cacheService.get(Constants.FILE_TYPE_MASTER_CD, response.getLangCd(), fileDetails.getFileTypeCd(), null));
                        fileDetails.setFileSubtype(cacheService.get(Constants.FILE_SUB_TYPE_MASTER_CD,response.getLangCd(),fileDetails.getFileSubtypeCd(),null));
                    }
            );
        }
    }

     private String fetchMasterValue(String masterCode, Integer valueCd, Integer langCd, Integer parentCd) {
        return lookUpMasterRepository.fetchMasterValue(masterCode, valueCd, langCd, parentCd
        );
    }

    public CourtDataDomain getCourtTypeAndName(String srcCourtTypeCd) {
        CourtDataDomain courtData = socialBackgroundViewRepository.getCourtTypeAndName(srcCourtTypeCd);
        if(courtData == null) {
            return null;
        }else {
            return CourtDataDomain.builder()
                    .courtComplexCd(courtData.getCourtComplexCd())
                    .courtComplexName(courtData.getCourtComplexName())
                    .establishmentName(courtData.getEstablishmentName())
                    .cisDistrictCd(courtData.getCisDistrictCd())
                    .cisDistrictName(courtData.getCisDistrictName())
                    .build();
        }
    }

    private void setFamilyMaster(JuvBackgroundReportDomain response){
       response.getFamilyDtls().forEach(family->{
           family.setOccupation(cacheService.get(Constants.OCCUPATION_MASTER_CODE, response.getLangCd(), family.getOccupationCd(), null));
           family.setRelationType(cacheService.get(Constants.RELATION_TYP_MASTER_CODE, response.getLangCd(), family.getRelationTypeCd(), null));
       });
    }


    @Override
    public AccusedDetailsDomain fetchAccusedDetails(Long apprehendSrno) {
        return socialBackgroundViewRepository.fetchAccusedDetails(apprehendSrno);
    }

    @Override
    public List<ActSectionDomain> getActSection(Long firRegNum) {
        return socialBackgroundViewRepository.getActSection(firRegNum);
    }


    private List<ActSectionDomain> getGdActSection(String ciclGdNum) {
        return apprehendViewRepository.getCiclGdActSection(ciclGdNum);
    }
}
