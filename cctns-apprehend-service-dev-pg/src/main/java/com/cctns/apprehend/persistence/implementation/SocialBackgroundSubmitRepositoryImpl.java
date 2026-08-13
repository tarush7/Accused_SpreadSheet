package com.cctns.apprehend.persistence.implementation;

import com.cctns.apprehend.core.domain.SocialBackgroundResponseDomain;
import com.cctns.apprehend.core.domain.socialbg.JuvBackgroundReportDomain;
import com.cctns.apprehend.core.repository.SocialBackgroundSubmitRepository;
import com.cctns.apprehend.mapper.DomainEntityMapper;
import com.cctns.apprehend.persistence.entity.socialbg.TJuvBackgroundReportEntity;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class SocialBackgroundSubmitRepositoryImpl implements SocialBackgroundSubmitRepository {
    private final DomainEntityMapper domainEntityMapper;
    private final SocialBackgroundSubmitJpaRepository socialBackgroundSubmitJpaRepository;

    public SocialBackgroundSubmitRepositoryImpl(DomainEntityMapper domainEntityMapper, SocialBackgroundSubmitJpaRepository socialBackgroundSubmitJpaRepository) {
        this.domainEntityMapper = domainEntityMapper;
        this.socialBackgroundSubmitJpaRepository = socialBackgroundSubmitJpaRepository;
    }
    @Transactional
    @Override
    public SocialBackgroundResponseDomain submitSocialBgReport(JuvBackgroundReportDomain request){
        //domain to entity mapper
        TJuvBackgroundReportEntity entity=domainEntityMapper.toEntity(request);
        // SET PARENT REFERENCE HERE
        if (entity.getFamilyDtls() != null) {
            entity.getFamilyDtls().forEach(child -> child.setJuvBackgroundReport(entity));
        }

        if (entity.getPhyAbuse() != null) {
            entity.getPhyAbuse().forEach(child -> child.setJuvBackgroundReport(entity));
        }

        if (entity.getBgFiles() != null) {
            entity.getBgFiles().forEach(child -> child.setJuvBackgroundReport(entity));
        }

        if (entity.getPhysicalFeaturesList() != null) {
            entity.getPhysicalFeaturesList().forEach(child -> child.setJuvBackgroundReport(entity));
        }

        if (entity.getIdentityMarkList() != null) {
            entity.getIdentityMarkList().forEach(child -> child.setJuvBackgroundReport(entity));
        }

        if(entity.getDressTypeList() !=null){
            entity.getDressTypeList().forEach(child->child.setJuvBackgroundReport(entity));
        }
        //save entity
        TJuvBackgroundReportEntity savedEntity=socialBackgroundSubmitJpaRepository.save(entity);
        //create response
        SocialBackgroundResponseDomain response=new SocialBackgroundResponseDomain();
        response.setBgReportSrno(savedEntity.getBgReportSrno());
        return response;
    }
}
