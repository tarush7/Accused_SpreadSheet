package com.cctns.apprehend.persistence.implementation;

import com.cctns.apprehend.persistence.entity.MRegSeqNumEntity;
import com.cctns.apprehend.persistence.projection.NextSeqNumberProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SrNoJpaRepository extends JpaRepository<MRegSeqNumEntity,Long> {
    @Query(value = """
    SELECT next_seq_num, ps_cd 
    FROM admin.get_next_seq_number(
        :psId,
        :regYear,
        :regTypeCd
    )
    """, nativeQuery = true)
    NextSeqNumberProjection getAndUpdateSrNo(
            @Param("psId") Long psId,
            @Param("regYear") Integer regYear,
            @Param("regTypeCd") Integer regTypeCd
    );

//    @Query(value = """
//                UPDATE ADMIN.M_REG_SEQ_NUM
//                SET NEXT_SEQ_SRNO= :nextSeqNo
//                WHERE PS_CD = :psCd
//                        AND REG_YEAR = :regYear
//                        AND REG_TYPE_CD = :regTypeCd
//            """, nativeQuery = true)
//    @Modifying
//    @Transactional
//    void updateNextSrNo(
//            @Param("psId") Long psId,
//            @Param("regYear") Integer regYear,
//            @Param("regTypeCd") Integer regTypeCd,
//            @Param("nextSeqNo") Long nextSeqNo
//    );

}
