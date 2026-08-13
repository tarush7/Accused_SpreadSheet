package com.cctns.apprehend.mapper;

import com.cctns.apprehend.core.domain.AccusedAddressDomain;
import com.cctns.apprehend.core.domain.AccusedDetailsDomain;
import com.cctns.apprehend.core.domain.AccusedListDomain;
import com.cctns.apprehend.core.domain.AccusedNationalIdDomain;
import com.cctns.apprehend.core.domain.CiclGdAccusedDomain;
import com.cctns.apprehend.core.domain.FileSubmitDataDomain;
import com.cctns.apprehend.core.domain.FirAccusedInfoUpdateDomain;
import com.cctns.apprehend.core.domain.FirListBgDomain;
import com.cctns.apprehend.core.domain.FirListDisposalDomain;
import com.cctns.apprehend.core.domain.FirListDomain;
import com.cctns.apprehend.core.domain.accused.FirAccusedAddressDomain;
import com.cctns.apprehend.core.domain.accused.FirAccusedFilesDomain;
import com.cctns.apprehend.core.domain.accused.FirAccusedInfoDomain;
import com.cctns.apprehend.core.domain.accused.FirAccusedNationalIdDomain;
import com.cctns.apprehend.core.domain.acts.ActSectionDomain;
import com.cctns.apprehend.core.domain.apprehend.ApprehendActSectionDomain;
import com.cctns.apprehend.core.domain.apprehend.ApprehendAddressDomain;
import com.cctns.apprehend.core.domain.apprehend.ApprehendFilesDomain;
import com.cctns.apprehend.core.domain.apprehend.ApprehendIntimateAddrDomain;
import com.cctns.apprehend.core.domain.apprehend.ApprehendMemoDomain;
import com.cctns.apprehend.core.domain.apprehend.ApprehendNationalIdDomain;
import com.cctns.apprehend.core.domain.apprehend.ApprehendWitnessAddrDomain;
import com.cctns.apprehend.core.domain.apprehend.ApprehendWitnessDomain;
import com.cctns.apprehend.core.domain.apprehend.ApprehendWitnessNationalityDomain;
import com.cctns.apprehend.core.domain.disposal.JuvDisposalDomain;
import com.cctns.apprehend.core.domain.disposal.JuvDisposalFilesDomain;
import com.cctns.apprehend.core.domain.socialbg.JclBackgroundFilesDomain;
import com.cctns.apprehend.core.domain.socialbg.JuvBackgroundReportDomain;
import com.cctns.apprehend.core.domain.socialbg.JuvDressDomain;
import com.cctns.apprehend.core.domain.socialbg.JuvFamilyDtlsDomain;
import com.cctns.apprehend.core.domain.socialbg.JuvIdentityMarksDomain;
import com.cctns.apprehend.core.domain.socialbg.JuvPhyAbuseDomain;
import com.cctns.apprehend.core.domain.socialbg.JuvPhyFeatureDomain;
import com.cctns.apprehend.persistence.entity.accused.FirAccusedInfoUpdateEntity;
import com.cctns.apprehend.persistence.entity.accused.TFirAccusedInfoEntity;
import com.cctns.apprehend.persistence.entity.apprehend.TApprehendActSectionEntity;
import com.cctns.apprehend.persistence.entity.apprehend.TApprehendAddressesEntity;
import com.cctns.apprehend.persistence.entity.apprehend.TApprehendFilesEntity;
import com.cctns.apprehend.persistence.entity.apprehend.TApprehendIntimateAddrEntity;
import com.cctns.apprehend.persistence.entity.apprehend.TApprehendMemoEntity;
import com.cctns.apprehend.persistence.entity.apprehend.TApprehendNationalIdEntity;
import com.cctns.apprehend.persistence.entity.apprehend.TApprehendWitnessAddrEntity;
import com.cctns.apprehend.persistence.entity.apprehend.TApprehendWitnessEntity;
import com.cctns.apprehend.persistence.entity.apprehend.TApprehendWitnessNationalityEntity;
import com.cctns.apprehend.persistence.entity.disposal.TJuvDisposalEntity;
import com.cctns.apprehend.persistence.entity.disposal.TJuvDisposalFilesEntity;
import com.cctns.apprehend.persistence.entity.socialbg.TJclBackgroundFilesEntity;
import com.cctns.apprehend.persistence.entity.socialbg.TJuvBackgroundReportEntity;
import com.cctns.apprehend.persistence.entity.socialbg.TJuvDressEntity;
import com.cctns.apprehend.persistence.entity.socialbg.TJuvFamilyDtlsEntity;
import com.cctns.apprehend.persistence.entity.socialbg.TJuvIdentityMarksEntity;
import com.cctns.apprehend.persistence.entity.socialbg.TJuvPhyAbuseEntity;
import com.cctns.apprehend.persistence.entity.socialbg.TJuvPhyFeatureEntity;
import com.cctns.apprehend.persistence.projection.AccusedAddressProjection;
import com.cctns.apprehend.persistence.projection.AccusedDetailsProjection;
import com.cctns.apprehend.persistence.projection.AccusedListProjection;
import com.cctns.apprehend.persistence.projection.AccusedNationalIdProjection;
import com.cctns.apprehend.persistence.projection.ActSectionProjection;
import com.cctns.apprehend.persistence.projection.CiclGdAccusedProjection;
import com.cctns.apprehend.persistence.projection.FirListBgProjection;
import com.cctns.apprehend.persistence.projection.FirListDisposalProjection;
import com.cctns.apprehend.persistence.projection.FirListProjection;
import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper( config = GlobalMapperConfig.class )
public interface EntityDomainMapper {

