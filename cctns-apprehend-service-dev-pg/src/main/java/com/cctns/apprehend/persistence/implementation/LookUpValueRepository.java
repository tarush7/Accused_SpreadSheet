package com.cctns.apprehend.persistence.implementation;

import com.cctns.apprehend.persistence.entity.LookUpApprEntity;
import com.cctns.apprehend.persistence.projection.AddressMasterValuesProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LookUpValueRepository extends JpaRepository<LookUpApprEntity,Long> {

    @Query(value = "SELECT FILE_TYPE AS LOOK_UP_VALUE "
            + "FROM mdm.m_upload_file_types "
            + "WHERE LANG_CD= :langCd AND FILE_TYPE_CD= :fileTypeCd; ",
            nativeQuery = true)
    String getFileType(@Param("langCd") Integer langCd, @Param("fileTypeCd") Integer fileTypeCd);

    @Query(value = "SELECT FILE_SUB_TYPE AS LOOK_UP_VALUE "
            + "FROM mdm.m_upload_file_subtypes "
            + "WHERE LANG_CD= :langCd AND FILE_SUBTYPE_CD= :fileSubTypeCd; ",
            nativeQuery = true)
    String getFileSubType(@Param("langCd") Integer langCd, @Param("fileSubTypeCd") Integer fileSubTypeCd);

    @Query(value = """
    SELECT *
    FROM mdm.common_get_address_master_values(
        :langCd,
        :addressTypeCd,
        :countryCd,
        :stateId,
        :lgDistrictCd,
        :subDistrictCd,
        :villageCd,
        :psId
    )
    """, nativeQuery = true)
    AddressMasterValuesProjection getAddressMasterValues(
            @Param("langCd") Integer langCd,
            @Param("addressTypeCd") Integer addressTypeCd,
            @Param("countryCd") Integer countryCd,
            @Param("stateId") Long stateId,
            @Param("lgDistrictCd") Integer lgDistrictCd,
            @Param("subDistrictCd") Long subDistrictCd,
            @Param("villageCd") Long villageCd,
            @Param("psId") Long psId
    );
}
