package com.cctns.apprehend.persistence.implementation;

import com.cctns.apprehend.persistence.entity.accused.TFirAccusedInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AccusedInfoJpaRepository extends JpaRepository<TFirAccusedInfoEntity,Long> {

    @Query(value="SELECT psCd from get_ps_data_from_id(:psId)",nativeQuery = true)
    Integer getPsCdById(Long psId);
}
