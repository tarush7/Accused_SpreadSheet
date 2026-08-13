package com.cctns.apprehend.persistence.implementation;

import com.cctns.apprehend.persistence.entity.apprehend.TApprehendMemoEntity;
import com.cctns.apprehend.persistence.projection.ActSectionProjection;
import com.cctns.apprehend.persistence.projection.CiclGdAccusedProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ApprehendViewJpaRepository extends JpaRepository<TApprehendMemoEntity,Long> {

    /**
     * Fetches Act-Section data
     * @param sectionCds : Section Codes
     * @param langCd : Language Code
     * @return Act Section Projection
     */
    @Query(value = """
              select * from  mdm.common_fn_get_sections_by_section_cd(:sectionCds,:langCd)
            """, nativeQuery = true)
    List<ActSectionProjection> findSectionsBySectionCdIn(@Param("sectionCds") String[] sectionCds,
                                                         @Param("langCd") Integer langCd);

    @Query(value = """
    SELECT *
    FROM apprehend.get_cicl_gd_accused_list(:ciclGdNum)
    """, nativeQuery = true)
    Page<CiclGdAccusedProjection> getCiclGdAccusedList(
            @Param("ciclGdNum") String ciclGdNum,
            Pageable pageable);

    @Query(value = """
    SELECT *
    FROM apprehend.get_cicl_gd_accused_list_bg(:ciclGdNum)
    """, nativeQuery = true)
    Page<CiclGdAccusedProjection> getCiclGdAccusedListBg(
            @Param("ciclGdNum") String ciclGdNum,
            Pageable pageable);

    @Query(value = """
        SELECT apprehend.get_fir_display(:firRegNum)
        """, nativeQuery = true)
    String getFirDisplay(@Param("firRegNum") Long firRegNum);

    @Query(value = """
        SELECT apprehend.get_gd_display(:gdNum)
        """, nativeQuery = true)
    String getGdDisplay(@Param("gdNum") String gdNum);

    @Query(value = """
        SELECT apprehend.get_fir_date(:firRegNum)
        """, nativeQuery = true)
    String getFirDate(@Param("firRegNum") Long firRegNum);

}
