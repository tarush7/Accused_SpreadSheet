package com.cctns.apprehend.persistence.implementation;

import com.cctns.apprehend.core.domain.AccusedListDomain;
import com.cctns.apprehend.core.domain.FirListDisposalDomain;
import com.cctns.apprehend.core.domain.JuvDisposalReqDomain;
import com.cctns.apprehend.core.domain.JuvDisposalResponseDomain;
import com.cctns.apprehend.core.domain.JuvenileProfileDomain;
import com.cctns.apprehend.core.domain.PageDomain;
import com.cctns.apprehend.core.domain.disposal.JuvDisposalDomain;
import com.cctns.apprehend.core.exception.ApprehendDetailsNotFoundException;
import com.cctns.apprehend.core.repository.JuvenileDisposalRepository;
import com.cctns.apprehend.mapper.DomainEntityMapper;
import com.cctns.apprehend.mapper.EntityDomainMapper;
import com.cctns.apprehend.persistence.entity.disposal.TJuvDisposalEntity;
import com.cctns.apprehend.persistence.projection.AccusedListProjection;
import com.cctns.apprehend.persistence.projection.ActSectionProjection;
import com.cctns.apprehend.persistence.projection.FirListDisposalProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class JuvenileDisposalRepositoryImpl implements JuvenileDisposalRepository {
    private final JuvenileDisposalJpaRepository juvenileDisposalJpaRepository;
    private final ApprehendPrepareJpaRepository apprehendPrepareJpaRepository;
    private final EntityDomainMapper entityDomainMapper;
    private final DomainEntityMapper domainEntityMapper;

    public JuvenileDisposalRepositoryImpl(JuvenileDisposalJpaRepository juvenileDisposalJpaRepository, ApprehendPrepareJpaRepository apprehendPrepareJpaRepository, EntityDomainMapper entityDomainMapper, DomainEntityMapper domainEntityMapper) {
        this.juvenileDisposalJpaRepository = juvenileDisposalJpaRepository;
        this.apprehendPrepareJpaRepository = apprehendPrepareJpaRepository;
        this.entityDomainMapper = entityDomainMapper;
        this.domainEntityMapper = domainEntityMapper;
    }

    @Override
    public PageDomain<List<FirListDisposalDomain>> fetchFirListPrepare(FirListDisposalDomain searchRequest) {
        // Create pageable object
        Pageable pageRequest = PageRequest.of(
                searchRequest.getPageable().getPage(),
                searchRequest.getPageable().getPageSize()
        );
        // Fetching paginated projection result from DB
        Page<FirListDisposalProjection> projectionPage = juvenileDisposalJpaRepository.getFirListPrepare(
                searchRequest.getFirSrno(),
                searchRequest.getFromDate(),
                searchRequest.getToDate(),
                searchRequest.getPsId(),
                searchRequest.getYear(),
                pageRequest
        );
        // Direct list mapping
        List<FirListDisposalDomain> domainList = entityDomainMapper.toDomainDisposalList(projectionPage.getContent());

        if (domainList.isEmpty()) {
            return PageDomain.<List<FirListDisposalDomain>>builder()
                    .list(Collections.emptyList())
                    .totalSize(0L)
                    .pageCount(0)
                    .build();
        }
        return PageDomain.<List<FirListDisposalDomain>>builder()
                .list(domainList)
                .totalSize(projectionPage.getTotalElements())
                .pageCount(projectionPage.getTotalPages())
                .build();
    }

    @Override
    public PageDomain<List<FirListDisposalDomain>> fetchGdListPrepare(FirListDisposalDomain searchRequest) {
        Pageable pageRequest = PageRequest.of(
                searchRequest.getPageable().getPage(),
                searchRequest.getPageable().getPageSize()
        );
        // Fetching paginated projection result from DB
        Page<FirListDisposalProjection> projectionPage = juvenileDisposalJpaRepository.getGdListPrepare(
                searchRequest.getFirSrno(),
                searchRequest.getPsId(),
                searchRequest.getFromDate(),
                searchRequest.getToDate(),
                searchRequest.getYear(),
                pageRequest
        );
        // Direct list mapping
        List<FirListDisposalDomain> domainList =
                entityDomainMapper.toDomainDisposalList(projectionPage.getContent());

        if (domainList.isEmpty()) {
            return PageDomain.<List<FirListDisposalDomain>>builder()
                    .list(Collections.emptyList())
                    .totalSize(0L)
                    .pageCount(0)
                    .build();
        }
        return PageDomain.<List<FirListDisposalDomain>>builder()
                .list(domainList)
                .totalSize(projectionPage.getTotalElements())
                .pageCount(projectionPage.getTotalPages())
                .build();
    }

    public PageDomain<List<FirListDisposalDomain>> fetchFirListView(FirListDisposalDomain searchRequest) {
        // Create pageable object
        Pageable pageRequest = PageRequest.of(
                searchRequest.getPageable().getPage(),
                searchRequest.getPageable().getPageSize()
        );
        // Fetching paginated projection result from DB
        Page<FirListDisposalProjection> projectionPage = juvenileDisposalJpaRepository.getFirListView(
                searchRequest.getFirSrno(),
                searchRequest.getFromDate(),
                searchRequest.getToDate(),
                searchRequest.getPsIdList(),
                searchRequest.getYear(),
                pageRequest
        );
        // Direct list mapping
        List<FirListDisposalDomain> domainList = entityDomainMapper.toDomainDisposalList(projectionPage.getContent());

        if (domainList.isEmpty()) {
            return PageDomain.<List<FirListDisposalDomain>>builder()
                    .list(Collections.emptyList())
                    .totalSize(0L)
                    .pageCount(0)
                    .build();
        }
        return PageDomain.<List<FirListDisposalDomain>>builder()
                .list(domainList)
                .totalSize(projectionPage.getTotalElements())
                .pageCount(projectionPage.getTotalPages())
                .build();
    }

    public PageDomain<List<FirListDisposalDomain>> fetchGdListView(FirListDisposalDomain searchRequest) {
        Pageable pageRequest = PageRequest.of(
                searchRequest.getPageable().getPage(),
                searchRequest.getPageable().getPageSize()
        );
        // Fetching paginated projection result from DB
        Page<FirListDisposalProjection> projectionPage = juvenileDisposalJpaRepository.getGdListView(
                searchRequest.getFirSrno(),
                searchRequest.getFromDate(),
                searchRequest.getToDate(),
                searchRequest.getPsIdList(),
                searchRequest.getYear(),
                pageRequest
        );
        // Direct list mapping
        List<FirListDisposalDomain> domainList =
                entityDomainMapper.toDomainDisposalList(projectionPage.getContent());

        if (domainList.isEmpty()) {
            return PageDomain.<List<FirListDisposalDomain>>builder()
                    .list(Collections.emptyList())
                    .totalSize(0L)
                    .pageCount(0)
                    .build();
        }
        return PageDomain.<List<FirListDisposalDomain>>builder()
                .list(domainList)
                .totalSize(projectionPage.getTotalElements())
                .pageCount(projectionPage.getTotalPages())
                .build();
    }

    @Override
    public JuvDisposalResponseDomain submitJuvDisposal(JuvDisposalDomain request) {
        TJuvDisposalEntity entity = domainEntityMapper.toEntity(request);
        // SET PARENT REFERENCE HERE

        if (entity.getFileList() != null) {
            entity.getFileList().forEach(child -> child.setJuvDisposal(entity));
        }
        //save entity
        TJuvDisposalEntity savedEntity = juvenileDisposalJpaRepository.save(entity);
        //create response
        JuvDisposalResponseDomain response = new JuvDisposalResponseDomain();
        response.setJuvDisposalSrno(savedEntity.getJuvDisposalSrno());
        return response;
    }

    @Override
    public JuvDisposalDomain getJuvDisposal(JuvDisposalReqDomain request) {
        Optional<TJuvDisposalEntity> juvDetails = juvenileDisposalJpaRepository.findById(request.getJuvDisposalSrno());
        //throw exception if no records found
        TJuvDisposalEntity entity = juvDetails.orElseThrow(
                () -> new ApprehendDetailsNotFoundException("Apprehend Details Not Found For Given SrNo")
        );
        //map entity to response object
        JuvDisposalDomain response = entityDomainMapper.toDomain(entity);

        return response;
    }

    @Override
    public JuvenileProfileDomain fetchDetailsForDisposalPrepare(JuvenileProfileDomain request) {
        Long firRegNum = request.getFirRegNum();
        // Fetch projections
        List<ActSectionProjection> actSections =
                juvenileDisposalJpaRepository.getActSectionList(firRegNum);
        List<AccusedListProjection> accusedList =
                juvenileDisposalJpaRepository.getAccusedDisposalListPrepare(firRegNum);
        // Map Act Sections
        if (actSections != null && !actSections.isEmpty()) {
            request.setActSectionList(
                    entityDomainMapper.toDomainActList(actSections)
            );
        }
        // Map Accused List
        if (accusedList != null && !accusedList.isEmpty()) {
            request.setAccList(
                    entityDomainMapper.toDomainAccList(accusedList)
            );
        }
        return request;
    }

    @Override
    public JuvenileProfileDomain fetchDetailsForDisposalView(JuvenileProfileDomain request) {
        Long firRegNum = request.getFirRegNum();
        // Fetch projections
        List<ActSectionProjection> actSections =
                juvenileDisposalJpaRepository.getActSectionList(firRegNum);
        List<AccusedListProjection> accusedList =
                juvenileDisposalJpaRepository.getAccusedDisposalListView(firRegNum);
        // Map Act Sections
        if (actSections != null && !actSections.isEmpty()) {
            request.setActSectionList(
                    entityDomainMapper.toDomainActList(actSections)
            );
        }
        // Map Accused List
        if (accusedList != null && !accusedList.isEmpty()) {
            request.setAccList(
                    entityDomainMapper.toDomainAccList(accusedList)
            );
        }
        return request;
    }

    @Override
    public JuvenileProfileDomain fetchDetailsForGdPrepare(JuvenileProfileDomain request) {
     //   String ciclGdNum = request.getCiclGdNum();
        List<AccusedListProjection> accusedList = juvenileDisposalJpaRepository.getAccusedDisposalListPrepareGd(request.getCiclGdNum());

        if (accusedList != null && !accusedList.isEmpty()) {
            request.setAccList(
                    entityDomainMapper.toDomainAccList(accusedList)
            );
        }
        String ciclGdNum = accusedList.get(0).getCiclGdNum();
        if (ciclGdNum != null) {
            List<ActSectionProjection> actSectionProjections =
                    apprehendPrepareJpaRepository.getGdActSectionList(ciclGdNum);

            request.setActSectionList(
                    entityDomainMapper.toDomainActSectionList(actSectionProjections)
            );
        }
        return request;
    }

    @Override
    public JuvenileProfileDomain fetchDetailsForGdView(JuvenileProfileDomain request) {
     //   Long firRegNum = request.getFirRegNum();
        List<AccusedListProjection> accusedList = juvenileDisposalJpaRepository.getAccusedDisposalListViewGd(request.getCiclGdNum());
        if (accusedList != null && !accusedList.isEmpty()) {
            request.setAccList(
                    entityDomainMapper.toDomainAccList(accusedList)
            );
        }
        String ciclGdNum = accusedList.get(0).getCiclGdNum();
        if (ciclGdNum != null) {
            List<ActSectionProjection> actSectionProjections =
                    apprehendPrepareJpaRepository.getGdActSectionList(ciclGdNum);

            request.setActSectionList(
                    entityDomainMapper.toDomainActSectionList(actSectionProjections)
            );
        }
        return request;
    }

}

