package com.cctns.apprehend.core.repository;

import com.cctns.apprehend.persistence.projection.NextSeqNumberProjection;

public interface SrNoRepository {
 //   Long getSrNo(Long psId,Integer apprehendYear, Integer regTypeCd);


    NextSeqNumberProjection getAndUpdateSrNo(Long psId, Integer arrestYear, Integer regTypeCd);

   // void updateNextSrNo(Long psId, Integer apprehendYear, Integer regTypeCd, Long nextSeqNo);

}
