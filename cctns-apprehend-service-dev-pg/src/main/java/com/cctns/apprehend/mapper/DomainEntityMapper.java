package com.cctns.apprehend.mapper;

import com.cctns.apprehend.core.domain.ActSectionWrapper;
import com.cctns.apprehend.core.domain.FirAccusedInfoUpdateDomain;
import com.cctns.apprehend.core.domain.accused.FirAccusedInfoDomain;
import com.cctns.apprehend.core.domain.accused.FirMultiAccusedDomain;
import com.cctns.apprehend.core.domain.apprehend.ApprehendActSectionDomain;
import com.cctns.apprehend.core.domain.apprehend.ApprehendAddressDomain;
import com.cctns.apprehend.core.domain.apprehend.ApprehendFilesDomain;
import com.cctns.apprehend.core.domain.apprehend.ApprehendIntimateAddrDomain;
import com.cctns.apprehend.core.domain.apprehend.ApprehendMemoDomain;
import com.cctns.apprehend.core.domain.apprehend.ApprehendWitnessAddrDomain;
import com.cctns.apprehend.core.domain.apprehend.ApprehendWitnessDomain;
import com.cctns.apprehend.core.domain.apprehend.ApprehendWitnessNationalityDomain;
import com.cctns.apprehend.core.domain.disposal.JuvDisposalDomain;
import com.cctns.apprehend.core.domain.socialbg.JclBackgroundFilesDomain;
import com.cctns.apprehend.core.domain.socialbg.JuvBackgroundReportDomain;
import com.cctns.apprehend.core.domain.socialbg.JuvFamilyDtlsDomain;
import com.cctns.apprehend.core.domain.socialbg.JuvIdentityMarksDomain;
import com.cctns.apprehend.core.domain.socialbg.JuvPhyAbuseDomain;
import com.cctns.apprehend.core.domain.socialbg.JuvPhyFeatureDomain;
import com.cctns.apprehend.persistence.entity.accused.FirAccusedInfoUpdateEntity;
import com.cctns.apprehend.persistence.entity.accused.TFirAccusedInfoEntity;
import com.cctns.apprehend.persistence.entity.accused.TFirMultiAccusedEntity;
import com.cctns.apprehend.persistence.entity.apprehend.TApprehendActSectionEntity;
import com.cctns.apprehend.persistence.entity.apprehend.TApprehendAddressesEntity;
import com.cctns.apprehend.persistence.entity.apprehend.TApprehendFilesEntity;
import com.cctns.apprehend.persistence.entity.apprehend.TApprehendIntimateAddrEntity;
import com.cctns.apprehend.persistence.entity.apprehend.TApprehendMemoEntity;
import com.cctns.apprehend.persistence.entity.apprehend.TApprehendWitnessAddrEntity;
import com.cctns.apprehend.persistence.entity.apprehend.TApprehendWitnessEntity;
import com.cctns.apprehend.persistence.entity.apprehend.TApprehendWitnessNationalityEntity;
import com.cctns.apprehend.persistence.entity.disposal.TJuvDisposalEntity;
import com.cctns.apprehend.persistence.entity.socialbg.TJclBackgroundFilesEntity;
import com.cctns.apprehend.persistence.entity.socialbg.TJuvBackgroundReportEntity;
import com.cctns.apprehend.persistence.entity.socialbg.TJuvFamilyDtlsEntity;
import com.cctns.apprehend.persistence.entity.socialbg.TJuvIdentityMarksEntity;
import com.cctns.apprehend.persistence.entity.socialbg.TJuvPhyAbuseEntity;
import com.cctns.apprehend.persistence.entity.socialbg.TJuvPhyFeatureEntity;
import com.cctns.apprehend.persistence.projection.ActSectionProjection;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = GlobalMapperConfig.class )
public interface DomainEntityMapper {

    TApprehendMemoEntity toEntity(ApprehendMemoDomain domain);

    TFirAccusedInfoEntity toEntity(FirAccusedInfoDomain domain, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    TFirMultiAccusedEntity toEntity(FirMultiAccusedDomain domain);

    TApprehendAddressesEntity toEntity(ApprehendAddressDomain domain);

    TApprehendFilesEntity toEntity(ApprehendFilesDomain domain);

//    @Mapping(target = "witnessAddress",ignore = true)
    TApprehendWitnessEntity toEntity(ApprehendWitnessDomain domain);

    @Mapping(target="apprehendWitness",ignore = true)
    TApprehendWitnessAddrEntity toEntity(ApprehendWitnessAddrDomain domain);

    @Mapping(target = "apprehendWitness",ignore = true)
    TApprehendWitnessNationalityEntity toEntity(ApprehendWitnessNationalityDomain domain);

    TApprehendIntimateAddrEntity toEntity(ApprehendIntimateAddrDomain domain);

    TApprehendActSectionEntity toEntity(ApprehendActSectionDomain domain);

    TJuvBackgroundReportEntity toEntity(JuvBackgroundReportDomain domain);

    TJclBackgroundFilesEntity toEntity(JclBackgroundFilesDomain domain);

    TJuvFamilyDtlsEntity toEntity(JuvFamilyDtlsDomain domain);

    TJuvPhyAbuseEntity toEntity(JuvPhyAbuseDomain domain);

    TJuvIdentityMarksEntity toEntity(JuvIdentityMarksDomain domain);

    TJuvPhyFeatureEntity toEntity(JuvPhyFeatureDomain domain);

    TJuvDisposalEntity toEntity(JuvDisposalDomain domain);

    FirAccusedInfoUpdateEntity toEntity(FirAccusedInfoUpdateDomain domain);

    ActSectionWrapper mapActSectionProjectionToWrapper(ActSectionProjection actSectionProjection);

    @AfterMapping
    default void linkWitnessAddress(@MappingTarget TApprehendWitnessEntity witness){
        if(witness.getWitnessAddress() !=null){
            witness.getWitnessAddress().forEach(addr->{
                addr.setApprehendWitness(witness);
            });
        }
    }

    @AfterMapping
    default void linkWitnessNationality(@MappingTarget TApprehendWitnessEntity witness){
        if(witness.getIdList() !=null){
            witness.getIdList().forEach(id->{
                id.setApprehendWitness(witness);
            });
        }
    }
}

