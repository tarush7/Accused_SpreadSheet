package com.cctns.apprehend.web.dto.request;

import com.cctns.apprehend.constants.Constants;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommonParamsDTO  {
    //user credentials
    @NotNull(message = Constants.STAFF_ID_NOT_NULL_MSG)
    private Long staffId;
    private String loginId;
    @NotNull(message =Constants.LANG_CD_NOT_NULL_MSG)
    private Integer langCd;
    @NotNull(message = Constants.OFFICE_CD_NOT_NULL_MSG)
    private Long officeCd;
    @NotNull(message = Constants.STATE_ID_NOT_NULL_MSG)
    private Long stateId;
    private Long districtId;
    private Long psId;
    private Integer officeTypeCd;
    private Integer rankCd;
    private Integer officeLevelCd;
    @NotEmpty(message = Constants.ROLES_NOT_EMPTY_MSG)
    private List<Integer> allowedRoleCd; // all available role for that use
    private Long oicStaffId;
    private String oicLoginId;

    //New
    private String loginparams;   //Needed for all external calls (For passing header)
    private String requestId;     //Need for grafana and cross service logs
    private String authToken;     //Needed for Auth

}
