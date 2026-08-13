package com.cctns.apprehend.persistence.implementation;

import com.cctns.apprehend.persistence.entity.ProjectionEntity;
import com.cctns.apprehend.persistence.projection.AccusedAddressProjection;
import com.cctns.apprehend.persistence.projection.AccusedDetailsProjection;
import com.cctns.apprehend.persistence.projection.AccusedListProjection;
import com.cctns.apprehend.persistence.projection.ActSectionProjection;
import com.cctns.apprehend.persistence.projection.FirListBgProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SocialBackgroundPrepareJpaRepository extends JpaRepository<ProjectionEntity, Long> {

    @Query(value = "SELECT * FROM apprehend.get_fir_list_prepare_bg(:firSrno, :fromDate, :toDate, :psId, :year)", nativeQuery = true)
    Page<FirListBgProjection> getFirListPrepare(
            @Param("firSrno") String firSrno,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("psId") Long psId,
            @Param("year") Integer year,
            Pageable pageable
    );

    @Query(value = "SELECT * FROM apprehend.get_gd_list_prepare_bg(:firSrno, :fromDate, :toDate, :psId, :year)", nativeQuery = true)
    Page<FirListBgProjection> getGdListPrepare(
            @Param("firSrno") String firSrno,
            @Param("psId") Long psId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("year") Integer year,
            Pageable pageable
    );

    @Query(value = """
    SELECT *
    FROM disposal.get_act_section_data(:firRegNum)
    """, nativeQuery = true)
    List<ActSectionProjection> getActSectionList(
            @Param("firRegNum") Long firRegNum
    );

    @Query(value = "SELECT * FROM apprehend.get_accused_list_prepare_bg(:firRegNum)", nativeQuery = true)
    List<AccusedListProjection> getAccusedBgListPrepare(
            @Param("firRegNum") Long firRegNum
    );

    @Query(value = "SELECT * FROM apprehend.get_accused_list_view_bg(:firRegNum)", nativeQuery = true)
    List<AccusedListProjection> getAccusedBgListView(
            @Param("firRegNum") Long firRegNum
    );

    @Query(value = "SELECT * FROM apprehend.get_accused_details_apprehend(:apprehendSrno)", nativeQuery = true)
    AccusedDetailsProjection getAccusedDetails(@Param("apprehendSrno") Long apprehendSrno);

    @Query(value = "SELECT * FROM apprehend.get_accused_address_apprehend(:apprehendSrno)", nativeQuery = true)
    List<AccusedAddressProjection> getAccusedAddress(@Param("apprehendSrno") Long apprehendSrno);

}