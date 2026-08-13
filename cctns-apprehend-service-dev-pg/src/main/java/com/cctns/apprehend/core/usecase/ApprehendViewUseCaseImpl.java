package com.cctns.apprehend.core.usecase;

import com.cache.service.CacheService;
import com.cctns.apprehend.constants.Constants;
import com.cctns.apprehend.core.domain.ActSectionWrapper;
import com.cctns.apprehend.core.domain.ApprehendViewReqDomain;
import com.cctns.apprehend.core.domain.CiclGdAccusedDomain;
import com.cctns.apprehend.core.domain.PageDomain;
import com.cctns.apprehend.core.domain.acts.ActSectionDomain;
import com.cctns.apprehend.core.domain.apprehend.ApprehendAddressDomain;
import com.cctns.apprehend.core.domain.apprehend.ApprehendFilesDomain;
import com.cctns.apprehend.core.domain.apprehend.ApprehendIntimateAddrDomain;
import com.cctns.apprehend.core.domain.apprehend.ApprehendMemoDomain;
import com.cctns.apprehend.core.domain.apprehend.ApprehendNationalIdDomain;
import com.cctns.apprehend.core.domain.apprehend.ApprehendWitnessAddrDomain;
import com.cctns.apprehend.core.domain.apprehend.ApprehendWitnessDomain;
import com.cctns.apprehend.core.exception.InvalidFlagException;
import com.cctns.apprehend.core.repository.ApprehendViewRepository;
import com.cctns.apprehend.core.repository.SocialBackgroundViewRepository;
import com.cctns.apprehend.persistence.implementation.LookUpValueRepository;
import com.cctns.apprehend.persistence.projection.AddressMasterValuesProjection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ApprehendViewUseCaseImpl implements ApprehendViewUseCase {

    private final ApprehendViewRepository apprehendViewRepository;
    private final SocialBackgroundViewRepository socialBackgroundViewRepository;
    private final LookUpValueRepository lookUpValueRepository;
    private final CacheService cacheService;

    public ApprehendViewUseCaseImpl(ApprehendViewRepository apprehendViewRepository, SocialBackgroundViewRepository socialBackgroundViewRepository, LookUpValueRepository lookUpValueRepository, CacheService cacheService) {
        this.apprehendViewRepository = apprehendViewRepository;
        this.socialBackgroundViewRepository = socialBackgroundViewRepository;
        this.lookUpValueRepository = lookUpValueRepository;
        this.cacheService = cacheService;
    }
    public ApprehendMemoDomain getApprehendMemo(ApprehendViewReqDomain reqDomain) {

        ApprehendMemoDomain response = apprehendViewRepository.getApprehendMemo(reqDomain);

        setAddresses(response);
        setMasterValues(response);
        if(response.getIsFromGd()){
            response.setActSectionList(getCiclGdActSection(response.getCiclGdNum()));
        }

        return response;
    }

    @Override
    public PageDomain<List<CiclGdAccusedDomain>> fetchCiclGdAccusedList(CiclGdAccusedDomain request) {
        String flag= request.getFlag();

        if(Constants.APPR_FLAG.equalsIgnoreCase(flag)) {
            return apprehendViewRepository.fetchGdListView(request);
        }
        else if(Constants.BG_FLAG.equalsIgnoreCase(flag)){
            return apprehendViewRepository.fetchGdAccusedBg(request);
        }
        throw new InvalidFlagException("Invalid flag: " + flag);
    }

    private void setMasterValues(ApprehendMemoDomain response){
        List<ApprehendWitnessDomain> apprehendWitnessList = response.getApprehendWitness();
        if (apprehendWitnessList != null && !apprehendWitnessList.isEmpty()) {
            apprehendWitnessList.forEach(arrestWitnessDetails -> {
                        arrestWitnessDetails.setWitnEvidTender(cacheService.get(Constants.EVIDENCE_TENDERED_CODE, arrestWitnessDetails.getLangCd(), arrestWitnessDetails.getWitnEvidTenderCd(), null));
                    }
            );
        }

        List<ApprehendFilesDomain> files=response.getFileList();
        if (files != null && !files.isEmpty()) {
            files.forEach(fileDetails -> {
                        fileDetails.setFileType(cacheService.get(Constants.FILE_TYPE_MASTER_CD, response.getLangCd(), fileDetails.getFileTypeCd(), null));
                        fileDetails.setFileSubtype(cacheService.get(Constants.FILE_SUB_TYPE_MASTER_CD,response.getLangCd(),fileDetails.getFileSubtypeCd(),null));
                    }
            );
        }

      for(ApprehendWitnessDomain witnessDomain:response.getApprehendWitness()){
          witnessDomain.getIdList().forEach((id->{
              id.setNationalIdType(cacheService.get(Constants.NATIONAL_ID_TYPE,response.getLangCd(),id.getNationalIdTypeCd(),null));
          }));
      }

     List<ApprehendNationalIdDomain> nationalId=response.getIdList();
      nationalId.forEach(id->id.setNationalIdType(cacheService.get(Constants.NATIONAL_ID_TYPE,response.getLangCd(),id.getNationalIdTypeCd(),null)));

        if (response != null) {

            if (response.getFirRegNum() != null) {
                response.setActSectionList(getActSection(response.getFirRegNum()));
                response.setFirDisplayNum(apprehendViewRepository.getFirDisplayNum(response.getFirRegNum()));
                response.setFirRegDt(apprehendViewRepository.getFirDate(response.getFirRegNum()));
            }

            if (response.getApprFromStateId() != null) {
                response.setApprFromState(
                        cacheService.get(
                                Constants.MASTER_STATE_KEY,
                                response.getLangCd(),
                                Math.toIntExact(response.getApprFromStateId()),
                                null
                        )
                );
            }

            if (response.getApprFromDistrictId() != null) {
                response.setApprFromDistrict(
                        cacheService.get(
                                Constants.MASTER_DISTRICT_KEY,
                                response.getLangCd(),
                                Math.toIntExact(response.getApprFromDistrictId()),
                                null
                        )
                );
            }

            if (response.getApprFromPsId() != null) {
                response.setApprFromPs(
                        cacheService.get(
                                Constants.MASTER_PS_KEY,
                                response.getLangCd(),
                                Math.toIntExact(response.getApprFromPsId()),
                                null
                        )
                );
            }

            if (response.getStateId() != null) {
                response.setState(
                        cacheService.get(
                                Constants.MASTER_STATE_KEY,
                                response.getLangCd(),
                                Math.toIntExact(response.getStateId()),
                                null
                        )
                );
            }

            if (response.getDistrictId() != null) {
                response.setDistrict(
                        cacheService.get(
                                Constants.MASTER_DISTRICT_KEY,
                                response.getLangCd(),
                                Math.toIntExact(response.getDistrictId()),
                                null
                        )
                );
            }

            if (response.getPsId() != null) {
                response.setPs(
                        cacheService.get(
                                Constants.MASTER_PS_KEY,
                                response.getLangCd(),
                                Math.toIntExact(response.getPsId()),
                                null
                        )
                );
            }

            if (response.getGdNum() != null) {
                response.setGdDisplayNum(apprehendViewRepository.getGdDisplayNum(response.getGdNum()));
            }

            if(response.getCiclGdNum() !=null){
                response.setCiclGdDisplayNum(apprehendViewRepository.getGdDisplayNum(response.getCiclGdNum()));
            }

            if(response.getIntimateRelTypeCd()!=null){
                response.setIntimateRelType(cacheService.get(Constants.RELATION_TYP_MASTER_CODE,response.getLangCd(),response.getIntimateRelTypeCd(),null));
            }

        }

    }

    private void setAddresses(ApprehendMemoDomain responseDomain){

        // Apprehend Address
        if (responseDomain.getApprehendAddress() != null) {
            for (ApprehendAddressDomain address : responseDomain.getApprehendAddress()) {
                setAddressMasterValues(
                        address.getLangCd(),
                        address.getAddressTypeCd(),
                        address.getCountryCd(),
                        address.getStateId(),
                        address.getLgDistrictCd(),
                        address.getSubDistrictCd() != null ? address.getSubDistrictCd().longValue() : null,
                        address.getVillageCd(),
                        address.getPsId(),
                        address
                );
            }
        }

        // Witness Address
        if (responseDomain.getApprehendWitness() != null) {
            for (ApprehendWitnessDomain witness : responseDomain.getApprehendWitness()) {
                if (witness.getWitnessAddress() != null) {
                    for (ApprehendWitnessAddrDomain witnessAddress : witness.getWitnessAddress()) {
                        setAddressMasterValues(
                                witnessAddress.getLangCd(),
                                witnessAddress.getAddressTypeCd(),
                                witnessAddress.getCountryCd(),
                                witnessAddress.getStateId(),
                                witnessAddress.getLgDistrictCd(),
                                witnessAddress.getSubDistrictCd() != null ? witnessAddress.getSubDistrictCd().longValue() : null,
                                witnessAddress.getVillageCd(),
                                witnessAddress.getPsId(),
                                witnessAddress
                        );
                    }
                }
            }
        }

        // Intimate Address
        if (responseDomain.getIntimateAddress() != null) {
                    for (ApprehendIntimateAddrDomain intimateAddress : responseDomain.getIntimateAddress()) {
                        setAddressMasterValues(
                                intimateAddress.getLangCd(),
                                intimateAddress.getAddressTypeCd(),
                                intimateAddress.getCountryCd(),
                                intimateAddress.getStateId(),
                                intimateAddress.getLgDistrictCd(),
                                intimateAddress.getSubDistrictCd() != null ? intimateAddress.getSubDistrictCd().longValue() : null,
                                intimateAddress.getVillageCd(),
                                intimateAddress.getPsId(),
                                intimateAddress
                        );
                    }
                }
    }

    private void setAddressMasterValues(
            Integer langCd,
            Integer addressTypeCd,
            Integer countryCd,
            Long stateId,
            Integer lgDistrictCd,
            Long subDistrictCd,
            Long villageCd,
            Long psId,
            Object addressObj
    ) {

        if (langCd == null) {
            return;
        }

        AddressMasterValuesProjection projection = lookUpValueRepository.getAddressMasterValues(langCd, addressTypeCd, countryCd, stateId, lgDistrictCd, subDistrictCd, villageCd, psId);

        if (projection == null) {
            return;
        }

        if (addressObj instanceof ApprehendAddressDomain address) {

            address.setAddressType(projection.getAddressType());
            address.setCountry(projection.getCountry());
            address.setState(projection.getState());
            address.setDistrict(projection.getDistrict());
            address.setSubDistrict(projection.getSubDistrict());
            address.setVillage(projection.getVillage());
            address.setPs(projection.getPs());
        }

        else if (addressObj instanceof ApprehendWitnessAddrDomain witnessAddress) {

            witnessAddress.setAddressType(projection.getAddressType());
            witnessAddress.setCountry(projection.getCountry());
            witnessAddress.setState(projection.getState());
            witnessAddress.setDistrict(projection.getDistrict());
            witnessAddress.setSubDistrict(projection.getSubDistrict());
            witnessAddress.setVillage(projection.getVillage());
            witnessAddress.setPs(projection.getPs());
        }

        else if (addressObj instanceof ApprehendIntimateAddrDomain intimateAddress) {

            intimateAddress.setAddressType(projection.getAddressType());
            intimateAddress.setCountry(projection.getCountry());
            intimateAddress.setState(projection.getState());
            intimateAddress.setDistrict(projection.getDistrict());
            intimateAddress.setSubDistrict(projection.getSubDistrict());
            intimateAddress.setVillage(projection.getVillage());
            intimateAddress.setPs(projection.getPs());
        }
    }

    private void getActSectionData(ApprehendMemoDomain response){
        if (response.getActSectionList() != null && !response.getActSectionList().isEmpty()) {

            List<String> sectionCdList = new ArrayList<>();
            response.getActSectionList().forEach(actSection -> sectionCdList.add(actSection.getSectionCd()));

            Map<String, ActSectionWrapper> wrapperMap = apprehendViewRepository.fetchActSectionMasterData(sectionCdList, response.getLangCd());
            response.getActSectionList().forEach(actSection -> {
                ActSectionWrapper actSectionWrapper = wrapperMap.get(actSection.getSectionCd());
                if (actSectionWrapper != null) {
                    actSection.setSection(actSectionWrapper.getSection());
                    actSection.setActLong(actSectionWrapper.getActLong());
                    actSection.setActShort(actSectionWrapper.getActShort());
                    actSection.setSectionDesc(actSectionWrapper.getSectionDesc());
                }
            });
        }
    }

    public List<ActSectionDomain> getActSection(Long firRegNum) {
        return socialBackgroundViewRepository.getActSection(firRegNum);
    }

    public List<ActSectionDomain>getCiclGdActSection(String ciclGdNum){
        return apprehendViewRepository.getCiclGdActSection(ciclGdNum);
    }

}
