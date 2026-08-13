-- =============================================================================
-- APPREHEND MEMO VIEW JSON FUNCTION
-- =============================================================================
--
-- Purpose
-- -------
-- Rebuild the `data` object currently assembled by the Java endpoint:
--
--     POST /apprehend/view/view-apprehend-memo
--
-- Input:
--     one apprehend.t_apprehend_memo.apprehend_srno
--
-- Output:
--     one JSONB object shaped like ApprehendMemoDTO, or SQL NULL when the
--     requested memo does not exist.
--
-- The controller's outer API envelope (`message`, `status`, `statusCode`,
-- `data`, `errors`) deliberately remains Java/controller responsibility.
--
-- Why this is a FUNCTION and not a PROCEDURE
-- -------------------------------------------
-- A PostgreSQL function can return a value and can be called with SELECT.
-- A procedure is mainly for commands/side effects and is called with CALL.
-- This operation is read-only and returns JSON, so FUNCTION is the correct
-- database object.
--
-- Sources used for this implementation
-- ------------------------------------
-- 1. ApprehendMemoDTO and its nested DTO classes.
-- 2. TApprehendMemoEntity and its @OneToMany child entities.
-- 3. ApprehendViewUseCaseImpl Java enrichment and branching rules.
-- 4. ApprehendViewJpaRepository / ApprehendPrepareJpaRepository helpers.
-- 5. Apprehend/apprehend_lookup.csv and the Apprehend lookup workbook sheet.
-- 6. Arrest/arrest_proc.sql as the local PostgreSQL JSON construction style.
--
-- IMPORTANT PRE-DEPLOYMENT FACTS TO VERIFY
-- ----------------------------------------
-- This repository has no live database connection or definitions for the
-- existing helper functions below. Their names come directly from Java:
--
--     apprehend.get_fir_display(bigint)
--     apprehend.get_gd_display(text)
--     apprehend.get_fir_date(bigint)
--     disposal.get_act_section_data(bigint)
--     apprehend.get_gd_act_section_list(text)
--     mdm.common_get_address_master_values(...)
--
-- Confirm their signatures and returned column names in the target database
-- before deploying this file. See the companion reverse-engineering guide.
-- =============================================================================


-- =============================================================================
-- CHUNK 1: SMALL LOOKUP ADAPTER
-- =============================================================================
-- Java calls CacheService.get(apiMasterCode, langCd, lookupCode, null).
-- In SQL we resolve the same ordinary lookup through mdm.m_lookup_masters.
-- A module-specific name is used to avoid changing the Arrest helper.
CREATE OR REPLACE FUNCTION apprehend.view_lookup_value(
    p_api_master_code TEXT,
    p_lookup_code TEXT,
    p_lang_cd INTEGER
)
RETURNS TEXT
LANGUAGE sql
STABLE
AS $$
    SELECT lookup.look_up_value
    FROM mdm.m_lookup_masters lookup
    WHERE lookup.api_master_code = p_api_master_code
      AND lookup.look_up_code::TEXT = p_lookup_code
      AND lookup.lang_cd = p_lang_cd
    ORDER BY
        CASE WHEN lookup.active_status = 'Y' THEN 0 ELSE 1 END,
        lookup.lookup_master_srno DESC
    LIMIT 1;
$$;


