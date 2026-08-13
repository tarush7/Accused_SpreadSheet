package com.cctns.apprehend.persistence.implementation;

import com.cctns.apprehend.persistence.entity.disposal.TJuvDisposalEntity;
import com.cctns.apprehend.persistence.projection.AccusedListProjection;
import com.cctns.apprehend.persistence.projection.ActSectionProjection;
import com.cctns.apprehend.persistence.projection.FirListDisposalProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface JuvenileDisposalJpaRepository extends JpaRepository<TJuvDisposalEntity,Long> {

    @Query(value = "SELECT * FROM apprehend.get_fir_list_prepare_disposal(:firSrno, :fromDate, :toDate, :psId, :year)", nativeQuery = true)
    Page<FirListDisposalProjection> getFirListPrepare(
            @Param("firSrno") String firSrno,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("psId") Long psId,
            @Param("year") Integer year,
            Pageable pageable
    );

    @Query(value = "SELECT * FROM apprehend.get_gd_list_prepare_disposal(:firSrno, :fromDate, :toDate, :psId, :year)", nativeQuery = true)
    Page<FirListDisposalProjection> getGdListPrepare(
            @Param("firSrno") String firSrno,
            @Param("psId") Long psId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("year") Integer year,
            Pageable pageable
    );

    @Query(value = "SELECT * FROM apprehend.get_fir_list_view_disposal(:firSrno, :fromDate, :toDate, :psIdList, :year)", nativeQuery = true)
    Page<FirListDisposalProjection> getFirListView(
            @Param("firSrno") String firSrno,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("psIdList") Integer[] psIdList,
            @Param("year") Integer year,
            Pageable pageable
    );

    @Query(value = "SELECT * FROM apprehend.get_gd_list_view_disposal(:firSrno, :fromDate, :toDate, :psIdList, :year)", nativeQuery = true)
    Page<FirListDisposalProjection> getGdListView(
            @Param("firSrno") String firSrno,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("psIdList") Integer[] psIdList,
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

    @Query(value = "SELECT * FROM apprehend.get_accused_list_prepare_disposal(:firRegNum)", nativeQuery = true)
    List<AccusedListProjection> getAccusedDisposalListPrepare(
            @Param("firRegNum") Long firRegNum
    );

    @Query(value = "SELECT * FROM apprehend.get_accused_list_view_disposal(:firRegNum)", nativeQuery = true)
    List<AccusedListProjection> getAccusedDisposalListView(
            @Param("firRegNum") Long firRegNum
    );

    @Query(value = "SELECT * FROM apprehend.get_accused_list_prepare_disposal_gd(:ciclGdNum)", nativeQuery = true)
    List<AccusedListProjection> getAccusedDisposalListPrepareGd(
            @Param("ciclGdNum") String ciclGdNum
    );

    @Query(value = "SELECT * FROM apprehend.get_accused_list_view_disposal_gd(:ciclGdNum)", nativeQuery = true)
    List<AccusedListProjection> getAccusedDisposalListViewGd(
            @Param("ciclGdNum") String ciclGdNum
    );

}