    ApprehendMemoDomain toDomain(TApprehendMemoEntity entity);

    JuvDisposalDomain toDomain(TJuvDisposalEntity entity);

    @Mapping(target = "apprehendMemo",ignore = true)
    ApprehendAddressDomain toDomain(TApprehendAddressesEntity entity);

    @Mapping(target = "apprehendMemo",ignore = true)
    ApprehendFilesDomain toDomain(TApprehendFilesEntity entity);

    @Mapping(target = "apprehendMemo",ignore = true)
    ApprehendWitnessDomain toDomain(TApprehendWitnessEntity entity);

    @Mapping(target = "apprehendWitness",ignore = true)
    ApprehendWitnessAddrDomain toDomain(TApprehendWitnessAddrEntity entity);

    @Mapping(target = "apprehendWitness",ignore = true)
    ApprehendWitnessNationalityDomain toDomain(TApprehendWitnessNationalityEntity entity);

    @Mapping(target = "apprehendMemo",ignore = true)
    ApprehendIntimateAddrDomain toDomain(TApprehendIntimateAddrEntity entity);

    @Mapping(target = "apprehendMemo",ignore = true)
    ApprehendActSectionDomain toDomain(TApprehendActSectionEntity entity);

    @Mapping(target = "apprehendMemo",ignore = true)
    ApprehendNationalIdDomain toDomain(TApprehendNationalIdEntity entity);

    JuvBackgroundReportDomain toDomain(TJuvBackgroundReportEntity entity);

    @Mapping(target = "juvBackgroundReport",ignore = true)
    JuvFamilyDtlsDomain toDomain(TJuvFamilyDtlsEntity entity);

    @Mapping(target = "juvBackgroundReport",ignore = true)
    JuvPhyAbuseDomain toDomain(TJuvPhyAbuseEntity entity);

    @Mapping(target = "juvBackgroundReport",ignore = true)
    JclBackgroundFilesDomain toDomain(TJclBackgroundFilesEntity entity);

    @Mapping(target = "juvBackgroundReport",ignore = true)
    JuvPhyFeatureDomain toDomain(TJuvPhyFeatureEntity entity);

    @Mapping(target = "juvBackgroundReport",ignore = true)
    JuvIdentityMarksDomain toDomain(TJuvIdentityMarksEntity entity);