-- =============================================================================
-- CHUNK 2: MAIN FUNCTION SHELL
-- =============================================================================
CREATE OR REPLACE FUNCTION apprehend.get_apprehend_memo_json(
    p_apprehend_srno BIGINT
)
RETURNS JSONB
LANGUAGE sql
STABLE
AS $$
    SELECT jsonb_build_object(

        -- =====================================================================
        -- CHUNK 3: INHERITED CommonParamsDTO FIELDS
        -- =====================================================================
        -- ApprehendMemoDTO extends CommonParamsDTO. The entity mapper can fill
        -- langCd/stateId/districtId/psId because the root table has those
        -- fields. Request/header-only values have no persisted source and are
        -- therefore NULL, matching the current entity -> domain -> DTO path.
        'staffId', NULL,
        'loginId', NULL,
        'langCd', memo.lang_cd,
        'officeCd', NULL,
        'stateId', memo.state_id::TEXT,
        'districtId', memo.district_id::TEXT,
        'psId', memo.ps_id::TEXT,
        'officeTypeCd', NULL,
        'rankCd', NULL,
        'officeLevelCd', NULL,
        'allowedRoleCd', NULL,
        'oicStaffId', NULL,
        'oicLoginId', NULL,
        'loginparams', NULL,
        'requestId', NULL,
        'authToken', NULL
    ) || jsonb_build_object(

        -- =====================================================================
        -- CHUNK 4: ROOT APPREHEND MEMO FIELDS
        -- =====================================================================
        -- Java globally serializes every Long as a JSON string. PostgreSQL does
        -- not, so Long-backed values are explicitly cast to TEXT.
        'apprehendSrno', memo.apprehend_srno::TEXT,

        -- CONTRACT DECISION TO VERIFY:
        -- The DTO exposes accusedVid, while the entity/table exposes
        -- juvenileVid/juvenile_vid. MapStruct has no explicit rename, so the
        -- current Java code may emit accusedVid = null. The intended business
        -- identifier appears to be juvenile_vid, so this SQL exposes it as
        -- accusedVid. Compare this one field with a real API response.
        'accusedVid', memo.juvenile_vid::TEXT,

        'firRegNum', memo.fir_reg_num::TEXT,
        'apprehendYear', memo.apprehend_year,
        'apprehendTypeCd', memo.apprehend_type_cd,
        'apprehendDt', to_char(memo.apprehend_dt, 'YYYY-MM-DD HH24:MI'),
        'firstName', memo.first_name,
        'middleName', memo.middle_name,
        'lastName', memo.last_name,
        'firstNameEng', memo.first_name_eng,
        'middleNameEng', memo.middle_name_eng,
        'lastNameEng', memo.last_name_eng,
        'relationTypeCd', memo.relation_type_cd,
        'relativeName', memo.relative_name,
        'relativeNameEng', memo.relative_name_eng,
        'relMobileNum', memo.rel_mobile_num::TEXT,
        'genderCd', memo.gender_cd,
        'ageTypeCd', memo.age_type_cd,
        'ageYrs', memo.age_yrs,
        'ageMnths', memo.age_months,
        'yob', memo.yob,
        'dob', memo.dob,
        'ageFromYrs', memo.age_from_yrs,
        'ageToYrs', memo.age_to_yrs,
        'apprehendBeatCd', memo.apprehend_beat_cd,
        'apprehendActionTakenCd', memo.apprehend_action_taken_cd,
        'apprehendReason', memo.apprehend_reason,
        'apprehendByOthers', memo.apprehend_by_others,
        'isRelatIntimated', memo.is_relat_intimated,
        'intimateRelTypeCd', memo.intimate_rel_type_cd,
        'intimateRelType', apprehend.view_lookup_value(
            'RELATION_TYP', memo.intimate_rel_type_cd::TEXT, memo.lang_cd
        ),
        'intimateRelName', memo.intimate_rel_name,
        'intimateDt', to_char(memo.intimate_dt, 'YYYY-MM-DD HH24:MI'),
        'intimateModeCd', memo.intimate_mode_cd,
        'intimationRemarks', memo.intimation_remarks,
        'isApprGrndComm', memo.is_appr_grnd_comm,
        'intimateMobNum', memo.intimate_mob_num::TEXT,
        'firCopyGivenTo', memo.fir_copy_given_to,
        'apprehendPlace', memo.apprehend_place,
        'custodyTypeCd', memo.custody_type_cd,
        'custodyTypeName', memo.custody_type_name,
        'jcwoName', memo.jcwo_name,
        'apprehendCircumstances', memo.apprehend_circumstances,
        'jcwoParentMobileNum', memo.jcwo_parent_mobile_num::TEXT,
        'jcwoParentAddress', memo.jcwo_parent_address,
        'intimatePostAddr', memo.intimate_postal_addr,
        'apprFromStateId', memo.appr_from_state_id::TEXT,
        'apprFromDistrictId', memo.appr_from_district_id::TEXT,
        'apprFromPsId', memo.appr_from_ps_id::TEXT
    ) || jsonb_build_object(

        -- =====================================================================
        -- CHUNK 5: ROOT MASTER/DISPLAY ENRICHMENT
        -- =====================================================================
        -- These scalar subqueries replace Java CacheService calls for STATE,
        -- DISTRICT and PS. LIMIT 1 prevents duplicate master rows from
        -- multiplying the root memo.
        'apprFromState', (
            SELECT state_master.state
            FROM mdm.m_state state_master
            WHERE state_master.state_id = memo.appr_from_state_id
              AND state_master.lang_cd = memo.lang_cd
            LIMIT 1
        ),
        'apprFromDistrict', (
            SELECT district_master.district
            FROM mdm.m_district district_master
            WHERE district_master.district_id = memo.appr_from_district_id
              AND district_master.lang_cd = memo.lang_cd
            LIMIT 1
        ),
        'apprFromPs', (
            SELECT ps_master.ps
            FROM mdm.m_police_station ps_master
            WHERE ps_master.ps_id = memo.appr_from_ps_id
              AND ps_master.lang_cd = memo.lang_cd
            LIMIT 1
        ),
        'ps', (
            SELECT ps_master.ps
            FROM mdm.m_police_station ps_master
            WHERE ps_master.ps_id = memo.ps_id
              AND ps_master.lang_cd = memo.lang_cd
            LIMIT 1
        ),
        'district', (
            SELECT district_master.district
            FROM mdm.m_district district_master
            WHERE district_master.district_id = memo.district_id
              AND district_master.lang_cd = memo.lang_cd
            LIMIT 1
        ),
        'state', (
            SELECT state_master.state
            FROM mdm.m_state state_master
            WHERE state_master.state_id = memo.state_id
              AND state_master.lang_cd = memo.lang_cd
            LIMIT 1
        ),

        -- Java calls these existing PostgreSQL functions conditionally.
        'firDisplayNum', CASE
            WHEN memo.fir_reg_num IS NOT NULL
            THEN apprehend.get_fir_display(memo.fir_reg_num)
            ELSE NULL
        END,
        'gdDisplayNum', CASE
            WHEN memo.gd_num IS NOT NULL
            THEN apprehend.get_gd_display(memo.gd_num)
            ELSE NULL
        END,
        'ciclGdDisplayNum', CASE
            WHEN memo.cicl_gd_num IS NOT NULL
            THEN apprehend.get_gd_display(memo.cicl_gd_num)
            ELSE NULL
        END,
        'firRegDt', CASE
            WHEN memo.fir_reg_num IS NOT NULL
            THEN apprehend.get_fir_date(memo.fir_reg_num)
            ELSE NULL
        END
    ) || jsonb_build_object(

        -- =====================================================================
        -- CHUNK 6: REMAINING ROOT DTO FIELDS AND TRANSIENT GAPS
        -- =====================================================================
        'religionCd', memo.religion_cd,
        'nationalityCd', memo.nationality_cd,
        'categoryCd', memo.category_cd,
        'occupationCd', memo.occupation_cd,
        'injuryDetails', memo.injury_details,

        -- No matching persisted fields or Java view enrichment were found for
        -- these values. Returning null is more honest than inventing a join.
        'nameRnkWlfOfcr', NULL,

        'ciclGdNum', memo.cicl_gd_num,
        'ciclGdDt', memo.cicl_gt_dt,
        'isFromGd', memo.is_from_gd,

        -- These are submit/decision fields, not columns on t_apprehend_memo.
        -- The current Java findById view also has no code that reconstructs
        -- them. They therefore remain null in this view function.
        'isExistingAccused', NULL,
        'gdNum', memo.gd_num,
        'gdDt', memo.gt_dt,
        'isNew', NULL,
        'existingAccSrno', NULL,
        'existingAccFirRegNum', NULL,

        -- Alias is already JSONB. Do not guess how to repair historical rows
        -- that contain an object instead of the DTO's expected array.
        'aliases', memo.alias
    ) || jsonb_build_object(

        -- =====================================================================
        -- CHUNK 7: APPREHENDED PERSON ADDRESSES
        -- =====================================================================
        'apprehendAddress', (
            SELECT COALESCE(
                jsonb_agg(
                    jsonb_build_object(
                        'id', address.appr_addr_srno::TEXT,
                        'langCd', address.lang_cd,
                        'apprehendSrno', address.apprehend_srno::TEXT,
                        'juvenileSrno', address.juvenile_srno::TEXT,
                        'juvenileVid', address.juvenile_vid,
                        'juvenileSrnoMigr', address.juvenile_srno_migr::TEXT,
                        'addressTypeCd', address.address_type_cd,
                        'addressLine1', address.address_line_1,
                        'addressLine2', address.address_line_2,
                        'addressLine3', address.address_line_3,
                        'subDistrictCd', address.sub_district_cd,
                        'villageCd', address.village_cd::TEXT,
                        'village', address_master.village,
                        'tehsil', address.tehsil,
                        'countryCd', address.country_cd,
                        'lgDistrictCd', address.lg_district_cd,
                        'psId', address.ps_id::TEXT,
                        'stateId', address.state_id::TEXT,
                        'pincode', address.pincode,
                        'isCommAddr', address.is_comm_addr,
                        'outsideIndiaAddr', address.outside_india_addr,
                        'addressEng', address.address_eng,
                        'isPermAddrSame', address.is_perm_addr_same,
                        'addressType', address_master."addressType",
                        'subDistrict', address_master."subDistrict",
                        'country', address_master.country,
                        'state', address_master.state,
                        'district', address_master.district,
                        'ps', address_master.ps
                    )
                    ORDER BY address.appr_addr_srno
                ),
                '[]'::JSONB
            )
            FROM apprehend.t_apprehend_addresses address
            LEFT JOIN LATERAL mdm.common_get_address_master_values(
                address.lang_cd,
                address.address_type_cd,
                address.country_cd,
                address.state_id,
                address.lg_district_cd,
                address.sub_district_cd::BIGINT,
                address.village_cd,
                address.ps_id
            ) address_master ON TRUE
            WHERE address.apprehend_srno = memo.apprehend_srno
        )
    ) || jsonb_build_object(

        -- =====================================================================
        -- CHUNK 8: APPREHENDED PERSON NATIONAL IDS
        -- =====================================================================
        'idList', (
            SELECT COALESCE(
                jsonb_agg(
                    jsonb_build_object(
                        'id', national_id.national_id_srno::TEXT,
                        'nationalIdTypeCd', national_id.nationality_id_type_cd,
                        -- Java Constants.NATIONAL_ID_TYPE = IDENTITY_TYP.
                        -- This intentionally follows Java even though the CSV
                        -- maps the column to NTNL_ID_DOC_TYP. Validate against
                        -- one real API response before production deployment.
                        'nationalIdType', apprehend.view_lookup_value(
                            'IDENTITY_TYP',
                            national_id.nationality_id_type_cd::TEXT,
                            memo.lang_cd
                        ),
                        'nationalIdNum', national_id.national_id_num,
                        'passportIssueDt', national_id.passport_issue_dt,
                        'passportIssuePlc', national_id.passport_issue_plc
                    )
                    ORDER BY national_id.national_id_srno
                ),
                '[]'::JSONB
            )
            FROM apprehend.t_apprehend_national_id national_id
            WHERE national_id.apprehend_srno = memo.apprehend_srno
        )
    ) || jsonb_build_object(

        -- =====================================================================
        -- CHUNK 9: WITNESSES, WITH TWO NESTED CHILD COLLECTIONS
        -- =====================================================================
        'apprehendWitness', (
            SELECT COALESCE(
                jsonb_agg(
                    jsonb_build_object(
                        'id', witness.appr_witns_srno::TEXT,
                        'langCd', witness.lang_cd,
                        'firstName', witness.first_name,
                        'middleName', witness.middle_name,
                        'lastName', witness.last_name,
                        'firstNameEng', witness.first_name_eng,
                        'middleNameEng', witness.middle_name_eng,
                        'lastNameEng', witness.last_name_eng,
                        'relationTypeCd', witness.relation_type_cd,
                        'relativeName', witness.relative_name,
                        'ageTypeCd', witness.age_type_cd,
                        'ageYrs', witness.age_yrs,
                        'ageMnths', witness.age_mnths,
                        'yob', witness.yob,
                        'dob', witness.dob::DATE,
                        'ageFrmYrs', witness.age_frm_yrs,
                        'ageToYrs', witness.age_to_yrs,
                        'mobileNum', witness.mobile_num::TEXT,
                        'email', witness.email,
                        'nationalityCd', witness.nationality_cd,
                        'occupationCd', witness.occupation_cd,
                        'genderCd', witness.gender_cd,
                        'maritalStatusCd', witness.marital_status_cd,
                        'witnEvidTenderCd', witness.witn_evid_tender_cd,
                        'witnEvidTender', apprehend.view_lookup_value(
                            'EVIDENCE_TENDERED',
                            witness.witn_evid_tender_cd::TEXT,
                            witness.lang_cd
                        ),
                        'witnessStatement', witness.witness_statement,
                        'isMobVerified', witness.is_witn_mobile_verf,
                        'witnCategoryCd', witness.witn_category_cd,
                        'aliases', witness.alias,

                        -- CHUNK 9A: WITNESS ADDRESSES (`addressGrid` in JSON)
                        'addressGrid', (
                            SELECT COALESCE(
                                jsonb_agg(
                                    jsonb_build_object(
                                        'id', wit_address.appr_witn_addr_srno::TEXT,
                                        'langCd', wit_address.lang_cd,

                                        -- The physical FK exists, but the Java
                                        -- entity exposes only the relationship;
                                        -- its scalar DTO field is not populated
                                        -- by the current mapper.
                                        'apprWitnsSrno', NULL,

                                        'addressTypeCd', wit_address.address_type_cd,
                                        'addressLine1', wit_address.address_line_1,
                                        'addressLine2', wit_address.address_line_2,
                                        'addressLine3', wit_address.address_line_3,
                                        'subDistrictCd', wit_address.sub_district_cd,
                                        'villageCd', wit_address.village_cd::TEXT,
                                        'village', wit_address_master.village,
                                        'tehsil', wit_address.tehsil,
                                        'countryCd', wit_address.country_cd,
                                        'lgDistrictCd', wit_address.lg_district_cd,
                                        'psId', wit_address.ps_id::TEXT,
                                        'stateId', wit_address.state_id::TEXT,
                                        'pincode', wit_address.pincode,
                                        'isCommAddr', wit_address.is_comm_addr,
                                        'isPermAddrSame', wit_address.is_perm_addr_same,
                                        'outsideIndiaAddr', wit_address.outside_india_addr,
                                        'addressEng', wit_address.address_eng,
                                        'addressType', wit_address_master."addressType",
                                        'subDistrict', wit_address_master."subDistrict",
                                        'country', wit_address_master.country,
                                        'state', wit_address_master.state,
                                        'district', wit_address_master.district,
                                        'ps', wit_address_master.ps
                                    )
                                    ORDER BY wit_address.appr_witn_addr_srno
                                ),
                                '[]'::JSONB
                            )
                            FROM apprehend.t_apprehend_witness_addr wit_address
                            LEFT JOIN LATERAL mdm.common_get_address_master_values(
                                wit_address.lang_cd,
                                wit_address.address_type_cd,
                                wit_address.country_cd,
                                wit_address.state_id,
                                wit_address.lg_district_cd,
                                wit_address.sub_district_cd::BIGINT,
                                wit_address.village_cd,
                                wit_address.ps_id
                            ) wit_address_master ON TRUE
                            WHERE wit_address.appr_witns_srno = witness.appr_witns_srno
                        ),

                        -- CHUNK 9B: WITNESS NATIONAL IDS
                        -- This table is present in Java but absent from the
                        -- supplied lookup CSV, so its entity mapping is the
                        -- source of truth for columns and relationship.
                        'idList', (
                            SELECT COALESCE(
                                jsonb_agg(
                                    jsonb_build_object(
                                        'id', wit_id.appr_witn_nat_srno::TEXT,
                                        'langCd', wit_id.lang_cd,
                                        'apprWitnsSrno', NULL,
                                        'apprWitnsSrnoMigr', wit_id.appr_witns_srno_migr::TEXT,
                                        'nationalIdTypeCd', wit_id.national_id_type_cd,
                                        'nationalIdType', apprehend.view_lookup_value(
                                            'IDENTITY_TYP',
                                            wit_id.national_id_type_cd::TEXT,
                                            memo.lang_cd
                                        ),
                                        'nationalIdNum', wit_id.national_id_num,
                                        'passportIssueDt', wit_id.passport_issue_dt::DATE,
                                        'passportIssuePlc', wit_id.passport_issue_plc
                                    )
                                    ORDER BY wit_id.appr_witn_nat_srno
                                ),
                                '[]'::JSONB
                            )
                            FROM apprehend.t_apprehend_witn_nationality wit_id
                            WHERE wit_id.appr_witns_srno = witness.appr_witns_srno
                        )
                    )
                    ORDER BY witness.appr_witns_srno
                ),
                '[]'::JSONB
            )
            FROM apprehend.t_apprehend_witness witness
            WHERE witness.apprehend_srno = memo.apprehend_srno
        )
    ) || jsonb_build_object(

        -- =====================================================================
        -- CHUNK 10: INTIMATION ADDRESSES
        -- =====================================================================
        'intimateAddress', (
            SELECT COALESCE(
                jsonb_agg(
                    jsonb_build_object(
                        'id', intimate_address.intmt_addr_srno::TEXT,
                        'langCd', intimate_address.lang_cd,
                        'apprehendSrno', intimate_address.apprehend_srno::TEXT,
                        'addressTypeCd', intimate_address.address_type_cd,
                        'addressLine1', intimate_address.address_line_1,
                        'addressLine2', intimate_address.address_line_2,
                        'addressLine3', intimate_address.address_line_3,
                        'subDistrictCd', intimate_address.sub_district_cd,
                        'villageCd', intimate_address.village_cd::TEXT,
                        'village', intimate_address_master.village,
                        'tehsil', intimate_address.tehsil,
                        'countryCd', intimate_address.country_cd,
                        'lgDistrictCd', intimate_address.lg_district_cd,
                        'psId', intimate_address.ps_id::TEXT,
                        'stateId', intimate_address.state_id::TEXT,
                        'pincode', intimate_address.pincode,
                        'isCommAddr', intimate_address.is_comm_addr,
                        'outsideIndiaAddr', intimate_address.outside_india_addr,
                        'addressEng', intimate_address.address_eng,
                        'isPermAddrSame', intimate_address.is_perm_addr_same,
                        'addressType', intimate_address_master."addressType",
                        'subDistrict', intimate_address_master."subDistrict",
                        'country', intimate_address_master.country,
                        'state', intimate_address_master.state,
                        'district', intimate_address_master.district,
                        'ps', intimate_address_master.ps
                    )
                    ORDER BY intimate_address.intmt_addr_srno
                ),
                '[]'::JSONB
            )
            FROM apprehend.t_apprehend_intimate_addr intimate_address
            LEFT JOIN LATERAL mdm.common_get_address_master_values(
                intimate_address.lang_cd,
                intimate_address.address_type_cd,
                intimate_address.country_cd,
                intimate_address.state_id,
                intimate_address.lg_district_cd,
                intimate_address.sub_district_cd::BIGINT,
                intimate_address.village_cd,
                intimate_address.ps_id
            ) intimate_address_master ON TRUE
            WHERE intimate_address.apprehend_srno = memo.apprehend_srno
        )
    ) || jsonb_build_object(

        -- =====================================================================
        -- CHUNK 11: FILES AND FILE MASTER VALUES
        -- =====================================================================
        'fileList', (
            SELECT COALESCE(
                jsonb_agg(
                    jsonb_build_object(
                        'id', file_row.appr_file_srno::TEXT,
                        'langCd', file_row.lang_cd,
                        'apprehendSrno', file_row.apprehend_srno::TEXT,
                        'fileSrno', file_row.file_srno,
                        'fileTypeCd', file_row.file_type_cd,
                        'fileSubtypeCd', file_row.file_subtype_cd,
                        'fileType', (
                            SELECT file_type_master.file_type
                            FROM mdm.m_upload_file_types file_type_master
                            WHERE file_type_master.lang_cd = memo.lang_cd
                              AND file_type_master.file_type_cd = file_row.file_type_cd
                            LIMIT 1
                        ),
                        'fileSubtype', (
                            SELECT subtype_master.file_sub_type
                            FROM mdm.m_upload_file_subtypes subtype_master
                            WHERE subtype_master.lang_cd = memo.lang_cd
                              AND subtype_master.file_subtype_cd = file_row.file_subtype_cd
                            LIMIT 1
                        ),
                        'fileBelongsTo', file_row.file_belongs_to,
                        'fileBelongsToSrno', file_row.file_belongs_to_srno::TEXT,
                        'fileName', file_row.file_name,
                        'filePath', file_row.file_path,
                        'fileDesc', file_row.file_desc,
                        'fileSize', file_row.file_size,
                        'fileGuid', file_row.file_guid,

                        -- These DTO fields are populated during upload, but are
                        -- not columns on TApprehendFilesEntity and are not
                        -- enriched by the Java view use-case.
                        'contentType', NULL,
                        'moduleName', NULL,
                        'fileUploadedOn', NULL
                    )
                    ORDER BY file_row.appr_file_srno
                ),
                '[]'::JSONB
            )
            FROM apprehend.t_apprehend_files file_row
            WHERE file_row.apprehend_srno = memo.apprehend_srno
        )
    ) || jsonb_build_object(

        -- =====================================================================
        -- CHUNK 12: ACT/SECTION CONDITIONAL OVERRIDE
        -- =====================================================================
        -- Java order is significant:
        --   1. FIR present -> set FIR act sections.
        --   2. isFromGd true -> overwrite with CCL/GD act sections.
        -- SQL expresses the final state by checking is_from_gd first.
        'actSectionList', CASE
            WHEN memo.is_from_gd IS TRUE THEN (
                SELECT COALESCE(
                    jsonb_agg(
                        jsonb_build_object(
                            'id', gd_act.id::TEXT,
                            'actCd', gd_act.act_cd,
                            'actLong', gd_act.act_long,
                            'actShort', gd_act.act_short,
                            'sectionCd', gd_act.section_cd,
                            'section', gd_act.section,
                            'sectionDesc', gd_act.section_desc
                        )
                        ORDER BY gd_act.id
                    ),
                    '[]'::JSONB
                )
                FROM apprehend.get_gd_act_section_list(memo.cicl_gd_num) gd_act
            )
            WHEN memo.fir_reg_num IS NOT NULL THEN (
                SELECT COALESCE(
                    jsonb_agg(
                        jsonb_build_object(
                            'id', fir_act.id::TEXT,
                            'actCd', fir_act.act_cd,
                            'actLong', fir_act.act_long,
                            'actShort', fir_act.act_short,
                            'sectionCd', fir_act.section_cd,
                            'section', fir_act.section,
                            'sectionDesc', fir_act.section_desc
                        )
                        ORDER BY fir_act.id
                    ),
                    '[]'::JSONB
                )
                FROM disposal.get_act_section_data(memo.fir_reg_num) fir_act
            )
            ELSE (
                -- Rare fallback: if neither branch runs, Java retains the
                -- entity-mapped t_apprehend_act_section children. The unused
                -- Java getActSectionData method means their display fields may
                -- remain null.
                SELECT COALESCE(
                    jsonb_agg(
                        jsonb_build_object(
                            'id', NULL,
                            'actCd', saved_act.act_cd,
                            'actLong', NULL,
                            'actShort', NULL,
                            'sectionCd', saved_act.section_cd,
                            'section', NULL,
                            'sectionDesc', NULL
                        )
                        ORDER BY saved_act.apprehend_act_srno
                    ),
                    '[]'::JSONB
                )
                FROM apprehend.t_apprehend_act_section saved_act
                WHERE saved_act.apprehend_srno = memo.apprehend_srno
            )
        END
    ) || jsonb_build_object(

        -- =====================================================================
        -- CHUNK 13: ROOT AUDIT FIELDS EXPOSED BY ApprehendMemoDTO
        -- =====================================================================
        'recordStatus', memo.record_status,
        'recordCreatedOn', memo.record_created_on,
        'recordCreatedBy', memo.record_created_by::TEXT
    )
    FROM apprehend.t_apprehend_memo memo
    WHERE memo.apprehend_srno = p_apprehend_srno;
