package com.cctns.apprehend.persistence.implementation;

import com.cctns.apprehend.persistence.entity.LookUpApprEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LookUpMasterJdbcRepository extends JpaRepository<LookUpApprEntity, Long> {

    /**
     * Fetch master values .
     *
     * @param apiMasterCd    the api master cd
     * @param langCd         the lang cd
     * @param lookUpCd       the look-up cd
     * @param lookUpParentCd the look-up-parent cd
     * @return the mlc master projection
     */
    @Query(value = """
               SELECT * FROM mdm.get_lookup_master_data_v2(:apiMasterCd, :lookUpCd, :langCd, :lookUpParentCd);
            """, nativeQuery = true)
    String fetchMasterValue(@Param("apiMasterCd") String apiMasterCd,
                            @Param("lookUpCd") Integer lookUpCd,
                            @Param("langCd") Integer langCd,
                            @Param("lookUpParentCd") Integer lookUpParentCd);

}