    @Mapping(target = "juvBackgroundReport",ignore = true)
    JuvDressDomain toDomain(TJuvDressEntity entity);

    @Mapping(target = "juvDisposal",ignore = true)
    JuvDisposalFilesDomain toDomain(TJuvDisposalFilesEntity entity);

    FirAccusedInfoDomain toDomain(TFirAccusedInfoEntity entity, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    FirListDomain toDomain(FirListProjection projection);

    List<FirListDomain> toDomainFirList(List<FirListProjection> projections);

    List<CiclGdAccusedDomain> toDomainGdList(List<CiclGdAccusedProjection> projections);

    List<ActSectionDomain>  toDomainActSectionList(List<ActSectionProjection> projections);

    List<FirListBgDomain> toDomainBgList(List<FirListBgProjection> projections);

    List<ActSectionDomain> toDomainActList(List<ActSectionProjection> projections);

    List<AccusedListDomain> toDomainAccList(List<AccusedListProjection> projections);

    List<AccusedAddressDomain> toDomainAddressList(List<AccusedAddressProjection> projections);

    List<AccusedNationalIdDomain> toDomainNationalIdList(List<AccusedNationalIdProjection> projections);

    AccusedDetailsDomain toDomain(AccusedDetailsProjection projection);

    FirAccusedInfoUpdateDomain toDomain(FirAccusedInfoUpdateEntity entity);

    List<FirListDisposalDomain> toDomainDisposalList(List<FirListDisposalProjection> projections);


    void updateAccusedDetailsFromProjection(
            AccusedDetailsProjection source,
            @MappingTarget AccusedDetailsDomain target
    );

    @Mapping(source="idList",target="firAccusedNationalityList")
    @Mapping(source = "apprehendAddress", target = "firAccusedAddressList")
    @Mapping(source = "fileList", target = "firAccusedFilesList")
    FirAccusedInfoDomain toFirAccusedDomain(ApprehendMemoDomain source);

    @Mapping(source="firAccusedNationalityList",target="idList")
    @Mapping(source = "firAccusedAddressList", target = "apprehendAddress")
    @Mapping(source = "firAccusedFilesList", target = "fileList")
    ApprehendMemoDomain toApprehendAccusedDomain(FirAccusedInfoDomain source);

    FirAccusedInfoUpdateDomain toFirAccusedUpdateDomain(FirAccusedInfoDomain source);

    ApprehendMemoDomain toApprehendMemoDomain(FirAccusedInfoDomain source);

 //   FirAccusedAddressDomain toFirAccusedAddressDomain(ApprehendAddressDomain source);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "apprehendAddress", target = "firAccusedAddressList")
    @Mapping(source= "idList", target = "firAccusedNationalityList" )
    @Mapping(source= "fileList",target= "firAccusedFilesList")
    void updateFirAccusedFromMemo(
            ApprehendMemoDomain source,
            @MappingTarget FirAccusedInfoDomain target
    );

    FirAccusedAddressDomain toFirAccusedAddress(ApprehendAddressDomain source);

    List<FirAccusedAddressDomain> toFirAccusedAddressList(
            List<ApprehendAddressDomain> source);

    FirAccusedNationalIdDomain toFirNationalId(ApprehendNationalIdDomain source);

    List<FirAccusedNationalIdDomain> toFirAccusedNationalIdList(
            List<ApprehendNationalIdDomain> source);

    FirAccusedFilesDomain toFirFiles(ApprehendFilesDomain source);

    List<FirAccusedFilesDomain> toFirAccusedFilesList(
            List<ApprehendFilesDomain> source);

    FileSubmitDataDomain tofileDomain(ApprehendFilesDomain source);


    ApprehendFilesDomain tofileSubmitDomain(FileSubmitDataDomain source);

//    @Named("localDateToLocalDateTime")
//    default LocalDateTime localDateToLocalDateTime(LocalDate date) {
//        return date == null ? null : date.atStartOfDay();
//    }

}
