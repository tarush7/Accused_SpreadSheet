-- =============================================================================
-- ARREST MODULE JSON FUNCTION
--
-- Assumption: the 15 Arrest tables are in the PostgreSQL schema "arrest".
-- Confirm that schema name in the target database before running this file.
-- =============================================================================

-- CREATE SCHEMA IF NOT EXISTS arrest;


-- Converts a flat object's keys from snake_case to camelCase.
-- Example: arr_surr_srno -> arrSurrSrno
CREATE OR REPLACE FUNCTION arrest.jsonb_camel_keys(data JSONB)
RETURNS JSONB
LANGUAGE sql
IMMUTABLE
STRICT
AS $$
SELECT jsonb_object_agg(
    (
        SELECT string_agg(
            CASE WHEN part_number = 1 THEN part ELSE initcap(part) END,
            ''
        )
        FROM (
            SELECT part, row_number() OVER () AS part_number
            FROM unnest(string_to_array(key, '_')) AS part
        ) parts
    ),
    value
)
FROM jsonb_each(data);
$$;


-- Resolves a normal m_lookup_masters code to its display value.
CREATE OR REPLACE FUNCTION arrest.lookup_value(
    p_api_master_code TEXT,
    p_lookup_code TEXT,
    p_lang_cd INTEGER
)
RETURNS TEXT
LANGUAGE sql
STABLE
AS $$
SELECT ml.look_up_value
FROM mdm.m_lookup_masters ml
WHERE ml.api_master_code = p_api_master_code
  AND ml.look_up_code::TEXT = p_lookup_code
  AND ml.lang_cd = p_lang_cd
ORDER BY CASE WHEN ml.active_status = 'Y' THEN 0 ELSE 1 END,
         ml.lookup_master_srno DESC
LIMIT 1;
$$;


-- Some physical-feature fields use the parent code/value columns.
CREATE OR REPLACE FUNCTION arrest.lookup_parent_value(
    p_api_master_code TEXT,
    p_parent_code TEXT,
    p_lang_cd INTEGER
)
RETURNS TEXT
LANGUAGE sql
STABLE
AS $$
SELECT ml.look_up_parentvalue
FROM mdm.m_lookup_masters ml
WHERE ml.api_master_code = p_api_master_code
  AND ml.look_up_parentcode::TEXT = p_parent_code
  AND ml.lang_cd = p_lang_cd
ORDER BY CASE WHEN ml.active_status = 'Y' THEN 0 ELSE 1 END,
         ml.lookup_master_srno DESC
LIMIT 1;
$$;


-- =============================================================================
-- MAIN FUNCTION
-- One arr_surr_srno in, one complete Arrest JSON object out.
-- =============================================================================
CREATE OR REPLACE FUNCTION arrest.get_arrest_json(p_arr_surr_srno BIGINT)
RETURNS JSONB
LANGUAGE plpgsql
STABLE
AS $$
DECLARE
    v_result JSONB;