$$;


-- =============================================================================
-- CHUNK 14: SAFE MANUAL TESTS (run after deployment)
-- =============================================================================
-- Existing memo:
-- SELECT jsonb_pretty(
--     apprehend.get_apprehend_memo_json(3810100126000078)
-- );
--
-- Missing memo must return SQL NULL, not an empty object:
-- SELECT apprehend.get_apprehend_memo_json(-1) IS NULL AS should_be_true;
--
-- Verify GD override:
-- SELECT
--     result -> 'isFromGd' AS is_from_gd,
--     jsonb_array_length(result -> 'actSectionList') AS act_count
-- FROM (
--     SELECT apprehend.get_apprehend_memo_json(
--         3810100126000078
--     ) AS result
-- ) test;
--
-- Verify arrays never become null for an existing memo:
-- SELECT
--     jsonb_typeof(result -> 'apprehendAddress') AS address_type,
--     jsonb_typeof(result -> 'idList') AS id_list_type,
--     jsonb_typeof(result -> 'apprehendWitness') AS witness_type,
--     jsonb_typeof(result -> 'intimateAddress') AS intimate_type,
--     jsonb_typeof(result -> 'fileList') AS file_type,
--     jsonb_typeof(result -> 'actSectionList') AS act_type
-- FROM (
--     SELECT apprehend.get_apprehend_memo_json(
--         3810100126000078
--     ) AS result
-- ) test;
