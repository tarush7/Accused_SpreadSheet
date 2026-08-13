package com.cctns.apprehend.persistence.implementation;

import com.cctns.apprehend.core.domain.AccusedDetailsDomain;
import com.cctns.apprehend.core.domain.AccusedProfileDomain;
import com.cctns.apprehend.core.domain.FirListBgDomain;
import com.cctns.apprehend.core.domain.PageDomain;
import com.cctns.apprehend.core.repository.SocialBackgroundPrepareRepository;
import com.cctns.apprehend.mapper.EntityDomainMapper;
import com.cctns.apprehend.persistence.projection.AccusedAddressProjection;
import com.cctns.apprehend.persistence.projection.AccusedDetailsProjection;
import com.cctns.apprehend.persistence.projection.AccusedListProjection;
import com.cctns.apprehend.persistence.projection.ActSectionProjection;
import com.cctns.apprehend.persistence.projection.FirListBgProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class SocialBackgroundPrepareRepositoryImpl implements SocialBackgroundPrepareRepository {
    private final SocialBackgroundPrepareJpaRepository socialBackgroundPrepareJpaRepository;
    private final EntityDomainMapper entityDomainMapper;

    public SocialBackgroundPrepareRepositoryImpl(SocialBackgroundPrepareJpaRepository socialBackgroundPrepareJpaRepository, EntityDomainMapper entityDomainMapper) {
        this.socialBackgroundPrepareJpaRepository = socialBackgroundPrepareJpaRepository;
        this.entityDomainMapper = entityDomainMapper;
    }

    @Override
    public PageDomain<List<FirListBgDomain>> fetchFirListPrepare(FirListBgDomain searchRequest) {
        // Create pageable object
        Pageable pageRequest = PageRequest.of(
                searchRequest.getPageable().getPage(),
                searchRequest.getPageable().getPageSize()
        );
        // Fetching paginated projection result from DB
        Page<FirListBgProjection> projectionPage = socialBackgroundPrepareJpaRepository.getFirListPrepare(
                searchRequest.getFirSrno(),
                searchRequest.getFromDate(),
                searchRequest.getToDate(),
                searchRequest.getPsId(),
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
    public PageDomain<List<FirListBgDomain>> fetchGdListPrepare(FirListBgDomain searchRequest) {
        Pageable pageRequest = PageRequest.of(
                searchRequest.getPageable().getPage(),
                searchRequest.getPageable().getPageSize()
        );
        // Fetching paginated projection result from DB
        Page<FirListBgProjection> projectionPage = socialBackgroundPrepareJpaRepository.getGdListPrepare(
                searchRequest.getFirSrno(),
                searchRequest.getPsId(),
                searchRequest.getFromDate(),
                searchRequest.getToDate(),
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

    public AccusedDetailsDomain fetchAccusedDetails(AccusedDetailsDomain accusedDetailsDomain) {

        Long apprehendSrno = accusedDetailsDomain.getApprehendSrno();
        // Fetch data
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
    public AccusedProfileDomain fetchDetailsForBgPrepare(AccusedProfileDomain accusedProfileDomain) {
        Long firRegNum = accusedProfileDomain.getFirRegNum();
        // Fetch projections
        List<ActSectionProjection> actSections =
                socialBackgroundPrepareJpaRepository.getActSectionList(firRegNum);
        List<AccusedListProjection> accusedList =
                socialBackgroundPrepareJpaRepository.getAccusedBgListPrepare(firRegNum);
        // Map Act Sections
        if (actSections != null && !actSections.isEmpty()) {
            accusedProfileDomain.setActSectionList(
                    entityDomainMapper.toDomainActList(actSections)
            );
        }
        // Map Accused List
        if (accusedList != null && !accusedList.isEmpty()) {
            accusedProfileDomain.setAccList(
                    entityDomainMapper.toDomainAccList(accusedList)
            );
        }
        return accusedProfileDomain;
    }

    @Override
    public AccusedProfileDomain fetchDetailsForBgView(AccusedProfileDomain accusedProfileDomain) {
        Long firRegNum = accusedProfileDomain.getFirRegNum();
        // Fetch projections
        List<ActSectionProjection> actSections =
                socialBackgroundPrepareJpaRepository.getActSectionList(firRegNum);
        List<AccusedListProjection> accusedList =
                socialBackgroundPrepareJpaRepository.getAccusedBgListView(firRegNum);
        // Map Act Sections
        if (actSections != null && !actSections.isEmpty()) {
            accusedProfileDomain.setActSectionList(
                    entityDomainMapper.toDomainActList(actSections)
            );
        }
        // Map Accused List
        if (accusedList != null && !accusedList.isEmpty()) {
            accusedProfileDomain.setAccList(
                    entityDomainMapper.toDomainAccList(accusedList)
            );
        }
        return accusedProfileDomain;
    }
}
