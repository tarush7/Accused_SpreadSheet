package com.cctns.apprehend.constants;

import lombok.Data;

@Data
public class Constants {
    private Constants() {
        super();
    }

    public static final String SUCCESS = "SUCCESS";
    public static final String FIR_SUCCESS = "FIR list fetched successfully";
    public static final String DETAILS_SUCCESS = "Apprehend details fetched successfully";
    public static final String LIST_SUCCESS = "Apprehend list fetched successfully";
    public static final String INTER_SUCCESS ="Fir Interrogation list fetched successfully";
    public static final String TIME_FORMAT = "HH:mm:ss";
    public static final String DATE_FORMAT = "dd/MM/yyyy";
    public static final String DATE_TIME_FORMAT = "dd-MM-yyyy HH:mm";

    //Validation Response Code  :
    public static final String SQL_ERROR = "EXF0001";
    public static final String EX0001 = "CONSTRAINT SQL ERROR";
    public static final String EX0002 = "SQL Error";
    public static final String EX0003 = "UNIQUE KEY CONSTRAINT 1";
    public static final String NOT_FOUND = "EXF0002";
    public static final String CONSTRAINT_SQL_ERROR = "EXF0003";
    public static final String LIST_NOT_FOUND = "EXF0004";
    public static final String UNIQUE_KEY_CONSTRAINT_ERROR = "EXF0005";
    public static final String DATA_NOT_FOUND = "Data Not Found";
    public static final String DATA_FOUND = " Data fetched successfully";

    //Error Constants
    public static final String PARSE_MAPPING_ERRORS = "EXF0001";
    public static final String VALIDATION_ERRORS = "EXF0002";
    public static final String DATABASE_CONSTRAINTS_ERRORS = "EXF0003";
    public static final String ENTITY_NOT_FOUND_ERRORS = "EXF0004";
    public static final String LAZY_INIT_ERRORS = "EXF0005";
    public static final String ILLEGAL_ARGS_ERRORS = "EXF0006";
    public static final String CONCURRENT_UPDATE_CONFLICT = "EXF0007";
    public static final String TRANSACTION_FAILURE_ERROR = "EXF0008";
    public static final String FEIGN_ERRORS = "EXF0009";
    public static final String FALLBACK_ERRORS = "EXF0010";
    public static final String METHOD_ARGUMENT_NOT_VALID = "EXF0011";
    public static final String INVALID_FIR_NUMBER_ERROR = "Enter Valid FIR Number!";

    //File constants :
    public static final String FILE_BELONGS_TO_PHOTO = "PHOTOGRAPH";
    public static final String FILE_BELONGS_TO_DOCUMENT = "DOCUMENT UPLOAD";
    public static final String FTF_FILE_TYPE = "FTFFITYEX";
    public static final String MODULE_NAME = "APPREHEND";
    public static final String PHY_FEAT = "PHYSCL_FEATURES";
    public static final String PHY_MASTER_CODE = "PHY_DESC_TYP";
    public static final String OCCUPATION_MASTER_CODE = "OCCUPATION";
    public static final String RELATION_TYP_MASTER_CODE = "RELATION_TYP";

    //Fir type Constants
    public static final String FIR = "F";
    public static final String PETTY_CASE = "P";
    public static final String GD = "G";
    public static final String YES = "Y";
    public static final String NO = "N";
    public static final String IS_ARCHIVED = "Y";
    public static final String RECORD_STATUS_CREATED = "C";
    public static final String RECORD_STATUS_UPDATED = "U";
    public static final String RECORD_STATUS_DELETED = "D";
    public static final String SUBMIT_GRID = "SUBMIT";
    public static final String VIEW_GRID = "VIEW";
    public static final String APPR_FLAG = "APPR";
    public static final String BG_FLAG="BG";


