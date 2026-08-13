package com.cctns.apprehend.core.repository;

public interface SequenceGeneratorRepoService {
    public Long getAndIncrement(Integer propTypeCd,Integer regYear,Integer psCd);
    Long getAccusedNextSequence(Integer psCd, Integer regYear, Integer regTypeFir);
}
