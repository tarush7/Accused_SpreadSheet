package com.cctns.apprehend.persistence.implementation;

import com.cctns.apprehend.core.domain.AccusedDetailsDomain;
import com.cctns.apprehend.core.domain.CourtDataDomain;
import com.cctns.apprehend.core.domain.FirListBgDomain;
import com.cctns.apprehend.core.domain.PageDomain;
import com.cctns.apprehend.core.domain.acts.ActSectionDomain;
import com.cctns.apprehend.core.domain.socialbg.JuvBackgroundReportDomain;
import com.cctns.apprehend.core.exception.ApprehendDetailsNotFoundException;
import com.cctns.apprehend.core.repository.SocialBackgroundViewRepository;
import com.cctns.apprehend.mapper.EntityDomainMapper;
import com.cctns.apprehend.persistence.entity.socialbg.TJuvBackgroundReportEntity;
import com.cctns.apprehend.persistence.projection.AccusedAddressProjection;
import com.cctns.apprehend.persistence.projection.AccusedDetailsProjection;
import com.cctns.apprehend.persistence.projection.ActSectionProjection;
import com.cctns.apprehend.persistence.projection.CourtDataProjection;
import com.cctns.apprehend.persistence.projection.FirListBgProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
@Service
public class SocialBackgroundViewRepositoryImpl implements SocialBackgroundViewRepository {
    private final SocialBackgroundViewJpaRepository socialBackgroundViewJpaRepository;
    private final SocialBackgroundPrepareJpaRepository socialBackgroundPrepareJpaRepository;
    private final EntityDomainMapper entityDomainMapper;

    public SocialBackgroundViewRepositoryImpl(SocialBackgroundViewJpaRepository socialBackgroundViewJpaRepository, SocialBackgroundPrepareJpaRepository socialBackgroundPrepareJpaRepository, EntityDomainMapper entityDomainMapper) {
        this.socialBackgroundViewJpaRepository = socialBackgroundViewJpaRepository;
        this.socialBackgroundPrepareJpaRepository = socialBackgroundPrepareJpaRepository;
        this.entityDomainMapper = entityDomainMapper;
    }

    public JuvBackgroundReportDomain getBgReport(JuvBackgroundReportDomain juvBackgroundReportDomain){
        Optional<TJuvBackgroundReportEntity> juvDetails=socialBackgroundViewJpaRepository.findById(juvBackgroundReportDomain.getBgReportSrno());
        //throw exception if no records found
        TJuvBackgroundReportEntity entity=juvDetails.orElseThrow(
                ()-> new ApprehendDetailsNotFoundException("Apprehend Details Not Found For Given SrNo")
        );
        //map entity to response object
        JuvBackgroundReportDomain response=entityDomainMapper.toDomain(entity);
        return response;
    }

    public  PageDomain<List<FirListBgDomain>> fetchFirListView(FirListBgDomain searchRequest){
        // Create pageable object
        Pageable pageRequest = PageRequest.of(
                searchRequest.getPageable().getPage(),
                searchRequest.getPageable().getPageSize()
        );
        // Fetching paginated projection result from DB
        Page<FirListBgProjection> projectionPage = socialBackgroundViewJpaRepository.getFirListView(
                searchRequest.getFirSrno(),
                searchRequest.getFromDate(),
                searchRequest.getToDate(),
                searchRequest.getPsIdList(),
                searchRequest.getYear(),
                pageRequest
        );
        // Direct list mapping
        List<FirListBgDomain> domainList =
                entityDomainMapper.toDomainBgList(projectionPage.getContent());

        if (domainList.isEmpty()) {
            return PageDomain.<List<FirListBgDomain>>builder()
                    .list(Collections.emptyList())
                    .totalSize(0L)
                    .pageCount(0)
                    .build();
        }
        return PageDomain.<List<FirListBgDomain>>builder()
                .list(domainList)
                .totalSize(projectionPage.getTotalElements())
                .pageCount(projectionPage.getTotalPages())
                .build();
    }

    public PageDomain<List<FirListBgDomain>> fetchGdListView(FirListBgDomain searchRequest){
        Pageable pageRequest = PageRequest.of(
                searchRequest.getPageable().getPage(),
                searchRequest.getPageable().getPageSize()
        );
        // Fetching paginated projection result from DB
        Page<FirListBgProjection> projectionPage = socialBackgroundViewJpaRepository.getGdListView(
                searchRequest.getFirSrno(),
                searchRequest.getFromDate(),
                searchRequest.getToDate(),
                searchRequest.getPsIdList(),
                searchRequest.getYear(),
                pageRequest
        );
        // Direct list mapping
        List<FirListBgDomain> domainList =
                entityDomainMapper.toDomainBgList(projectionPage.getContent());

        if (domainList.isEmpty()) {
            return PageDomain.<List<FirListBgDomain>>builder()
                    .list(Collections.emptyList())
                    .totalSize(0L)
                    .pageCount(0)
                    .build();
        }
        return PageDomain.<List<FirListBgDomain>>builder()
                .list(domainList)
                .totalSize(projectionPage.getTotalElements())
                .pageCount(projectionPage.getTotalPages())
                .build();
    }

    @Override
    public CourtDataDomain getCourtTypeAndName(String srcCourtTypeCd) {
        CourtDataProjection courtEntitydata = socialBackgroundViewJpaRepository.getCourtTypeAndName(srcCourtTypeCd);
        if (courtEntitydata == null) {
            return null;
        } else {
            return CourtDataDomain.builder()
                    .courtComplexCd(courtEntitydata.getCourtComplexCd())
                    .courtComplexName(courtEntitydata.getCourtComplexName())
                    .establishmentName(courtEntitydata.getEstablishmentName())
                    .cisDistrictName(courtEntitydata.getCisDistrictName())
                    .cisDistrictCd(courtEntitydata.getCisDistrictCd())
                    .build();
        }
    }

    @Override
    public AccusedDetailsDomain fetchAccusedDetails(Long apprehendSrno) {
        AccusedDetailsDomain accusedDetailsDomain =new AccusedDetailsDomain();
        AccusedDetailsProjection accDetails =
                socialBackgroundPrepareJpaRepository.getAccusedDetails(apprehendSrno);
        List<AccusedAddressProjection> accAddress =
                socialBackgroundPrepareJpaRepository.getAccusedAddress(apprehendSrno);
        // Map basic details into same object
        if (accDetails != null) {
            entityDomainMapper.updateAccusedDetailsFromProjection(accDetails, accusedDetailsDomain);
        }
        // Map address list
        accusedDetailsDomain.setAccusedAddress(
                (accAddress == null || accAddress.isEmpty())
                        ? List.of()
                        : entityDomainMapper.toDomainAddressList(accAddress)
        );
        return accusedDetailsDomain;
    }

    @Override
    public List<ActSectionDomain> getActSection(Long firRegNum) {

        List<ActSectionProjection> actSections =
                socialBackgroundPrepareJpaRepository.getActSectionList(firRegNum);

        if (actSections == null || actSections.isEmpty()) {
            return Collections.emptyList();
        }

        return entityDomainMapper.toDomainActList(actSections);
    }

}
