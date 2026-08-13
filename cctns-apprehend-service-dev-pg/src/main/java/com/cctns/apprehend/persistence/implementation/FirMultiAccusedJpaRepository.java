package com.cctns.apprehend.persistence.implementation;

import com.cctns.apprehend.persistence.entity.accused.TFirMultiAccusedEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FirMultiAccusedJpaRepository extends JpaRepository<TFirMultiAccusedEntity,Long> {
}
