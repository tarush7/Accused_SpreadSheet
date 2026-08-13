package com.cctns.apprehend.persistence.implementation;

import com.cctns.apprehend.persistence.entity.socialbg.TJuvBackgroundReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SocialBackgroundSubmitJpaRepository extends JpaRepository<TJuvBackgroundReportEntity,Long> {
}
