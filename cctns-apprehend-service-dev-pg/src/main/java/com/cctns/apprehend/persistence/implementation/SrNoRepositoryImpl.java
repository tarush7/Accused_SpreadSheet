package com.cctns.apprehend.persistence.implementation;
import com.cctns.apprehend.core.repository.SrNoRepository;
import com.cctns.apprehend.persistence.projection.NextSeqNumberProjection;
import org.springframework.stereotype.Service;

@Service
public class SrNoRepositoryImpl implements SrNoRepository {
    private final SrNoJpaRepository srNoJpaRepository;

    public SrNoRepositoryImpl(SrNoJpaRepository srNoJpaRepository){
        this.srNoJpaRepository = srNoJpaRepository;
    }
//    @Override
//    public Long getSrNo(Long psId,Integer apprehendYear, Integer regTypeCd ){
//        return srNoJpaRepository.getSrNo(psId, apprehendYear, regTypeCd);
//    }
@Override
public NextSeqNumberProjection getAndUpdateSrNo(Long psId, Integer arrestYear, Integer regTypeCd) {
    return srNoJpaRepository.getAndUpdateSrNo(psId, arrestYear, regTypeCd);
}

//    @Override
//    public void updateNextSrNo(Long psId, Integer apprehendYear, Integer regTypeCd, Long nextSeqNo){
//        srNoJpaRepository.updateNextSrNo(psId, apprehendYear, regTypeCd, nextSeqNo);
//    }
}
