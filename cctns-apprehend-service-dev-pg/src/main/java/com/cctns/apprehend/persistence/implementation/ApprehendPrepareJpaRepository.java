package com.cctns.apprehend.persistence.implementation;

import com.cctns.apprehend.persistence.entity.ProjectionEntity;
import com.cctns.apprehend.persistence.projection.AccusedAddressProjection;
import com.cctns.apprehend.persistence.projection.AccusedDetailsProjection;
import com.cctns.apprehend.persistence.projection.AccusedListProjection;
import com.cctns.apprehend.persistence.projection.AccusedNationalIdProjection;
import com.cctns.apprehend.persistence.projection.ActSectionProjection;
import com.cctns.apprehend.persistence.projection.FirListProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ApprehendPrepareJpaRepository extends JpaRepository<ProjectionEntity, Long> {

    @Query(value = """
            SELECT *
            FROM apprehend.get_fir_list_prepare(:staffId, CAST(:firSrno AS integer), :year, :fromDate, :toDate, :psId)
            """, nativeQuery = true)
    Page<FirListProjection> getFirListPrepare(
            @Param("staffId") Long staffId,
            @Param("firSrno") String firSrno,
            @Param("year") Integer year,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("psId") Long psId,
            Pageable pageable
    );

    @Query(value = """
            SELECT *
            FROM apprehend.get_gd_prepare_list_apprehend(:psId,:firSrno,:year, :fromDate, :toDate )
            """, nativeQuery = true)
    Page<FirListProjection> getGdListPrepare(
            @Param("psId") Long psId,
            @Param("firSrno") String firSrno,
            @Param("year") Integer year,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            Pageable pageable
    );

    @Query(value = """
            SELECT *
            FROM apprehend.get_fir_list_view(:firSrno, :psIdList, :fromDate, :toDate, :year )
            """, nativeQuery = true)
    Page<FirListProjection> getFirListView(
            @Param("firSrno") String firSrno,
            @Param("psIdList") Integer[] psCdList,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("year") Integer year,
            Pageable pageable
    );

    @Query(value = """
            SELECT *
            FROM apprehend.get_gd_list_view( :firSrno, :psIdList, :fromDate, :toDate, :year)
            """,
            nativeQuery = true)
    Page<FirListProjection> getGdListView(
            @Param("firSrno") String firSrno,
            @Param("psIdList") Integer[] psIdList,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("year") Integer year,
            Pageable pageable
    );

    @Query(value = """
    SELECT *
    FROM apprehend.get_gd_act_section_list(:firRegNum)
    """, nativeQuery = true)
    List<ActSectionProjection> getGdActSectionList(
            @Param("firRegNum") String firRegNum
    );

    @Query(value = """
    SELECT *
    FROM apprehend.get_gd_act_section_list_multiple(:gdNums)
    """, nativeQuery = true)
    List<ActSectionProjection> getGdActSectionListMultiple(
            @Param("gdNums") String[] gdNums);

    @Query(value = """
    SELECT *
    FROM disposal.get_act_section_data(:firRegNum)
    """, nativeQuery = true)
    List<ActSectionProjection> getActSectionList(
            @Param("firRegNum") Long firRegNum
    );

    @Query(value = """
              Select
                	tfai.accused_srno as accusedSrno,
                	tfai.accused_vid as accusedVid,
                    TRIM(
                         COALESCE(tfai.first_name, '') || ' ' ||
                         COALESCE(tfai.middle_name, '') || ' ' ||
                         COALESCE(tfai.last_name, '')
                         ) AS juvenileName,
                         tfai.relation_type_cd as relationTypeCd ,
                         tfai.relative_name as relativeName ,
                         tfai.age_yrs as age
                         FROM fir.t_fir_accused_info tfai
                         left join apprehend.t_apprehend_memo tam
                         on tfai.fir_reg_num =tam.fir_reg_num
                         WHERE
                          tfai.fir_reg_num = :firRegNum
                          AND tam.apprehend_srno is NULL
                          AND tfai.record_status='C'
            """, nativeQuery = true)
    List<AccusedListProjection> getAccusedListPrepare(
            @Param("firRegNum") Long firRegNum
    );

    @Query(value = """
            SELECT *
            FROM apprehend.get_juvenile_list_by_fir_view(:firRegNum)
            """, nativeQuery = true)
    List<AccusedListProjection> getAccusedListView(
            @Param("firRegNum") Long firRegNum
    );

    @Query(value = """
            SELECT * FROM apprehend.get_accused_details_fir(:accusedVid)
            """, nativeQuery = true)
    AccusedDetailsProjection getAccusedDetails(
            @Param("accusedVid") Long accusedVid
    );

    @Query(value = "SELECT * FROM apprehend.get_accused_address_fir(:accusedVid)", nativeQuery = true)
    List<AccusedAddressProjection> getAccusedAddress(@Param("accusedVid") Long accusedVid);

    @Query(value = "SELECT * FROM apprehend.get_accused_national_id_details_fir(:accusedVid)", nativeQuery = true)
    List<AccusedNationalIdProjection> getAccusedNationalId(@Param("accusedVid") Long accusedVid);

}



