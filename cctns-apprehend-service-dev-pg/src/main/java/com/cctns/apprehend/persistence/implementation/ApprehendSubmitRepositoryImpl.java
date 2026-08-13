package com.cctns.apprehend.persistence.implementation;

import com.cctns.apprehend.core.domain.ApprehendResponseDomain;
import com.cctns.apprehend.core.domain.apprehend.ApprehendMemoDomain;
import com.cctns.apprehend.core.repository.ApprehendSubmitRepository;
import com.cctns.apprehend.mapper.DomainEntityMapper;
import com.cctns.apprehend.persistence.entity.apprehend.TApprehendMemoEntity;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class ApprehendSubmitRepositoryImpl implements ApprehendSubmitRepository {
    private final ApprehendSubmitJpaRepository apprehendSubmitJpaRepository;
    private final DomainEntityMapper domainEntityMapper;

    public ApprehendSubmitRepositoryImpl(ApprehendSubmitJpaRepository apprehendSubmitJpaRepository, DomainEntityMapper domainEntityMapper) {
        this.apprehendSubmitJpaRepository = apprehendSubmitJpaRepository;
        this.domainEntityMapper = domainEntityMapper;
    }

    @Transactional
    @Override
    public ApprehendResponseDomain submitApprehendMemo(ApprehendMemoDomain request) {

        //Domain to entity mapper
        TApprehendMemoEntity entity = domainEntityMapper.toEntity(request);

        // Save entity
        TApprehendMemoEntity savedEntity = apprehendSubmitJpaRepository.save(entity);

        //create response
        ApprehendResponseDomain response = new ApprehendResponseDomain();
        response.setApprehendSrno(savedEntity.getApprehendSrno());
        return response;
    }

    @Override
    public void updateGdStatus(String gdNum, Long staffId) {
        apprehendSubmitJpaRepository.updateGdStatus(gdNum,staffId);
    }

}
