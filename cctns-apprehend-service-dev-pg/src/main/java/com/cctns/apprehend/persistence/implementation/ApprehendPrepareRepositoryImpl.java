package com.cctns.apprehend.persistence.implementation;

import com.cctns.apprehend.core.domain.AccusedDetailsDomain;
import com.cctns.apprehend.core.domain.AccusedProfileDomain;
import com.cctns.apprehend.core.domain.FirListDomain;
import com.cctns.apprehend.core.domain.PageDomain;
import com.cctns.apprehend.core.domain.acts.ActSectionDomain;
import com.cctns.apprehend.core.repository.ApprehendPrepareRepository;
import com.cctns.apprehend.mapper.EntityDomainMapper;
import com.cctns.apprehend.persistence.projection.AccusedAddressProjection;
import com.cctns.apprehend.persistence.projection.AccusedDetailsProjection;
import com.cctns.apprehend.persistence.projection.AccusedListProjection;
import com.cctns.apprehend.persistence.projection.AccusedNationalIdProjection;
import com.cctns.apprehend.persistence.projection.ActSectionProjection;
import com.cctns.apprehend.persistence.projection.FirListProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ApprehendPrepareRepositoryImpl implements ApprehendPrepareRepository {
    private final ApprehendPrepareJpaRepository apprehendPrepareJpaRepository;
    private final EntityDomainMapper entityDomainMapper;

    public ApprehendPrepareRepositoryImpl(ApprehendPrepareJpaRepository apprehendPrepareJpaRepository, EntityDomainMapper entityDomainMapper) {
        this.apprehendPrepareJpaRepository = apprehendPrepareJpaRepository;
        this.entityDomainMapper = entityDomainMapper;
    }

    @Override
    public PageDomain<List<FirListDomain>> fetchFirListPrepare(FirListDomain searchRequest) {
        // Create pageable object
        Pageable pageRequest = PageRequest.of(
                searchRequest.getPageable().getPage(),
                searchRequest.getPageable().getPageSize()
        );
        // Fetching paginated projection result from DB
        Page<FirListProjection> projectionPage = apprehendPrepareJpaRepository.getFirListPrepare(
                searchRequest.getStaffId(),
                searchRequest.getFirSrno(),
                searchRequest.getYear(),
                searchRequest.getFromDate(),
                searchRequest.getToDate(),
                searchRequest.getPsId(),
                pageRequest
        );
        // Direct list mapping
        List<FirListDomain> domainList =
                entityDomainMapper.toDomainFirList(projectionPage.getContent());

        if (domainList.isEmpty()) {
            return PageDomain.<List<FirListDomain>>builder()
                    .list(Collections.emptyList())
                    .totalSize(0L)
                    .pageCount(0)
                    .build();
        }
        return PageDomain.<List<FirListDomain>>builder()
                .list(domainList)
                .totalSize(projectionPage.getTotalElements())
                .pageCount(projectionPage.getTotalPages())
                .build();
    }

    @Override
    public PageDomain<List<FirListDomain>> fetchGdListPrepare(FirListDomain searchRequest) {
        // Create pageable object
        Pageable pageRequest = PageRequest.of(
                searchRequest.getPageable().getPage(),
                searchRequest.getPageable().getPageSize()
        );
        // Fetching paginated projection result from DB
        Page<FirListProjection> projectionPage = apprehendPrepareJpaRepository.getGdListPrepare(
                searchRequest.getPsId(),
                searchRequest.getFirSrno(),
                searchRequest.getYear(),
                searchRequest.getFromDate(),
                searchRequest.getToDate(),
                pageRequest
        );
        // Direct list mapping
        List<FirListDomain> domainList =
                entityDomainMapper.toDomainFirList(projectionPage.getContent());

        //adding gdActSection
        for(FirListDomain domain: domainList){
            List<ActSectionProjection> projectionList =
                    apprehendPrepareJpaRepository.getGdActSectionList(domain.getFirRegNum());

            List<ActSectionDomain> actSectionList =
                    entityDomainMapper.toDomainActSectionList(projectionList);

            domain.setGdActSectionList(actSectionList);
        }

        if (domainList.isEmpty()) {
            return PageDomain.<List<FirListDomain>>builder()
                    .list(Collections.emptyList())
                    .totalSize(0L)
                    .pageCount(0)
                    .build();
        }
        return PageDomain.<List<FirListDomain>>builder()
                .list(domainList)
                .totalSize(projectionPage.getTotalElements())
                .pageCount(projectionPage.getTotalPages())
                .build();
    }

//    @Override
//    public PageDomain<List<FirListDomain>> fetchGdListPrepare(FirListDomain searchRequest) {
//
//        Pageable pageRequest = PageRequest.of(
//                searchRequest.getPageable().getPage(),
//                searchRequest.getPageable().getPageSize()
//        );
//
//        Page<FirListProjection> projectionPage =
//                apprehendPrepareJpaRepository.getGdListPrepare(
//                        searchRequest.getPsId(),
//                        searchRequest.getFirSrno(),
//                        searchRequest.getYear(),
//                        searchRequest.getFromDate(),
//                        searchRequest.getToDate(),
//                        pageRequest
//                );
//
//        List<FirListDomain> domainList =
//                entityDomainMapper.toDomainFirList(projectionPage.getContent());
//
//        if (domainList.isEmpty()) {
//            return PageDomain.<List<FirListDomain>>builder()
//                    .list(Collections.emptyList())
//                    .totalSize(0L)
//                    .pageCount(0)
//                    .build();
//        }
//
//        String[] gdNums = domainList.stream()
//                .map(FirListDomain::getFirRegNum)
//                .filter(Objects::nonNull)
//                .toArray(String[]::new);
//
//        List<ActSectionProjection> projectionList =
//                apprehendPrepareJpaRepository.getGdActSectionListMultiple(gdNums);
//
//        List<ActSectionDomain> actSectionDomainList =
//                entityDomainMapper.toDomainActSectionList(projectionList);
//
//        // Group act sections by FIR
//        Map<String, List<ActSectionDomain>> actSectionMap =
//                actSectionDomainList.stream()
//                        .collect(Collectors.groupingBy(ActSectionDomain::getGdNum));
//
//        // Attach act sections to each FIR
//        domainList.forEach(domain ->
//                domain.setGdActSectionList(
//                        actSectionMap.getOrDefault(
//                                domain.getFirRegNum(),
//                                Collections.emptyList()
//                        )
//                )
//        );
//
//        return PageDomain.<List<FirListDomain>>builder()
//                .list(domainList)
//                .totalSize(projectionPage.getTotalElements())
//                .pageCount(projectionPage.getTotalPages())
//                .build();
//    }


    @Override
    public PageDomain<List<FirListDomain>> fetchFirListView(FirListDomain searchRequest) {
        Pageable pageRequest = PageRequest.of(
                searchRequest.getPageable().getPage(),
                searchRequest.getPageable().getPageSize()
        );
        // Fetching paginated projection result from DB
        Page<FirListProjection> projectionPage = apprehendPrepareJpaRepository.getFirListView(
                searchRequest.getFirSrno(),
                searchRequest.getPsIdList(),
                searchRequest.getFromDate(),
                searchRequest.getToDate(),
                searchRequest.getYear(),
                pageRequest
        );
        // Direct list mapping
        List<FirListDomain> domainList =
                entityDomainMapper.toDomainFirList(projectionPage.getContent());

        if (domainList.isEmpty()) {
            return PageDomain.<List<FirListDomain>>builder()
                    .list(Collections.emptyList())
                    .totalSize(0L)
                    .pageCount(0)
                    .build();
        }
        return PageDomain.<List<FirListDomain>>builder()
                .list(domainList)
                .totalSize(projectionPage.getTotalElements())
                .pageCount(projectionPage.getTotalPages())
                .build();
    }

    @Override
    public PageDomain<List<FirListDomain>> fetchGdListView(FirListDomain searchRequest) {
        // Create pageable object
        Pageable pageRequest = PageRequest.of(
                searchRequest.getPageable().getPage(),
                searchRequest.getPageable().getPageSize()
        );
        // Fetching paginated projection result from DB
        Page<FirListProjection> projectionPage = apprehendPrepareJpaRepository.getGdListView(
                searchRequest.getFirSrno(),
                searchRequest.getPsIdList(),
                searchRequest.getFromDate(),
                searchRequest.getToDate(),
                searchRequest.getYear(),
                pageRequest
        );
        // Direct list mapping
        List<FirListDomain> domainList =
                entityDomainMapper.toDomainFirList(projectionPage.getContent());

        if (domainList.isEmpty()) {
            return PageDomain.<List<FirListDomain>>builder()
                    .list(Collections.emptyList())
                    .totalSize(0L)
                    .pageCount(0)
                    .build();
        }
        return PageDomain.<List<FirListDomain>>builder()
                .list(domainList)
                .totalSize(projectionPage.getTotalElements())
                .pageCount(projectionPage.getTotalPages())
                .build();
    }

    @Override
    public AccusedProfileDomain fetchDetailsForApprehendPrepare(AccusedProfileDomain accusedProfileDomain) {

        Long firRegNum = accusedProfileDomain.getFirRegNum();
        // Fetch projections
        List<ActSectionProjection> actSections =
                apprehendPrepareJpaRepository.getActSectionList(firRegNum);
        List<AccusedListProjection> accusedList =
                apprehendPrepareJpaRepository.getAccusedListPrepare(firRegNum);
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
    public AccusedProfileDomain fetchDetailsForApprehendView(AccusedProfileDomain accusedProfileDomain){
        Long firRegNum = accusedProfileDomain.getFirRegNum();
        // Fetch projections
        List<ActSectionProjection> actSections =
                apprehendPrepareJpaRepository.getActSectionList(firRegNum);
        List<AccusedListProjection> accusedList =
                apprehendPrepareJpaRepository.getAccusedListView(firRegNum);
        // Map Act Sections
        if (actSections != null && !actSections.isEmpty()) {
            List<ActSectionDomain> actSectionDomainList =
                    entityDomainMapper.toDomainActList(actSections);
            int id = 1;
            for (ActSectionDomain actSection : actSectionDomainList) {
                actSection.setId(String.valueOf(id++));
            }
            accusedProfileDomain.setActSectionList(actSectionDomainList);
        }
        // Map Accused List
        if (accusedList != null && !accusedList.isEmpty()) {
            accusedProfileDomain.setAccList(
                    entityDomainMapper.toDomainAccList(accusedList)
            );
        }
        return accusedProfileDomain;
    }

    public AccusedDetailsDomain fetchAccusedDetails(AccusedDetailsDomain accusedDetailsDomain) {

        Long accusedVid = accusedDetailsDomain.getAccusedVid();
        // Fetch data
        AccusedDetailsProjection accDetails =
                apprehendPrepareJpaRepository.getAccusedDetails(accusedVid);
        List<AccusedAddressProjection> accAddress =
                apprehendPrepareJpaRepository.getAccusedAddress(accusedVid);
        List<AccusedNationalIdProjection> accNationalId=
                apprehendPrepareJpaRepository.getAccusedNationalId(accusedVid);
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
        //Map national id list
        accusedDetailsDomain.setIdList(
                (accNationalId==null || accNationalId.isEmpty())
                ?List.of()
                :entityDomainMapper.toDomainNationalIdList(accNationalId)
        );
        return accusedDetailsDomain;
    }

}
