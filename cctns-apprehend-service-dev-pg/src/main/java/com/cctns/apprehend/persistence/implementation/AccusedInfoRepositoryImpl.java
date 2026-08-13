package com.cctns.apprehend.persistence.implementation;

import com.cctns.apprehend.core.domain.AccusedInfoSaveResultDomain;
import com.cctns.apprehend.core.domain.FirAccusedInfoUpdateDomain;
import com.cctns.apprehend.core.domain.accused.FirAccusedInfoDomain;
import com.cctns.apprehend.core.domain.accused.FirMultiAccusedDomain;
import com.cctns.apprehend.core.exception.AccusedDetailsNotFoundException;
import com.cctns.apprehend.core.repository.AccusedInfoRepository;
import com.cctns.apprehend.mapper.CycleAvoidingMappingContext;
import com.cctns.apprehend.mapper.DomainEntityMapper;
import com.cctns.apprehend.mapper.EntityDomainMapper;
import com.cctns.apprehend.persistence.entity.accused.FirAccusedInfoUpdateEntity;
import com.cctns.apprehend.persistence.entity.accused.TFirAccusedInfoEntity;
import com.cctns.apprehend.persistence.entity.accused.TFirMultiAccusedEntity;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class AccusedInfoRepositoryImpl implements AccusedInfoRepository {

    private final SrNoJpaRepository srNoJpaRepository;
    private final DomainEntityMapper domainEntityMapper;
    private final EntityDomainMapper entityDomainMapper;
    private final AccusedInfoJpaRepository accusedInfoJpaRepository;
    private final AccusedInfoUpdateJpaRepository accusedInfoUpdateJpaRepository;
    private final FirMultiAccusedJpaRepository firMultiAccusedJpaRepository;

    public AccusedInfoRepositoryImpl(SrNoJpaRepository srNoJpaRepository, DomainEntityMapper domainEntityMapper, EntityDomainMapper entityDomainMapper, AccusedInfoJpaRepository accusedInfoJpaRepository, AccusedInfoUpdateJpaRepository accusedInfoUpdateJpaRepository, FirMultiAccusedJpaRepository firMultiAccusedJpaRepository) {
        this.srNoJpaRepository = srNoJpaRepository;
        this.domainEntityMapper = domainEntityMapper;
        this.entityDomainMapper = entityDomainMapper;
        this.accusedInfoJpaRepository = accusedInfoJpaRepository;
        this.accusedInfoUpdateJpaRepository = accusedInfoUpdateJpaRepository;
        this.firMultiAccusedJpaRepository = firMultiAccusedJpaRepository;
    }

    @Transactional
    @Override
    public AccusedInfoSaveResultDomain submitAccusedInFir(FirAccusedInfoDomain request) {
        CycleAvoidingMappingContext cycleAvoidingMappingContext=new CycleAvoidingMappingContext();
        // DOMAIN → ENTITY
        TFirAccusedInfoEntity entity =
                domainEntityMapper.toEntity(request,cycleAvoidingMappingContext);

        // SAVE
        TFirAccusedInfoEntity savedEntity=accusedInfoJpaRepository.save(entity);
        return new AccusedInfoSaveResultDomain(savedEntity.getAccusedSrno(), savedEntity.getAccusedVid());
    }

    @Override
    public FirAccusedInfoDomain getDetailsById(Long accusedVid) {
        CycleAvoidingMappingContext cycleAvoidingMappingContext=new CycleAvoidingMappingContext();
        TFirAccusedInfoEntity entity = accusedInfoJpaRepository.findById(accusedVid)
                .orElseThrow(() ->
                        new AccusedDetailsNotFoundException("Accused Details not found for given accusedVid")
                );
        return entityDomainMapper.toDomain(entity,cycleAvoidingMappingContext);

    }

    @Override
    public FirAccusedInfoUpdateDomain getUpdateDetailsById(Long accusedVid) {
        return accusedInfoUpdateJpaRepository.findById(accusedVid)
                .map(entityDomainMapper::toDomain)
                .orElse(null);
    }

    @Override
    public void save(FirAccusedInfoUpdateDomain request) {
        FirAccusedInfoUpdateEntity entity=domainEntityMapper.toEntity(request);
        accusedInfoUpdateJpaRepository.save(entity);
    }

    @Override
    public void submitMultiAccused(FirMultiAccusedDomain request) {
        TFirMultiAccusedEntity entity=domainEntityMapper.toEntity(request);
        firMultiAccusedJpaRepository.save(entity);

    }

    @Override
    public Integer getPsCdById(Long psId) {
        return accusedInfoJpaRepository.getPsCdById(psId);
    }

}

