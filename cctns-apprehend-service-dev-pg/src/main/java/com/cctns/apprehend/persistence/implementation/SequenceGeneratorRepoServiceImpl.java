package com.cctns.apprehend.persistence.implementation;

import com.cctns.apprehend.constants.Constants;
import com.cctns.apprehend.core.repository.SequenceGeneratorRepoService;
import com.cctns.apprehend.persistence.entity.AppRegSeqNum;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SequenceGeneratorRepoServiceImpl implements SequenceGeneratorRepoService {
    private final SequenceGeneratorRepository sequenceGenNumRepository;

    public SequenceGeneratorRepoServiceImpl(SequenceGeneratorRepository sequenceGenNumRepository) {
        this.sequenceGenNumRepository = sequenceGenNumRepository;
    }

    @Override
    public Long getAndIncrement(Integer propTypeCd, Integer regYear, Integer psCd) {

        Integer seqType = Constants.PROP_SEQ_TYPE_CD;

        AppRegSeqNum regSeq = sequenceGenNumRepository
                .findByPsCdAndRegYearAndSeqType(psCd, regYear, seqType)
                .orElseGet(() -> {
                    AppRegSeqNum newSeq = AppRegSeqNum.builder()
                            .psCd(psCd)
                            .regYear(regYear)
                            .seqType(seqType)
                            .seqTypeDesc("PROPERTY")
                            .seqNum(1L)
                            .updatedOn(LocalDateTime.now())
                            .build();

                    return sequenceGenNumRepository.save(newSeq);
                });

        Long currentSeq = regSeq.getSeqNum();
        regSeq.setSeqNum(currentSeq + 1);
        regSeq.setUpdatedOn(LocalDateTime.now());
        sequenceGenNumRepository.save(regSeq);

        String formatted = String.format(
                "%d%02d%02d%05d",
                psCd,
                regYear % 100,
                propTypeCd,
                currentSeq
        );

        return Long.parseLong(formatted);
    }

    @Override
    public Long getAccusedNextSequence(Integer psCd, Integer regYear, Integer regType) {


        AppRegSeqNum sequence = sequenceGenNumRepository
                .findByPsCdAndRegYearAndSeqType(psCd, regYear, regType)
                .orElseGet(() -> {
                    AppRegSeqNum newSeq = AppRegSeqNum.builder()
                            .psCd(psCd)
                            .regYear(regYear)
                            .seqType(regType)
                            .seqTypeDesc("CRIME ACCUSED")
                            .seqNum(1L)
                            .updatedOn(LocalDateTime.now())
                            .build();

                    return sequenceGenNumRepository.save(newSeq);
                });

        Long currentSeq = sequence.getSeqNum();

        // increment
        sequence.setSeqNum(currentSeq + 1);
        sequence.setUpdatedOn(LocalDateTime.now());

        sequenceGenNumRepository.save(sequence);

        int regYearShort = regYear % 100;

        String formattedSeq = String.format("%d%02d%06d", psCd, regYearShort, currentSeq);

        return Long.parseLong(formattedSeq);
    }
}
