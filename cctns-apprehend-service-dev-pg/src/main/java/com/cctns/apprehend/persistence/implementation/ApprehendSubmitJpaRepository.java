package com.cctns.apprehend.persistence.implementation;

import com.cctns.apprehend.persistence.entity.apprehend.TApprehendMemoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ApprehendSubmitJpaRepository extends JpaRepository<TApprehendMemoEntity,Long> {

    @Query(value = """
            select gd.fn_update_gd_is_used_for_arrest_memo(:gdNum,:staffId)
            """, nativeQuery = true)
    void updateGdStatus(@Param("gdNum") String gdNum,
                        @Param("staffId") Long staffId);
}
