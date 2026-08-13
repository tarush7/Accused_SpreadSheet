-- =============================================================================
-- HELPER : ncr.jsonb_camel_keys
-- Converts all keys of a flat JSONB object from snake_case to camelCase.
-- =============================================================================
CREATE OR REPLACE FUNCTION ncr.jsonb_camel_keys(data JSONB)
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
-- HELPER : ncr.jsonb_camel_keys
-- Converts all keys of a flat JSONB object from snake_case to camelCase.
-- =============================================================================
CREATE OR REPLACE FUNCTION ncr.to_camel_key(p_text text)
RETURNS text
LANGUAGE plpgsql
IMMUTABLE
AS
$$
DECLARE
    cleaned text;
    result text;
BEGIN
    cleaned := regexp_replace(lower(coalesce(p_text,'')), '[^a-z0-9]+', ' ', 'g');

    SELECT string_agg(
        CASE
            WHEN rn = 1 THEN word
            ELSE initcap(word)
        END,
        ''
    )192.168.181.219
    INTO result
    FROM (
        SELECT
            word,
            row_number() OVER() rn
        FROM regexp_split_to_table(trim(cleaned), '\s+') word
    ) x;

    RETURN result;
END;
$$;

-- =============================================================================
-- FUNCTION : ncr.get_ncr_json
-- PURPOSE : Returns a complete nested JSONB document for a single NCR
-- registration in camelCase, mirroring the FIR function style.
-- =============================================================================
CREATE OR REPLACE FUNCTION ncr.get_ncr_json(p_ncr_reg_num BIGINT)
RETURNS JSONB
LANGUAGE plpgsql
STABLE
SECURITY DEFINER
AS $$
DECLARE
v_result JSONB;
BEGIN
SELECT
ncr.jsonb_camel_keys(to_jsonb(r.*))
|| jsonb_build_object(

-- ROOT LOOKUPS
'langCdValue', ml_lang.look_up_value,
'cisDistCd', ml_cis_court.cis_district_code,
'cisDistName', UPPER(ml_cis_court.district_name),
'courtComplexCd', ml_cis_court.court_complex_cd,
'courtComplexName', ml_cis_court.court_complex_name,
'establishmentCd', ml_cis_court.estblishment_code,
'establishmentName', ml_cis_court.establishment_name,
'occurPlace', (
    SELECT
        ncr.jsonb_camel_keys(to_jsonb(a.*))
        || jsonb_build_object(
            'addressType', m."addressType",
            'country', m.country,
            'state', m.state,
            'district', m.district,
            'subDistrict', m."subDistrict",
            'village', m.village,
            'ps', m.ps
        )
    FROM ncr.t_ncr_person_address a
    LEFT JOIN LATERAL mdm.common_get_address_master_values(
        a.lang_cd, a.address_type_cd, a.country_cd,
        a.state_id, a.lg_district_cd, a.sub_district_cd,
        a.village_cd, a.ps_id
    ) m ON TRUE
    WHERE a.ncr_addr_srno = r.occ_plc_addr_cd
    LIMIT 1
),
'occurenceAddress', (
    SELECT
        ncr.jsonb_camel_keys(to_jsonb(a.*))
        || jsonb_build_object(
            'addressType', m."addressType",
            'country', m.country,
            'state', m.state,
            'district', m.district,
            'subDistrict', m."subDistrict",
            'village', m.village,
            'ps', m.ps
        )
    FROM ncr.t_ncr_person_address a
    LEFT JOIN LATERAL mdm.common_get_address_master_values(
        a.lang_cd, a.address_type_cd, a.country_cd,
        a.state_id, a.lg_district_cd, a.sub_district_cd,
        a.village_cd, a.ps_id
    ) m ON TRUE
    WHERE a.ncr_addr_srno = r.occ_plc_addr_cd
    LIMIT 1
),
'regOfficerFullName', NULLIF(TRIM(CONCAT_WS(' ', ro_st.first_name, ro_st.middle_name, ro_st.last_name)), ''),
'regOfficerRankDesc', ro_st.rank_desc,
'regOfficerLoginId', ro_st.login_id,
'beatCdValue', pb.beat_name,
'occPlcAddrCdValue', ml_occ_plc.look_up_value,
'ncrStatusCdValue', ml_ncr_status.look_up_value,
'courtTypeCdValue', ml_court_type.look_up_value,
'actionTaken', ml_action_taken.look_up_value,
'courtOrdersPassed',ml_court_action_taken.look_up_value,
'ncrStatusDesc', (CASE 
    WHEN r.action_taken_cd = 3
      OR (r.action_taken_cd IN (1,2) 
          AND r.court_orders_passed_cd IS NULL 
          AND enq.action_taken_cd IS NULL)
    THEN CONCAT_WS('-', ml_ncr_status.look_up_value, ml_enq_action_taken.look_up_value)

    WHEN enq.action_taken_cd IS NULL 
      AND r.court_orders_passed_cd IS NOT NULL 
      AND r.court_orders_passed_cd IN (1,3)
    THEN ml_court_action.look_up_value

    WHEN enq.action_taken_cd IS NULL 
      AND r.court_orders_passed_cd IS NOT NULL 
      AND r.court_orders_passed_cd IN (2,4)
    THEN ml_ncr_status.look_up_value

    WHEN enq.action_taken_cd IS NOT NULL 
      AND enq.action_taken_cd IN (1, 2)
    THEN ml_ncr_status.look_up_value

    ELSE ml_ncr_status.look_up_value
END)::TEXT,
'recordCreatedByFullName', NULLIF(TRIM(CONCAT_WS(' ', rcb_e.first_name, rcb_e.middle_name, rcb_e.last_name)), ''),
'recordCreatedByRankDesc', rcb_e.rank_desc,
'recordCreatedByLoginId', rcb_e.login_id,
'recordUpdatedByFullName', NULLIF(TRIM(CONCAT_WS(' ', rub_e.first_name, rub_e.middle_name, rub_e.last_name)), ''),
'recordUpdatedByRankDesc', rub_e.rank_desc,
'recordUpdatedByLoginId', rub_e.login_id,
'stateCd', st.state_cd,
'state', st.state,
'districtCd', di.district_cd,
'district', di.district,
'psCd', mps.ps_cd,
'ps', mps.ps,

-- NESTED: fileUploads
'fileUploads', (
SELECT COALESCE(jsonb_agg(
ncr.jsonb_camel_keys(to_jsonb(f.*))
|| jsonb_build_object(
'langCdValue', ml_f_lang.look_up_value,
'fileTypeCdValue', ml_ftype.look_up_value,
'fileSubtypeCdValue', ml_fsubtype.look_up_value,
'recordCreatedByFullName', NULLIF(TRIM(CONCAT_WS(' ', rcb_f.first_name, rcb_f.middle_name, rcb_f.last_name)), ''),
'recordCreatedByRankDesc', rcb_f.rank_desc,
'recordCreatedByLoginId', rcb_f.login_id,
'recordUpdatedByFullName', NULLIF(TRIM(CONCAT_WS(' ', rub_f.first_name, rub_f.middle_name, rub_f.last_name)), ''),
'recordUpdatedByRankDesc', rub_f.rank_desc,
'recordUpdatedByLoginId', rub_f.login_id
)
ORDER BY f.ncr_file_srno
), '[]'::jsonb)
FROM ncr.t_ncr_file_uploads f
LEFT JOIN mdm.m_lookup_masters ml_f_lang
ON ml_f_lang.api_master_code = 'OFFCL_LANG'
AND ml_f_lang.look_up_code::TEXT = f.lang_cd::TEXT
AND ml_f_lang.lang_cd = f.lang_cd
LEFT JOIN mdm.m_lookup_masters ml_ftype
ON ml_ftype.api_master_code = 'UPLOAD_FILE_TYP'
AND ml_ftype.look_up_code::TEXT = f.file_type_cd::TEXT
AND ml_ftype.lang_cd = f.lang_cd
LEFT JOIN mdm.m_lookup_masters ml_fsubtype
ON ml_fsubtype.api_master_code = 'UPLOAD_FILE_SUB_TYP'
AND ml_fsubtype.look_up_code::TEXT = f.file_subtype_cd::TEXT
AND ml_fsubtype.lang_cd = f.lang_cd
LEFT JOIN users.t_police_staff_info rcb_f
ON rcb_f.staff_id = f.record_created_by
LEFT JOIN users.t_police_staff_info rub_f
ON rub_f.staff_id = f.record_updated_by
WHERE f.ncr_reg_num = p_ncr_reg_num
),

-- NESTED: actSection
'actSection', (
SELECT COALESCE(jsonb_agg(
ncr.jsonb_camel_keys(to_jsonb(a.*))
|| jsonb_build_object(
'langCdValue', ml_a_lang.look_up_value,
'actShort', mact.act_short,
'actLong', mact.act_long,
'section', msec.section,
'sectionDesc',msec.section_desc,
'recordCreatedByFullName', NULLIF(TRIM(CONCAT_WS(' ', rcb_a.first_name, rcb_a.middle_name, rcb_a.last_name)), ''),
'recordCreatedByRankDesc', rcb_a.rank_desc,
'recordCreatedByLoginId', rcb_a.login_id,
'recordUpdatedByFullName', NULLIF(TRIM(CONCAT_WS(' ', rub_a.first_name, rub_a.middle_name, rub_a.last_name)), ''),
'recordUpdatedByRankDesc', rub_a.rank_desc,
'recordUpdatedByLoginId', rub_a.login_id
)
ORDER BY a.ncr_act_srno
), '[]'::jsonb)
FROM ncr.t_ncr_act_section a
LEFT JOIN mdm.m_lookup_masters ml_a_lang
ON ml_a_lang.api_master_code = 'OFFCL_LANG'
AND ml_a_lang.look_up_code::TEXT = a.lang_cd::TEXT
AND ml_a_lang.lang_cd = a.lang_cd
LEFT JOIN mdm.m_act mact
ON mact.act_cd = a.act_cd
AND mact.lang_cd = a.lang_cd
LEFT JOIN mdm.m_section msec
ON msec.section_code::TEXT = a.section_cd::TEXT
AND msec.lang_cd = a.lang_cd
LEFT JOIN users.t_police_staff_info rcb_a
ON rcb_a.staff_id = a.record_created_by
LEFT JOIN users.t_police_staff_info rub_a
ON rub_a.staff_id = a.record_updated_by
WHERE a.ncr_reg_num = p_ncr_reg_num
),

-- NESTED: enquiry
'enquiry', (
SELECT COALESCE(jsonb_agg(
ncr.jsonb_camel_keys(to_jsonb(enq.*))
|| jsonb_build_object(
'langCdValue', ml_e_lang.look_up_value,
'assignEoFullName', NULLIF(TRIM(CONCAT_WS(' ', ass_eo.first_name, ass_eo.middle_name, ass_eo.last_name)), ''),
'assignEoRankDesc', ass_eo.rank_desc,
'assignEoLoginId', ass_eo.login_id,
'mobileNum',ass_eo.mobile_num,
'telephone',ass_eo.telephone,
'reassignEoFullName', NULLIF(TRIM(CONCAT_WS(' ', reass_eo.first_name, reass_eo.middle_name, reass_eo.last_name)), ''),
'reassignEoRankDesc', reass_eo.rank_desc,
'reassignEoLoginId', reass_eo.login_id,
'enqApprvrFullName', NULLIF(TRIM(CONCAT_WS(' ', app_eo.first_name, app_eo.middle_name, app_eo.last_name)), ''),
'enqApprvrRankDesc', app_eo.rank_desc,
'enqApprvrLoginId', app_eo.login_id,
'recordCreatedByFullName', NULLIF(TRIM(CONCAT_WS(' ', rcb_enq.first_name, rcb_enq.middle_name, rcb_enq.last_name)), ''),
'recordCreatedByRankDesc', rcb_enq.rank_desc,
'recordCreatedByLoginId', rcb_enq.login_id,
'recordUpdatedByFullName', NULLIF(TRIM(CONCAT_WS(' ', rub_enq.first_name, rub_enq.middle_name, rub_enq.last_name)), ''),
'recordUpdatedByRankDesc', rub_enq.rank_desc,
'recordUpdatedByLoginId', rub_enq.login_id
)
ORDER BY enq.ncr_eo_srno
), '[]'::jsonb)
FROM ncr.t_ncr_enquiry enq
LEFT JOIN mdm.m_lookup_masters ml_e_lang
ON ml_e_lang.api_master_code = 'OFFCL_LANG'
AND ml_e_lang.look_up_code::TEXT = enq.lang_cd::TEXT
AND ml_e_lang.lang_cd = enq.lang_cd
LEFT JOIN users.t_police_staff_info ass_eo
ON ass_eo.staff_id = enq.assign_eo_cd
LEFT JOIN users.t_police_staff_info reass_eo
ON reass_eo.staff_id = enq.reassign_eo_cd
LEFT JOIN users.t_police_staff_info app_eo
ON app_eo.staff_id = enq.enq_apprvr_cd
LEFT JOIN users.t_police_staff_info rcb_enq
ON rcb_enq.staff_id = enq.record_created_by
LEFT JOIN users.t_police_staff_info rub_enq
ON rub_enq.staff_id = enq.record_updated_by
WHERE enq.ncr_reg_num = p_ncr_reg_num
),

-- NESTED: linkOtherCase
'linkOtherCase', (
SELECT COALESCE(jsonb_agg(
ncr.jsonb_camel_keys(to_jsonb(l.*))
|| jsonb_build_object(
'langCdValue', ml_l_lang.look_up_value,
'otherCaseTypeCdValue', ml_case_typ.look_up_value,
'recordCreatedByFullName', NULLIF(TRIM(CONCAT_WS(' ', rcb_l.first_name, rcb_l.middle_name, rcb_l.last_name)), ''),
'recordCreatedByRankDesc', rcb_l.rank_desc,
'recordCreatedByLoginId', rcb_l.login_id,
'recordUpdatedByFullName', NULLIF(TRIM(CONCAT_WS(' ', rub_l.first_name, rub_l.middle_name, rub_l.last_name)), ''),
'recordUpdatedByRankDesc', rub_l.rank_desc,
'recordUpdatedByLoginId', rub_l.login_id
)
ORDER BY l.ncr_link_srno
), '[]'::jsonb)
FROM ncr.t_ncr_link_other_case l
LEFT JOIN mdm.m_lookup_masters ml_l_lang
ON ml_l_lang.api_master_code = 'OFFCL_LANG'
AND ml_l_lang.look_up_code::TEXT = l.lang_cd::TEXT
AND ml_l_lang.lang_cd = l.lang_cd
LEFT JOIN mdm.m_lookup_masters ml_case_typ
ON ml_case_typ.api_master_code = 'CASE_TYP'
AND ml_case_typ.look_up_code::TEXT = l.other_case_type_cd::TEXT
AND ml_case_typ.lang_cd = l.lang_cd
LEFT JOIN users.t_police_staff_info rcb_l
ON rcb_l.staff_id = l.record_created_by
LEFT JOIN users.t_police_staff_info rub_l
ON rub_l.staff_id = l.record_updated_by
WHERE l.ncr_reg_num = p_ncr_reg_num
),

-- NESTED: property
'property', (
    SELECT COALESCE(jsonb_agg(
        ncr.jsonb_camel_keys(to_jsonb(p_info.*))
        || jsonb_build_object(
            'propertyType', ml_prop_type.look_up_value,
            'propertySubtype', ml_prop_subtype.look_up_value,
            'propertyNature', ml_prop_nature.look_up_value,
            'propertyTypeCd', p_info.property_type_cd,
            'propSubtypeCd', p_info.prop_subtype_cd,
            'propertyNatureCd', p_info.property_nature_cd,
            'estimatedValue', p_info.estimated_value
        )
        ORDER BY p_info.prop_vid
    ), '[]'::jsonb)
    FROM seizure.t_property_info p_info
    LEFT JOIN mdm.m_lookup_masters ml_prop_type
        ON ml_prop_type.api_master_code = 'PROP_TYP'
        AND ml_prop_type.look_up_code::TEXT = p_info.property_type_cd::TEXT
        AND ml_prop_type.lang_cd = p_info.lang_cd
    LEFT JOIN mdm.m_lookup_masters ml_prop_subtype
        ON ml_prop_subtype.api_master_code = 'PROP_SUB_TYP'
        AND ml_prop_subtype.look_up_code::TEXT = p_info.prop_subtype_cd::TEXT
        AND ml_prop_subtype.lang_cd = p_info.lang_cd
    LEFT JOIN mdm.m_lookup_masters ml_prop_nature
        ON ml_prop_nature.api_master_code = 'PROP_NATURE'
        AND ml_prop_nature.look_up_code::TEXT = p_info.property_nature_cd::TEXT
        AND ml_prop_nature.lang_cd = p_info.lang_cd
    WHERE p_info.reg_type_cd = 6
      AND p_info.reg_num = p_ncr_reg_num
)
)
||
COALESCE(
(
    SELECT jsonb_object_agg(
            person_type_key,
            person_array
        )
    FROM
    (
        SELECT
            ncr.to_camel_key(
                COALESCE(
                    person_type_name,
                    'unknownPersonType'
                )
            ) AS person_type_key,

            CASE 
                WHEN ncr.to_camel_key(COALESCE(person_type_name, '')) IN ('ncrComplainant', 'complainant') 
                THEN (jsonb_agg(person_json ORDER BY ncr_person_srno) -> 0)
                ELSE jsonb_agg(person_json ORDER BY ncr_person_srno)
            END AS person_array

        FROM
        (
            SELECT
                p.ncr_person_srno,

                (
                    ncr.jsonb_camel_keys(to_jsonb(p.*))
                    || jsonb_build_object(

                        'langCdValue', ml_p_lang.look_up_value,
                        'personTypeCdValue', ml_p_type.look_up_value,
                        'relationTypeCdValue', ml_rel_type.look_up_value,
                        'nationality', ml_p_nat.look_up_value,
                        'gender', ml_p_gender.look_up_value,
                        'maritalStatus', ml_p_mar.look_up_value,
                        'occupation', ml_p_occ.look_up_value,
                        'witnEvidTypeCdValue', ml_p_evid.look_up_value,
                        'FullName', NULLIF(TRIM(CONCAT_WS(' ', p.first_name, p.middle_name, p.last_name)), ''),

                        'recordCreatedByFullName',
                        NULLIF(TRIM(CONCAT_WS(' ',
                            rcb_p.first_name,
                            rcb_p.middle_name,
                            rcb_p.last_name
                        )), ''),

                        'recordCreatedByRankDesc',
                        rcb_p.rank_desc,

                        'recordCreatedByLoginId',
                        rcb_p.login_id,

                        'recordUpdatedByFullName',
                        NULLIF(TRIM(CONCAT_WS(' ',
                            rub_p.first_name,
                            rub_p.middle_name,
                            rub_p.last_name
                        )), ''),

                        'recordUpdatedByRankDesc',
                        rub_p.rank_desc,

                        'recordUpdatedByLoginId',
                        rub_p.login_id,

                        'addressGrid',
                        (
                            SELECT COALESCE(
                                jsonb_agg(
                                    ncr.jsonb_camel_keys(to_jsonb(addr.*))
                                    || jsonb_build_object(
                                        'langCdValue', ml_addr_lang.look_up_value,
                                        'addressTypeCdValue', ml_add_typ.look_up_value,
                                        'subDistrictCdValue', msd.sub_district,
                                        'villageCdValue', msdv.village_name,
                                        'countryCdValue', ml_country.look_up_value,
                                        'stateCd', st_addr.state_cd,
                                        'state', st_addr.state,
                                        'lgDistrictCdValue', di_addr.lg_district_name,
                                        'psCd', mps_addr.ps_cd,
                                        'ps', mps_addr.ps,
                                        'homeAddress', NULLIF(TRIM(CONCAT_WS(' ', addr.address_line_1, addr.address_line_2, addr.address_line_3)), ''),
                                        'permanentAddress', CASE 
                                            WHEN addr.is_perm_addr_same::text IN ('true', 't', '1', 'Y', 'y') THEN 
                                                NULLIF(TRIM(CONCAT_WS(', ', 
                                                    NULLIF(TRIM(CONCAT_WS(' ', addr.address_line_1, addr.address_line_2, addr.address_line_3)), ''),
                                                    msdv.village_name,
                                                    msd.sub_district,
                                                    ml_country.look_up_value,
                                                    addr.pincode::text
                                                )), '')
                                            ELSE NULL 
                                        END,

                                        'recordCreatedByFullName',
                                        NULLIF(TRIM(CONCAT_WS(' ',
                                            rcb_addr.first_name,
                                            rcb_addr.middle_name,
                                            rcb_addr.last_name
                                        )), ''),

                                        'recordCreatedByRankDesc',
                                        rcb_addr.rank_desc,

                                        'recordCreatedByLoginId',
                                        rcb_addr.login_id,

                                        'recordUpdatedByFullName',
                                        NULLIF(TRIM(CONCAT_WS(' ',
                                            rub_addr.first_name,
                                            rub_addr.middle_name,
                                            rub_addr.last_name
                                        )), ''),

                                        'recordUpdatedByRankDesc',
                                        rub_addr.rank_desc,

                                        'recordUpdatedByLoginId',
                                        rub_addr.login_id
                                    )
                                    ORDER BY addr.ncr_addr_srno
                                ),
                                '[]'::jsonb
                            )
                            FROM ncr.t_ncr_person_address addr

                            LEFT JOIN mdm.m_lookup_masters ml_addr_lang
                                ON ml_addr_lang.api_master_code='OFFCL_LANG'
                               AND ml_addr_lang.look_up_code::text=addr.lang_cd::text
                               AND ml_addr_lang.lang_cd=addr.lang_cd

                            LEFT JOIN mdm.m_lookup_masters ml_add_typ
                                ON ml_add_typ.api_master_code='ADD_TYP'
                               AND ml_add_typ.look_up_code::text=addr.address_type_cd::text
                               AND ml_add_typ.lang_cd=addr.lang_cd

                            LEFT JOIN mdm.m_sub_district msd
                                ON msd.sub_district_cd=addr.sub_district_cd
                               AND msd.lang_cd=addr.lang_cd

                            LEFT JOIN mdm.m_subdist_villages msdv
                                ON msdv.village_cd=addr.village_cd
                               AND msdv.lang_cd=addr.lang_cd

                            LEFT JOIN mdm.m_lookup_masters ml_country
                                ON ml_country.api_master_code='NATIONALITY'
                               AND ml_country.look_up_code::text=addr.country_cd::text
                               AND ml_country.lang_cd=addr.lang_cd

                            LEFT JOIN mdm.m_state st_addr
                                ON st_addr.state_id=addr.state_id
                               AND st_addr.lang_cd=addr.lang_cd

                            LEFT JOIN mdm.m_lgd_district di_addr
                                ON di_addr.lg_act_dist_cd=addr.lg_district_cd
                               AND di_addr.lang_cd=addr.lang_cd

                            LEFT JOIN mdm.m_police_station mps_addr
                                ON mps_addr.ps_id=addr.ps_id
                               AND mps_addr.lang_cd=addr.lang_cd

                            LEFT JOIN users.t_police_staff_info rcb_addr
                                ON rcb_addr.staff_id=addr.record_created_by

                            LEFT JOIN users.t_police_staff_info rub_addr
                                ON rub_addr.staff_id=addr.record_updated_by

                            WHERE addr.ncr_person_srno = p.ncr_person_srno
                        ),

                        'idList',
                        (
                            SELECT COALESCE(
                                jsonb_agg(
                                    ncr.jsonb_camel_keys(to_jsonb(nat.*))
                                    || jsonb_build_object(
                                        'langCdValue', ml_nat_lang.look_up_value,
                                        'nationalIdTypeCdValue', ml_nat_id_typ.look_up_value,

                                        'recordCreatedByFullName',
                                        NULLIF(TRIM(CONCAT_WS(' ',
                                            rcb_nat.first_name,
                                            rcb_nat.middle_name,
                                            rcb_nat.last_name
                                        )), ''),

                                        'recordCreatedByRankDesc',
                                        rcb_nat.rank_desc,

                                        'recordCreatedByLoginId',
                                        rcb_nat.login_id,

                                        'recordUpdatedByFullName',
                                        NULLIF(TRIM(CONCAT_WS(' ',
                                            rub_nat.first_name,
                                            rub_nat.middle_name,
                                            rub_nat.last_name
                                        )), ''),

                                        'recordUpdatedByRankDesc',
                                        rub_nat.rank_desc,

                                        'recordUpdatedByLoginId',
                                        rub_nat.login_id
                                    )
                                    ORDER BY nat.ncr_national_srno
                                ),
                                '[]'::jsonb
                            )
                            FROM ncr.t_ncr_person_nationality nat

                            LEFT JOIN mdm.m_lookup_masters ml_nat_lang
                                ON ml_nat_lang.api_master_code='OFFCL_LANG'
                               AND ml_nat_lang.look_up_code::text=nat.lang_cd::text
                               AND ml_nat_lang.lang_cd=nat.lang_cd

                            LEFT JOIN mdm.m_lookup_masters ml_nat_id_typ
                                ON ml_nat_id_typ.api_master_code='NTNL_ID_DOC_TYP'
                               AND ml_nat_id_typ.look_up_code::text=nat.national_id_type_cd::text
                               AND ml_nat_id_typ.lang_cd=nat.lang_cd

                            LEFT JOIN users.t_police_staff_info rcb_nat
                                ON rcb_nat.staff_id=nat.record_created_by

                            LEFT JOIN users.t_police_staff_info rub_nat
                                ON rub_nat.staff_id=nat.record_updated_by

                            WHERE nat.ncr_person_srno = p.ncr_person_srno
                        )
                    )
                ) AS person_json,

                ml_p_type.look_up_value AS person_type_name

            FROM ncr.t_ncr_person_info p

            LEFT JOIN mdm.m_lookup_masters ml_p_lang
                ON ml_p_lang.api_master_code='OFFCL_LANG'
               AND ml_p_lang.look_up_code::text=p.lang_cd::text
               AND ml_p_lang.lang_cd=p.lang_cd

            LEFT JOIN mdm.m_lookup_masters ml_p_type
                ON ml_p_type.api_master_code='MODULE_PERS_TYPES'
               AND ml_p_type.look_up_code::text=p.person_type_cd::text
               AND ml_p_type.lang_cd=p.lang_cd

            LEFT JOIN mdm.m_lookup_masters ml_rel_type
                ON ml_rel_type.api_master_code='RELATION_TYP'
               AND ml_rel_type.look_up_code::text=p.relation_type_cd::text
               AND ml_rel_type.lang_cd=p.lang_cd

            LEFT JOIN mdm.m_lookup_masters ml_p_nat
                ON ml_p_nat.api_master_code='NATIONALITY'
               AND ml_p_nat.look_up_code::text=p.nationality_cd::text
               AND ml_p_nat.lang_cd=p.lang_cd

            LEFT JOIN mdm.m_lookup_masters ml_p_gender
                ON ml_p_gender.api_master_code='GENDER'
               AND ml_p_gender.look_up_code::text=p.gender_cd::text
               AND ml_p_gender.lang_cd=p.lang_cd

            LEFT JOIN mdm.m_lookup_masters ml_p_mar
                ON ml_p_mar.api_master_code='MARTL_STATUS'
               AND ml_p_mar.look_up_code::text=p.marital_status_cd::text
               AND ml_p_mar.lang_cd=p.lang_cd

            LEFT JOIN mdm.m_lookup_masters ml_p_occ
                ON ml_p_occ.api_master_code='OCCUPATION'
               AND ml_p_occ.look_up_code::text=p.occupation_cd::text
               AND ml_p_occ.lang_cd=p.lang_cd

            LEFT JOIN mdm.m_lookup_masters ml_p_evid
                ON ml_p_evid.api_master_code='EVIDENCE_TYP'
               AND ml_p_evid.look_up_code::text=p.witn_evid_type_cd::text
               AND ml_p_evid.lang_cd=p.lang_cd

            LEFT JOIN users.t_police_staff_info rcb_p
                ON rcb_p.staff_id=p.record_created_by

            LEFT JOIN users.t_police_staff_info rub_p
                ON rub_p.staff_id=p.record_updated_by

            WHERE p.ncr_reg_num = p_ncr_reg_num
        ) person_data

        GROUP BY person_type_name
    ) grouped_persons
),
'{}'::jsonb
)
|| COALESCE(
    (
        SELECT jsonb_build_object(
            'gdSrno', g.gd_srno::TEXT,
            'gdyear', EXTRACT(YEAR FROM g.gd_incident_dt)::TEXT
        )
        FROM gd.t_gd_entry g
        WHERE g.gd_num = r.reg_gd_num
        LIMIT 1
    ),
    '{}'::jsonb
)
INTO v_result
FROM ncr.t_ncr_registration r
LEFT JOIN mdm.m_lookup_masters ml_lang
ON ml_lang.api_master_code = 'OFFCL_LANG'
AND ml_lang.look_up_code::TEXT = r.lang_cd::TEXT
AND ml_lang.lang_cd = r.lang_cd
LEFT JOIN users.t_police_staff_info ro_st
ON ro_st.staff_id = r.reg_officer_cd
LEFT JOIN mdm.m_ps_beat pb
ON pb.beat_cd = r.beat_cd
AND pb.lang_cd = r.lang_cd
LEFT JOIN mdm.m_lookup_masters ml_occ_plc
ON ml_occ_plc.api_master_code = 'OCCURRENCE_PLACE'
AND ml_occ_plc.look_up_code::TEXT = r.occ_plc_addr_cd::TEXT
AND ml_occ_plc.lang_cd = r.lang_cd
LEFT JOIN mdm.m_lookup_masters ml_ncr_status
ON ml_ncr_status.api_master_code = 'COMPL_NCR_STATUS'
AND ml_ncr_status.look_up_code::TEXT = r.ncr_status_cd::TEXT
AND ml_ncr_status.lang_cd = r.lang_cd
LEFT JOIN mdm.m_lookup_masters ml_court_type
ON ml_court_type.api_master_code = 'COURT_TYP'
AND ml_court_type.look_up_code::TEXT = r.court_type_cd::TEXT
AND ml_court_type.lang_cd = r.lang_cd
LEFT JOIN mdm.m_cis_court_estblshmnt ml_cis_court
ON ml_cis_court.estblishment_code::TEXT = r.court_type_cd::TEXT
LEFT JOIN mdm.m_lookup_masters ml_action_taken
ON ml_action_taken.api_master_code = 'NCR_REG_ACTION'
AND ml_action_taken.look_up_code::TEXT = r.action_taken_cd::TEXT
AND ml_action_taken.lang_cd = r.lang_cd
LEFT JOIN mdm.m_lookup_masters ml_court_action_taken
ON ml_court_action_taken.api_master_code = 'NCR_COURT_ACTION'
AND ml_court_action_taken.look_up_code::TEXT = r.court_orders_passed_cd::TEXT
AND ml_court_action_taken.lang_cd = r.lang_cd
LEFT JOIN (
    SELECT DISTINCT ON (e.ncr_reg_num) e.ncr_reg_num, e.action_taken_cd
    FROM ncr.t_ncr_enquiry e
    ORDER BY e.ncr_reg_num, e.ncr_eo_srno DESC
) enq ON enq.ncr_reg_num = r.ncr_reg_num
LEFT JOIN mdm.m_lookup_masters ml_court_action
ON ml_court_action.api_master_code = 'NCR_COURT_ACTION'
AND ml_court_action.look_up_code::TEXT = r.court_orders_passed_cd::TEXT
AND ml_court_action.lang_cd = r.lang_cd
LEFT JOIN mdm.m_lookup_masters ml_enq_action_taken
ON ml_enq_action_taken.api_master_code = 'NCR_REG_ACTION'
AND ml_enq_action_taken.look_up_code::TEXT = enq.action_taken_cd::TEXT
AND ml_enq_action_taken.lang_cd = r.lang_cd
LEFT JOIN users.t_police_staff_info rcb_e
ON rcb_e.staff_id = r.record_created_by
LEFT JOIN users.t_police_staff_info rub_e
ON rub_e.staff_id = r.record_updated_by
LEFT JOIN mdm.m_state st
ON st.state_id = r.state_id
AND st.lang_cd = r.lang_cd
LEFT JOIN mdm.m_district di
ON di.district_id = r.district_id
AND di.lang_cd = r.lang_cd
LEFT JOIN mdm.m_police_station mps
ON mps.ps_id = r.ps_id
AND mps.lang_cd = r.lang_cd
WHERE r.ncr_reg_num = p_ncr_reg_num;

RETURN v_result;
END;
$$;