package com.cctns.apprehend.persistence.implementation;

import com.cctns.apprehend.persistence.entity.socialbg.TJuvBackgroundReportEntity;
import com.cctns.apprehend.persistence.projection.CourtDataProjection;
import com.cctns.apprehend.persistence.projection.FirListBgProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface SocialBackgroundViewJpaRepository extends JpaRepository<TJuvBackgroundReportEntity, Long> {
    @Query(value = "SELECT * FROM apprehend.get_fir_list_view_bg(:firSrno, :fromDate, :toDate, :psIdList, :year)", nativeQuery = true)
    Page<FirListBgProjection> getFirListView(
            @Param("firSrno") String firSrno,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("psIdList") Integer[] psIdList,
            @Param("year") Integer year,
            Pageable pageable
    );

    @Query(value = "SELECT * FROM apprehend.get_gd_list_view_bg(:firSrno, :fromDate, :toDate, :psIdList, :year)", nativeQuery = true)
    Page<FirListBgProjection> getGdListView(
            @Param("firSrno") String firSrno,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("psIdList") Integer[] psIdList,
            @Param("year") Integer year,
            Pageable pageable
    );

    @Query(value = """
            SELECT * FROM mdm.get_court_type_and_name(:srcCourtTypeCd)
            """, nativeQuery = true)
    CourtDataProjection getCourtTypeAndName(String srcCourtTypeCd);

}
