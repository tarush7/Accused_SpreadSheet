package com.cctns.apprehend.core.usecase;

import com.cctns.apprehend.core.domain.AccusedDetailsDomain;
import com.cctns.apprehend.core.domain.AccusedProfileDomain;
import com.cctns.apprehend.core.domain.FirListDomain;
import com.cctns.apprehend.core.domain.PageDomain;
import com.cctns.apprehend.core.exception.InvalidFlagException;
import com.cctns.apprehend.core.repository.ApprehendPrepareRepository;

import java.util.List;

import static com.cctns.apprehend.constants.Constants.FIR;
import static com.cctns.apprehend.constants.Constants.GD;
import static com.cctns.apprehend.constants.Constants.SUBMIT_GRID;
import static com.cctns.apprehend.constants.Constants.VIEW_GRID;

public class ApprehendPrepareUseCaseImpl implements ApprehendPrepareUseCase {
    private final ApprehendPrepareRepository apprehendPrepareRepository;

    public ApprehendPrepareUseCaseImpl(ApprehendPrepareRepository apprehendPrepareRepository) {
        this.apprehendPrepareRepository = apprehendPrepareRepository;
    }

    public PageDomain<List<FirListDomain>> fetchFirList(FirListDomain request) {
        String gridFlag = request.getGridFlag();
        String firTypeFlag = request.getFirTypeFlag();
        //SUBMIT GRID FLOW
        if (SUBMIT_GRID.equalsIgnoreCase(gridFlag)) {
            if (FIR.equalsIgnoreCase(firTypeFlag)) {
                return apprehendPrepareRepository.fetchFirListPrepare(request);
            } else if (GD.equalsIgnoreCase(firTypeFlag)) {
                return apprehendPrepareRepository.fetchGdListPrepare(request);
            }
        }
        // VIEW GRID FLOW
        else if (VIEW_GRID.equalsIgnoreCase(gridFlag)) {
            if (FIR.equalsIgnoreCase(firTypeFlag)) {
                return apprehendPrepareRepository.fetchFirListView(request);
            } else if (GD.equalsIgnoreCase(firTypeFlag)) {
                return apprehendPrepareRepository.fetchGdListView(request);
            }
        }
        throw new InvalidFlagException(
                "Invalid combination of gridFlag: " + gridFlag +
                        " and firTypeFlag: " + firTypeFlag
        );

    }

    @Override
    public AccusedProfileDomain fetchDetailsForApprehendPrepare(AccusedProfileDomain request) {
            return apprehendPrepareRepository.fetchDetailsForApprehendView(request);
    }


    public AccusedDetailsDomain fetchAccusedDetails(AccusedDetailsDomain request){
        return apprehendPrepareRepository.fetchAccusedDetails(request);
    }

//    private WitnessDomain fetchIODetailsAsWitness(Long staffId, Integer langCd) {
//        PoliceStaffDomain staffDomain = apprehendPrepareRepository.getPoliceStaffDetails(staffId, langCd);
//        if (staffDomain == null) {
//            return null;
//        }
//        return witnessMapper.fromPoliceStaff(staffDomain);
//    }
//
//    private List<WitnessDomain> fetchComplainantAsWitness(Long firRegNum) {
//        List<ComplainantDomain> complainantList = apprehendPrepareRepository.getComplainantFromFir(firRegNum);
//        if (null == complainantList || complainantList.isEmpty())
//            return Collections.emptyList();
//        else
//            return witnessMapper.fromComplainantList(complainantList);
//
//    }
}
