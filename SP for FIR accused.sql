-- =============================================================================
-- HELPER : fir.jsonb_camel_keys
-- Identical to the helper already created by get_fir_json_5_.sql.
-- Re-declared here (CREATE OR REPLACE, so it's a no-op if it already exists)
-- purely so this script can be deployed standalone. Safe to delete this
-- block if fir.jsonb_camel_keys is already present in your database.
-- =============================================================================

CREATE OR REPLACE FUNCTION fir.jsonb_camel_keys(data JSONB)
RETURNS JSONB
LANGUAGE sql IMMUTABLE STRICT
AS $$
    SELECT jsonb_object_agg(
        (
            SELECT string_agg(
                CASE part_num
                    WHEN 1 THEN part
                    ELSE initcap(part)
                END,
                ''
            )
            FROM (
                SELECT part, row_number() OVER () AS part_num
                FROM unnest(string_to_array(key, '_')) AS part
            ) parts
        ),
        value
    )
    FROM jsonb_each(data);
$$;

-- =============================================================================
-- FUNCTION : fir.get_accused_json
-- PURPOSE  : Returns the accused(s) of a FIR as one nested JSONB document,
--            built exactly the way fir.get_fir_json builds the rest of the
--            FIR: to_jsonb(row) for the raw columns, merged via || with a
--            jsonb_build_object(...) of lookup enrichments, with every child
--            table correlated back to the parent.
--
--            Root table fir.t_fir_accused_info carries the surrogate key
--            accused_vid. EVERY child table below also carries accused_vid,
--            so every nested array is the result of:
--                <child_table>.accused_vid = ai.accused_vid
--            That accused_vid join is what turns one row of t_fir_accused_info
--            plus its seven related tables into a single accusedInfos[]
--            element with its own nested arrays.
--
-- INPUT    : p_fir_reg_num  BIGINT  – fir.t_fir_registration.fir_reg_num
--                            (returns every accused recorded against the FIR)
-- RETURNS  : JSONB  ( NULL if the FIR itself does not exist )
--
--            {
--              "firRegNum": ...,
--              "accusedInfos": [
--                {
--                  ...t_fir_accused_info columns + lookups...,
--                  "addressGrid":         [ ... ],
--                  "bankDetailsList":     [ ... ],
--                  "physicalDescription": [ ... ],
--                  "nationalidList":      [ ... ],
--                  "dress":               [ ... ],
--                  "idMarks":             [ ... ],
--                  "files":               [ ... ]
--                }
--              ]
--            }
--
-- SOURCE TABLES (per fir_accused.ods, root renamed to match live schema):
--   root                    fir.t_fir_accused_info
--   .addressGrid[]          fir.t_fir_accused_address
--   .bankDetailsList[]      fir.t_fir_accused_bank_dtls
--   .physicalDescription[]  fir.t_fir_accused_phy_feature
--   .nationalidList[]       fir.t_fir_acc_national_id
--   .dress[]                fir.t_fir_accused_dress
--   .idMarks[]              fir.t_fir_accused_id_marks
--   .files[]                fir.t_fir_accused_files
--
-- LOOKUP NAMING CONVENTION (same as fir.get_fir_json):
--   mdm.m_lookup_masters (single code)      → {field}CdValue
--   mdm.m_lookup_masters (jsonb array code) → {field}CdValue   (jsonb array
--                                              of look_up_value — see
--                                              edu_qual_cd below)
--   mdm.m_state            (state_id)       → {prefix}StateCd / {prefix}State
--   mdm.m_district          (district_id)   → {prefix}DistrictCd / {prefix}District
--   mdm.m_district  ("lg_district" pattern, joined by district_cd) → {field}CdValue (name only)
--   mdm.m_police_station    (ps_id)         → {prefix}PsCd / {prefix}Ps
--   mdm.m_sub_district      (sub_district_cd) → {field}CdValue (sub_district)
--   mdm.m_subdist_villages  (village_cd)    → {field}CdValue (village_name)
--   users.t_police_staff_info (staff_id)    → {field}FullName / {field}LoginId / {field}RankDesc
--
-- NOTES:
--   * t_fir_accused_info.social_media, .language_used and .alias are stored
--     JSONB columns already keyed in camelCase (same pattern as
--     t_fir_complainant_info.alias / social_media in fir.get_fir_json) —
--     included automatically by to_jsonb(ai.*), no separate sub-query.
--   * t_fir_accused_phy_feature.phy_feat_category / phy_feature_major /
--     phy_feature_minor, and t_fir_accused_dress.dress_for / dress_type /
--     dress_subtype, are denormalized human-readable text columns already
--     stored on those rows (per the sample data) — also included
--     automatically, no lookup join needed for those *_cd columns.
--   * edu_qual_cd is a Postgres array column (e.g. {8,17}) — to_jsonb()
--     renders it as a JSON array, which is why the sample looked like one,
--     but it's resolved here with unnest() + a lateral-style join, not
--     jsonb_array_elements_text() (which only accepts a true jsonb value).
--
-- OPEN ITEMS — the supplied lookup sheet marks these columns "??" (lookup
-- master not confirmed). They are passed through as raw, un-enriched codes
-- below; swap in the real JOIN once the master table is known:
--   acc_police_cd, dysp_login_id, physical_cond_cd, criminal_gang_cd,
--   surrenderd_estbl_cd, arr_intimate_rel_cd, arr_intimate_mode_cd
-- =============================================================================

-- Drop the earlier two-parameter overload (p_fir_reg_num, p_accused_vid).
-- CREATE OR REPLACE does NOT swap out a function whose parameter list
-- changed — it just adds a second overload, which is what made the call
-- ambiguous ("function ... is not unique"). This removes that old version
-- so only the single-parameter signature below exists going forward.


CREATE OR REPLACE FUNCTION fir.get_accused_json(
    p_fir_reg_num BIGINT
)
RETURNS JSONB
LANGUAGE plpgsql
STABLE
SECURITY DEFINER
AS $$
DECLARE
    v_result JSONB;
BEGIN

    SELECT
        jsonb_build_object(
            'firRegNum', r.fir_reg_num,

            -- ── NESTED: accusedInfos[]  (fir.t_fir_accused_info) ────────────────
            'accusedInfos', (
                SELECT COALESCE(jsonb_agg(
                    fir.jsonb_camel_keys(to_jsonb(ai.*))
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
                                AND ml_edu.lang_cd = 99
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
                                fir.jsonb_camel_keys(to_jsonb(addr.*))
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
                                    -- mdm.m_district ("lg_district" pattern, by district_cd)
                                    'lgDistrictCdValue', dist_addr.district,
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
                                AND ml_lang_addr.lang_cd = 99
                            LEFT JOIN mdm.m_lookup_masters ml_addtyp_addr
                                ON ml_addtyp_addr.api_master_code = 'ADD_TYP'
                                AND ml_addtyp_addr.look_up_code::TEXT = addr.address_type_cd::TEXT
                                AND ml_addtyp_addr.lang_cd = 99
                            LEFT JOIN mdm.m_sub_district msd_addr
                                ON msd_addr.sub_district_cd::TEXT = addr.sub_district_cd::TEXT
                            LEFT JOIN mdm.m_subdist_villages mv_addr
                                ON mv_addr.village_cd::TEXT = addr.village_cd::TEXT
                            LEFT JOIN mdm.m_lookup_masters ml_country_addr
                                ON ml_country_addr.api_master_code = 'NATIONALITY'
                                AND ml_country_addr.look_up_code::TEXT = addr.country_cd::TEXT
                                AND ml_country_addr.lang_cd = 99
                            LEFT JOIN mdm.m_district dist_addr
                                ON dist_addr.district_cd::TEXT = addr.lg_district_cd::TEXT
								AND dist_addr.lang_cd = 99
                            LEFT JOIN mdm.m_police_station ps_addr
                                ON ps_addr.ps_id = addr.ps_id
                            LEFT JOIN mdm.m_state st_addr
                                ON st_addr.state_id = addr.state_id
                            LEFT JOIN users.t_police_staff_info rcb_addr
                                ON rcb_addr.staff_id = addr.record_created_by
                            LEFT JOIN users.t_police_staff_info rub_addr
                                ON rub_addr.staff_id = addr.record_updated_by
                            WHERE addr.accused_vid = ai.accused_vid
                        ),

                        -- ── NESTED: bankDetailsList (fir.t_fir_accused_bank_dtls) ──
                        'bankDetailsList', (
                            SELECT COALESCE(jsonb_agg(
                                fir.jsonb_camel_keys(to_jsonb(bk.*))
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
                                AND ml_lang_bk.lang_cd = 99
                            LEFT JOIN mdm.m_lookup_masters ml_accttyp_bk
                                ON ml_accttyp_bk.api_master_code = 'ACCNT_TYP'
                                AND ml_accttyp_bk.look_up_code::TEXT = bk.account_type_cd::TEXT
                                AND ml_accttyp_bk.lang_cd = 99
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
                                fir.jsonb_camel_keys(to_jsonb(pf.*))
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
                                AND ml_lang_pf.lang_cd = 99
                            LEFT JOIN users.t_police_staff_info rcb_pf
                                ON rcb_pf.staff_id = pf.record_created_by
                            LEFT JOIN users.t_police_staff_info rub_pf
                                ON rub_pf.staff_id = pf.record_updated_by
                            WHERE pf.accused_vid = ai.accused_vid
                        ),

                        -- ── NESTED: nationalidList (fir.t_fir_acc_national_id) ──
                        'nationalidList', (
                            SELECT COALESCE(jsonb_agg(
                                fir.jsonb_camel_keys(to_jsonb(nid.*))
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
                                AND ml_lang_nid.lang_cd = 99
                            LEFT JOIN mdm.m_lookup_masters ml_idtyp_nid
                                ON ml_idtyp_nid.api_master_code = 'NTNL_ID_DOC_TYP'
                                AND ml_idtyp_nid.look_up_code::TEXT = nid.national_id_type_cd::TEXT
                                AND ml_idtyp_nid.lang_cd = 99
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
                                fir.jsonb_camel_keys(to_jsonb(dr.*))
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
                                AND ml_lang_dr.lang_cd = 99
                            LEFT JOIN users.t_police_staff_info rcb_dr
                                ON rcb_dr.staff_id = dr.record_created_by
                            LEFT JOIN users.t_police_staff_info rub_dr
                                ON rub_dr.staff_id = dr.record_updated_by
                            WHERE dr.accused_vid = ai.accused_vid
                        ),

                        -- ── NESTED: idMarks (fir.t_fir_accused_id_marks) ──
                        'idMarks', (
                            SELECT COALESCE(jsonb_agg(
                                fir.jsonb_camel_keys(to_jsonb(idm.*))
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
                                AND ml_lang_idm.lang_cd = 99
                            LEFT JOIN mdm.m_lookup_masters ml_idmtyp_idm
                                ON ml_idmtyp_idm.api_master_code = 'IDENTITY_MARKS'
                                AND ml_idmtyp_idm.look_up_code::TEXT = idm.id_marks_type_cd::TEXT
                                AND ml_idmtyp_idm.lang_cd = 99
                            LEFT JOIN users.t_police_staff_info rcb_idm
                                ON rcb_idm.staff_id = idm.record_created_by
                            LEFT JOIN users.t_police_staff_info rub_idm
                                ON rub_idm.staff_id = idm.record_updated_by
                            WHERE idm.accused_vid = ai.accused_vid
                        ),

                        -- ── NESTED: files (fir.t_fir_accused_files) ──
                        'files', (
                            SELECT COALESCE(jsonb_agg(
                                fir.jsonb_camel_keys(to_jsonb(fl.*))
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
                                AND ml_lang_fl.lang_cd = 99
                            LEFT JOIN mdm.m_lookup_masters ml_ftyp_fl
                                ON ml_ftyp_fl.api_master_code = 'UPLOAD_FILE_TYP'
                                AND ml_ftyp_fl.look_up_code::TEXT = fl.file_type_cd::TEXT
                                AND ml_ftyp_fl.lang_cd = 99
                            LEFT JOIN mdm.m_lookup_masters ml_fsubtyp_fl
                                ON ml_fsubtyp_fl.api_master_code = 'UPLOAD_FILE_SUB_TYP'
                                AND ml_fsubtyp_fl.look_up_code::TEXT = fl.file_subtype_cd::TEXT
                                AND ml_fsubtyp_fl.lang_cd = 99
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
                    AND ml_lang_ai.lang_cd = 99
                LEFT JOIN mdm.m_lookup_masters ml_rel_ai
                    ON ml_rel_ai.api_master_code = 'RELATION_TYP'
                    AND ml_rel_ai.look_up_code::TEXT = ai.relation_type_cd::TEXT
                    AND ml_rel_ai.lang_cd = 99
                LEFT JOIN mdm.m_lookup_masters ml_orel_ai
                    ON ml_orel_ai.api_master_code = 'RELATION_TYP'
                    AND ml_orel_ai.look_up_code::TEXT = ai.othr_rel_type_cd::TEXT
                    AND ml_orel_ai.lang_cd = 99
                LEFT JOIN mdm.m_lookup_masters ml_nat_ai
                    ON ml_nat_ai.api_master_code = 'NATIONALITY'
                    AND ml_nat_ai.look_up_code::TEXT = ai.nationality_cd::TEXT
                    AND ml_nat_ai.lang_cd = 99
                LEFT JOIN mdm.m_lookup_masters ml_cat_ai
                    ON ml_cat_ai.api_master_code = 'CATEGORY'
                    AND ml_cat_ai.look_up_code::TEXT = ai.category_cd::TEXT
                    AND ml_cat_ai.lang_cd = 99
                LEFT JOIN mdm.m_lookup_masters ml_occ_ai
                    ON ml_occ_ai.api_master_code = 'OCCUPATION'
                    AND ml_occ_ai.look_up_code::TEXT = ai.occupation_cd::TEXT
                    AND ml_occ_ai.lang_cd = 99
                LEFT JOIN mdm.m_lookup_masters ml_agedet_ai
                    ON ml_agedet_ai.api_master_code = 'AGE_DETERM'
                    AND ml_agedet_ai.look_up_code::TEXT = ai.age_proof_type_cd::TEXT
                    AND ml_agedet_ai.lang_cd = 99
                LEFT JOIN mdm.m_lookup_masters ml_incgrp_ai
                    ON ml_incgrp_ai.api_master_code = 'INCOME_GROUP'
                    AND ml_incgrp_ai.look_up_code::TEXT = ai.income_group_cd::TEXT
                    AND ml_incgrp_ai.lang_cd = 99
                LEFT JOIN mdm.m_lookup_masters ml_gen_ai
                    ON ml_gen_ai.api_master_code = 'GENDER'
                    AND ml_gen_ai.look_up_code::TEXT = ai.gender_cd::TEXT
                    AND ml_gen_ai.lang_cd = 99
                LEFT JOIN mdm.m_lookup_masters ml_relg_ai
                    ON ml_relg_ai.api_master_code = 'RELIGION'
                    AND ml_relg_ai.look_up_code::TEXT = ai.religion_cd::TEXT
                    AND ml_relg_ai.lang_cd = 99
                LEFT JOIN mdm.m_lookup_masters ml_mar_ai
                    ON ml_mar_ai.api_master_code = 'MARTL_STATUS'
                    AND ml_mar_ai.look_up_code::TEXT = ai.marital_status_cd::TEXT
                    AND ml_mar_ai.lang_cd = 99
                LEFT JOIN mdm.m_lookup_masters ml_arrtyp_ai
                    ON ml_arrtyp_ai.api_master_code = 'ARR_SURR_TYPE'
                    AND ml_arrtyp_ai.look_up_code::TEXT = ai.arrest_type_cd::TEXT
                    AND ml_arrtyp_ai.lang_cd = 99
                LEFT JOIN mdm.m_state st_arrsurr_ai
                    ON st_arrsurr_ai.state_id = ai.arrest_surr_stat_id
                LEFT JOIN mdm.m_district di_arrsurr_ai
                    ON di_arrsurr_ai.district_id = ai.arrest_surr_dist_id
                LEFT JOIN mdm.m_lookup_masters ml_arract_ai
                    ON ml_arract_ai.api_master_code = 'ARRST_ACTN'
                    AND ml_arract_ai.look_up_code::TEXT = ai.arrest_action_taken_cd::TEXT
                    AND ml_arract_ai.lang_cd = 99
                LEFT JOIN mdm.m_lookup_masters ml_accstat_ai
                    ON ml_accstat_ai.api_master_code = 'ACC_STATUS'
                    AND ml_accstat_ai.look_up_code::TEXT = ai.accused_status_cd::TEXT
                    AND ml_accstat_ai.lang_cd = 99
                LEFT JOIN mdm.m_lookup_masters ml_blood_ai
                    ON ml_blood_ai.api_master_code = 'BLOOD_GROUP'
                    AND ml_blood_ai.look_up_code::TEXT = ai.blood_group_cd::TEXT
                    AND ml_blood_ai.lang_cd = 99
                LEFT JOIN users.t_police_staff_info rcb_ai
                    ON rcb_ai.staff_id = ai.record_created_by
                LEFT JOIN users.t_police_staff_info rub_ai
                    ON rub_ai.staff_id = ai.record_updated_by
                LEFT JOIN mdm.m_lookup_masters ml_oreg_ai
                    ON ml_oreg_ai.api_master_code = 'LINK_REG_TYPE'
                    AND ml_oreg_ai.look_up_code::TEXT = ai.other_reg_type_cd::TEXT
                    AND ml_oreg_ai.lang_cd = 99
                LEFT JOIN mdm.m_police_station ps_arrsurr_ai
                    ON ps_arrsurr_ai.ps_id = ai.arrest_surr_ps_id
                LEFT JOIN mdm.m_lookup_masters ml_build_ai
                    ON ml_build_ai.api_master_code = 'PHY_FEAT_PCODE_BUILD'
                    AND ml_build_ai.look_up_code::TEXT = ai.build_type_cd::TEXT
                    AND ml_build_ai.lang_cd = 99
                LEFT JOIN mdm.m_lookup_masters ml_compl_ai
                    ON ml_compl_ai.api_master_code = 'PHY_FEAT_PCODE_COMPL'
                    AND ml_compl_ai.look_up_code::TEXT = ai.complexion_type_cd::TEXT
                    AND ml_compl_ai.lang_cd = 99
                LEFT JOIN mdm.m_lookup_masters ml_living_ai
                    ON ml_living_ai.api_master_code = 'LIVING_STATUS'
                    AND ml_living_ai.look_up_code::TEXT = ai.living_status_cd::TEXT
                    AND ml_living_ai.lang_cd = 99

                WHERE ai.fir_reg_num = r.fir_reg_num
            )
        )
    INTO v_result
    FROM fir.t_fir_registration r
    WHERE r.fir_reg_num = p_fir_reg_num;

    RETURN v_result;

END;
$$;

select * from fir.t_fir_accused_info;

select fir.get_accused_json('38101001160067')

