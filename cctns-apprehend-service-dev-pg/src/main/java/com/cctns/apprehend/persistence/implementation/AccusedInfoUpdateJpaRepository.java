package com.cctns.apprehend.persistence.implementation;

import com.cctns.apprehend.persistence.entity.accused.FirAccusedInfoUpdateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccusedInfoUpdateJpaRepository extends JpaRepository<FirAccusedInfoUpdateEntity,Long> {
}
