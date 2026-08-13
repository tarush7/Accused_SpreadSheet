package com.cctns.apprehend.persistence.implementation;

import com.cctns.apprehend.persistence.entity.AppRegSeqNum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface SequenceGeneratorRepository extends JpaRepository<AppRegSeqNum, Long> {
    Optional<AppRegSeqNum> findByPsCdAndRegYearAndSeqType(
            Integer psCd,
            Integer regYear,
            Integer seqType
    );

}