    //Master Constants
    public static final String BANKS_MASTER_CODE = "BANKS";
    public static final String NATIONALITY_MASTER_CODE = "NATIONALITY";
    public static final String SOCIAL_MEDIA_TYP_MASTER_CODE = "SOCIAL_MEDIA_TYP";
    public static final String ADD_TYP_MASTER_CODE = "ADD_TYP";
    public static final String EDU_QUAL_MASTER_CODE = "EDU_QUAL";
    public static final String UPLOAD_FILE_TYP_MASTER_CODE = "UPLOAD_FILE_TYP";
    public static final String UPLOAD_FILE_SUB_TYP_MASTER_CODE = "UPLOAD_FILE_SUB_TYP";
    public static final String INCOME_GROUP_MASTER_CODE = "INCOME_GROUP";
    public static final String LIVING_STATUS_MASTER_CODE = "LIVING_STATUS";
   // public static final String OCCUPATION_MASTER_CODE = "OCCUPATION";
    public static final String CATEGORY_MASTER_CODE = "CATEGORY";
    public static final String CASTE_TRIBE_MASTER_CODE = "CASTE_TRIBE";
    public static final String BLOOD_GROUP_MASTER_CODE = "BLOOD_GROUP";
  //  public static final String RELATION_TYP_MASTER_CODE = "RELATION_TYP";
    public static final String INFO_MODE_MASTER_CODE = "INFO_MODE";
    public static final String EVIDENCE_TENDERED_CODE= "EVIDENCE_TENDERED";
    public static final String ACCNT_TYP_CODE= "ACCNT_TYP";
    public static final String RELIGION_CODE= "RELIGION";
    public static final String NATIONAL_ID_TYPE= "IDENTITY_TYP";
    public static final String IDEN_MARKS="PHY_FEAT_PCODE_IDENTF";

    public static final String TABLE_NAME = "TABLE_NAME";
    public static final String ID_COLUMN = "ID_COLUMN";
    public static final String UNKNOWN_ID = "UNKNOWN_ID";

    //Apprehend Memo Constants
    public static final Integer APPREHEND_REG_TYPE_CD = 25;
    public static final Integer PROP_SEQ_TYPE_CD = 3;
    public static final Integer REG_TYPE_FIR = 1;
    public static final Integer REG_TYPE_ARREST=3;

    public static final String INVALID_HEADER_MISSING_COMMON_PARAMS_EXCEPTION = "IHMCPEX";
    public static final String INVALID_HEADER_FORMAT_EXCEPTION = "IHFEX";

    public static final String SECRET_KEY_INIT_FAILED_EX = "SECKEYINITFAILEX";
    public static final String ENCRYPTION_FAILED_EX = "ENCFAILEX";
    public static final String DECRYPTION_FAILED_EX = "DECFAILEX";

    public static final String ALGORITHM = "AES/GCM/NoPadding";
    public static final String ENCRYPTION = "AES";
    public static final int GCM_TAG_LENGTH = 128; // bits
    public static final int GCM_IV_LENGTH = 12; // bytes (96 bits)
    public static final int AES_KEY_SIZE = 32;

    public static final String STAFF_ID_NOT_NULL_MSG = "STAFF ID REQUIRED";
    public static final String LANG_CD_NOT_NULL_MSG = "LANG CD REQUIRED";
    public static final String OFFICE_CD_NOT_NULL_MSG = "OFFICE CD REQUIRED";
    public static final String STATE_ID_NOT_NULL_MSG = "STATE ID REQUIRED";
    public static final String ROLES_NOT_EMPTY_MSG = "ROLES REQUIRED";
    public static final String FILE_TYPE_MASTER_CD = "UPLOAD_FILE_TYP";
    public static final String FILE_SUB_TYPE_MASTER_CD = "UPLOAD_FILE_SUB_TYP";

    public static final String MASTER_PS_KEY= "PS";
    public static final String MASTER_STATE_KEY= "STATE";
    public static final String MASTER_DISTRICT_KEY= "DISTRICT";
    public static final String MASTER_LG_DISTRICT_KEY= "LG_DISTRICT";


}
