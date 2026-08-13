package com.cctns.apprehend.web.dto.response;

import com.cctns.apprehend.web.dto.request.apprehend.ApprehendActSectionDTO;
import com.cctns.apprehend.web.dto.request.apprehend.ApprehendMemoDTO;
import com.cctns.apprehend.web.dto.request.apprehend.ApprehendWitnessDTO;

import java.util.List;

public class ApprehendMemoInfoResponseDTO {
    private List<ApprehendMemoDTO> accList;
    private List<ApprehendActSectionDTO> actSectionList;
    private List<ApprehendWitnessDTO> witnessList;
}
