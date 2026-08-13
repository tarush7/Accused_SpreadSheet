package com.cctns.apprehend.persistence.implementation;

import com.cctns.apprehend.core.domain.ActSectionWrapper;
import com.cctns.apprehend.core.domain.ApprehendViewReqDomain;
import com.cctns.apprehend.core.domain.CiclGdAccusedDomain;
import com.cctns.apprehend.core.domain.FirListDomain;
import com.cctns.apprehend.core.domain.PageDomain;
import com.cctns.apprehend.core.domain.acts.ActSectionDomain;
import com.cctns.apprehend.core.domain.apprehend.ApprehendMemoDomain;
import com.cctns.apprehend.core.exception.ApprehendDetailsNotFoundException;
import com.cctns.apprehend.core.repository.ApprehendViewRepository;
import com.cctns.apprehend.mapper.DomainEntityMapper;
import com.cctns.apprehend.mapper.EntityDomainMapper;
import com.cctns.apprehend.persistence.entity.apprehend.TApprehendMemoEntity;
import com.cctns.apprehend.persistence.projection.ActSectionProjection;
import com.cctns.apprehend.persistence.projection.CiclGdAccusedProjection;
import com.cctns.apprehend.persistence.projection.FirListProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ApprehendViewRepositoryImpl implements ApprehendViewRepository {
    private final ApprehendViewJpaRepository apprehendViewJpaRepository;
    private final ApprehendPrepareJpaRepository apprehendPrepareJpaRepository;
    private final EntityDomainMapper entityDomainMapper;
    private final DomainEntityMapper domainEntityMapper;

    public ApprehendViewRepositoryImpl(ApprehendViewJpaRepository apprehendViewJpaRepository, ApprehendPrepareJpaRepository apprehendPrepareJpaRepository, EntityDomainMapper entityDomainMapper, DomainEntityMapper domainEntityMapper) {
        this.apprehendViewJpaRepository = apprehendViewJpaRepository;
        this.apprehendPrepareJpaRepository = apprehendPrepareJpaRepository;
        this.entityDomainMapper = entityDomainMapper;
        this.domainEntityMapper = domainEntityMapper;
    }

    public ApprehendMemoDomain getApprehendMemo(ApprehendViewReqDomain reqDomain){
        Optional<TApprehendMemoEntity> apprehendDetails=apprehendViewJpaRepository.findById(reqDomain.getApprehendSrno());
        //throw exception if no records found
        TApprehendMemoEntity entity=apprehendDetails.orElseThrow(
                ()-> new ApprehendDetailsNotFoundException("Apprehend Details Not Found For Given SrNo")
        );
        //map entity to response object
        ApprehendMemoDomain response=entityDomainMapper.toDomain(entity);

        return response;
    }

    @Override
    public Map<String, ActSectionWrapper> fetchActSectionMasterData(List<String> sectionCdList, Integer langCd) {
        Map<String,ActSectionWrapper> response = new java.util.HashMap<>(Map.of());
        List<ActSectionProjection> actSectionProjections =  apprehendViewJpaRepository.findSectionsBySectionCdIn(toStringArray(sectionCdList),langCd);
        actSectionProjections.forEach(actSectionProjection -> {
            ActSectionWrapper actSectionWrapper = domainEntityMapper.mapActSectionProjectionToWrapper(actSectionProjection);
            response.put(actSectionWrapper.getSectionCd(),actSectionWrapper);
        });
        return response;
    }

    @Override
    public String getFirDisplayNum(Long firRegNum) {
        return apprehendViewJpaRepository.getFirDisplay(firRegNum);
    }

    @Override
    public String getGdDisplayNum(String gdNum) {
        return apprehendViewJpaRepository.getGdDisplay(gdNum);
    }

    @Override
    public String getFirDate(Long firRegNum) {
        return apprehendViewJpaRepository.getFirDate(firRegNum);
    }

    @Override
    public List<ActSectionDomain> getCiclGdActSection(String ciclGdNum) {
        List<ActSectionProjection> actSections =
                apprehendPrepareJpaRepository.getGdActSectionList(ciclGdNum);

        if (actSections == null || actSections.isEmpty()) {
            return Collections.emptyList();
        }

        return entityDomainMapper.toDomainActList(actSections);
    }

    @Override
    public PageDomain<List<CiclGdAccusedDomain>> fetchGdListView(CiclGdAccusedDomain searchRequest) {
        // Create pageable object
        Pageable pageRequest = PageRequest.of(
                searchRequest.getPageable().getPage(),
                searchRequest.getPageable().getPageSize()
        );
        // Fetching paginated projection result from DB
        Page<CiclGdAccusedProjection> projectionPage = apprehendViewJpaRepository.getCiclGdAccusedList(
                searchRequest.getCiclGdNum(),
                pageRequest
        );
        // Direct list mapping
        List<CiclGdAccusedDomain> domainList =
                entityDomainMapper.toDomainGdList(projectionPage.getContent());

        if (domainList.isEmpty()) {
            return PageDomain.<List<CiclGdAccusedDomain>>builder()
                    .list(Collections.emptyList())
                    .totalSize(0L)
                    .pageCount(0)
                    .build();
        }
        return PageDomain.<List<CiclGdAccusedDomain>>builder()
                .list(domainList)
                .totalSize(projectionPage.getTotalElements())
                .pageCount(projectionPage.getTotalPages())
                .build();
    }

    @Override
    public PageDomain<List<CiclGdAccusedDomain>> fetchGdAccusedBg(CiclGdAccusedDomain searchRequest) {
        // Create pageable object
        Pageable pageRequest = PageRequest.of(
                searchRequest.getPageable().getPage(),
                searchRequest.getPageable().getPageSize()
        );
        // Fetching paginated projection result from DB
            Page<CiclGdAccusedProjection> projectionPage = apprehendViewJpaRepository.getCiclGdAccusedListBg(
                searchRequest.getCiclGdNum(),
                pageRequest
        );
        // Direct list mapping
        List<CiclGdAccusedDomain> domainList =
                entityDomainMapper.toDomainGdList(projectionPage.getContent());

        if (domainList.isEmpty()) {
            return PageDomain.<List<CiclGdAccusedDomain>>builder()
                    .list(Collections.emptyList())
                    .totalSize(0L)
                    .pageCount(0)
                    .build();
        }
        return PageDomain.<List<CiclGdAccusedDomain>>builder()
                .list(domainList)
                .totalSize(projectionPage.getTotalElements())
                .pageCount(projectionPage.getTotalPages())
                .build();
    }

    /**
     * Convert a list of String into String array
     * @param list : List of string
     * @return String[]
     */
    public static String[] toStringArray(List<String> list) {
        if (list == null || list.isEmpty()) {
            return new String[0];
        }
        return list.toArray(new String[0]);
    }
}
