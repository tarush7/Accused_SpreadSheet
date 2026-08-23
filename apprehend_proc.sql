CREATE OR REPLACE FUNCTION apprehend.get_apprehend_memo_json(
    p_apprehend_srno BIGINT
)
RETURNS JSONB
LANGUAGE plpgsql
STABLE
AS $$
DECLARE
    v_result JSONB;
BEGIN

    SELECT

        -- ================================================================
        -- PART 1: IDENTITY, CASE, NAME AND AGE
        -- ================================================================
        jsonb_build_object(
            'apprehendSrno', memo.apprehend_srno::TEXT,
            'accusedVid', memo.juvenile_vid::TEXT,
            'firRegNum', memo.fir_reg_num::TEXT,
            'apprehendYear', memo.apprehend_year,
            'apprehendTypeCd', memo.apprehend_type_cd,

            'apprehendDt',
                to_char(
                    memo.apprehend_dt,
                    'YYYY-MM-DD HH24:MI'
                ),

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
            'dob', memo.dob::DATE,
            'ageFromYrs', memo.age_from_yrs,
            'ageToYrs', memo.age_to_yrs,

            'isFromGd', memo.is_from_gd,
            'gdNum', memo.gd_num,
            'gdDt', memo.gt_dt
        )

        ||

        -- ================================================================
        -- PART 2: APPREHENSION, INTIMATION AND CUSTODY
        -- ================================================================
        jsonb_build_object(
            'apprehendBeatCd',
                memo.apprehend_beat_cd,

            'apprehendActionTakenCd',
                memo.apprehend_action_taken_cd,

            'apprehendReason',
                memo.apprehend_reason,

            'apprehendByOthers',
                memo.apprehend_by_others,

            'isRelatIntimated',
                memo.is_relat_intimated,

            'intimateRelTypeCd',
                memo.intimate_rel_type_cd,

            'intimateRelName',
                memo.intimate_rel_name,

            'intimateDt',
                to_char(
                    memo.intimate_dt,
                    'YYYY-MM-DD HH24:MI'
                ),

            'intimateModeCd',
                memo.intimate_mode_cd,

            'intimationRemarks',
                memo.intimation_remarks,

            'isApprGrndComm',
                memo.is_appr_grnd_comm,

            'intimateMobNum',
                memo.intimate_mob_num::TEXT,

            'firCopyGivenTo',
                memo.fir_copy_given_to,

            'apprehendPlace',
                memo.apprehend_place,

            'custodyTypeCd',
                memo.custody_type_cd,

            'custodyTypeName',
                memo.custody_type_name,

            'jcwoName',
                memo.jcwo_name,

            'apprehendCircumstances',
                memo.apprehend_circumstances,

            'jcwoParentMobileNum',
                memo.jcwo_parent_mobile_num::TEXT,

            'jcwoParentAddress',
                memo.jcwo_parent_address,

            'intimatePostAddr',
                memo.intimate_postal_addr,

            'apprFromStateId',
                memo.appr_from_state_id::TEXT,

            'apprFromDistrictId',
                memo.appr_from_district_id::TEXT,

            'apprFromPsId',
                memo.appr_from_ps_id::TEXT
        )

        ||

        -- ================================================================
        -- PART 3: CLASSIFICATION, LOCATION, CCL/GD AND AUDIT
        -- ================================================================
        jsonb_build_object(
            'langCd',
                memo.lang_cd,

            'stateId',
                memo.state_id::TEXT,

            'districtId',
                memo.district_id::TEXT,

            'psId',
                memo.ps_id::TEXT,

            'religionCd',
                memo.religion_cd,

            'nationalityCd',
                memo.nationality_cd,

            'categoryCd',
                memo.category_cd,

            'occupationCd',
                memo.occupation_cd,

            'injuryDetails',
                memo.injury_details,

            'nameRnkWlfOfcr',
                memo.name_rnk_wlf_ofcr,

            'ciclGdNum',
                memo.cicl_gd_num,

            'ciclGdDt',
                memo.cicl_gt_dt,

            'aliases',
                memo.alias,

            'recordStatus',
                memo.record_status,

            'recordCreatedOn',
                memo.record_created_on,

            'recordCreatedBy',
                memo.record_created_by::TEXT
        )

        ||

        -- ================================================================
        -- PART 4: MASTER LABELS AND DISPLAY VALUES
        -- ================================================================
        jsonb_build_object(

            -- Root State
            'state',
                (
                    SELECT state_master.state
                    FROM mdm.m_state state_master
                    WHERE state_master.state_id =
                          memo.state_id
                      AND state_master.lang_cd =
                          memo.lang_cd
                    LIMIT 1
                ),

            -- Root District
            'district',
                (
                    SELECT district_master.district
                    FROM mdm.m_district district_master
                    WHERE district_master.district_id =
                          memo.district_id
                      AND district_master.lang_cd =
                          memo.lang_cd
                    LIMIT 1
                ),

            -- Root Police Station
            'ps',
                (
                    SELECT ps_master.ps
                    FROM mdm.m_police_station ps_master
                    WHERE ps_master.ps_id =
                          memo.ps_id
                      AND ps_master.lang_cd =
                          memo.lang_cd
                    LIMIT 1
                ),

            -- Apprehended From State
            'apprFromState',
                (
                    SELECT state_master.state
                    FROM mdm.m_state state_master
                    WHERE state_master.state_id =
                          memo.appr_from_state_id
                      AND state_master.lang_cd =
                          memo.lang_cd
                    LIMIT 1
                ),

            -- Apprehended From District
            'apprFromDistrict',
                (
                    SELECT district_master.district
                    FROM mdm.m_district district_master
                    WHERE district_master.district_id =
                          memo.appr_from_district_id
                      AND district_master.lang_cd =
                          memo.lang_cd
                    LIMIT 1
                ),

            -- Apprehended From Police Station
            'apprFromPs',
                (
                    SELECT ps_master.ps
                    FROM mdm.m_police_station ps_master
                    WHERE ps_master.ps_id =
                          memo.appr_from_ps_id
                      AND ps_master.lang_cd =
                          memo.lang_cd
                    LIMIT 1
                ),

            -- Relationship Label
            'intimateRelType',
                CASE
                    WHEN memo.intimate_rel_type_cd IS NOT NULL
                    THEN (
                        SELECT
                            lookup_master.look_up_value

                        FROM mdm.m_lookup_masters
                            lookup_master

                        WHERE lookup_master.api_master_code =
                              'RELATION_TYP'

                          AND lookup_master.look_up_code::TEXT =
                              memo.intimate_rel_type_cd::TEXT

                          AND lookup_master.lang_cd =
                              memo.lang_cd

                        ORDER BY
                            CASE
                                WHEN lookup_master.active_status = 'Y'
                                THEN 0
                                ELSE 1
                            END,
                            lookup_master.lookup_master_srno DESC

                        LIMIT 1
                    )
                    ELSE NULL
                END,

            -- FIR Display Number
            'firDisplayNum',
                CASE
                    WHEN memo.fir_reg_num IS NOT NULL
                    THEN apprehend.get_fir_display(
                        memo.fir_reg_num
                    )
                    ELSE NULL
                END,

            -- FIR Registration Date
            'firRegDt',
                CASE
                    WHEN memo.fir_reg_num IS NOT NULL
                    THEN apprehend.get_fir_date(
                        memo.fir_reg_num
                    )
                    ELSE NULL
                END,

            -- GD Display Number
            'gdDisplayNum',
                CASE
                    WHEN memo.gd_num IS NOT NULL
                    THEN apprehend.get_gd_display(
                        memo.gd_num
                    )
                    ELSE NULL
                END,

            -- CCL/GD Display Number
            'ciclGdDisplayNum',
                CASE
                    WHEN memo.cicl_gd_num IS NOT NULL
                    THEN apprehend.get_gd_display(
                        memo.cicl_gd_num
                    )
                    ELSE NULL
                END
        )

        ||

        -- ================================================================
        -- PART 5: NATIONAL-ID CHILD ARRAY
        -- ================================================================
        jsonb_build_object(
            'idList',
            (
                SELECT COALESCE(
                    jsonb_agg(
                        jsonb_build_object(
                            'id',
                                national_id.national_id_srno::TEXT,

                            'nationalIdTypeCd',
                                national_id.nationality_id_type_cd,

                            'nationalIdType',
                                (
                                    SELECT
                                        lookup_master.look_up_value

                                    FROM mdm.m_lookup_masters
                                        lookup_master

                                    WHERE lookup_master.api_master_code =
                                          'IDENTITY_TYP'

                                      AND lookup_master.look_up_code::TEXT =
                                          national_id.nationality_id_type_cd::TEXT

                                      AND lookup_master.lang_cd =
                                          memo.lang_cd

                                    ORDER BY
                                        CASE
                                            WHEN lookup_master.active_status = 'Y'
                                            THEN 0
                                            ELSE 1
                                        END,
                                        lookup_master.lookup_master_srno DESC

                                    LIMIT 1
                                ),

                            'nationalIdNum',
                                national_id.national_id_num,

                            'passportIssueDt',
                                to_char(
                                    national_id.passport_issue_dt,
                                    'YYYY-MM-DD'
                                ),

                            'passportIssuePlc',
                                national_id.passport_issue_plc
                        )
                        ORDER BY
                            national_id.national_id_srno
                    ),
                    '[]'::JSONB
                )

                FROM apprehend.t_apprehend_national_id
                    national_id

                WHERE national_id.apprehend_srno =
                      memo.apprehend_srno
            )
        )

        ||

        -- ================================================================
        -- PART 6: APPREHENDED PERSON ADDRESS ARRAY
        -- ================================================================
        jsonb_build_object(
            'apprehendAddress',
            (
                SELECT COALESCE(
                    jsonb_agg(
                        jsonb_build_object(
                            'id',
                                address.appr_addr_srno::TEXT,

                            'langCd',
                                address.lang_cd,

                            'apprehendSrno',
                                address.apprehend_srno::TEXT,

                            'juvenileSrno',
                                address.juvenile_srno::TEXT,

                            'juvenileVid',
                                address.juvenile_vid,

                            'juvenileSrnoMigr',
                                address.juvenile_srno_migr::TEXT,

                            'addressTypeCd',
                                address.address_type_cd,

                            'addressLine1',
                                address.address_line_1,

                            'addressLine2',
                                address.address_line_2,

                            'addressLine3',
                                address.address_line_3,

                            'subDistrictCd',
                                address.sub_district_cd,

                            'villageCd',
                                address.village_cd::TEXT,

                            'village',
                                address_master.village,

                            'tehsil',
                                address.tehsil,

                            'countryCd',
                                address.country_cd,

                            'lgDistrictCd',
                                address.lg_district_cd,

                            'psId',
                                address.ps_id::TEXT,

                            'stateId',
                                address.state_id::TEXT,

                            'pincode',
                                address.pincode,

                            'isCommAddr',
                                address.is_comm_addr,

                            'outsideIndiaAddr',
                                address.outside_india_addr,

                            'addressEng',
                                address.address_eng,

                            'isPermAddrSame',
                                address.is_perm_addr_same,

                            'addressType',
                                address_master."addressType",

                            'subDistrict',
                                address_master."subDistrict",

                            'country',
                                address_master.country,

                            'state',
                                address_master.state,

                            'district',
                                address_master.district,

                            'ps',
                                address_master.ps
                        )

                        ORDER BY
                            address.appr_addr_srno
                    ),

                    '[]'::JSONB
                )

                FROM apprehend.t_apprehend_addresses
                    address

                LEFT JOIN LATERAL
                    mdm.common_get_address_master_values(
                        address.lang_cd,
                        address.address_type_cd,
                        address.country_cd,
                        address.state_id,
                        address.lg_district_cd,
                        address.sub_district_cd::BIGINT,
                        address.village_cd,
                        address.ps_id
                    ) address_master
                    ON TRUE

                WHERE address.apprehend_srno =
                      memo.apprehend_srno
            )
        )

        ||

        -- ================================================================
        -- PART 7: APPREHEND WITNESS ARRAY
        -- ================================================================
        jsonb_build_object(
            'apprehendWitness',
            (
                SELECT COALESCE(
                    jsonb_agg(
                        jsonb_build_object(
                            'id',
                                witness.appr_witns_srno::TEXT,

                            'langCd',
                                witness.lang_cd,

                            'firstName',
                                witness.first_name,

                            'middleName',
                                witness.middle_name,

                            'lastName',
                                witness.last_name,

                            'firstNameEng',
                                witness.first_name_eng,

                            'middleNameEng',
                                witness.middle_name_eng,

                            'lastNameEng',
                                witness.last_name_eng,

                            'relationTypeCd',
                                witness.relation_type_cd,

                            'relativeName',
                                witness.relative_name,

                            'ageTypeCd',
                                witness.age_type_cd,

                            'ageYrs',
                                witness.age_yrs,

                            'ageMnths',
                                witness.age_mnths,

                            'yob',
                                witness.yob,

                            'dob',
                                to_char(
                                    witness.dob,
                                    'YYYY-MM-DD'
                                ),

                            'ageFrmYrs',
                                witness.age_frm_yrs,

                            'ageToYrs',
                                witness.age_to_yrs,

                            'mobileNum',
                                witness.mobile_num::TEXT,

                            'email',
                                witness.email,

                            'nationalityCd',
                                witness.nationality_cd,

                            'occupationCd',
                                witness.occupation_cd,

                            'genderCd',
                                witness.gender_cd,

                            'maritalStatusCd',
                                witness.marital_status_cd,

                            'witnEvidTenderCd',
                                witness.witn_evid_tender_cd,

                            'witnEvidTender',
                                (
                                    SELECT
                                        lookup_master.look_up_value

                                    FROM mdm.m_lookup_masters
                                        lookup_master

                                    WHERE lookup_master.api_master_code =
                                          'EVIDENCE_TENDERED'

                                      AND lookup_master.look_up_code::TEXT =
                                          witness.witn_evid_tender_cd::TEXT

                                      AND lookup_master.lang_cd =
                                          witness.lang_cd

                                    ORDER BY
                                        CASE
                                            WHEN lookup_master.active_status = 'Y'
                                            THEN 0
                                            ELSE 1
                                        END,
                                        lookup_master.lookup_master_srno DESC

                                    LIMIT 1
                                ),

                            'witnessStatement',
                                witness.witness_statement,

                            'isMobVerified',
                                witness.is_witn_mobile_verf,

                            'witnCategoryCd',
                                witness.witn_category_cd,

                            'aliases',
                                witness.alias,

                            -- ====================================================
                            -- WITNESS ADDRESS ARRAY
                            -- ====================================================
                            'addressGrid',
                            (
                                SELECT COALESCE(
                                    jsonb_agg(
                                        jsonb_build_object(
                                            'id',
                                                witness_address.appr_witn_addr_srno::TEXT,

                                            'langCd',
                                                witness_address.lang_cd,

                                            'apprWitnsSrno',
                                                NULL,

                                            'addressTypeCd',
                                                witness_address.address_type_cd,

                                            'addressLine1',
                                                witness_address.address_line_1,

                                            'addressLine2',
                                                witness_address.address_line_2,

                                            'addressLine3',
                                                witness_address.address_line_3,

                                            'subDistrictCd',
                                                witness_address.sub_district_cd,

                                            'villageCd',
                                                witness_address.village_cd::TEXT,

                                            'village',
                                                witness_address_master.village,

                                            'tehsil',
                                                witness_address.tehsil,

                                            'countryCd',
                                                witness_address.country_cd,

                                            'lgDistrictCd',
                                                witness_address.lg_district_cd,

                                            'psId',
                                                witness_address.ps_id::TEXT,

                                            'stateId',
                                                witness_address.state_id::TEXT,

                                            'pincode',
                                                witness_address.pincode,

                                            'isCommAddr',
                                                witness_address.is_comm_addr,

                                            'isPermAddrSame',
                                                witness_address.is_perm_addr_same,

                                            'outsideIndiaAddr',
                                                witness_address.outside_india_addr,

                                            'addressEng',
                                                witness_address.address_eng,

                                            'addressType',
                                                witness_address_master."addressType",

                                            'subDistrict',
                                                witness_address_master."subDistrict",

                                            'country',
                                                witness_address_master.country,

                                            'state',
                                                witness_address_master.state,

                                            'district',
                                                witness_address_master.district,

                                            'ps',
                                                witness_address_master.ps
                                        )

                                        ORDER BY
                                            witness_address.appr_witn_addr_srno
                                    ),

                                    '[]'::JSONB
                                )

                                FROM apprehend.t_apprehend_witness_addr
                                    witness_address

                                LEFT JOIN LATERAL
                                    mdm.common_get_address_master_values(
                                        witness_address.lang_cd,
                                        witness_address.address_type_cd,
                                        witness_address.country_cd,
                                        witness_address.state_id,
                                        witness_address.lg_district_cd,
                                        witness_address.sub_district_cd::BIGINT,
                                        witness_address.village_cd,
                                        witness_address.ps_id
                                    ) witness_address_master
                                    ON TRUE

                                WHERE witness_address.appr_witns_srno =
                                      witness.appr_witns_srno
                            ),

                            -- ====================================================
                            -- WITNESS NATIONAL-ID ARRAY
                            -- ====================================================
                            'idList',
                            (
                                SELECT COALESCE(
                                    jsonb_agg(
                                        jsonb_build_object(
                                            'id',
                                                witness_id.appr_witn_nat_srno::TEXT,

                                            'langCd',
                                                witness_id.lang_cd,

                                            'apprWitnsSrno',
                                                NULL,

                                            'apprWitnsSrnoMigr',
                                                witness_id.appr_witns_srno_migr::TEXT,

                                            'nationalIdTypeCd',
                                                witness_id.national_id_type_cd,

                                            'nationalIdType',
                                                (
                                                    SELECT
                                                        lookup_master.look_up_value

                                                    FROM mdm.m_lookup_masters
                                                        lookup_master

                                                    WHERE lookup_master.api_master_code =
                                                          'IDENTITY_TYP'

                                                      AND lookup_master.look_up_code::TEXT =
                                                          witness_id.national_id_type_cd::TEXT

                                                      AND lookup_master.lang_cd =
                                                          memo.lang_cd

                                                    ORDER BY
                                                        CASE
                                                            WHEN lookup_master.active_status = 'Y'
                                                            THEN 0
                                                            ELSE 1
                                                        END,
                                                        lookup_master.lookup_master_srno DESC

                                                    LIMIT 1
                                                ),

                                            'nationalIdNum',
                                                witness_id.national_id_num,

                                            'passportIssueDt',
                                                to_char(
                                                    witness_id.passport_issue_dt,
                                                    'YYYY-MM-DD'
                                                ),

                                            'passportIssuePlc',
                                                witness_id.passport_issue_plc
                                        )

                                        ORDER BY
                                            witness_id.appr_witn_nat_srno
                                    ),

                                    '[]'::JSONB
                                )

                                FROM apprehend.t_apprehend_witn_nationality
                                    witness_id

                                WHERE witness_id.appr_witns_srno =
                                      witness.appr_witns_srno
                            )
                        )

                        ORDER BY
                            witness.appr_witns_srno
                    ),

                    '[]'::JSONB
                )

                FROM apprehend.t_apprehend_witness
                    witness

                WHERE witness.apprehend_srno =
                      memo.apprehend_srno
            )
        )

        ||

        -- ================================================================
        -- PART 8: INTIMATE ADDRESS ARRAY
        -- ================================================================
        jsonb_build_object(
            'intimateAddress',
            (
                SELECT COALESCE(
                    jsonb_agg(
                        jsonb_build_object(
                            'id',
                                intimate_address.intmt_addr_srno::TEXT,

                            'langCd',
                                intimate_address.lang_cd,

                            'apprehendSrno',
                                intimate_address.apprehend_srno::TEXT,

                            'addressTypeCd',
                                intimate_address.address_type_cd,

                            'addressLine1',
                                intimate_address.address_line_1,

                            'addressLine2',
                                intimate_address.address_line_2,

                            'addressLine3',
                                intimate_address.address_line_3,

                            'subDistrictCd',
                                intimate_address.sub_district_cd,

                            'villageCd',
                                intimate_address.village_cd::TEXT,

                            'village',
                                intimate_address_master.village,

                            'tehsil',
                                intimate_address.tehsil,

                            'countryCd',
                                intimate_address.country_cd,

                            'lgDistrictCd',
                                intimate_address.lg_district_cd,

                            'psId',
                                intimate_address.ps_id::TEXT,

                            'stateId',
                                intimate_address.state_id::TEXT,

                            'pincode',
                                intimate_address.pincode,

                            'isCommAddr',
                                intimate_address.is_comm_addr,

                            'isPermAddrSame',
                                intimate_address.is_perm_addr_same,

                            'outsideIndiaAddr',
                                intimate_address.outside_india_addr,

                            'addressEng',
                                intimate_address.address_eng,

                            'addressType',
                                intimate_address_master."addressType",

                            'subDistrict',
                                intimate_address_master."subDistrict",

                            'country',
                                intimate_address_master.country,

                            'state',
                                intimate_address_master.state,

                            'district',
                                intimate_address_master.district,

                            'ps',
                                intimate_address_master.ps
                        )

                        ORDER BY
                            intimate_address.intmt_addr_srno
                    ),

                    '[]'::JSONB
                )

                FROM apprehend.t_apprehend_intimate_addr
                    intimate_address

                LEFT JOIN LATERAL
                    mdm.common_get_address_master_values(
                        intimate_address.lang_cd,
                        intimate_address.address_type_cd,
                        intimate_address.country_cd,
                        intimate_address.state_id,
                        intimate_address.lg_district_cd,
                        intimate_address.sub_district_cd::BIGINT,
                        intimate_address.village_cd,
                        intimate_address.ps_id
                    ) intimate_address_master
                    ON TRUE

                WHERE intimate_address.apprehend_srno =
                      memo.apprehend_srno
            )
        )

        ||

        -- ================================================================
        -- PART 9: FILE LIST
        -- ================================================================
        jsonb_build_object(
            'fileList',
            (
                SELECT COALESCE(
                    jsonb_agg(
                        jsonb_build_object(
                            'id',
                                file_record.appr_file_srno::TEXT,

                            'langCd',
                                file_record.lang_cd,

                            'apprehendSrno',
                                file_record.apprehend_srno::TEXT,

                            'fileSrno',
                                file_record.file_srno,

                            'fileTypeCd',
                                file_record.file_type_cd,

                            'fileSubtypeCd',
                                file_record.file_subtype_cd,

                            'fileType',
                                (
                                    SELECT
                                        file_type_master.file_type

                                    FROM mdm.m_upload_file_types
                                        file_type_master

                                    WHERE file_type_master.lang_cd =
                                          memo.lang_cd

                                      AND file_type_master.file_type_cd =
                                          file_record.file_type_cd

                                    LIMIT 1
                                ),

                            'fileSubtype',
                                (
                                    SELECT
                                        file_subtype_master.file_sub_type

                                    FROM mdm.m_upload_file_subtypes
                                        file_subtype_master

                                    WHERE file_subtype_master.lang_cd =
                                          memo.lang_cd

                                      AND file_subtype_master.file_subtype_cd =
                                          file_record.file_subtype_cd

                                    LIMIT 1
                                ),

                            'fileBelongsTo',
                                file_record.file_belongs_to,

                            'fileBelongsToSrno',
                                file_record.file_belongs_to_srno::TEXT,

                            'fileName',
                                file_record.file_name,

                            'filePath',
                                file_record.file_path,

                            'fileDesc',
                                file_record.file_desc,

                            'fileSize',
                                file_record.file_size,

                            'fileGuid',
                                file_record.file_guid,

                            'contentType',
                                NULL,

                            'moduleName',
                                NULL,

                            'fileUploadedOn',
                                NULL
                        )

                        ORDER BY
                            file_record.appr_file_srno
                    ),

                    '[]'::JSONB
                )

                FROM apprehend.t_apprehend_files
                    file_record

                WHERE file_record.apprehend_srno =
                      memo.apprehend_srno
            )
        )

        ||

        -- ================================================================
        -- PART 10: ACT / SECTION LIST
        -- ================================================================
        jsonb_build_object(
            'actSectionList',

            CASE

                /*
                 * BRANCH 1:
                 * GD / CCL based apprehension.
                 *
                 * Highest priority.
                 */
                WHEN memo.is_from_gd IS TRUE
                THEN (
                    SELECT COALESCE(
                        jsonb_agg(
                            jsonb_build_object(
                                'id',
                                    gd_act.id::TEXT,

                                'actCd',
                                    gd_act.actcd,

                                'actLong',
                                    gd_act.actlong,

                                'actShort',
                                    gd_act.actshort,

                                'sectionCd',
                                    gd_act.sectioncd,

                                'section',
                                    gd_act.section,

                                'sectionDesc',
                                    gd_act.sectiondesc
                            )

                            ORDER BY
                                gd_act.id
                        ),

                        '[]'::JSONB
                    )

                    FROM apprehend.get_gd_act_section_list(
                        memo.cicl_gd_num
                    ) gd_act
                )

                /*
                 * BRANCH 2:
                 * FIR based apprehension.
                 */
                WHEN memo.fir_reg_num IS NOT NULL
                THEN (
                    SELECT COALESCE(
                        jsonb_agg(
                            jsonb_build_object(
                                'id',
                                    fir_section.alter_act_srno::TEXT,

                                'actCd',
                                    fir_section.act_cd,

                                'actLong',
                                    act_master.act_long::TEXT,

                                'actShort',
                                    act_master.act_short::TEXT,

                                'sectionCd',
                                    fir_section.section_cd::TEXT,

                                'section',
                                    section_master.section::TEXT,

                                'sectionDesc',
                                    section_master.section_desc::TEXT
                            )

                            ORDER BY
                                fir_section.alter_act_srno
                        ),

                        '[]'::JSONB
                    )

                    FROM fir.t_alter_act_section fir_section

                    LEFT JOIN mdm.m_act act_master
                      ON act_master.act_cd = fir_section.act_cd
                     AND act_master.lang_cd = fir_section.lang_cd

                    LEFT JOIN mdm.m_section section_master
                      ON section_master.section_code =
                         fir_section.section_cd
                      AND section_master.lang_cd =
                         fir_section.lang_cd

                    WHERE fir_section.fir_reg_num =
                          memo.fir_reg_num

                    AND fir_section.record_status = 'C'
                )

                /*
                 * BRANCH 3:
                 * No GD and no FIR.
                 *
                 * Return physically stored
                 * act / section values.
                 */
                ELSE (
                    SELECT COALESCE(
                        jsonb_agg(
                            jsonb_build_object(
                                'id',
                                    NULL,

                                'actCd',
                                    saved_act.act_cd,

                                'actLong',
                                    NULL,

                                'actShort',
                                    NULL,

                                'sectionCd',
                                    saved_act.section_cd,

                                'section',
                                    NULL,

                                'sectionDesc',
                                    NULL
                            )

                            ORDER BY
                                saved_act.apprehend_act_srno
                        ),

                        '[]'::JSONB
                    )

                    FROM apprehend.t_apprehend_act_section
                        saved_act

                    WHERE saved_act.apprehend_srno =
                          memo.apprehend_srno
                )

            END
        )

		|| jsonb_build_object(
        'staffId', NULL,
        'loginId', NULL,
        'officeCd', NULL,
        'officeTypeCd', NULL,
        'rankCd', NULL,
        'officeLevelCd', NULL,
        'allowedRoleCd', NULL,
        'oicStaffId', NULL,
        'oicLoginId', NULL,
        'loginparams', NULL,
        'requestId', NULL,
        'authToken', NULL,
        'isExistingAccused', NULL,
        'isNew', NULL,
        'existingAccSrno', NULL,
        'existingAccFirRegNum', NULL
    )

	||

        jsonb_build_object(
            'juvenileBackgroundReports',
            COALESCE(
                (
                    SELECT jsonb_agg(
                        (
                        jsonb_build_object(
    -- ================================================================
    -- REPORT IDENTITY AND COMMON REQUEST/RESPONSE CONTEXT
    -- ================================================================
    'bgReportSrno', report.bg_report_srno::TEXT,
    'ciclGdNum', memo.cicl_gd_num,
    'firRegNum', report.fir_reg_num::TEXT,
    'apprehendSrno', report.apprehend_srno::TEXT,

    'langCd', report.lang_cd,
    'psId', report.ps_id::TEXT,

    -- These CommonParamsDTO values come from request headers/session in
    -- Java. They are not stored on the background-report record.
    'staffId', NULL,
    'loginId', NULL,
    'officeCd', NULL,
    'stateId', NULL,
    'districtId', NULL,
    'officeTypeCd', NULL,
    'rankCd', NULL,
    'officeLevelCd', NULL,
    'allowedRoleCd', NULL,
    'oicStaffId', NULL,
    'oicLoginId', NULL,
    'loginparams', NULL,
    'requestId', NULL,
    'authToken', NULL
                        )

                        ||

                        jsonb_build_object(

    -- ================================================================
    -- AGE, MARRIAGE AND HOUSEHOLD
    -- ================================================================
    'ageVers', report.age_vers,
    'ageSourceCd', report.age_source_cd,
    'isMarried', report.is_married,
    'spouseName', report.spouse_name,
    'spouseAge', report.spouse_age,
    'childrenDtls', report.children_dtls,
    'childAge', report.child_age,

    -- ================================================================
    -- HOME, EDUCATION, EMPLOYMENT AND SOCIAL ENVIRONMENT
    -- ================================================================
    'religionAttitude', report.religion_attitude,
    'presentLivingCond', report.present_living_cond,
    'homeDisciplineDtls', report.home_discipline_dtls,
    'otherFactors', report.other_factors,
    'employmentDtls', report.employment_dtls,
    'incomeUtilManner', report.income_util_manner,
    'workRecord', report.work_record,
    'eduQualCd', report.edu_qual_cd,
    'familyLeaveReason', report.family_leave_reason,
    'lastSchoolStud', report.last_school_stud,
    'vocationalTraining', report.vocational_training,
    'attTowardFriend', report.att_toward_friend,
    'attitudeOfFriend', report.attitude_of_friend,
    'attitudeOfTeacher', report.attitude_of_teacher,
    'neighbourObserv', report.neighbour_observ,

    -- The unusual spelling is intentional: it is the Java DTO name.
    'onbservAbtNeighbour', report.onbserv_abt_neighbour

                        )

                        ||

                        jsonb_build_object(

    -- ================================================================
    -- RISK, HISTORY, HEALTH, ASSESSMENT AND REHABILITATION
    -- ================================================================
    'isVictimOffence', report.is_victim_offence,
    'victimOffenceDtls', report.victim_offence_dtls,
    'isDrugPeddlerUsed', report.is_drug_peddler_used,
    'drugPeddlerUsedDtls', report.drug_peddler_used_dtls,
    'hasTendRunAway', report.has_tend_run_away,
    'runAwayDtls', report.run_away_dtls,
    'isEarlierApprehend', report.is_earlier_apprehend,
    'prevApprehendDtls', report.prev_apprehend_dtls,
    'prevCaseHistory', report.prev_case_history,
    'healthConditions', report.health_conditions,
    'mentalCondition', report.mental_condition,
    'anyOthrRemarks', report.any_othr_remarks,
    'emotionalFactors', report.emotional_factors,
    'intelligenceDtls', report.intelligence_dtls,
    'suggestiveProblems', report.suggestive_problems,
    'socioEcoStatus', report.socio_eco_status,
    'enquiryAnalysis', report.enquiry_analysis,
    'expertOpinion', report.expert_opinion,
    'rehabRecommend', report.rehab_recommend,
    'physicalCondCd', report.physical_cond_cd

                        )

                        ||

                        jsonb_build_object(

    -- ================================================================
    -- PHYSICAL SUMMARY
    -- Labels are populated inside physicalDescription in a later version.
    -- At the root, Java currently leaves the label-only values unset.
    -- ================================================================
    'bodyBuildTypeCd', report.build_type_cd,
    'bodyComplexionTypeCd', report.complexion_type_cd,
    'bodyBuildType', NULL,
    'bodyComplexionType', NULL,
    'otherPhysicalDetails', report.other_physical_desc,
    'heightFromCm', report.height_from,
    'heightToCm', report.height_to

                        )

                        ||

                        jsonb_build_object(

    -- ================================================================
    -- JUVENILE/JJB AND COURT INFORMATION
    -- ================================================================
    'isOffenceHeinous', report.is_offence_heinous,
    'isJuvDifferAble', report.is_juv_differ_able,
    'differAbleCd', report.differ_able_cd,

    -- JuvBackgroundReportDTO exposes courtEstblCd, but the Java entity does
    -- not map a source column for it in this report table.
    'courtEstblCd', NULL,
    'courtOrderNum', report.court_order_num,
    'otherDifferAble', report.differ_able_others,
    'othrAgeSrc', report.age_src_others,
    'jjbOrderDt', report.jjb_order_dt::DATE,
    'jjbName', report.jjb_name,

    -- These are conditionally enriched by Java only when courtEstblCd is
    -- available. With no mapped source, they remain null for now.
    'courtComplexCd', NULL,
    'courtComplexName', NULL,
    'establishmentName', NULL,
    'cisDistrictCd', NULL,
    'cisDistrictName', NULL,

    -- alleged_offence_dtls is already stored as JSON in the parent table.
    'allegedOffenceDtls', report.alleged_offence_dtls,
	'fileList',
        (
            SELECT COALESCE(
                jsonb_agg(
                    jsonb_build_object(
                        'fileUploadSrno',
                            background_file.file_upload_srno::TEXT,

                        'langCd',
                            background_file.lang_cd,

                        'bgReportSrno',
                            background_file.bg_report_srno::TEXT,

                        'fileSrno',
                            background_file.file_srno,

                        'fileTypeCd',
                            background_file.file_type_cd,

                        'fileSubtypeCd',
                            background_file.file_subtype_cd,

                        -- Java resolves file labels using the language of
                        -- the parent background report.
                        'fileType',
                            (
                                SELECT file_type_master.file_type
                                FROM mdm.m_upload_file_types file_type_master
                                WHERE file_type_master.lang_cd = report.lang_cd
                                  AND file_type_master.file_type_cd =
                                      background_file.file_type_cd
                                LIMIT 1
                            ),

                        'fileSubtype',
                            (
                                SELECT file_subtype_master.file_sub_type
                                FROM mdm.m_upload_file_subtypes
                                    file_subtype_master
                                WHERE file_subtype_master.lang_cd =
                                      report.lang_cd
                                  AND file_subtype_master.file_subtype_cd =
                                      background_file.file_subtype_cd
                                LIMIT 1
                            ),

                        'fileName',
                            background_file.file_name,

                        'filePath',
                            background_file.file_path,

                        'fileBelongsTo',
                            background_file.file_belongs_to,

                        'fileBelongsToSrno',
                            background_file.file_belongs_to_srno::TEXT,

                        'fileGuid',
                            background_file.file_guid,

                        'fileDesc',
                            background_file.file_desc,

                        'fileSize',
                            background_file.file_size,

                        -- These DTO values are not populated from this
                        -- entity by SocialBackgroundViewUseCaseImpl.
                        'contentType', NULL,
                        'moduleName', NULL,
                        'fileUploadedOn', NULL
                    )
                    ORDER BY background_file.file_upload_srno
                ),
                '[]'::JSONB
            )
            FROM apprehend.t_jcl_background_files background_file
            WHERE background_file.bg_report_srno = report.bg_report_srno
        ),
		'familyDtls',
        (
            SELECT COALESCE(
                jsonb_agg(
                    jsonb_build_object(
                        'id',
                            family.juv_family_srno::TEXT,

                        'relationTypeCd',
                            family.relation_type_cd,

                        -- Java enriches relationTypeCd using RELATION_TYP.
                        'relationType',
                            CASE
                                WHEN family.relation_type_cd IS NOT NULL
                                THEN (
                                    SELECT lookup_master.look_up_value
                                    FROM mdm.m_lookup_masters lookup_master
                                    WHERE lookup_master.api_master_code =
                                          'RELATION_TYP'
                                      AND lookup_master.look_up_code::TEXT =
                                          family.relation_type_cd::TEXT
                                      AND lookup_master.lang_cd = report.lang_cd
                                    ORDER BY
                                        CASE
                                            WHEN lookup_master.active_status = 'Y'
                                            THEN 0
                                            ELSE 1
                                        END,
                                        lookup_master.lookup_master_srno DESC
                                    LIMIT 1
                                )
                                ELSE NULL
                            END,

                        'relativeName',
                            family.relative_name,

                        'relativeNameEng',
                            family.relative_name_eng,

                        'ageTypeCd',
                            family.age_type_cd,

                        'ageYrs',
                            family.age_yrs,

                        -- Java exposes ageMonths as the JSON key ageMnths.
                        'ageMnths',
                            family.age_months,

                        'yob',
                            family.yob,

                        'dob',
                            family.dob::DATE,

                        'ageFromYrs',
                            family.age_from_yrs,

                        'ageToYrs',
                            family.age_to_yrs,

                        'genderCd',
                            family.gender_cd,

                        'eduQualCd',
                            family.edu_qual_cd,

                        'occupationCd',
                            family.occupation_cd,

                        -- Java enriches occupationCd using OCCUPATION.
                        'occupation',
                            CASE
                                WHEN family.occupation_cd IS NOT NULL
                                THEN (
                                    SELECT lookup_master.look_up_value
                                    FROM mdm.m_lookup_masters lookup_master
                                    WHERE lookup_master.api_master_code =
                                          'OCCUPATION'
                                      AND lookup_master.look_up_code::TEXT =
                                          family.occupation_cd::TEXT
                                      AND lookup_master.lang_cd = report.lang_cd
                                    ORDER BY
                                        CASE
                                            WHEN lookup_master.active_status = 'Y'
                                            THEN 0
                                            ELSE 1
                                        END,
                                        lookup_master.lookup_master_srno DESC
                                    LIMIT 1
                                )
                                ELSE NULL
                            END,

                        'healthStatus',
                            family.health_status,

                        'mentalHealthHist',
                            family.mental_health_hist,

                        'addictionCd',
                            family.addiction_cd,

                        'incomeCd',
                            family.income_type_cd,

                        'crimeNature',
                            family.crime_nature,

                        'legalStatus',
                            family.legal_status,

                        'arrestDtls',
                            family.arrest_dtls,

                        'punishmentAwarded',
                            family.punishment_awarded,

                        'confinePeriod',
                            family.confine_period
                    )
                    ORDER BY family.juv_family_srno
                ),
                '[]'::JSONB
            )
            FROM apprehend.t_juv_family_dtls family
            WHERE family.bg_report_srno = report.bg_report_srno
        ),
		'phyAbuse',
        (
            SELECT COALESCE(
                jsonb_agg(
                    jsonb_build_object(
                        -- TJuvPhyAbuseEntity.id maps to the DTO's String id.
                        'id',
                            abuse.juv_abuse_srno::TEXT,

                        -- The DTO declares juvAbuseSrno separately, but the
                        -- Java domain mapper has no matching source property.
                        'juvAbuseSrno',
                            NULL,

                        'langCd',
                            abuse.lang_cd,

                        'juvenileVid',
                            abuse.juvenile_vid,

                        -- Java globally serializes Long identifiers as text.
                        'juvenileSrno',
                            abuse.juvenile_srno::TEXT,

                        'abuseTypeCd',
                            abuse.abuse_type_cd,

                        -- SocialBackgroundViewUseCaseImpl does not enrich
                        -- abuseTypeCd into the DTO label abuseType.
                        'abuseType',
                            NULL,

                        'abuseRemarks',
                            abuse.abuse_remarks
                    )
                    ORDER BY abuse.juv_abuse_srno
                ),
                '[]'::JSONB
            )
            FROM apprehend.t_juv_phy_abuse abuse
            WHERE abuse.bg_report_srno = report.bg_report_srno
        ),
		'physicalDescription',
        jsonb_build_object(
            'bodyBuildTypeCd',
                report.build_type_cd,

            -- Java uses Constants.PHY_FEAT = PHYSCL_FEATURES here.
            'bodyBuildType',
                CASE
                    WHEN report.build_type_cd IS NOT NULL
                    THEN (
                        SELECT lookup_master.look_up_value
                        FROM mdm.m_lookup_masters lookup_master
                        WHERE lookup_master.api_master_code =
                              'PHYSCL_FEATURES'
                          AND lookup_master.look_up_code::TEXT =
                              report.build_type_cd::TEXT
                          AND lookup_master.lang_cd = report.lang_cd
                        ORDER BY
                            CASE
                                WHEN lookup_master.active_status = 'Y'
                                THEN 0
                                ELSE 1
                            END,
                            lookup_master.lookup_master_srno DESC
                        LIMIT 1
                    )
                    ELSE NULL
                END,

            'bodyComplexionTypeCd',
                report.complexion_type_cd,

            -- Java uses the same PHYSCL_FEATURES category for complexion.
            'bodyComplexionType',
                CASE
                    WHEN report.complexion_type_cd IS NOT NULL
                    THEN (
                        SELECT lookup_master.look_up_value
                        FROM mdm.m_lookup_masters lookup_master
                        WHERE lookup_master.api_master_code =
                              'PHYSCL_FEATURES'
                          AND lookup_master.look_up_code::TEXT =
                              report.complexion_type_cd::TEXT
                          AND lookup_master.lang_cd = report.lang_cd
                        ORDER BY
                            CASE
                                WHEN lookup_master.active_status = 'Y'
                                THEN 0
                                ELSE 1
                            END,
                            lookup_master.lookup_master_srno DESC
                        LIMIT 1
                    )
                    ELSE NULL
                END,

            'otherPhysicalDetails',
                report.other_physical_desc,

            'heightFromCm',
                report.height_from,

            'heightToCm',
                report.height_to,

            'dressTypeList',
                (
                    SELECT COALESCE(
                        jsonb_agg(
                            jsonb_build_object(
                                'id',
                                    dress.juv_dress_srno::TEXT,

                                'langCd',
                                    dress.lang_cd,

                                -- These DTO properties have no mapped Java
                                -- entity source and therefore remain null.
                                'firAccusedSrnoMigr', NULL,
                                'accusedVid', NULL,
                                'accusedSrno', NULL,

                                'firRegNum',
                                    dress.fir_reg_num::TEXT,

                                'regTypeCd', NULL,
                                'crmDetailSrno', NULL,
                                'crmSeqNum', NULL,

                                'dressForCd',
                                    dress.dress_for_cd,

                                -- Java enriches dressForCd using
                                -- Constants.PHY_MASTER_CODE = PHY_DESC_TYP.
                                'dressFor',
                                    CASE
                                        WHEN dress.dress_for_cd IS NOT NULL
                                        THEN (
                                            SELECT lookup_master.look_up_value
                                            FROM mdm.m_lookup_masters
                                                lookup_master
                                            WHERE lookup_master.api_master_code =
                                                  'PHY_DESC_TYP'
                                              AND lookup_master.look_up_code::TEXT =
                                                  dress.dress_for_cd::TEXT
                                              AND lookup_master.lang_cd =
                                                  report.lang_cd
                                            ORDER BY
                                                CASE
                                                    WHEN lookup_master.active_status = 'Y'
                                                    THEN 0
                                                    ELSE 1
                                                END,
                                                lookup_master.lookup_master_srno DESC
                                            LIMIT 1
                                        )
                                        ELSE NULL
                                    END,

                                'dressTypeCd',
                                    dress.dress_type_cd,

                                'dressType',
                                    dress.dress_type,

                                'dressSubtypeCd',
                                    dress.dress_subtype_cd,

                                'dressSubtype',
                                    dress.dress_subtype,

                                'othrDressDtls',
                                    dress.othr_dress_dtls
                            )
                            ORDER BY dress.juv_dress_srno
                        ),
                        '[]'::JSONB
                    )
                    FROM apprehend.t_juv_dress dress
                    WHERE dress.bg_report_srno = report.bg_report_srno
                ),

            -- These arrays are placeholders until Versions 20 and 21.
            'identityMarkList',
                (
                    SELECT COALESCE(
                        jsonb_agg(
                            jsonb_build_object(
                                'id',
                                    identity_mark.juv_identity_srno::TEXT,

                                'langCd',
                                    identity_mark.lang_cd,

                                'bgReportSrno',
                                    identity_mark.bg_report_srno::TEXT,

                                'idMarksTypeCd',
                                    identity_mark.id_marks_type_cd,

                                'bodyPartLocCd',
                                    identity_mark.body_part_loc_cd,

                                'tattooTypeCd',
                                    identity_mark.tattoo_type_cd,

                                'tattooMarkDesc',
                                    identity_mark.tattoo_mark_desc,

                                -- Java uses Constants.IDEN_MARKS =
                                -- PHY_FEAT_PCODE_IDENTF for this label.
                                'idMarksType',
                                    CASE
                                        WHEN identity_mark.id_marks_type_cd
                                             IS NOT NULL
                                        THEN (
                                            SELECT lookup_master.look_up_value
                                            FROM mdm.m_lookup_masters
                                                lookup_master
                                            WHERE lookup_master.api_master_code =
                                                  'PHY_FEAT_PCODE_IDENTF'
                                              AND lookup_master.look_up_code::TEXT =
                                                  identity_mark.id_marks_type_cd::TEXT
                                              AND lookup_master.lang_cd =
                                                  report.lang_cd
                                            ORDER BY
                                                CASE
                                                    WHEN lookup_master.active_status = 'Y'
                                                    THEN 0
                                                    ELSE 1
                                                END,
                                                lookup_master.lookup_master_srno DESC
                                            LIMIT 1
                                        )
                                        ELSE NULL
                                    END,

                                -- Java uses Constants.PHY_FEAT =
                                -- PHYSCL_FEATURES for the body-part label.
                                'bodyPartLoc',
                                    CASE
                                        WHEN identity_mark.body_part_loc_cd
                                             IS NOT NULL
                                        THEN (
                                            SELECT lookup_master.look_up_value
                                            FROM mdm.m_lookup_masters
                                                lookup_master
                                            WHERE lookup_master.api_master_code =
                                                  'PHYSCL_FEATURES'
                                              AND lookup_master.look_up_code::TEXT =
                                                  identity_mark.body_part_loc_cd::TEXT
                                              AND lookup_master.lang_cd =
                                                  report.lang_cd
                                            ORDER BY
                                                CASE
                                                    WHEN lookup_master.active_status = 'Y'
                                                    THEN 0
                                                    ELSE 1
                                                END,
                                                lookup_master.lookup_master_srno DESC
                                            LIMIT 1
                                        )
                                        ELSE NULL
                                    END
                            )
                            ORDER BY identity_mark.juv_identity_srno
                        ),
                        '[]'::JSONB
                    )
                    FROM apprehend.t_juv_identity_marks identity_mark
                    WHERE identity_mark.bg_report_srno =
                          report.bg_report_srno
                ),


            -- Java @JsonProperty renames physicalFeaturesList to this key.
            'physicalFeatureList',
                (
                    SELECT COALESCE(
                        jsonb_agg(
                            jsonb_build_object(
                                'id',
                                    physical_feature.juv_phy_feat_srno::TEXT,

                                'juvenileVid',
                                    physical_feature.juvenile_vid,

                                'langCd',
                                    physical_feature.lang_cd,

                                'bgReportSrno',
                                    physical_feature.bg_report_srno::TEXT,

                                -- Java globally serializes Long IDs as text.
                                'juvenileSrno',
                                    physical_feature.juvenile_srno::TEXT,

                                'phyFeatCategoryCd',
                                    physical_feature.phy_feat_category_cd,

                                'phyFeatureMajCd',
                                    physical_feature.phy_feature_maj_cd,

                                'phyFeatureMinCd',
                                    physical_feature.phy_feature_min_cd,

                                -- Java keeps the category and major labels
                                -- stored on the child entity.
                                'phyFeatCategory',
                                    physical_feature.phy_feat_category,

                                'phyFeatureMaj',
                                    physical_feature.phy_feature_major,

                                -- Java overwrites the stored minor label by
                                -- looking up phyFeatureMinCd through
                                -- Constants.PHY_FEAT = PHYSCL_FEATURES.
                                'phyFeatureMin',
                                    CASE
                                        WHEN physical_feature.phy_feature_min_cd
                                             IS NOT NULL
                                        THEN (
                                            SELECT lookup_master.look_up_value
                                            FROM mdm.m_lookup_masters
                                                lookup_master
                                            WHERE lookup_master.api_master_code =
                                                  'PHYSCL_FEATURES'
                                              AND lookup_master.look_up_code::TEXT =
                                                  physical_feature.phy_feature_min_cd::TEXT
                                              AND lookup_master.lang_cd =
                                                  report.lang_cd
                                            ORDER BY
                                                CASE
                                                    WHEN lookup_master.active_status = 'Y'
                                                    THEN 0
                                                    ELSE 1
                                                END,
                                                lookup_master.lookup_master_srno DESC
                                            LIMIT 1
                                        )
                                        ELSE NULL
                                    END
                            )
                            ORDER BY physical_feature.juv_phy_feat_srno
                        ),
                        '[]'::JSONB
                    )
                    FROM apprehend.t_juv_phy_feature physical_feature
                    WHERE physical_feature.bg_report_srno =
                          report.bg_report_srno
                )
        ),

		'actSectionList',
        CASE
            -- ============================================================
            -- BRANCH 1: FIR background report
            -- ============================================================
            WHEN report.fir_reg_num IS NOT NULL
            THEN (
                SELECT COALESCE(
                    jsonb_agg(
                        jsonb_build_object(
                            'id',
                                fir_section.alter_act_srno::TEXT,

                            'actCd',
                                fir_section.act_cd,

                            'actLong',
                                act_master.act_long::TEXT,

                            'actShort',
                                act_master.act_short::TEXT,

                            'sectionCd',
                                fir_section.section_cd::TEXT,

                            'section',
                                section_master.section::TEXT,

                            'sectionDesc',
                                section_master.section_desc::TEXT
                        )
                        ORDER BY fir_section.alter_act_srno
                    ),
                    '[]'::JSONB
                )
                FROM fir.t_alter_act_section fir_section
                LEFT JOIN mdm.m_act act_master
                  ON act_master.act_cd = fir_section.act_cd
                 AND act_master.lang_cd = fir_section.lang_cd
                LEFT JOIN mdm.m_section section_master
                  ON section_master.section_code = fir_section.section_cd
                 AND section_master.lang_cd = fir_section.lang_cd
                WHERE fir_section.fir_reg_num = report.fir_reg_num
                  AND fir_section.record_status = 'C'
            )

            -- ============================================================
            -- BRANCH 2: CICL/GD background report
            -- Java reaches this branch whenever firRegNum is null.
            -- ============================================================
            WHEN memo.cicl_gd_num IS NOT NULL
            THEN (
                SELECT COALESCE(
                    jsonb_agg(
                        jsonb_build_object(
                            'id', gd_act.id::TEXT,
                            'actCd', gd_act.actcd,
                            'actLong', gd_act.actlong,
                            'actShort', gd_act.actshort,
                            'sectionCd', gd_act.sectioncd,
                            'section', gd_act.section,
                            'sectionDesc', gd_act.sectiondesc
                        )
                        ORDER BY gd_act.id
                    ),
                    '[]'::JSONB
                )
                FROM apprehend.get_gd_act_section_list(
                    memo.cicl_gd_num
                ) gd_act
            )

            -- ============================================================
            -- BRANCH 3: no FIR and no CICL/GD key
            -- ============================================================
            ELSE '[]'::JSONB
        END,

		'accusedDetails',
        (
            SELECT jsonb_build_object(
                'apprehendSrno',
                    accused_details.apprehendsrno::TEXT,

                -- These DTO fields are not returned by the Java helper.
                'accusedSrno', NULL,
                'accusedVid', NULL,

                'firstName',
                    accused_details.firstname,

                'middleName',
                    accused_details.middlename,

                'lastName',
                    accused_details.lastname,

                'firstNameEng',
                    accused_details.firstnameeng,

                'middleNameEng',
                    accused_details.middlenameeng,

                'lastNameEng',
                    accused_details.lastnameeng,

                'relationTypeCd',
                    accused_details.relationtypecd,

                'relativeName',
                    accused_details.relativename,

                'relativeNameEng',
                    accused_details.relativenameeng,

                'relMobileNum',
                    accused_details.relmobilenum::TEXT,

                'genderCd',
                    accused_details.gendercd,

                'ageTypeCd',
                    accused_details.agetypecd,

                'ageYrs',
                    accused_details.ageyrs,

                -- AccusedDetailsDTO uses ageMonths, not ageMnths.
                'ageMonths',
                    accused_details.agemonths,

                'yob',
                    accused_details.yob,

                -- Java formats this LocalDateTime as yyyy-MM-dd.
                'dob',
                    accused_details.dob::DATE,

                'ageFromYrs',
                    accused_details.agefromyrs,

                'ageToYrs',
                    accused_details.agetoyrs,

                -- Present in the DTO/projection but absent from the actual
                -- helper result, so Java leaves them null.
                'nationalityCd', NULL,
                'categoryCd', NULL,
                'religionCd', NULL,
                'occupationCd', NULL,
                'injuryDetails', NULL,

                'addressGrid',
                    (
                        SELECT COALESCE(
                            jsonb_agg(
                                jsonb_build_object(
                                    'id',
                                        accused_address.id::TEXT,

                                    'addressTypeCd',
                                        accused_address.addresstypecd,

                                    'addressLine1',
                                        accused_address.addressline1,

                                    'addressLine2',
                                        accused_address.addressline2,

                                    'addressLine3',
                                        accused_address.addressline3,

                                    'subDistrictCd',
                                        accused_address.subdistrictcd::TEXT,

                                    'villageCd',
                                        accused_address.villagecd::TEXT,

                                    'village',
                                        accused_address.village,

                                    'tehsil',
                                        accused_address.tehsil,

                                    'countryCd',
                                        accused_address.countrycd,

                                    'psId',
                                        accused_address.psid::TEXT,

                                    'stateId',
                                        accused_address.stateid::TEXT,

                                    'lgDistrictCd',
                                        accused_address.lgdistrictcd,

                                    'pincode',
                                        accused_address.pincode,

                                    'isCommAddr',
                                        accused_address.iscommaddr,

                                    'outsideIndiaAddr',
                                        accused_address.outsideindiaaddr,

                                    'addressEng',
                                        accused_address.addresseng,

                                    'isPermAddrSame',
                                        accused_address.ispermaddrsame,

                                    'subDistrict',
                                        accused_address.subdistrict,

                                    'country',
                                        accused_address.country,

                                    'state',
                                        accused_address.state,

                                    'district',
                                        accused_address.district,

                                    'ps',
                                        accused_address.ps
                                )
                                ORDER BY accused_address.id
                            ),
                            '[]'::JSONB
                        )
                        FROM apprehend.get_accused_address_apprehend(
                            report.apprehend_srno
                        ) accused_address
                    ),

                -- SocialBackgroundViewRepositoryImpl does not populate IDs.
                'idList', NULL
            )

            -- The anchor row ensures Java's empty AccusedDetailsDomain shape
            -- is still returned even when the personal-details helper has no
            -- row. In that case, its scalar fields are JSON null.
            FROM (VALUES (1)) AS anchor(dummy)
            LEFT JOIN LATERAL (
                SELECT details_row.*
                FROM apprehend.get_accused_details_apprehend(
                    report.apprehend_srno
                ) details_row
                LIMIT 1
            ) accused_details ON TRUE
        ),


    -- ================================================================
    -- AUDIT VALUES EXPOSED BY THE JAVA DTO
    -- ================================================================
    'recordStatus', report.record_status,
    'recordCreatedOn', report.record_created_on,
                            'recordCreatedBy', report.record_created_by::TEXT
                        )
                        )
                        ORDER BY report.bg_report_srno
                    )
                    FROM apprehend.t_juv_background_report report
                    WHERE report.apprehend_srno = memo.apprehend_srno
                ),
                '[]'::JSONB
            )
        )


    INTO v_result

    FROM apprehend.t_apprehend_memo
        memo

    WHERE memo.apprehend_srno =
          p_apprehend_srno;


    RETURN v_result;

END;
$$;
