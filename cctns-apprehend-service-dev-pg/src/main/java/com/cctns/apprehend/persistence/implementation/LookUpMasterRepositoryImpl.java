package com.cctns.apprehend.persistence.implementation;

import com.cctns.apprehend.core.repository.LookUpMasterRepository;
import com.cctns.apprehend.mapper.EntityDomainMapper;
import org.springframework.stereotype.Service;

@Service
public class LookUpMasterRepositoryImpl implements LookUpMasterRepository {

    private final LookUpMasterJdbcRepository lookUpMasterJdbcRepository;

    /**LookUpMasterJdbcRepository
     * Instantiates a new Look up master repo.
     *
     * @param lookUpMasterJdbcRepository the look up master jdbc repository
     * @param entityDomainMapper                the model mapper
     */
    public LookUpMasterRepositoryImpl(LookUpMasterJdbcRepository lookUpMasterJdbcRepository, EntityDomainMapper entityDomainMapper) {
        this.lookUpMasterJdbcRepository = lookUpMasterJdbcRepository;
    }

    /**
     * Implementation of fetchMasterValue of LookUpMasterRepo interface
     * This method fetch the master data
     *
     * @param apiMasterCd    the api master code
     * @param langCd         the lang code
     * @param lookUpCd       the look-up code
     * @param lookUpParentCd the look-up-parent code
     * @return Optional<MlcMasterDomain>
     */
    @Override
    public String fetchMasterValue(String apiMasterCd, Integer lookUpCd, Integer langCd,
                                   Integer lookUpParentCd) {
        return lookUpMasterJdbcRepository.fetchMasterValue(apiMasterCd, lookUpCd, langCd, lookUpParentCd);

    }

}