BEGIN
    SELECT
        -- Keep every root-table column, but expose application-friendly keys.
        arrest.jsonb_camel_keys(to_jsonb(memo.*))

        -- Root display values and external-master details.
        || jsonb_build_object(
            'langCdValue', arrest.lookup_value(
                'OFFCL_LANG', memo.lang_cd::TEXT, memo.lang_cd
            ),
            'fullName', NULLIF(TRIM(CONCAT_WS(
                ' ', memo.first_name, memo.middle_name, memo.last_name
            )), ''),
            'state', (
                SELECT state.state
                FROM mdm.m_state state
                WHERE state.state_id = memo.state_id
                  AND state.lang_cd = memo.lang_cd
                LIMIT 1
            ),
            'district', (
                SELECT district.district
                FROM mdm.m_district district
                WHERE district.district_id = memo.district_id
                  AND district.lang_cd = memo.lang_cd
                LIMIT 1
            ),
            'policeStation', (
                SELECT station.ps
                FROM mdm.m_police_station station
                WHERE station.ps_id = memo.ps_id
                  AND station.lang_cd = memo.lang_cd
                LIMIT 1
            ),
            'arrestTypeCdValue', arrest.lookup_value(
                'ARR_SURR_TYPE', memo.arrest_type_cd::TEXT, memo.lang_cd
            ),
            'relationTypeCdValue', arrest.lookup_value(
                'RELATION_TYP', memo.relation_type_cd::TEXT, memo.lang_cd
            ),
            'otherRelationTypeCdValue', arrest.lookup_value(
                'RELATION_TYP', memo.othr_rel_type_cd::TEXT, memo.lang_cd
            ),
            'religionCdValue', arrest.lookup_value(
                'RELIGION', memo.religion_cd::TEXT, memo.lang_cd
            ),
            'nationalIdTypeCdValue', arrest.lookup_value(
                'NTNL_ID_DOC_TYP', memo.national_id_type_cd::TEXT, memo.lang_cd
            ),
            'genderCdValue', arrest.lookup_value(
                'GENDER', memo.gender_cd::TEXT, memo.lang_cd
            ),
            'categoryCdValue', arrest.lookup_value(
                'CATEGORY', memo.category_cd::TEXT, memo.lang_cd
            ),
            'livingStatusCdValue', arrest.lookup_value(
                'LIVING_STATUS', memo.living_status_cd::TEXT, memo.lang_cd
            ),
            'maritalStatusCdValue', arrest.lookup_value(
                'MARTL_STATUS', memo.marital_status_cd::TEXT, memo.lang_cd
            ),
            'occupationCdValue', arrest.lookup_value(
                'OCCUPATION', memo.occupation_cd::TEXT, memo.lang_cd
            ),
            'ageProofTypeCdValue', arrest.lookup_value(
                'AGE_DETERM', memo.age_proof_type_cd::TEXT, memo.lang_cd
            ),
            'languageDialectCdValue', arrest.lookup_value(
                'LANG_DIALECTS',
                COALESCE(memo.language_dialect_cd, memo.lang_dialect_cd)::TEXT,
                memo.lang_cd
            ),
            'ageTypeCdValue', arrest.lookup_value(
                'AGE_PANEL_TYPE', memo.age_type_cd::TEXT, memo.lang_cd
            ),
            'nationalityCdValue', arrest.lookup_value(
                'NATIONALITY', memo.nationality_cd::TEXT, memo.lang_cd
            ),
            'bloodGroupCdValue', arrest.lookup_value(
                'BLOOD_GROUP', memo.blood_group_cd::TEXT, memo.lang_cd
            ),
            'incomeGroupCdValue', arrest.lookup_value(
                'INCOME_GROUP', memo.income_group_cd::TEXT, memo.lang_cd
            ),
            'arrestFromDistrictCdValue', (
                SELECT district.district
                FROM mdm.m_district district
                WHERE district.district_cd = memo.arr_from_district_cd
                  AND district.lang_cd = memo.lang_cd
                LIMIT 1
            ),
            'arrestBeatCdValue', (
                SELECT beat.beat_name
                FROM mdm.m_ps_beat beat
                WHERE beat.beat_cd = memo.arrest_beat_cd
                  AND beat.lang_cd = memo.lang_cd
                LIMIT 1
            ),
            'majorPlaceTypeCdValue', arrest.lookup_value(
                'MAJOR_PLACE_OCCURANCE', memo.major_place_type_cd::TEXT, memo.lang_cd
            ),
            'minorPlaceTypeCdValue', arrest.lookup_value(
                'PLACE_TYP', memo.minor_place_type_cd::TEXT, memo.lang_cd
            ),
            'dyspFullName', NULLIF(TRIM(CONCAT_WS(
                ' ', dysp.first_name, dysp.middle_name, dysp.last_name
            )), ''),
            'dyspRankDesc', dysp.rank_desc,
            'dyspLoginIdValue', dysp.login_id,
            'arrestActionTakenCdValue', arrest.lookup_value(
                'ARRST_ACTN', memo.arrest_action_taken_cd::TEXT, memo.lang_cd
            ),
            'arrestStatusCdValue', arrest.lookup_value(
                'ARRST_STATUS', memo.arrest_status_cd::TEXT, memo.lang_cd
            ),
            'ioFullName', NULLIF(TRIM(CONCAT_WS(
                ' ', io_staff.first_name, io_staff.middle_name, io_staff.last_name
            )), ''),
            'ioRankDesc', io_staff.rank_desc,
            'ioLoginId', io_staff.login_id,
            'intimateRelationTypeCdValue', arrest.lookup_value(
                'RELATION_TYP', memo.intimate_rel_type_cd::TEXT, memo.lang_cd
            ),
            'intimateModeCdValue', arrest.lookup_value(
                'INFO_MODE', memo.intimate_mode_cd::TEXT, memo.lang_cd
            ),
            'evidenceTypeCdValue', arrest.lookup_value(
                'EVIDENCE_TYP', memo.evidence_type_cd::TEXT, memo.lang_cd
            ),
            'identityTypeCdValue', arrest.lookup_value(
                'IDENTITY_TYP', memo.id_type_cd::TEXT, memo.lang_cd
            ),
            'buildTypeCdValue', arrest.lookup_value(
                'PHY_FEAT_PCODE_BUILD', memo.build_type_cd::TEXT, memo.lang_cd
            ),
            'complexionTypeCdValue', arrest.lookup_value(
                'PHY_FEAT_PCODE_COMPL', memo.complexion_type_cd::TEXT, memo.lang_cd
            ),
            'arrestFromState', (
                SELECT state.state
                FROM mdm.m_state state
                WHERE state.state_id = memo.arr_from_state_id
                  AND state.lang_cd = memo.lang_cd
                LIMIT 1
            ),
            'arrestFromDistrict', (
                SELECT district.district
                FROM mdm.m_district district
                WHERE district.district_id = memo.arr_from_district_id
                  AND district.lang_cd = memo.lang_cd
                LIMIT 1
            ),
            'arrestFromPoliceStation', (
                SELECT station.ps
                FROM mdm.m_police_station station
                WHERE station.ps_id = memo.arr_from_ps_id
                  AND station.lang_cd = memo.lang_cd
                LIMIT 1
            )
        )

        -- Audit users are scalar fields, matching the NCR/Complaint output style.
        || jsonb_build_object(
            'recordCreatedByFullName', NULLIF(TRIM(CONCAT_WS(
                ' ', rcb_memo.first_name, rcb_memo.middle_name, rcb_memo.last_name
            )), ''),
            'recordCreatedByRankDesc', rcb_memo.rank_desc,
            'recordCreatedByLoginId', rcb_memo.login_id,
            'recordUpdatedByFullName', NULLIF(TRIM(CONCAT_WS(
                ' ', rub_memo.first_name, rub_memo.middle_name, rub_memo.last_name
            )), ''),
            'recordUpdatedByRankDesc', rub_memo.rank_desc,
            'recordUpdatedByLoginId', rub_memo.login_id
        )

        -- All one-to-many branches. Empty branches are always [] rather than NULL.
        || jsonb_build_object(
            -- FIR accused profile for this arrest, including all accused child tables.
            -- accused_vid identifies the person; fir_reg_num verifies the FIR context.
            'accusedInfos', (
                SELECT COALESCE(jsonb_agg(
                    arrest.jsonb_camel_keys(to_jsonb(ai.*))
                    || jsonb_build_object(

                        -- mdm.m_lookup_masters (lang_cd → OFFCL_LANG)
                        'langCdValue', ml_lang_ai.look_up_value,

                        -- mdm.m_lookup_masters (relation_type_cd → RELATION_TYP)
                        'relationTypeCdValue', ml_rel_ai.look_up_value,

                        -- mdm.m_lookup_masters (othr_rel_type_cd → RELATION_TYP)
                        'othrRelTypeCdValue', ml_orel_ai.look_up_value,

                        -- mdm.m_lookup_masters (nationality_cd → NATIONALITY)
                        'nationalityCdValue', ml_nat_ai.look_up_value,

                        -- mdm.m_lookup_masters (category_cd → CATEGORY)
                        'categoryCdValue', ml_cat_ai.look_up_value,

                        -- mdm.m_lookup_masters (occupation_cd → OCCUPATION)
                        'occupationCdValue', ml_occ_ai.look_up_value,

                        -- mdm.m_lookup_masters (age_proof_type_cd → AGE_DETERM)
                        'ageProofTypeCdValue', ml_agedet_ai.look_up_value,

                        -- mdm.m_lookup_masters (income_group_cd → INCOME_GROUP)
                        'incomeGroupCdValue', ml_incgrp_ai.look_up_value,

                        -- mdm.m_lookup_masters (gender_cd → GENDER)
                        'genderCdValue', ml_gen_ai.look_up_value,

                        -- mdm.m_lookup_masters (religion_cd → RELIGION)
                        'religionCdValue', ml_relg_ai.look_up_value,

                        -- mdm.m_lookup_masters (marital_status_cd → MARTL_STATUS)
                        'maritalStatusCdValue', ml_mar_ai.look_up_value,

                        -- mdm.m_lookup_masters (edu_qual_cd → EDU_QUAL)  [array column]
                        'eduQualCdValue', (
                            SELECT COALESCE(jsonb_agg(ml_edu.look_up_value), '[]'::jsonb)
                            FROM unnest(ai.edu_qual_cd) AS eq(code)
                            LEFT JOIN mdm.m_lookup_masters ml_edu
                                ON ml_edu.api_master_code = 'EDU_QUAL'
                                AND ml_edu.look_up_code::TEXT = eq.code::TEXT
                                AND ml_edu.lang_cd = ai.lang_cd
                        ),

                        -- acc_police_cd        -- TODO: lookup master unconfirmed ("??" in source sheet)
                        -- dysp_login_id         -- TODO: lookup master unconfirmed ("??" in source sheet)
                        -- physical_cond_cd      -- TODO: lookup master unconfirmed ("??" in source sheet)

                        -- mdm.m_lookup_masters (arrest_type_cd → ARR_SURR_TYPE)
                        'arrestTypeCdValue', ml_arrtyp_ai.look_up_value,

                        -- surrenderd_estbl_cd   -- TODO: lookup master unconfirmed ("??" in source sheet)

                        -- mdm.m_state (arrest_surr_stat_id)
                        'arrestSurrStateCd', st_arrsurr_ai.state_cd,
                        'arrestSurrState', st_arrsurr_ai.state,

                        -- mdm.m_district (arrest_surr_dist_id)
                        'arrestSurrDistrictCd', di_arrsurr_ai.district_cd,
                        'arrestSurrDistrict', di_arrsurr_ai.district,

                        -- mdm.m_lookup_masters (arrest_action_taken_cd → ARRST_ACTN)
                        'arrestActionTakenCdValue', ml_arract_ai.look_up_value,

                        -- arr_intimate_rel_cd   -- TODO: lookup master unconfirmed ("??" in source sheet)
                        -- arr_intimate_mode_cd  -- TODO: lookup master unconfirmed ("??" in source sheet)

                        -- mdm.m_lookup_masters (accused_status_cd → ACC_STATUS)
                        'accusedStatusCdValue', ml_accstat_ai.look_up_value,

                        -- mdm.m_lookup_masters (blood_group_cd → BLOOD_GROUP)
                        'bloodGroupCdValue', ml_blood_ai.look_up_value,

                        -- users.t_police_staff_info (record_created_by)
                        'recordCreatedByFullName', TRIM(CONCAT_WS(' ', rcb_ai.first_name, rcb_ai.middle_name, rcb_ai.last_name)),
                        'recordCreatedByLoginId', rcb_ai.login_id,
                        'recordCreatedByRankDesc', rcb_ai.rank_desc,

                        -- users.t_police_staff_info (record_updated_by)
                        'recordUpdatedByFullName', TRIM(CONCAT_WS(' ', rub_ai.first_name, rub_ai.middle_name, rub_ai.last_name)),
                        'recordUpdatedByLoginId', rub_ai.login_id,
                        'recordUpdatedByRankDesc', rub_ai.rank_desc,

                        -- mdm.m_lookup_masters (other_reg_type_cd → LINK_REG_TYPE)
                        'otherRegTypeCdValue', ml_oreg_ai.look_up_value,

                        -- mdm.m_police_station (arrest_surr_ps_id)
                        'arrestSurrPsCd', ps_arrsurr_ai.ps_cd,
                        'arrestSurrPs', ps_arrsurr_ai.ps,

                        -- mdm.m_lookup_masters (build_type_cd → PHY_FEAT_PCODE_BUILD)
                        'buildTypeCdValue', ml_build_ai.look_up_value,

                        -- mdm.m_lookup_masters (complexion_type_cd → PHY_FEAT_PCODE_COMPL)
                        'complexionTypeCdValue', ml_compl_ai.look_up_value,

                        -- mdm.m_lookup_masters (living_status_cd → LIVING_STATUS)
                        'livingStatusCdValue', ml_living_ai.look_up_value,

                        -- ── NESTED: addressGrid (fir.t_fir_accused_address) ─────
                        'addressGrid', (
                            SELECT COALESCE(jsonb_agg(
                                arrest.jsonb_camel_keys(to_jsonb(addr.*))
                                || jsonb_build_object(
                                    -- mdm.m_lookup_masters (lang_cd → OFFCL_LANG)
                                    'langCdValue', ml_lang_addr.look_up_value,
                                    -- mdm.m_lookup_masters (address_type_cd → ADD_TYP)
                                    'addressTypeCdValue', ml_addtyp_addr.look_up_value,
                                    -- mdm.m_sub_district (sub_district_cd)
                                    'subDistrictCdValue', msd_addr.sub_district,
                                    -- mdm.m_subdist_villages (village_cd)
                                    'villageCdValue', mv_addr.village_name,
                                    -- mdm.m_lookup_masters (country_cd → NATIONALITY)
                                    'countryCdValue', ml_country_addr.look_up_value,
                                    -- mdm.m_lgd_district (lg_district_cd)
                                    'lgDistrictCdValue', dist_addr.lg_district_name,
                                    -- mdm.m_police_station (ps_id)
                                    'psCd', ps_addr.ps_cd,
                                    'ps', ps_addr.ps,
                                    -- mdm.m_state (state_id)
                                    'stateCd', st_addr.state_cd,
                                    'state', st_addr.state,
                                    -- users.t_police_staff_info (record_created_by)
                                    'recordCreatedByFullName', TRIM(CONCAT_WS(' ', rcb_addr.first_name, rcb_addr.middle_name, rcb_addr.last_name)),
                                    'recordCreatedByLoginId', rcb_addr.login_id,
                                    'recordCreatedByRankDesc', rcb_addr.rank_desc,
                                    -- users.t_police_staff_info (record_updated_by)
                                    'recordUpdatedByFullName', TRIM(CONCAT_WS(' ', rub_addr.first_name, rub_addr.middle_name, rub_addr.last_name)),
                                    'recordUpdatedByLoginId', rub_addr.login_id,
                                    'recordUpdatedByRankDesc', rub_addr.rank_desc
                                )
                            ORDER BY addr.fir_acc_addr_srno), '[]'::jsonb)
                            FROM fir.t_fir_accused_address addr
                            LEFT JOIN mdm.m_lookup_masters ml_lang_addr
                                ON ml_lang_addr.api_master_code = 'OFFCL_LANG'
                                AND ml_lang_addr.look_up_code::TEXT = addr.lang_cd::TEXT
                                AND ml_lang_addr.lang_cd = addr.lang_cd
                            LEFT JOIN mdm.m_lookup_masters ml_addtyp_addr
                                ON ml_addtyp_addr.api_master_code = 'ADD_TYP'
                                AND ml_addtyp_addr.look_up_code::TEXT = addr.address_type_cd::TEXT
                                AND ml_addtyp_addr.lang_cd = addr.lang_cd
                            LEFT JOIN mdm.m_sub_district msd_addr
                                ON msd_addr.sub_district_cd::TEXT = addr.sub_district_cd::TEXT
                                AND msd_addr.lang_cd = addr.lang_cd
                            LEFT JOIN mdm.m_subdist_villages mv_addr
                                ON mv_addr.village_cd::TEXT = addr.village_cd::TEXT
                                AND mv_addr.lang_cd = addr.lang_cd
                            LEFT JOIN mdm.m_lookup_masters ml_country_addr
                                ON ml_country_addr.api_master_code = 'NATIONALITY'
                                AND ml_country_addr.look_up_code::TEXT = addr.country_cd::TEXT
                                AND ml_country_addr.lang_cd = addr.lang_cd
                            LEFT JOIN mdm.m_lgd_district dist_addr
                                ON dist_addr.lg_act_dist_cd::TEXT = addr.lg_district_cd::TEXT
                                AND dist_addr.lang_cd = addr.lang_cd
                            LEFT JOIN mdm.m_police_station ps_addr
                                ON ps_addr.ps_id = addr.ps_id
                                AND ps_addr.lang_cd = addr.lang_cd
                            LEFT JOIN mdm.m_state st_addr
                                ON st_addr.state_id = addr.state_id
                                AND st_addr.lang_cd = addr.lang_cd
                            LEFT JOIN users.t_police_staff_info rcb_addr
                                ON rcb_addr.staff_id = addr.record_created_by
                            LEFT JOIN users.t_police_staff_info rub_addr
                                ON rub_addr.staff_id = addr.record_updated_by
                            WHERE addr.accused_vid = ai.accused_vid
                        ),

                        -- ── NESTED: bankDetailsList (fir.t_fir_accused_bank_dtls) ──
                        'bankDetailsList', (
                            SELECT COALESCE(jsonb_agg(
                                arrest.jsonb_camel_keys(to_jsonb(bk.*))
                                || jsonb_build_object(
                                    -- mdm.m_lookup_masters (lang_cd → OFFCL_LANG)
                                    'langCdValue', ml_lang_bk.look_up_value,
                                    -- mdm.m_lookup_masters (account_type_cd → ACCNT_TYP)
                                    'accountTypeCdValue', ml_accttyp_bk.look_up_value,
                                    -- users.t_police_staff_info (record_created_by)
                                    'recordCreatedByFullName', TRIM(CONCAT_WS(' ', rcb_bk.first_name, rcb_bk.middle_name, rcb_bk.last_name)),
                                    'recordCreatedByLoginId', rcb_bk.login_id,
                                    'recordCreatedByRankDesc', rcb_bk.rank_desc,
                                    -- users.t_police_staff_info (record_updated_by)
                                    'recordUpdatedByFullName', TRIM(CONCAT_WS(' ', rub_bk.first_name, rub_bk.middle_name, rub_bk.last_name)),
                                    'recordUpdatedByLoginId', rub_bk.login_id,
                                    'recordUpdatedByRankDesc', rub_bk.rank_desc
                                )
                            ORDER BY bk.bankcard_id_srno), '[]'::jsonb)
                            FROM fir.t_fir_accused_bank_dtls bk
                            LEFT JOIN mdm.m_lookup_masters ml_lang_bk
                                ON ml_lang_bk.api_master_code = 'OFFCL_LANG'
                                AND ml_lang_bk.look_up_code::TEXT = bk.lang_cd::TEXT
                                AND ml_lang_bk.lang_cd = bk.lang_cd
                            LEFT JOIN mdm.m_lookup_masters ml_accttyp_bk
                                ON ml_accttyp_bk.api_master_code = 'ACCNT_TYP'
                                AND ml_accttyp_bk.look_up_code::TEXT = bk.account_type_cd::TEXT
                                AND ml_accttyp_bk.lang_cd = bk.lang_cd
                            LEFT JOIN users.t_police_staff_info rcb_bk
                                ON rcb_bk.staff_id = bk.record_created_by
                            LEFT JOIN users.t_police_staff_info rub_bk
                                ON rub_bk.staff_id = bk.record_updated_by
                            WHERE bk.accused_vid = ai.accused_vid
                        ),

                        -- ── NESTED: physicalDescription (fir.t_fir_accused_phy_feature) ──
                        -- phy_feat_category / phy_feature_major / phy_feature_minor are
                        -- denormalized text columns already stored on the row and are
                        -- included automatically by to_jsonb(pf.*); no lookup join
                        -- needed for phy_feat_category_cd / _maj_cd / _min_cd.
                        'physicalDescription', (
                            SELECT COALESCE(jsonb_agg(
                                arrest.jsonb_camel_keys(to_jsonb(pf.*))
                                || jsonb_build_object(
                                    'langCdValue', ml_lang_pf.look_up_value,
                                    'recordCreatedByFullName', TRIM(CONCAT_WS(' ', rcb_pf.first_name, rcb_pf.middle_name, rcb_pf.last_name)),
                                    'recordCreatedByLoginId', rcb_pf.login_id,
                                    'recordCreatedByRankDesc', rcb_pf.rank_desc,
                                    'recordUpdatedByFullName', TRIM(CONCAT_WS(' ', rub_pf.first_name, rub_pf.middle_name, rub_pf.last_name)),
                                    'recordUpdatedByLoginId', rub_pf.login_id,
                                    'recordUpdatedByRankDesc', rub_pf.rank_desc
                                )
                            ORDER BY pf.acc_phy_feat_srno), '[]'::jsonb)
                            FROM fir.t_fir_accused_phy_feature pf
                            LEFT JOIN mdm.m_lookup_masters ml_lang_pf
                                ON ml_lang_pf.api_master_code = 'OFFCL_LANG'
                                AND ml_lang_pf.look_up_code::TEXT = pf.lang_cd::TEXT
                                AND ml_lang_pf.lang_cd = pf.lang_cd
                            LEFT JOIN users.t_police_staff_info rcb_pf
                                ON rcb_pf.staff_id = pf.record_created_by
                            LEFT JOIN users.t_police_staff_info rub_pf
                                ON rub_pf.staff_id = pf.record_updated_by
                            WHERE pf.accused_vid = ai.accused_vid
                        ),

                        -- ── NESTED: nationalidList (fir.t_fir_acc_national_id) ──
                        'nationalidList', (
                            SELECT COALESCE(jsonb_agg(
                                arrest.jsonb_camel_keys(to_jsonb(nid.*))
                                || jsonb_build_object(
                                    -- mdm.m_lookup_masters (lang_cd → OFFCL_LANG)
                                    'langCdValue', ml_lang_nid.look_up_value,
                                    -- mdm.m_lookup_masters (national_id_type_cd → NTNL_ID_DOC_TYP)
                                    'nationalIdTypeCdValue', ml_idtyp_nid.look_up_value,
                                    -- users.t_police_staff_info (record_created_by)
                                    'recordCreatedByFullName', TRIM(CONCAT_WS(' ', rcb_nid.first_name, rcb_nid.middle_name, rcb_nid.last_name)),
                                    'recordCreatedByLoginId', rcb_nid.login_id,
                                    'recordCreatedByRankDesc', rcb_nid.rank_desc,
                                    -- users.t_police_staff_info (record_updated_by)
                                    'recordUpdatedByFullName', TRIM(CONCAT_WS(' ', rub_nid.first_name, rub_nid.middle_name, rub_nid.last_name)),
                                    'recordUpdatedByLoginId', rub_nid.login_id,
                                    'recordUpdatedByRankDesc', rub_nid.rank_desc
                                )
                            ORDER BY nid.national_id_srno), '[]'::jsonb)
                            FROM fir.t_fir_acc_national_id nid
                            LEFT JOIN mdm.m_lookup_masters ml_lang_nid
                                ON ml_lang_nid.api_master_code = 'OFFCL_LANG'
                                AND ml_lang_nid.look_up_code::TEXT = nid.lang_cd::TEXT
                                AND ml_lang_nid.lang_cd = nid.lang_cd
                            LEFT JOIN mdm.m_lookup_masters ml_idtyp_nid
                                ON ml_idtyp_nid.api_master_code = 'NTNL_ID_DOC_TYP'
                                AND ml_idtyp_nid.look_up_code::TEXT = nid.national_id_type_cd::TEXT
                                AND ml_idtyp_nid.lang_cd = nid.lang_cd
                            LEFT JOIN users.t_police_staff_info rcb_nid
                                ON rcb_nid.staff_id = nid.record_created_by
                            LEFT JOIN users.t_police_staff_info rub_nid
                                ON rub_nid.staff_id = nid.record_updated_by
                            WHERE nid.accused_vid = ai.accused_vid
                        ),

                        -- ── NESTED: dress (fir.t_fir_accused_dress) ──
                        -- dress_for / dress_type / dress_subtype are denormalized text
                        -- columns already stored on the row; included automatically,
                        -- no lookup join needed for dress_for_cd / _type_cd / _subtype_cd.
                        'dress', (
                            SELECT COALESCE(jsonb_agg(
                                arrest.jsonb_camel_keys(to_jsonb(dr.*))
                                || jsonb_build_object(
                                    'langCdValue', ml_lang_dr.look_up_value,
                                    'recordCreatedByFullName', TRIM(CONCAT_WS(' ', rcb_dr.first_name, rcb_dr.middle_name, rcb_dr.last_name)),
                                    'recordCreatedByLoginId', rcb_dr.login_id,
                                    'recordCreatedByRankDesc', rcb_dr.rank_desc,
                                    'recordUpdatedByFullName', TRIM(CONCAT_WS(' ', rub_dr.first_name, rub_dr.middle_name, rub_dr.last_name)),
                                    'recordUpdatedByLoginId', rub_dr.login_id,
                                    'recordUpdatedByRankDesc', rub_dr.rank_desc
                                )
                            ORDER BY dr.acc_dress_srno), '[]'::jsonb)
                            FROM fir.t_fir_accused_dress dr
                            LEFT JOIN mdm.m_lookup_masters ml_lang_dr
                                ON ml_lang_dr.api_master_code = 'OFFCL_LANG'
                                AND ml_lang_dr.look_up_code::TEXT = dr.lang_cd::TEXT
                                AND ml_lang_dr.lang_cd = dr.lang_cd
                            LEFT JOIN users.t_police_staff_info rcb_dr
                                ON rcb_dr.staff_id = dr.record_created_by
                            LEFT JOIN users.t_police_staff_info rub_dr
                                ON rub_dr.staff_id = dr.record_updated_by
                            WHERE dr.accused_vid = ai.accused_vid
                        ),

                        -- ── NESTED: idMarks (fir.t_fir_accused_id_marks) ──
                        'idMarks', (
                            SELECT COALESCE(jsonb_agg(
                                arrest.jsonb_camel_keys(to_jsonb(idm.*))
                                || jsonb_build_object(
                                    -- mdm.m_lookup_masters (lang_cd → OFFCL_LANG)
                                    'langCdValue', ml_lang_idm.look_up_value,
                                    -- mdm.m_lookup_masters (id_marks_type_cd → IDENTITY_MARKS)
                                    'idMarksTypeCdValue', ml_idmtyp_idm.look_up_value,
                                    -- users.t_police_staff_info (record_created_by)
                                    'recordCreatedByFullName', TRIM(CONCAT_WS(' ', rcb_idm.first_name, rcb_idm.middle_name, rcb_idm.last_name)),
                                    'recordCreatedByLoginId', rcb_idm.login_id,
                                    'recordCreatedByRankDesc', rcb_idm.rank_desc,
                                    -- users.t_police_staff_info (record_updated_by)
                                    'recordUpdatedByFullName', TRIM(CONCAT_WS(' ', rub_idm.first_name, rub_idm.middle_name, rub_idm.last_name)),
                                    'recordUpdatedByLoginId', rub_idm.login_id,
                                    'recordUpdatedByRankDesc', rub_idm.rank_desc
                                )
                            ORDER BY idm.fir_acc_id_marks_srno), '[]'::jsonb)
                            FROM fir.t_fir_accused_id_marks idm
                            LEFT JOIN mdm.m_lookup_masters ml_lang_idm
                                ON ml_lang_idm.api_master_code = 'OFFCL_LANG'
                                AND ml_lang_idm.look_up_code::TEXT = idm.lang_cd::TEXT
                                AND ml_lang_idm.lang_cd = idm.lang_cd
                            LEFT JOIN mdm.m_lookup_masters ml_idmtyp_idm
                                ON ml_idmtyp_idm.api_master_code = 'IDENTITY_MARKS'
                                AND ml_idmtyp_idm.look_up_code::TEXT = idm.id_marks_type_cd::TEXT
                                AND ml_idmtyp_idm.lang_cd = idm.lang_cd
                            LEFT JOIN users.t_police_staff_info rcb_idm
                                ON rcb_idm.staff_id = idm.record_created_by
                            LEFT JOIN users.t_police_staff_info rub_idm
                                ON rub_idm.staff_id = idm.record_updated_by
                            WHERE idm.accused_vid = ai.accused_vid
                        ),

                        -- ── NESTED: files (fir.t_fir_accused_files) ──
                        'files', (
                            SELECT COALESCE(jsonb_agg(
                                arrest.jsonb_camel_keys(to_jsonb(fl.*))
                                || jsonb_build_object(
                                    -- mdm.m_lookup_masters (lang_cd → OFFCL_LANG)
                                    'langCdValue', ml_lang_fl.look_up_value,
                                    -- mdm.m_lookup_masters (file_type_cd → UPLOAD_FILE_TYP)
                                    'fileTypeCdValue', ml_ftyp_fl.look_up_value,
                                    -- mdm.m_lookup_masters (file_subtype_cd → UPLOAD_FILE_SUB_TYP)
                                    'fileSubtypeCdValue', ml_fsubtyp_fl.look_up_value,
                                    -- users.t_police_staff_info (record_created_by)
                                    'recordCreatedByFullName', TRIM(CONCAT_WS(' ', rcb_fl.first_name, rcb_fl.middle_name, rcb_fl.last_name)),
                                    'recordCreatedByLoginId', rcb_fl.login_id,
                                    'recordCreatedByRankDesc', rcb_fl.rank_desc,
                                    -- users.t_police_staff_info (record_updated_by)
                                    'recordUpdatedByFullName', TRIM(CONCAT_WS(' ', rub_fl.first_name, rub_fl.middle_name, rub_fl.last_name)),
                                    'recordUpdatedByLoginId', rub_fl.login_id,
                                    'recordUpdatedByRankDesc', rub_fl.rank_desc
                                )
                            ORDER BY fl.accused_file_srno), '[]'::jsonb)
                            FROM fir.t_fir_accused_files fl
                            LEFT JOIN mdm.m_lookup_masters ml_lang_fl
                                ON ml_lang_fl.api_master_code = 'OFFCL_LANG'
                                AND ml_lang_fl.look_up_code::TEXT = fl.lang_cd::TEXT
                                AND ml_lang_fl.lang_cd = fl.lang_cd
                            LEFT JOIN mdm.m_lookup_masters ml_ftyp_fl
                                ON ml_ftyp_fl.api_master_code = 'UPLOAD_FILE_TYP'
                                AND ml_ftyp_fl.look_up_code::TEXT = fl.file_type_cd::TEXT
                                AND ml_ftyp_fl.lang_cd = fl.lang_cd
                            LEFT JOIN mdm.m_lookup_masters ml_fsubtyp_fl
                                ON ml_fsubtyp_fl.api_master_code = 'UPLOAD_FILE_SUB_TYP'
                                AND ml_fsubtyp_fl.look_up_code::TEXT = fl.file_subtype_cd::TEXT
                                AND ml_fsubtyp_fl.lang_cd = fl.lang_cd
                            LEFT JOIN users.t_police_staff_info rcb_fl
                                ON rcb_fl.staff_id = fl.record_created_by
                            LEFT JOIN users.t_police_staff_info rub_fl
                                ON rub_fl.staff_id = fl.record_updated_by
                            WHERE fl.accused_vid = ai.accused_vid
                        )
                    )
                ORDER BY ai.accused_vid), '[]'::jsonb)

                -- ── ROOT TABLE (t_fir_accused_info) & ITS LOOKUP JOINS ──────
                FROM fir.t_fir_accused_info ai
                LEFT JOIN mdm.m_lookup_masters ml_lang_ai
                    ON ml_lang_ai.api_master_code = 'OFFCL_LANG'
                    AND ml_lang_ai.look_up_code::TEXT = ai.lang_cd::TEXT
                    AND ml_lang_ai.lang_cd = ai.lang_cd
                LEFT JOIN mdm.m_lookup_masters ml_rel_ai
                    ON ml_rel_ai.api_master_code = 'RELATION_TYP'
                    AND ml_rel_ai.look_up_code::TEXT = ai.relation_type_cd::TEXT
                    AND ml_rel_ai.lang_cd = ai.lang_cd
                LEFT JOIN mdm.m_lookup_masters ml_orel_ai
                    ON ml_orel_ai.api_master_code = 'RELATION_TYP'
                    AND ml_orel_ai.look_up_code::TEXT = ai.othr_rel_type_cd::TEXT
                    AND ml_orel_ai.lang_cd = ai.lang_cd
                LEFT JOIN mdm.m_lookup_masters ml_nat_ai
                    ON ml_nat_ai.api_master_code = 'NATIONALITY'
                    AND ml_nat_ai.look_up_code::TEXT = ai.nationality_cd::TEXT
                    AND ml_nat_ai.lang_cd = ai.lang_cd
                LEFT JOIN mdm.m_lookup_masters ml_cat_ai
                    ON ml_cat_ai.api_master_code = 'CATEGORY'
                    AND ml_cat_ai.look_up_code::TEXT = ai.category_cd::TEXT
                    AND ml_cat_ai.lang_cd = ai.lang_cd
                LEFT JOIN mdm.m_lookup_masters ml_occ_ai
                    ON ml_occ_ai.api_master_code = 'OCCUPATION'
                    AND ml_occ_ai.look_up_code::TEXT = ai.occupation_cd::TEXT
                    AND ml_occ_ai.lang_cd = ai.lang_cd
                LEFT JOIN mdm.m_lookup_masters ml_agedet_ai
                    ON ml_agedet_ai.api_master_code = 'AGE_DETERM'
                    AND ml_agedet_ai.look_up_code::TEXT = ai.age_proof_type_cd::TEXT
                    AND ml_agedet_ai.lang_cd = ai.lang_cd
                LEFT JOIN mdm.m_lookup_masters ml_incgrp_ai
                    ON ml_incgrp_ai.api_master_code = 'INCOME_GROUP'
                    AND ml_incgrp_ai.look_up_code::TEXT = ai.income_group_cd::TEXT
                    AND ml_incgrp_ai.lang_cd = ai.lang_cd
                LEFT JOIN mdm.m_lookup_masters ml_gen_ai
                    ON ml_gen_ai.api_master_code = 'GENDER'
                    AND ml_gen_ai.look_up_code::TEXT = ai.gender_cd::TEXT
                    AND ml_gen_ai.lang_cd = ai.lang_cd
                LEFT JOIN mdm.m_lookup_masters ml_relg_ai
                    ON ml_relg_ai.api_master_code = 'RELIGION'
                    AND ml_relg_ai.look_up_code::TEXT = ai.religion_cd::TEXT
                    AND ml_relg_ai.lang_cd = ai.lang_cd
                LEFT JOIN mdm.m_lookup_masters ml_mar_ai
                    ON ml_mar_ai.api_master_code = 'MARTL_STATUS'
                    AND ml_mar_ai.look_up_code::TEXT = ai.marital_status_cd::TEXT
                    AND ml_mar_ai.lang_cd = ai.lang_cd
                LEFT JOIN mdm.m_lookup_masters ml_arrtyp_ai
                    ON ml_arrtyp_ai.api_master_code = 'ARR_SURR_TYPE'
                    AND ml_arrtyp_ai.look_up_code::TEXT = ai.arrest_type_cd::TEXT
                    AND ml_arrtyp_ai.lang_cd = ai.lang_cd
                LEFT JOIN mdm.m_state st_arrsurr_ai
                    ON st_arrsurr_ai.state_id = ai.arrest_surr_stat_id
                    AND st_arrsurr_ai.lang_cd = ai.lang_cd
                LEFT JOIN mdm.m_district di_arrsurr_ai
                    ON di_arrsurr_ai.district_id = ai.arrest_surr_dist_id
                    AND di_arrsurr_ai.lang_cd = ai.lang_cd
                LEFT JOIN mdm.m_lookup_masters ml_arract_ai
                    ON ml_arract_ai.api_master_code = 'ARRST_ACTN'
                    AND ml_arract_ai.look_up_code::TEXT = ai.arrest_action_taken_cd::TEXT
                    AND ml_arract_ai.lang_cd = ai.lang_cd
                LEFT JOIN mdm.m_lookup_masters ml_accstat_ai
                    ON ml_accstat_ai.api_master_code = 'ACC_STATUS'
                    AND ml_accstat_ai.look_up_code::TEXT = ai.accused_status_cd::TEXT
                    AND ml_accstat_ai.lang_cd = ai.lang_cd
                LEFT JOIN mdm.m_lookup_masters ml_blood_ai
                    ON ml_blood_ai.api_master_code = 'BLOOD_GROUP'
                    AND ml_blood_ai.look_up_code::TEXT = ai.blood_group_cd::TEXT
                    AND ml_blood_ai.lang_cd = ai.lang_cd
                LEFT JOIN users.t_police_staff_info rcb_ai
                    ON rcb_ai.staff_id = ai.record_created_by
                LEFT JOIN users.t_police_staff_info rub_ai
                    ON rub_ai.staff_id = ai.record_updated_by
                LEFT JOIN mdm.m_lookup_masters ml_oreg_ai
                    ON ml_oreg_ai.api_master_code = 'LINK_REG_TYPE'
                    AND ml_oreg_ai.look_up_code::TEXT = ai.other_reg_type_cd::TEXT
                    AND ml_oreg_ai.lang_cd = ai.lang_cd
                LEFT JOIN mdm.m_police_station ps_arrsurr_ai
                    ON ps_arrsurr_ai.ps_id = ai.arrest_surr_ps_id
                    AND ps_arrsurr_ai.lang_cd = ai.lang_cd
                LEFT JOIN mdm.m_lookup_masters ml_build_ai
                    ON ml_build_ai.api_master_code = 'PHY_FEAT_PCODE_BUILD'
                    AND ml_build_ai.look_up_code::TEXT = ai.build_type_cd::TEXT
                    AND ml_build_ai.lang_cd = ai.lang_cd
                LEFT JOIN mdm.m_lookup_masters ml_compl_ai
                    ON ml_compl_ai.api_master_code = 'PHY_FEAT_PCODE_COMPL'
                    AND ml_compl_ai.look_up_code::TEXT = ai.complexion_type_cd::TEXT
                    AND ml_compl_ai.lang_cd = ai.lang_cd
                LEFT JOIN mdm.m_lookup_masters ml_living_ai
                    ON ml_living_ai.api_master_code = 'LIVING_STATUS'
                    AND ml_living_ai.look_up_code::TEXT = ai.living_status_cd::TEXT
                    AND ml_living_ai.lang_cd = ai.lang_cd

                WHERE ai.accused_vid = memo.accused_vid
                  AND ai.fir_reg_num = memo.fir_reg_num
            ),
            'actSection', (
                SELECT COALESCE(jsonb_agg(
                    arrest.jsonb_camel_keys(to_jsonb(act.*))
                    || jsonb_build_object(
                        'langCdValue', arrest.lookup_value(
                            'OFFCL_LANG', act.lang_cd::TEXT, act.lang_cd
                        ),
                        'actLong', (
                            SELECT master_act.act_long
                            FROM mdm.m_act master_act
                            WHERE master_act.act_cd = act.act_cd
                              AND master_act.lang_cd = act.lang_cd
                            LIMIT 1
                        ),
                        'actShort', (
                            SELECT master_act.act_short
                            FROM mdm.m_act master_act
                            WHERE master_act.act_cd = act.act_cd
                              AND master_act.lang_cd = act.lang_cd
                            LIMIT 1
                        ),
                        'section', (
                            SELECT master_section.section
                            FROM mdm.m_section master_section
                            WHERE master_section.section_code::TEXT = act.section_cd::TEXT
                              AND master_section.lang_cd = act.lang_cd
                            LIMIT 1
                        ),
                        'sectionDesc', (
                            SELECT master_section.section_desc
                            FROM mdm.m_section master_section
                            WHERE master_section.section_code::TEXT = act.section_cd::TEXT
                              AND master_section.lang_cd = act.lang_cd
                            LIMIT 1
                        ),
                        'recordCreatedByFullName', NULLIF(TRIM(CONCAT_WS(
                            ' ', rcb_act.first_name, rcb_act.middle_name, rcb_act.last_name
                        )), ''),
                        'recordCreatedByRankDesc', rcb_act.rank_desc,
                        'recordCreatedByLoginId', rcb_act.login_id,
                        'recordUpdatedByFullName', NULLIF(TRIM(CONCAT_WS(
                            ' ', rub_act.first_name, rub_act.middle_name, rub_act.last_name
                        )), ''),
                        'recordUpdatedByRankDesc', rub_act.rank_desc,
                        'recordUpdatedByLoginId', rub_act.login_id
                    )
                    ORDER BY act.arrest_act_srno
                ), '[]'::JSONB)
                FROM arrest.t_arrest_act_section act
                LEFT JOIN users.t_police_staff_info rcb_act
                    ON rcb_act.staff_id = act.record_created_by
                LEFT JOIN users.t_police_staff_info rub_act
                    ON rub_act.staff_id = act.record_updated_by
                WHERE act.arr_surr_srno = memo.arr_surr_srno
            ),

            'addresses', (
                SELECT COALESCE(jsonb_agg(
                    arrest.jsonb_camel_keys(to_jsonb(address.*))
                    || jsonb_build_object(
                        'addressType', address_master."addressType",
                        'country', address_master.country,
                        'state', address_master.state,
                        'district', address_master.district,
                        'subDistrict', address_master."subDistrict",
                        'village', address_master.village,
                        'policeStation', address_master.ps,
                        'homeAddress', NULLIF(TRIM(CONCAT_WS(
                            ' ', address.address_line_1, address.address_line_2,
                            address.address_line_3
                        )), ''),
                        'communicationAddress', CASE
                            WHEN address.is_comm_addr::TEXT IN (
                                'true', 't', '1', 'Y', 'y'
                            ) THEN COALESCE(
                                NULLIF(TRIM(address.address_eng), ''),
                                NULLIF(TRIM(CONCAT_WS(
                                ', ',
                                NULLIF(TRIM(CONCAT_WS(
                                    ' ', address.address_line_1,
                                    address.address_line_2,
                                    address.address_line_3
                                )), ''),
                                address_master.village,
                                address_master."subDistrict",
                                address_master.country,
                                address.pincode::TEXT
                            )), '')
                            )
                            ELSE NULL 
                        END,
                        'recordCreatedByFullName', NULLIF(TRIM(CONCAT_WS(
                            ' ', rcb_address.first_name, rcb_address.middle_name,
                            rcb_address.last_name
                        )), ''),
                        'recordCreatedByRankDesc', rcb_address.rank_desc,
                        'recordCreatedByLoginId', rcb_address.login_id,
                        'recordUpdatedByFullName', NULLIF(TRIM(CONCAT_WS(
                            ' ', rub_address.first_name, rub_address.middle_name,
                            rub_address.last_name
                        )), ''),
                        'recordUpdatedByRankDesc', rub_address.rank_desc,
                        'recordUpdatedByLoginId', rub_address.login_id
                    )
                    ORDER BY address.arr_addr_srno
                ), '[]'::JSONB)
                FROM arrest.t_arrest_addresses address
                LEFT JOIN LATERAL mdm.common_get_address_master_values(
                    address.lang_cd,
                    address.address_type_cd,
                    address.country_cd,
                    address.state_id,
                    address.lg_district_cd,
                    address.sub_district_cd,
                    address.village_cd,
                    address.ps_id
                ) address_master ON TRUE
                LEFT JOIN users.t_police_staff_info rcb_address
                    ON rcb_address.staff_id = address.record_created_by
                LEFT JOIN users.t_police_staff_info rub_address
                    ON rub_address.staff_id = address.record_updated_by
                WHERE address.arr_surr_srno = memo.arr_surr_srno
            ),

            'bankDetails', (
                SELECT COALESCE(jsonb_agg(
                    arrest.jsonb_camel_keys(to_jsonb(bank.*))
                    || jsonb_build_object(
                        'langCdValue', arrest.lookup_value(
                            'OFFCL_LANG', bank.lang_cd::TEXT, bank.lang_cd
                        ),
                        'bankCdValue', arrest.lookup_value(
                            'BANKS', bank.bank_cd::TEXT, bank.lang_cd
                        ),
                        'accountTypeCdValue', arrest.lookup_value(
                            'ACCNT_TYP', bank.account_type_cd::TEXT, bank.lang_cd
                        ),
                        'recordCreatedByFullName', NULLIF(TRIM(CONCAT_WS(
                            ' ', rcb_bank.first_name, rcb_bank.middle_name, rcb_bank.last_name
                        )), ''),
                        'recordCreatedByRankDesc', rcb_bank.rank_desc,
                        'recordCreatedByLoginId', rcb_bank.login_id,
                        'recordUpdatedByFullName', NULLIF(TRIM(CONCAT_WS(
                            ' ', rub_bank.first_name, rub_bank.middle_name, rub_bank.last_name
                        )), ''),
                        'recordUpdatedByRankDesc', rub_bank.rank_desc,
                        'recordUpdatedByLoginId', rub_bank.login_id
                    )
                    ORDER BY bank.arr_bank_srno
                ), '[]'::JSONB)
                FROM arrest.t_arrest_bank_dtls bank
                LEFT JOIN users.t_police_staff_info rcb_bank
                    ON rcb_bank.staff_id = bank.record_created_by
                LEFT JOIN users.t_police_staff_info rub_bank
                    ON rub_bank.staff_id = bank.record_updated_by
                WHERE bank.arr_surr_srno = memo.arr_surr_srno
            ),

            'dressDetails', (
                SELECT COALESCE(jsonb_agg(
                    arrest.jsonb_camel_keys(to_jsonb(dress.*))
                    || jsonb_build_object(
                        'langCdValue', arrest.lookup_value(
                            'OFFCL_LANG', dress.lang_cd::TEXT, dress.lang_cd
                        ),
                        'dressForCdValue', arrest.lookup_value(
                            'PHY_DESC_TYP', dress.dress_for_cd::TEXT, dress.lang_cd
                        ),
                        'dressTypeCdValue', arrest.lookup_value(
                            'PHYSCL_FEATURES', dress.dress_type_cd::TEXT, dress.lang_cd
                        ),
                        'dressSubtypeCdValue', arrest.lookup_value(
                            'PHYSCL_FEATURES', dress.dress_subtype_cd::TEXT, dress.lang_cd
                        ),
                        'recordCreatedByFullName', NULLIF(TRIM(CONCAT_WS(
                            ' ', rcb_dress.first_name, rcb_dress.middle_name, rcb_dress.last_name
                        )), ''),
                        'recordCreatedByRankDesc', rcb_dress.rank_desc,
                        'recordCreatedByLoginId', rcb_dress.login_id,
                        'recordUpdatedByFullName', NULLIF(TRIM(CONCAT_WS(
                            ' ', rub_dress.first_name, rub_dress.middle_name, rub_dress.last_name
                        )), ''),
                        'recordUpdatedByRankDesc', rub_dress.rank_desc,
                        'recordUpdatedByLoginId', rub_dress.login_id
                    )
                    ORDER BY dress.arr_dress_srno
                ), '[]'::JSONB)
                FROM arrest.t_arrest_dress dress
                LEFT JOIN users.t_police_staff_info rcb_dress
                    ON rcb_dress.staff_id = dress.record_created_by
                LEFT JOIN users.t_police_staff_info rub_dress
                    ON rub_dress.staff_id = dress.record_updated_by
                WHERE dress.arr_surr_srno = memo.arr_surr_srno
            ),

            'fileUploads', (
                SELECT COALESCE(jsonb_agg(
                    arrest.jsonb_camel_keys(to_jsonb(file_upload.*))
                    || jsonb_build_object(
                        'langCdValue', arrest.lookup_value(
                            'OFFCL_LANG', file_upload.lang_cd::TEXT, file_upload.lang_cd
                        ),
                        'fileTypeCdValue', arrest.lookup_value(
                            'UPLOAD_FILE_TYP', file_upload.file_type_cd::TEXT, file_upload.lang_cd
                        ),
                        'fileSubtypeCdValue', arrest.lookup_value(
                            'UPLOAD_FILE_SUB_TYP', file_upload.file_subtype_cd::TEXT, file_upload.lang_cd
                        ),
                        'recordCreatedByFullName', NULLIF(TRIM(CONCAT_WS(
                            ' ', rcb_file.first_name, rcb_file.middle_name, rcb_file.last_name
                        )), ''),
                        'recordCreatedByRankDesc', rcb_file.rank_desc,
                        'recordCreatedByLoginId', rcb_file.login_id,
                        'recordUpdatedByFullName', NULLIF(TRIM(CONCAT_WS(
                            ' ', rub_file.first_name, rub_file.middle_name, rub_file.last_name
                        )), ''),
                        'recordUpdatedByRankDesc', rub_file.rank_desc,
                        'recordUpdatedByLoginId', rub_file.login_id
                    )
                    ORDER BY file_upload.arr_file_srno
                ), '[]'::JSONB)
                FROM arrest.t_arrest_files file_upload
                LEFT JOIN users.t_police_staff_info rcb_file
                    ON rcb_file.staff_id = file_upload.record_created_by
                LEFT JOIN users.t_police_staff_info rub_file
                    ON rub_file.staff_id = file_upload.record_updated_by
                WHERE file_upload.arr_surr_srno = memo.arr_surr_srno
            ),

            'identityMarks', (
                SELECT COALESCE(jsonb_agg(
                    arrest.jsonb_camel_keys(to_jsonb(identity_mark.*))
                    || jsonb_build_object(
                        'langCdValue', arrest.lookup_value(
                            'OFFCL_LANG', identity_mark.lang_cd::TEXT, identity_mark.lang_cd
                        ),
                        'identityMarkTypeCdValue', arrest.lookup_value(
                            'IDENTITY_MARKS', identity_mark.id_marks_type_cd::TEXT,
                            identity_mark.lang_cd
                        ),
                        'bodyPartLocationCdValue', arrest.lookup_value(
                            'PHYSCL_FEATURES', identity_mark.body_part_loc_cd::TEXT,
                            identity_mark.lang_cd
                        ),
                        'tattooTypeCdValue', arrest.lookup_value(
                            'IDENTITY_MARKS', identity_mark.tattoo_type_cd::TEXT,
                            identity_mark.lang_cd
                        ),
                        'recordCreatedByFullName', NULLIF(TRIM(CONCAT_WS(
                            ' ', rcb_mark.first_name, rcb_mark.middle_name, rcb_mark.last_name
                        )), ''),
                        'recordCreatedByRankDesc', rcb_mark.rank_desc,
                        'recordCreatedByLoginId', rcb_mark.login_id,
                        'recordUpdatedByFullName', NULLIF(TRIM(CONCAT_WS(
                            ' ', rub_mark.first_name, rub_mark.middle_name, rub_mark.last_name
                        )), ''),
                        'recordUpdatedByRankDesc', rub_mark.rank_desc,
                        'recordUpdatedByLoginId', rub_mark.login_id
                    )
                    ORDER BY identity_mark.arr_identity_srno
                ), '[]'::JSONB)
                FROM arrest.t_arrest_identity_marks identity_mark
                LEFT JOIN users.t_police_staff_info rcb_mark
                    ON rcb_mark.staff_id = identity_mark.record_created_by
                LEFT JOIN users.t_police_staff_info rub_mark
                    ON rub_mark.staff_id = identity_mark.record_updated_by
                WHERE identity_mark.arr_surr_srno = memo.arr_surr_srno
            ),

            'intimationAddresses', (
                SELECT COALESCE(jsonb_agg(
                    arrest.jsonb_camel_keys(to_jsonb(address.*))
                    || jsonb_build_object(
                        'addressType', address_master."addressType",
                        'country', address_master.country,
                        'state', address_master.state,
                        'district', address_master.district,
                        'subDistrict', address_master."subDistrict",
                        'village', address_master.village,
                        'policeStation', address_master.ps,
                        'recordCreatedByFullName', NULLIF(TRIM(CONCAT_WS(
                            ' ', rcb_int_address.first_name, rcb_int_address.middle_name,
                            rcb_int_address.last_name
                        )), ''),
                        'recordCreatedByRankDesc', rcb_int_address.rank_desc,
                        'recordCreatedByLoginId', rcb_int_address.login_id,
                        'recordUpdatedByFullName', NULLIF(TRIM(CONCAT_WS(
                            ' ', rub_int_address.first_name, rub_int_address.middle_name,
                            rub_int_address.last_name
                        )), ''),
                        'recordUpdatedByRankDesc', rub_int_address.rank_desc,
                        'recordUpdatedByLoginId', rub_int_address.login_id
                    )
                    ORDER BY address.intmt_addr_srno
                ), '[]'::JSONB)
                FROM arrest.t_arrest_intimate_addr address
                LEFT JOIN LATERAL mdm.common_get_address_master_values(
                    address.lang_cd,
                    address.address_type_cd,
                    address.country_cd,
                    address.state_id,
                    address.lg_district_cd,
                    address.sub_district_cd,
                    address.village_cd,
                    address.ps_id
                ) address_master ON TRUE
                LEFT JOIN users.t_police_staff_info rcb_int_address
                    ON rcb_int_address.staff_id = address.record_created_by
                LEFT JOIN users.t_police_staff_info rub_int_address
                    ON rub_int_address.staff_id = address.record_updated_by
                WHERE address.arr_surr_srno = memo.arr_surr_srno
            ),

            'medicalExams', (
                SELECT COALESCE(jsonb_agg(
                    arrest.jsonb_camel_keys(to_jsonb(medical.*))
                    || jsonb_build_object(
                        'langCdValue', arrest.lookup_value(
                            'OFFCL_LANG', medical.lang_cd::TEXT, medical.lang_cd
                        ),
                        'recordCreatedByFullName', NULLIF(TRIM(CONCAT_WS(
                            ' ', rcb_medical.first_name, rcb_medical.middle_name,
                            rcb_medical.last_name
                        )), ''),
                        'recordCreatedByRankDesc', rcb_medical.rank_desc,
                        'recordCreatedByLoginId', rcb_medical.login_id,
                        'recordUpdatedByFullName', NULLIF(TRIM(CONCAT_WS(
                            ' ', rub_medical.first_name, rub_medical.middle_name,
                            rub_medical.last_name
                        )), ''),
                        'recordUpdatedByRankDesc', rub_medical.rank_desc,
                        'recordUpdatedByLoginId', rub_medical.login_id
                    )
                    ORDER BY medical.arr_med_exam_srno
                ), '[]'::JSONB)
                FROM arrest.t_arrest_med_exam medical
                LEFT JOIN users.t_police_staff_info rcb_medical
                    ON rcb_medical.staff_id = medical.record_created_by
                LEFT JOIN users.t_police_staff_info rub_medical
                    ON rub_medical.staff_id = medical.record_updated_by
                WHERE medical.arr_surr_srno = memo.arr_surr_srno
            ),

            'nationalIds', (
                SELECT COALESCE(jsonb_agg(
                    arrest.jsonb_camel_keys(to_jsonb(national_id.*))
                    || jsonb_build_object(
                        'langCdValue', arrest.lookup_value(
                            'OFFCL_LANG', national_id.lang_cd::TEXT, national_id.lang_cd
                        ),
                        'nationalIdTypeCdValue', arrest.lookup_value(
                            'NTNL_ID_DOC_TYP', national_id.nationality_id_type_cd::TEXT,
                            national_id.lang_cd
                        ),
                        'recordCreatedByFullName', NULLIF(TRIM(CONCAT_WS(
                            ' ', rcb_national.first_name, rcb_national.middle_name,
                            rcb_national.last_name
                        )), ''),
                        'recordCreatedByRankDesc', rcb_national.rank_desc,
                        'recordCreatedByLoginId', rcb_national.login_id,
                        'recordUpdatedByFullName', NULLIF(TRIM(CONCAT_WS(
                            ' ', rub_national.first_name, rub_national.middle_name,
                            rub_national.last_name
                        )), ''),
                        'recordUpdatedByRankDesc', rub_national.rank_desc,
                        'recordUpdatedByLoginId', rub_national.login_id
                    )
                    ORDER BY national_id.national_id_srno
                ), '[]'::JSONB)
                FROM arrest.t_arrest_national_id national_id
                LEFT JOIN users.t_police_staff_info rcb_national
                    ON rcb_national.staff_id = national_id.record_created_by
                LEFT JOIN users.t_police_staff_info rub_national
                    ON rub_national.staff_id = national_id.record_updated_by
                WHERE national_id.arr_surr_srno = memo.arr_surr_srno
            ),

            'physicalFeatures', (
                SELECT COALESCE(jsonb_agg(
                    arrest.jsonb_camel_keys(to_jsonb(feature.*))
                    || jsonb_build_object(
                        'langCdValue', arrest.lookup_value(
                            'OFFCL_LANG', feature.lang_cd::TEXT, feature.lang_cd
                        ),
                        'featureCategoryCdValue', arrest.lookup_value(
                            'PHY_DESC_TYP', feature.phy_feat_category_cd::TEXT,
                            feature.lang_cd
                        ),
                        'featureMajorCdValue', arrest.lookup_parent_value(
                            'PHYSCL_FEATURES', feature.phy_feature_maj_cd::TEXT,
                            feature.lang_cd
                        ),
                        'featureMinorCdValue', arrest.lookup_value(
                            'PHYSCL_FEATURES', feature.phy_feature_min_cd::TEXT,
                            feature.lang_cd
                        ),
                        'recordCreatedByFullName', NULLIF(TRIM(CONCAT_WS(
                            ' ', rcb_feature.first_name, rcb_feature.middle_name,
                            rcb_feature.last_name
                        )), ''),
                        'recordCreatedByRankDesc', rcb_feature.rank_desc,
                        'recordCreatedByLoginId', rcb_feature.login_id,
                        'recordUpdatedByFullName', NULLIF(TRIM(CONCAT_WS(
                            ' ', rub_feature.first_name, rub_feature.middle_name,
                            rub_feature.last_name
                        )), ''),
                        'recordUpdatedByRankDesc', rub_feature.rank_desc,
                        'recordUpdatedByLoginId', rub_feature.login_id
                    )
                    ORDER BY feature.arr_phy_feat_srno
                ), '[]'::JSONB)
                FROM arrest.t_arrest_phy_feature feature
                LEFT JOIN users.t_police_staff_info rcb_feature
                    ON rcb_feature.staff_id = feature.record_created_by
                LEFT JOIN users.t_police_staff_info rub_feature
                    ON rub_feature.staff_id = feature.record_updated_by
                WHERE feature.arr_surr_srno = memo.arr_surr_srno
            ),

            'witnesses', (
                SELECT COALESCE(jsonb_agg(
                    arrest.jsonb_camel_keys(to_jsonb(witness.*))
                    || jsonb_build_object(
                        'langCdValue', arrest.lookup_value(
                            'OFFCL_LANG', witness.lang_cd::TEXT, witness.lang_cd
                        ),
                        'fullName', NULLIF(TRIM(CONCAT_WS(
                            ' ', witness.first_name, witness.middle_name, witness.last_name
                        )), ''),
                        'relationTypeCdValue', arrest.lookup_value(
                            'RELATION_TYP', witness.relation_type_cd::TEXT, witness.lang_cd
                        ),
                        'ageTypeCdValue', arrest.lookup_value(
                            'AGE_PANEL_TYPE', witness.age_type_cd::TEXT, witness.lang_cd
                        ),
                        'nationalityCdValue', arrest.lookup_value(
                            'NATIONALITY', witness.nationality_cd::TEXT, witness.lang_cd
                        ),
                        'occupationCdValue', arrest.lookup_value(
                            'OCCUPATION', witness.occupation_cd::TEXT, witness.lang_cd
                        ),
                        'genderCdValue', arrest.lookup_value(
                            'GENDER', witness.gender_cd::TEXT, witness.lang_cd
                        ),
                        'maritalStatusCdValue', arrest.lookup_value(
                            'MARTL_STATUS', witness.marital_status_cd::TEXT, witness.lang_cd
                        ),
                        'evidenceTenderedCdValue', arrest.lookup_value(
                            'EVIDENCE_TENDERED', witness.witn_evid_tender_cd::TEXT,
                            witness.lang_cd
                        ),
                        'witnessCategoryCdValue', arrest.lookup_value(
                            'CATEGORY', witness.witn_category_cd::TEXT, witness.lang_cd
                        ),
                        'recordCreatedByFullName', NULLIF(TRIM(CONCAT_WS(
                            ' ', rcb_witness.first_name, rcb_witness.middle_name,
                            rcb_witness.last_name
                        )), ''),
                        'recordCreatedByRankDesc', rcb_witness.rank_desc,
                        'recordCreatedByLoginId', rcb_witness.login_id,
                        'recordUpdatedByFullName', NULLIF(TRIM(CONCAT_WS(
                            ' ', rub_witness.first_name, rub_witness.middle_name,
                            rub_witness.last_name
                        )), ''),
                        'recordUpdatedByRankDesc', rub_witness.rank_desc,
                        'recordUpdatedByLoginId', rub_witness.login_id,
                        'addressGrid', (
                            SELECT COALESCE(jsonb_agg(
                                arrest.jsonb_camel_keys(to_jsonb(address.*))
                                || jsonb_build_object(
                                    'addressType', address_master."addressType",
                                    'country', address_master.country,
                                    'state', address_master.state,
                                    'district', address_master.district,
                                    'subDistrict', address_master."subDistrict",
                                    'village', address_master.village,
                                    'policeStation', address_master.ps,
                                    'recordCreatedByFullName', NULLIF(TRIM(CONCAT_WS(
                                        ' ', rcb_witness_address.first_name,
                                        rcb_witness_address.middle_name,
                                        rcb_witness_address.last_name
                                    )), ''),
                                    'recordCreatedByRankDesc', rcb_witness_address.rank_desc,
                                    'recordCreatedByLoginId', rcb_witness_address.login_id,
                                    'recordUpdatedByFullName', NULLIF(TRIM(CONCAT_WS(
                                        ' ', rub_witness_address.first_name,
                                        rub_witness_address.middle_name,
                                        rub_witness_address.last_name
                                    )), ''),
                                    'recordUpdatedByRankDesc', rub_witness_address.rank_desc,
                                    'recordUpdatedByLoginId', rub_witness_address.login_id
                                )
                                ORDER BY address.arr_witn_addr_srno
                            ), '[]'::JSONB)
                            FROM arrest.t_arrest_witness_addr address
                            LEFT JOIN LATERAL mdm.common_get_address_master_values(
                                address.lang_cd,
                                address.address_type_cd,
                                address.country_cd,
                                address.state_id,
                                address.lg_district_cd,
                                address.sub_district_cd,
                                address.village_cd,
                                address.ps_id
                            ) address_master ON TRUE
                            LEFT JOIN users.t_police_staff_info rcb_witness_address
                                ON rcb_witness_address.staff_id = address.record_created_by
                            LEFT JOIN users.t_police_staff_info rub_witness_address
                                ON rub_witness_address.staff_id = address.record_updated_by
                            WHERE address.arr_witns_srno = witness.arr_witns_srno
                        )
                    )
                    ORDER BY witness.arr_witns_srno
                ), '[]'::JSONB)
                FROM arrest.t_arrest_witness witness
                LEFT JOIN users.t_police_staff_info rcb_witness
                    ON rcb_witness.staff_id = witness.record_created_by
                LEFT JOIN users.t_police_staff_info rub_witness
                    ON rub_witness.staff_id = witness.record_updated_by
                WHERE witness.arr_surr_srno = memo.arr_surr_srno
            ),

            'personSearches', (
                SELECT COALESCE(jsonb_agg(
                    arrest.jsonb_camel_keys(to_jsonb(person_search.*))
                    || jsonb_build_object(
                        'langCdValue', arrest.lookup_value(
                            'OFFCL_LANG', person_search.lang_cd::TEXT, person_search.lang_cd
                        ),
                        'recordCreatedByFullName', NULLIF(TRIM(CONCAT_WS(
                            ' ', rcb_search.first_name, rcb_search.middle_name,
                            rcb_search.last_name
                        )), ''),
                        'recordCreatedByRankDesc', rcb_search.rank_desc,
                        'recordCreatedByLoginId', rcb_search.login_id,
                        'recordUpdatedByFullName', NULLIF(TRIM(CONCAT_WS(
                            ' ', rub_search.first_name, rub_search.middle_name,
                            rub_search.last_name
                        )), ''),
                        'recordUpdatedByRankDesc', rub_search.rank_desc,
                        'recordUpdatedByLoginId', rub_search.login_id,
                        'items', (
                            SELECT COALESCE(jsonb_agg(
                                arrest.jsonb_camel_keys(to_jsonb(item.*))
                                || jsonb_build_object(
                                    'langCdValue', arrest.lookup_value(
                                        'OFFCL_LANG', item.lang_cd::TEXT, item.lang_cd
                                    ),
                                    'quantityUnitCdValue', arrest.lookup_value(
                                        'MESURING_UNITS', item.quantity_unit_cd::TEXT,
                                        item.lang_cd
                                    ),
                                    'recordCreatedByFullName', NULLIF(TRIM(CONCAT_WS(
                                        ' ', rcb_item.first_name, rcb_item.middle_name,
                                        rcb_item.last_name
                                    )), ''),
                                    'recordCreatedByRankDesc', rcb_item.rank_desc,
                                    'recordCreatedByLoginId', rcb_item.login_id,
                                    'recordUpdatedByFullName', NULLIF(TRIM(CONCAT_WS(
                                        ' ', rub_item.first_name, rub_item.middle_name,
                                        rub_item.last_name
                                    )), ''),
                                    'recordUpdatedByRankDesc', rub_item.rank_desc,
                                    'recordUpdatedByLoginId', rub_item.login_id
                                )
                                ORDER BY item.prop_item_srno
                            ), '[]'::JSONB)
                            FROM arrest.t_person_search_items item
                            LEFT JOIN users.t_police_staff_info rcb_item
                                ON rcb_item.staff_id = item.record_created_by
                            LEFT JOIN users.t_police_staff_info rub_item
                                ON rub_item.staff_id = item.record_updated_by
                            WHERE item.prop_srno = person_search.prop_srno
                        )
                    )
                    ORDER BY person_search.prop_srno
                ), '[]'::JSONB)
                FROM arrest.t_person_search_property person_search
                LEFT JOIN users.t_police_staff_info rcb_search
                    ON rcb_search.staff_id = person_search.record_created_by
                LEFT JOIN users.t_police_staff_info rub_search
                    ON rub_search.staff_id = person_search.record_updated_by
                WHERE person_search.arr_surr_srno = memo.arr_surr_srno
            )
        )
    INTO v_result
    FROM arrest.t_arrest_memo memo
    LEFT JOIN LATERAL (
        SELECT staff.*
        FROM users.t_police_staff_info staff
        WHERE staff.staff_id::TEXT = memo.dysp_login_id::TEXT
           OR staff.login_id = memo.dysp_login_id::TEXT
        ORDER BY CASE
            WHEN staff.staff_id::TEXT = memo.dysp_login_id::TEXT THEN 0
            ELSE 1
        END
        LIMIT 1
    ) dysp ON TRUE
    LEFT JOIN users.t_police_staff_info io_staff
        ON io_staff.staff_id = memo.io_cd
    LEFT JOIN users.t_police_staff_info rcb_memo
        ON rcb_memo.staff_id = memo.record_created_by
    LEFT JOIN users.t_police_staff_info rub_memo
        ON rub_memo.staff_id = memo.record_updated_by
    WHERE memo.arr_surr_srno = p_arr_surr_srno;

    -- A missing Arrest number returns an empty JSON object, not SQL NULL.
    RETURN COALESCE(v_result, '{}'::JSONB);
END;
$$;


-- select * from arrest.t_arrest_memo
-- select * from arrest.get_arrest_json(38101001180004)
