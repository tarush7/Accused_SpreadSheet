package com.cctns.apprehend.core.usecase;

import com.cctns.apprehend.core.domain.AccusedDetailsDomain;
import com.cctns.apprehend.core.domain.AccusedProfileDomain;
import com.cctns.apprehend.core.domain.FirListBgDomain;
import com.cctns.apprehend.core.domain.PageDomain;
import com.cctns.apprehend.core.exception.InvalidFlagException;
import com.cctns.apprehend.core.repository.SocialBackgroundPrepareRepository;
import com.cctns.apprehend.core.repository.SocialBackgroundViewRepository;

import java.util.List;

import static com.cctns.apprehend.constants.Constants.FIR;
import static com.cctns.apprehend.constants.Constants.GD;
import static com.cctns.apprehend.constants.Constants.SUBMIT_GRID;
import static com.cctns.apprehend.constants.Constants.VIEW_GRID;

public class SocialBackgroundPrepareUseCaseImpl implements SocialBackgroundPrepareUseCase {
    private final SocialBackgroundPrepareRepository socialBackgroundPrepareRepository;
    private final SocialBackgroundViewRepository socialBackgroundViewRepository;

    public SocialBackgroundPrepareUseCaseImpl(SocialBackgroundPrepareRepository socialBackgroundPrepareRepository, SocialBackgroundViewRepository socialBackgroundViewRepository) {
        this.socialBackgroundPrepareRepository = socialBackgroundPrepareRepository;
        this.socialBackgroundViewRepository = socialBackgroundViewRepository;
    }

    @Override
    public PageDomain<List<FirListBgDomain>> fetchFirList(FirListBgDomain request) {

        String gridFlag = request.getGridFlag();
        String firTypeFlag = request.getFirTypeFlag();

        if (SUBMIT_GRID.equalsIgnoreCase(gridFlag)) {

            if (FIR.equalsIgnoreCase(firTypeFlag)) {
                return socialBackgroundPrepareRepository.fetchFirListPrepare(request);
            } else if (GD.equalsIgnoreCase(firTypeFlag)) {
                return socialBackgroundPrepareRepository.fetchGdListPrepare(request);
            }

        } else if (VIEW_GRID.equalsIgnoreCase(gridFlag)) {

            if (FIR.equalsIgnoreCase(firTypeFlag)) {
                return socialBackgroundViewRepository.fetchFirListView(request);
            } else if (GD.equalsIgnoreCase(firTypeFlag)) {
                return socialBackgroundViewRepository.fetchGdListView(request);
            }
        }

        throw new InvalidFlagException(
                "Invalid combination of gridFlag: " + gridFlag +
                        " and firTypeFlag: " + firTypeFlag
        );
    }

    @Override
    public AccusedProfileDomain fetchDetailsForBgPrepare(AccusedProfileDomain request){
        String gridFlag = request.getGridFlag();
        if (gridFlag == null) {
            throw new InvalidFlagException("gridFlag cannot be null");
        }
        if (SUBMIT_GRID.equalsIgnoreCase(gridFlag)) {
            return socialBackgroundPrepareRepository.fetchDetailsForBgPrepare(request);
        } else if (VIEW_GRID.equalsIgnoreCase(gridFlag)) {
            return socialBackgroundPrepareRepository.fetchDetailsForBgView(request);
        }
        throw new InvalidFlagException("Invalid gridFlag: " + gridFlag);
    }

    @Override
    public AccusedDetailsDomain fetchAccusedDetails(AccusedDetailsDomain request){
        return socialBackgroundPrepareRepository.fetchAccusedDetails(request);
    }
}
