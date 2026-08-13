-- =============================================================================
-- SCHEMA SETUP
-- =============================================================================
CREATE SCHEMA IF NOT EXISTS compl;
 
-- =============================================================================
-- HELPER : compl.jsonb_camel_keys
-- Converts all keys of a flat JSONB object from snake_case to camelCase.
-- =============================================================================
CREATE OR REPLACE FUNCTION compl.jsonb_camel_keys(data JSONB)
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
-- HELPER : compl.to_camel_key
-- Converts a text string (e.g. 'Suspect Person') to camelCase ('suspectPerson')
-- =============================================================================
CREATE OR REPLACE FUNCTION compl.to_camel_key(p_text text)
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
    )
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
-- FUNCTION : compl.get_complaint_json
-- PURPOSE  : Returns a complete nested JSONB document for a single complaint
--            registration, dynamically grouping persons by their type.
-- =============================================================================
CREATE OR REPLACE FUNCTION compl.get_complaint_json(p_compl_reg_num BIGINT)
RETURNS JSONB
LANGUAGE plpgsql
STABLE
SECURITY DEFINER
AS $$
DECLARE
    v_result JSONB;
BEGIN

SELECT
    compl.jsonb_camel_keys(to_jsonb(r.*))
    || jsonb_build_object(
        
        -- ROOT LOOKUPS
        'lang', ml_lang.look_up_value,
        'recordCreatedByFullName', NULLIF(TRIM(CONCAT_WS(' ', rcb_r.first_name, rcb_r.middle_name, rcb_r.last_name)), ''),
        'recordCreatedByRank', rcb_r.rank_desc,
        'recordCreatedByLoginId', rcb_r.login_id,
        
        'recordUpdatedByFullName', NULLIF(TRIM(CONCAT_WS(' ', rub_r.first_name, rub_r.middle_name, rub_r.last_name)), ''),
        'recordUpdatedByRank', rub_r.rank_desc,
        'recordUpdatedByLoginId', rub_r.login_id,
        
        'complaintNature', ml_compl_nature.look_up_value,
        'informationSource', ml_info_source.look_up_value,
        'infoSrcSocMed', ml_soc_med.look_up_value,
        
        'cognzPoliceFullName', NULLIF(TRIM(CONCAT_WS(' ', cog_pol.first_name, cog_pol.middle_name, cog_pol.last_name)), ''),
        'cognzPoliceRank', cog_pol.rank_desc,
        'cognzPoliceLoginId', cog_pol.login_id,
        
        'srcCourtType', ml_court_type.look_up_value,
        'receiptMode', ml_receipt_mode.look_up_value,
        'complaintStatus', ml_compl_status.look_up_value,
        
        'submitPsName', mps.ps,
        'submitPsCd', mps.ps_cd,
        'submitState', st.state,
        'submitStateCd', st.state_cd,
        'submitDistrict', di.district,
        'submitDistrictCd', di.district_cd,
        'submitOffice', (
            SELECT office_name 
            FROM admin.m_office_wise_incharge 
            WHERE office_cd = r.submit_office_cd 
              AND record_status <> 'D' 
            LIMIT 1
        ),
        'incidentAddress', (
            SELECT
                compl.jsonb_camel_keys(to_jsonb(a.*))
                || jsonb_build_object(
                    'addressType', m."addressType",
                    'country', m.country,
                    'state', m.state,
                    'district', m.district,
                    'subDistrict', m."subDistrict",
                    'village', m.village,
                    'ps', m.ps
                )
            FROM compl.t_complaint_person_addr a
            LEFT JOIN LATERAL mdm.common_get_address_master_values(
                a.lang_cd, a.address_type_cd, a.country_cd,
                a.state_id, a.lg_district_cd, a.sub_district_cd,
                a.village_cd, a.ps_id
            ) m ON TRUE
            WHERE a.compl_addr_srno = r.inc_plc_addr_cd
            LIMIT 1
        ),
        'lostProps', (
            SELECT COALESCE(jsonb_agg(
                compl.jsonb_camel_keys(to_jsonb(p_info.*))
                || jsonb_build_object(
                    'propertyType', ml_prop_type.look_up_value,
                    'propertySubtype', ml_prop_subtype.look_up_value,
                    'propertyNature', ml_prop_nature.look_up_value,
                    'propertyTypeCd', p_info.property_type_cd,
                    'propSubtypeCd', p_info.prop_subtype_cd,
                    'propertyNatureCd', p_info.property_nature_cd,
                    'estimatedValue', p_info.estimated_value,
                    'arm', (
                        SELECT compl.jsonb_camel_keys(to_jsonb(sub_arm.*))
                        FROM seizure.t_property_arms sub_arm
                        WHERE sub_arm.prop_vid = p_info.prop_vid
                        LIMIT 1
                    ),
                    'automobile', (
                        SELECT compl.jsonb_camel_keys(to_jsonb(sub_auto.*))
                        FROM seizure.t_property_automobile sub_auto
                        WHERE sub_auto.prop_vid = p_info.prop_vid
                        LIMIT 1
                    ),
                    'cultural', (
                        SELECT compl.jsonb_camel_keys(to_jsonb(sub_cult.*))
                        FROM seizure.t_property_cultural sub_cult
                        WHERE sub_cult.prop_vid = p_info.prop_vid
                        LIMIT 1
                    ),
                    'currency', (
                        SELECT compl.jsonb_camel_keys(to_jsonb(sub_curr.*))
                        FROM seizure.t_property_currency sub_curr
                        WHERE sub_curr.prop_vid = p_info.prop_vid
                        LIMIT 1
                    ),
                    'document', (
                        SELECT compl.jsonb_camel_keys(to_jsonb(sub_doc.*))
                        FROM seizure.t_property_documents sub_doc
                        WHERE sub_doc.prop_vid = p_info.prop_vid
                        LIMIT 1
                    ),
                    'electricGoods', (
                        SELECT compl.jsonb_camel_keys(to_jsonb(sub_elec.*))
                        FROM seizure.t_property_electric_goods sub_elec
                        WHERE sub_elec.prop_vid = p_info.prop_vid
                        LIMIT 1
                    ),
                    'explosive', (
                        SELECT compl.jsonb_camel_keys(to_jsonb(sub_exp.*))
                        FROM seizure.t_property_explosives sub_exp
                        WHERE sub_exp.prop_vid = p_info.prop_vid
                        LIMIT 1
                    ),
                    'jewelry', (
                        SELECT compl.jsonb_camel_keys(to_jsonb(sub_jew.*))
                        FROM seizure.t_property_jewelry sub_jew
                        WHERE sub_jew.prop_vid = p_info.prop_vid
                        LIMIT 1
                    ),
                    'other', (
                        SELECT compl.jsonb_camel_keys(to_jsonb(sub_oth.*))
                        FROM seizure.t_property_others sub_oth
                        WHERE sub_oth.prop_vid = p_info.prop_vid
                        LIMIT 1
                    )
                )
                ORDER BY p_info.prop_vid
            ), '[]'::jsonb)
            FROM seizure.t_property_info p_info
            LEFT JOIN mdm.m_lookup_masters ml_prop_type
                ON ml_prop_type.api_master_code = 'PROP_TYP'
                AND ml_prop_type.look_up_code::TEXT = p_info.property_type_cd::TEXT
                AND ml_prop_type.lang_cd = r.lang_cd
            LEFT JOIN mdm.m_lookup_masters ml_prop_subtype
                ON ml_prop_subtype.api_master_code = 'PROP_SUB_TYP'
                AND ml_prop_subtype.look_up_code::TEXT = p_info.prop_subtype_cd::TEXT
                AND ml_prop_subtype.lang_cd = r.lang_cd
            LEFT JOIN mdm.m_lookup_masters ml_prop_nature
                ON ml_prop_nature.api_master_code = 'PROP_NATURE'
                AND ml_prop_nature.look_up_code::TEXT = p_info.property_nature_cd::TEXT
                AND ml_prop_nature.lang_cd = r.lang_cd
            WHERE p_info.reg_type_cd = 9
              AND p_info.reg_num = p_compl_reg_num
        ),

        -- NESTED: complaintEnquiryInfo
        'currEnquiry', (
            SELECT COALESCE(
                (
                    SELECT
                        compl.jsonb_camel_keys(to_jsonb(enq.*) - 'compl_eo_srno_migr')
                        || jsonb_build_object(
                            'lang', ml_enq_lang.look_up_value,
                            'eoFullName', NULLIF(TRIM(CONCAT_WS(' ', eo.first_name, eo.middle_name, eo.last_name)), ''),
                            'eoRank', eo.rank_desc,
                            'eoLoginId', eo.login_id,

                            'assignEoFullName', NULLIF(TRIM(CONCAT_WS(' ', a_eo.first_name, a_eo.middle_name, a_eo.last_name)), ''),
                            'assignEoRank', a_eo.rank_desc,
                            'assignEoLoginId', a_eo.login_id,

                            'reassignEoFullName', NULLIF(TRIM(CONCAT_WS(' ', ra_eo.first_name, ra_eo.middle_name, ra_eo.last_name)), ''),
                            'reassignEoRank', ra_eo.rank_desc,
                            'reassignEoLoginId', ra_eo.login_id,

                            'enqApprvrFullName', NULLIF(TRIM(CONCAT_WS(' ', app_eo.first_name, app_eo.middle_name, app_eo.last_name)), ''),
                            'enqApprvrRank', app_eo.rank_desc,
                            'enqApprvrLoginId', app_eo.login_id,

                            'recordCreatedByFullName', NULLIF(TRIM(CONCAT_WS(' ', rcb_enq.first_name, rcb_enq.middle_name, rcb_enq.last_name)), ''),
                            'recordCreatedByRank', rcb_enq.rank_desc,
                            'recordCreatedByLoginId', rcb_enq.login_id,
                            
                            'recordUpdatedByFullName', NULLIF(TRIM(CONCAT_WS(' ', rub_enq.first_name, rub_enq.middle_name, rub_enq.last_name)), ''),
                            'recordUpdatedByRank', rub_enq.rank_desc,
                            'recordUpdatedByLoginId', rub_enq.login_id,

                            -- DEEPLY NESTED: complaintEnquiryRemarks
                            'complaintEnquiryRemarks', (
                                SELECT COALESCE(jsonb_agg(
                                    compl.jsonb_camel_keys(to_jsonb(rem.*) - 'enq_remark_srno_migr')
                                    || jsonb_build_object(
                                        'lang', ml_rem_lang.look_up_value,
                                        'eoFullName', NULLIF(TRIM(CONCAT_WS(' ', rem_eo.first_name, rem_eo.middle_name, rem_eo.last_name)), ''),
                                        'eoRank', rem_eo.rank_desc,
                                        'eoLoginId', rem_eo.login_id,
                                        
                                        'remarksGivenByFullName', NULLIF(TRIM(CONCAT_WS(' ', rem_gvn.first_name, rem_gvn.middle_name, rem_gvn.last_name)), ''),
                                        'officeType', ml_rem_off.look_up_value,

                                        'recordCreatedByFullName', NULLIF(TRIM(CONCAT_WS(' ', rcb_rem.first_name, rcb_rem.middle_name, rcb_rem.last_name)), ''),
                                        'recordCreatedByRank', rcb_rem.rank_desc,
                                        'recordCreatedByLoginId', rcb_rem.login_id,

                                        'recordUpdatedByFullName', NULLIF(TRIM(CONCAT_WS(' ', rub_rem.first_name, rub_rem.middle_name, rub_rem.last_name)), ''),
                                        'recordUpdatedByRank', rub_rem.rank_desc,
                                        'recordUpdatedByLoginId', rub_rem.login_id
                                    )
                                    ORDER BY rem.enq_remark_srno
                                ), '[]'::jsonb)
                                FROM compl.t_complaint_enquiry_remarks rem
                                LEFT JOIN users.t_police_staff_info rem_eo ON rem_eo.staff_id = rem.eo_cd
                                LEFT JOIN users.t_police_staff_info rem_gvn ON rem_gvn.login_id = rem.remarks_given_by
                                LEFT JOIN mdm.m_lookup_masters ml_rem_off ON ml_rem_off.api_master_code = 'OFFICE_TYPES' AND ml_rem_off.look_up_code::TEXT = rem.office_type_cd::TEXT AND ml_rem_off.lang_cd = rem.lang_cd
                                LEFT JOIN users.t_police_staff_info rcb_rem ON rcb_rem.staff_id = rem.record_created_by
                                LEFT JOIN users.t_police_staff_info rub_rem ON rub_rem.staff_id = rem.record_updated_by
                                LEFT JOIN mdm.m_lookup_masters ml_rem_lang ON ml_rem_lang.api_master_code = 'OFFCL_LANG' AND ml_rem_lang.look_up_code::TEXT = rem.lang_cd::TEXT AND ml_rem_lang.lang_cd = rem.lang_cd
                                WHERE rem.compl_eo_srno = enq.compl_eo_srno
                            ),

                            -- DEEPLY NESTED: complaintFiles
                            'fileList', (
                                SELECT COALESCE(jsonb_agg(
                                    compl.jsonb_camel_keys(to_jsonb(f.*) - 'complaint_file_srno_migr')
                                    || jsonb_build_object(
                                        'lang', ml_f_lang.look_up_value,
                                        'fileType', ml_f_type.look_up_value,
                                        'fileSubtype', ml_f_sub.look_up_value,
                                        
                                        'recordCreatedByFullName', NULLIF(TRIM(CONCAT_WS(' ', rcb_f.first_name, rcb_f.middle_name, rcb_f.last_name)), ''),
                                        'recordCreatedByRank', rcb_f.rank_desc,
                                        'recordCreatedByLoginId', rcb_f.login_id,
                                        
                                        'recordUpdatedByFullName', NULLIF(TRIM(CONCAT_WS(' ', rub_f.first_name, rub_f.middle_name, rub_f.last_name)), ''),
                                        'recordUpdatedByRank', rub_f.rank_desc,
                                        'recordUpdatedByLoginId', rub_f.login_id
                                    )
                                    ORDER BY f.complaint_file_srno
                                ), '[]'::jsonb)
                                FROM compl.t_complaint_files f
                                LEFT JOIN mdm.m_lookup_masters ml_f_type ON ml_f_type.api_master_code = 'UPLOAD_FILE_TYP' AND ml_f_type.look_up_code::TEXT = f.file_type_cd::TEXT AND ml_f_type.lang_cd = f.lang_cd
                                LEFT JOIN mdm.m_lookup_masters ml_f_sub ON ml_f_sub.api_master_code = 'UPLOAD_FILE_SUB_TYP' AND ml_f_sub.look_up_code::TEXT = f.file_subtype_cd::TEXT AND ml_f_sub.lang_cd = f.lang_cd
                                LEFT JOIN users.t_police_staff_info rcb_f ON rcb_f.staff_id = f.record_created_by
                                LEFT JOIN users.t_police_staff_info rub_f ON rub_f.staff_id = f.record_updated_by
                                LEFT JOIN mdm.m_lookup_masters ml_f_lang ON ml_f_lang.api_master_code = 'OFFCL_LANG' AND ml_f_lang.look_up_code::TEXT = f.lang_cd::TEXT AND ml_f_lang.lang_cd = f.lang_cd
                                WHERE f.compl_eo_srno = enq.compl_eo_srno
                            )
                        )
                    FROM compl.t_complaint_enquiry_info enq
                    LEFT JOIN users.t_police_staff_info eo ON eo.staff_id = enq.eo_cd
                    LEFT JOIN users.t_police_staff_info a_eo ON a_eo.staff_id = enq.assign_eo_cd
                    LEFT JOIN users.t_police_staff_info ra_eo ON ra_eo.staff_id = enq.reassign_eo_cd
                    LEFT JOIN users.t_police_staff_info app_eo ON app_eo.staff_id = enq.enq_apprvr_cd
                    LEFT JOIN users.t_police_staff_info rcb_enq ON rcb_enq.staff_id = enq.record_created_by
                    LEFT JOIN users.t_police_staff_info rub_enq ON rub_enq.staff_id = enq.record_updated_by
                    LEFT JOIN mdm.m_lookup_masters ml_enq_lang ON ml_enq_lang.api_master_code = 'OFFCL_LANG' AND ml_enq_lang.look_up_code::TEXT = enq.lang_cd::TEXT AND ml_enq_lang.lang_cd = enq.lang_cd
                    WHERE enq.compl_reg_num = r.compl_reg_num
                    ORDER BY enq.compl_eo_srno DESC
                    LIMIT 1
                ),
                '{}'::jsonb
            )
        ),
        'previousEnquiryDtls', (
            SELECT COALESCE(jsonb_agg(
                compl.jsonb_camel_keys(to_jsonb(enq.*) - 'compl_eo_srno_migr')
                || jsonb_build_object(
                    'lang', ml_enq_lang.look_up_value,
                    'eoFullName', NULLIF(TRIM(CONCAT_WS(' ', eo.first_name, eo.middle_name, eo.last_name)), ''),
                    'eoRank', eo.rank_desc,
                    'eoLoginId', eo.login_id,

                    'assignEoFullName', NULLIF(TRIM(CONCAT_WS(' ', a_eo.first_name, a_eo.middle_name, a_eo.last_name)), ''),
                    'assignEoRank', a_eo.rank_desc,
                    'assignEoLoginId', a_eo.login_id,

                    'reassignEoFullName', NULLIF(TRIM(CONCAT_WS(' ', ra_eo.first_name, ra_eo.middle_name, ra_eo.last_name)), ''),
                    'reassignEoRank', ra_eo.rank_desc,
                    'reassignEoLoginId', ra_eo.login_id,

                    'enqApprvrFullName', NULLIF(TRIM(CONCAT_WS(' ', app_eo.first_name, app_eo.middle_name, app_eo.last_name)), ''),
                    'enqApprvrRank', app_eo.rank_desc,
                    'enqApprvrLoginId', app_eo.login_id,

                    'recordCreatedByFullName', NULLIF(TRIM(CONCAT_WS(' ', rcb_enq.first_name, rcb_enq.middle_name, rcb_enq.last_name)), ''),
                    'recordCreatedByRank', rcb_enq.rank_desc,
                    'recordCreatedByLoginId', rcb_enq.login_id,
                    
                    'recordUpdatedByFullName', NULLIF(TRIM(CONCAT_WS(' ', rub_enq.first_name, rub_enq.middle_name, rub_enq.last_name)), ''),
                    'recordUpdatedByRank', rub_enq.rank_desc,
                    'recordUpdatedByLoginId', rub_enq.login_id,

                    -- DEEPLY NESTED: complaintEnquiryRemarks
                    'complaintEnquiryRemarks', (
                        SELECT COALESCE(jsonb_agg(
                            compl.jsonb_camel_keys(to_jsonb(rem.*) - 'enq_remark_srno_migr')
                            || jsonb_build_object(
                                'lang', ml_rem_lang.look_up_value,
                                'eoFullName', NULLIF(TRIM(CONCAT_WS(' ', rem_eo.first_name, rem_eo.middle_name, rem_eo.last_name)), ''),
                                'eoRank', rem_eo.rank_desc,
                                'eoLoginId', rem_eo.login_id,
                                
                                'remarksGivenByFullName', NULLIF(TRIM(CONCAT_WS(' ', rem_gvn.first_name, rem_gvn.middle_name, rem_gvn.last_name)), ''),
                                'officeType', ml_rem_off.look_up_value,

                                'recordCreatedByFullName', NULLIF(TRIM(CONCAT_WS(' ', rcb_rem.first_name, rcb_rem.middle_name, rcb_rem.last_name)), ''),
                                'recordCreatedByRank', rcb_rem.rank_desc,
                                'recordCreatedByLoginId', rcb_rem.login_id,

                                'recordUpdatedByFullName', NULLIF(TRIM(CONCAT_WS(' ', rub_rem.first_name, rub_rem.middle_name, rub_rem.last_name)), ''),
                                'recordUpdatedByRank', rub_rem.rank_desc,
                                'recordUpdatedByLoginId', rub_rem.login_id
                            )
                            ORDER BY rem.enq_remark_srno
                        ), '[]'::jsonb)
                        FROM compl.t_complaint_enquiry_remarks rem
                        LEFT JOIN users.t_police_staff_info rem_eo ON rem_eo.staff_id = rem.eo_cd
                        LEFT JOIN users.t_police_staff_info rem_gvn ON rem_gvn.login_id = rem.remarks_given_by
                        LEFT JOIN mdm.m_lookup_masters ml_rem_off ON ml_rem_off.api_master_code = 'OFFICE_TYPES' AND ml_rem_off.look_up_code::TEXT = rem.office_type_cd::TEXT AND ml_rem_off.lang_cd = rem.lang_cd
                        LEFT JOIN users.t_police_staff_info rcb_rem ON rcb_rem.staff_id = rem.record_created_by
                        LEFT JOIN users.t_police_staff_info rub_rem ON rub_rem.staff_id = rem.record_updated_by
                        LEFT JOIN mdm.m_lookup_masters ml_rem_lang ON ml_rem_lang.api_master_code = 'OFFCL_LANG' AND ml_rem_lang.look_up_code::TEXT = rem.lang_cd::TEXT AND ml_rem_lang.lang_cd = rem.lang_cd
                        WHERE rem.compl_eo_srno = enq.compl_eo_srno
                    ),

                    -- DEEPLY NESTED: complaintFiles
                    'fileList', (
                        SELECT COALESCE(jsonb_agg(
                            compl.jsonb_camel_keys(to_jsonb(f.*) - 'complaint_file_srno_migr')
                            || jsonb_build_object(
                                'lang', ml_f_lang.look_up_value,
                                'fileType', ml_f_type.look_up_value,
                                'fileSubtype', ml_f_sub.look_up_value,
                                
                                'recordCreatedByFullName', NULLIF(TRIM(CONCAT_WS(' ', rcb_f.first_name, rcb_f.middle_name, rcb_f.last_name)), ''),
                                'recordCreatedByRank', rcb_f.rank_desc,
                                'recordCreatedByLoginId', rcb_f.login_id,
                                
                                'recordUpdatedByFullName', NULLIF(TRIM(CONCAT_WS(' ', rub_f.first_name, rub_f.middle_name, rub_f.last_name)), ''),
                                'recordUpdatedByRank', rub_f.rank_desc,
                                'recordUpdatedByLoginId', rub_f.login_id
                            )
                            ORDER BY f.complaint_file_srno
                        ), '[]'::jsonb)
                        FROM compl.t_complaint_files f
                        LEFT JOIN mdm.m_lookup_masters ml_f_type ON ml_f_type.api_master_code = 'UPLOAD_FILE_TYP' AND ml_f_type.look_up_code::TEXT = f.file_type_cd::TEXT AND ml_f_type.lang_cd = f.lang_cd
                        LEFT JOIN mdm.m_lookup_masters ml_f_sub ON ml_f_sub.api_master_code = 'UPLOAD_FILE_SUB_TYP' AND ml_f_sub.look_up_code::TEXT = f.file_subtype_cd::TEXT AND ml_f_sub.lang_cd = f.lang_cd
                        LEFT JOIN users.t_police_staff_info rcb_f ON rcb_f.staff_id = f.record_created_by
                        LEFT JOIN users.t_police_staff_info rub_f ON rub_f.staff_id = f.record_updated_by
                        LEFT JOIN mdm.m_lookup_masters ml_f_lang ON ml_f_lang.api_master_code = 'OFFCL_LANG' AND ml_f_lang.look_up_code::TEXT = f.lang_cd::TEXT AND ml_f_lang.lang_cd = f.lang_cd
                        WHERE f.compl_eo_srno = enq.compl_eo_srno
                    )
                )
                ORDER BY enq.compl_eo_srno DESC
            ), '[]'::jsonb)
            FROM compl.t_complaint_enquiry_info enq
            LEFT JOIN users.t_police_staff_info eo ON eo.staff_id = enq.eo_cd
            LEFT JOIN users.t_police_staff_info a_eo ON a_eo.staff_id = enq.assign_eo_cd
            LEFT JOIN users.t_police_staff_info ra_eo ON ra_eo.staff_id = enq.reassign_eo_cd
            LEFT JOIN users.t_police_staff_info app_eo ON app_eo.staff_id = enq.enq_apprvr_cd
            LEFT JOIN users.t_police_staff_info rcb_enq ON rcb_enq.staff_id = enq.record_created_by
            LEFT JOIN users.t_police_staff_info rub_enq ON rub_enq.staff_id = enq.record_updated_by
            LEFT JOIN mdm.m_lookup_masters ml_enq_lang ON ml_enq_lang.api_master_code = 'OFFCL_LANG' AND ml_enq_lang.look_up_code::TEXT = enq.lang_cd::TEXT AND ml_enq_lang.lang_cd = enq.lang_cd
            WHERE enq.compl_reg_num = r.compl_reg_num
              AND (enq.is_enq_rep_submit = true OR enq.is_enq_rep_submit::text = 'Y')
              AND enq.compl_eo_srno != (
                  SELECT COALESCE(MAX(compl_eo_srno), 0)
                  FROM compl.t_complaint_enquiry_info
                  WHERE compl_reg_num = r.compl_reg_num
              )
        ),

        -- NESTED: complaintLinking
        'linkings', (
            SELECT COALESCE(jsonb_agg(
                compl.jsonb_camel_keys(to_jsonb(cl.*) - 'compl_link_srno_migr')
                || jsonb_build_object(
                    'lang', ml_cl_lang.look_up_value,
                    'linkingApprovedByFullName', NULLIF(TRIM(CONCAT_WS(' ', appr_cl.first_name, appr_cl.middle_name, appr_cl.last_name)), ''),
                    
                    'recordCreatedByFullName', NULLIF(TRIM(CONCAT_WS(' ', rcb_cl.first_name, rcb_cl.middle_name, rcb_cl.last_name)), ''),
                    'recordCreatedByRank', rcb_cl.rank_desc,
                    'recordCreatedByLoginId', rcb_cl.login_id,
                    
                    'recordUpdatedByFullName', NULLIF(TRIM(CONCAT_WS(' ', rub_cl.first_name, rub_cl.middle_name, rub_cl.last_name)), ''),
                    'recordUpdatedByRank', rub_cl.rank_desc,
                    'recordUpdatedByLoginId', rub_cl.login_id
                )
                ORDER BY cl.compl_link_srno
            ), '[]'::jsonb)
            FROM compl.t_complaint_linking cl
            LEFT JOIN users.t_police_staff_info appr_cl ON appr_cl.login_id = cl.linking_approved_by
            LEFT JOIN users.t_police_staff_info rcb_cl ON rcb_cl.staff_id = cl.record_created_by
            LEFT JOIN users.t_police_staff_info rub_cl ON rub_cl.staff_id = cl.record_updated_by
            LEFT JOIN mdm.m_lookup_masters ml_cl_lang ON ml_cl_lang.api_master_code = 'OFFCL_LANG' AND ml_cl_lang.look_up_code::TEXT = cl.lang_cd::TEXT AND ml_cl_lang.lang_cd = cl.lang_cd
            WHERE r.compl_reg_num IN (cl.compl_reg_num_1, cl.compl_reg_num_2)
        ),

        -- NESTED: complaintTransfer
        'transferDetails', (
            SELECT COALESCE(jsonb_agg(
                compl.jsonb_camel_keys(to_jsonb(tr.*))
                || jsonb_build_object(
                    'recordCreatedByFullName', NULLIF(TRIM(CONCAT_WS(' ', rcb_tr.first_name, rcb_tr.middle_name, rcb_tr.last_name)), ''),
                    'recordCreatedByRank', rcb_tr.rank_desc,
                    'recordCreatedByLoginId', rcb_tr.login_id,
                    
                    'recordUpdatedByFullName', NULLIF(TRIM(CONCAT_WS(' ', rub_tr.first_name, rub_tr.middle_name, rub_tr.last_name)), ''),
                    'recordUpdatedByRank', rub_tr.rank_desc,
                    'recordUpdatedByLoginId', rub_tr.login_id,

                    'transferFromPs', tr_f_ps.ps,
                    'transferFromPsCd', tr_f_ps.ps_cd,
                    'transferPs', tr_ps.ps,
                    'transferPsCd', tr_ps.ps_cd,
                    
                    'transferFromState', tr_f_st.state,
                    'transferFromStateCd', tr_f_st.state_cd,
                    'transferState', tr_st.state,
                    'transferStateCd', tr_st.state_cd,

                    'transferFromDistrict', tr_f_di.district,
                    'transferFromDistrictCd', tr_f_di.district_cd,
                    'transferDistrict', tr_di.district,
                    'transferDistrictCd', tr_di.district_cd
                )
                ORDER BY tr.compl_trans_srno
            ), '[]'::jsonb)
            FROM compl.t_complaint_transfer tr
            LEFT JOIN users.t_police_staff_info rcb_tr ON rcb_tr.staff_id = tr.record_created_by
            LEFT JOIN users.t_police_staff_info rub_tr ON rub_tr.staff_id = tr.record_updated_by
            LEFT JOIN mdm.m_police_station tr_f_ps ON tr_f_ps.ps_id = tr.transfer_from_ps_id
            LEFT JOIN mdm.m_police_station tr_ps ON tr_ps.ps_id = tr.transfer_ps_id
            LEFT JOIN mdm.m_state tr_f_st ON tr_f_st.state_id = tr.transfer_from_state_id
            LEFT JOIN mdm.m_state tr_st ON tr_st.state_id = tr.transfer_state_id
            LEFT JOIN mdm.m_district tr_f_di ON tr_f_di.district_id = tr.transfer_from_district_id
            LEFT JOIN mdm.m_district tr_di ON tr_di.district_id = tr.transfer_district_id
            WHERE tr.compl_reg_num = r.compl_reg_num
        ),

        -- NESTED: fireIncidentDetails
        'fireIncident', (
            SELECT COALESCE(jsonb_agg(
                compl.jsonb_camel_keys(to_jsonb(fi.*))
                || jsonb_build_object(
                    'lang', ml_fi_lang.look_up_value,
                    'recordCreatedByFullName', NULLIF(TRIM(CONCAT_WS(' ', rcb_fi.first_name, rcb_fi.middle_name, rcb_fi.last_name)), ''),
                    'recordCreatedByRank', rcb_fi.rank_desc,
                    'recordCreatedByLoginId', rcb_fi.login_id,
                    
                    'recordUpdatedByFullName', NULLIF(TRIM(CONCAT_WS(' ', rub_fi.first_name, rub_fi.middle_name, rub_fi.last_name)), ''),
                    'recordUpdatedByRank', rub_fi.rank_desc,
                    'recordUpdatedByLoginId', rub_fi.login_id
                )
                ORDER BY fi.fire_incident_srno
            ), '[]'::jsonb)
            FROM compl.t_fire_incident_details fi
            LEFT JOIN users.t_police_staff_info rcb_fi ON rcb_fi.staff_id = fi.record_created_by
            LEFT JOIN users.t_police_staff_info rub_fi ON rub_fi.staff_id = fi.record_updated_by
            LEFT JOIN mdm.m_lookup_masters ml_fi_lang ON ml_fi_lang.api_master_code = 'OFFCL_LANG' AND ml_fi_lang.look_up_code::TEXT = fi.lang_cd::TEXT AND ml_fi_lang.lang_cd = fi.lang_cd
            WHERE fi.compl_reg_num = r.compl_reg_num
        ),

        -- NESTED: missingCattleDtls
        'missCattleDetails', (
            SELECT COALESCE(jsonb_agg(
                compl.jsonb_camel_keys(to_jsonb(mc.*) - 'cattle_othr_info_srno_migr')
                || jsonb_build_object(
                    'lang', ml_mc_lang.look_up_value,
                    'cattle', ml_mc_cattle.look_up_value,
                    'gender', ml_mc_gender.look_up_value,
                    'recordCreatedByFullName', NULLIF(TRIM(CONCAT_WS(' ', rcb_mc.first_name, rcb_mc.middle_name, rcb_mc.last_name)), ''),
                    'recordCreatedByRank', rcb_mc.rank_desc,
                    'recordCreatedByLoginId', rcb_mc.login_id
                )
                ORDER BY mc.miss_cattle_srno
            ), '[]'::jsonb)
            FROM compl.t_missing_cattle_dtls mc
            LEFT JOIN mdm.m_lookup_masters ml_mc_cattle ON ml_mc_cattle.api_master_code = 'CATTLE_TYPE' AND ml_mc_cattle.look_up_code::TEXT = mc.cattle_cd::TEXT AND ml_mc_cattle.lang_cd = mc.lang_cd
            LEFT JOIN mdm.m_lookup_masters ml_mc_gender ON ml_mc_gender.api_master_code = 'GENDER' AND ml_mc_gender.look_up_code::TEXT = mc.gender_cd::TEXT AND ml_mc_gender.lang_cd = mc.lang_cd
            LEFT JOIN users.t_police_staff_info rcb_mc ON rcb_mc.staff_id = mc.record_created_by
            LEFT JOIN mdm.m_lookup_masters ml_mc_lang ON ml_mc_lang.api_master_code = 'OFFCL_LANG' AND ml_mc_lang.look_up_code::TEXT = mc.lang_cd::TEXT AND ml_mc_lang.lang_cd = mc.lang_cd
            WHERE mc.compl_reg_num = r.compl_reg_num
        ),
        'fileList', (
            SELECT COALESCE(jsonb_agg(
                compl.jsonb_camel_keys(to_jsonb(f.*) - 'complaint_file_srno_migr')
                || jsonb_build_object(
                    'lang', ml_f_lang.look_up_value,
                    'fileType', ml_f_type.look_up_value,
                    'fileSubtype', ml_f_sub.look_up_value,
                    
                    'recordCreatedByFullName', NULLIF(TRIM(CONCAT_WS(' ', rcb_f.first_name, rcb_f.middle_name, rcb_f.last_name)), ''),
                    'recordCreatedByRank', rcb_f.rank_desc,
                    'recordCreatedByLoginId', rcb_f.login_id,
                    
                    'recordUpdatedByFullName', NULLIF(TRIM(CONCAT_WS(' ', rub_f.first_name, rub_f.middle_name, rub_f.last_name)), ''),
                    'recordUpdatedByRank', rub_f.rank_desc,
                    'recordUpdatedByLoginId', rub_f.login_id
                )
                ORDER BY f.complaint_file_srno
            ), '[]'::jsonb)
            FROM compl.t_complaint_files f
            LEFT JOIN mdm.m_lookup_masters ml_f_type ON ml_f_type.api_master_code = 'UPLOAD_FILE_TYP' AND ml_f_type.look_up_code::TEXT = f.file_type_cd::TEXT AND ml_f_type.lang_cd = f.lang_cd
            LEFT JOIN mdm.m_lookup_masters ml_f_sub ON ml_f_sub.api_master_code = 'UPLOAD_FILE_SUB_TYP' AND ml_f_sub.look_up_code::TEXT = f.file_subtype_cd::TEXT AND ml_f_sub.lang_cd = f.lang_cd
            LEFT JOIN users.t_police_staff_info rcb_f ON rcb_f.staff_id = f.record_created_by
            LEFT JOIN users.t_police_staff_info rub_f ON rub_f.staff_id = f.record_updated_by
            LEFT JOIN mdm.m_lookup_masters ml_f_lang ON ml_f_lang.api_master_code = 'OFFCL_LANG' AND ml_f_lang.look_up_code::TEXT = f.lang_cd::TEXT AND ml_f_lang.lang_cd = f.lang_cd
            WHERE f.compl_reg_num = r.compl_reg_num
        )
    )
    
    -- DYNAMIC GROUPING BY personType FOR t_complaint_person_info
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
                    CASE 
                        WHEN LOWER(COALESCE(person_type_name, '')) IN ('complainant', 'complainant details') 
                             OR compl.to_camel_key(COALESCE(person_type_name, '')) = 'complainantDetails' THEN 'complainantInfo'
                        WHEN LOWER(COALESCE(person_type_name, '')) IN ('accused', 'complaint accused', 'complaint accused info') 
                             OR compl.to_camel_key(COALESCE(person_type_name, '')) = 'complaintAccused' 
                             OR (compl.to_camel_key(COALESCE(person_type_name, '')) || 'Info') = 'complaintAccusedInfo' THEN 'complaintAgainstWhom'
                        ELSE compl.to_camel_key(COALESCE(person_type_name, 'unknown')) || 'Info'
                    END AS person_type_key,

                    jsonb_agg(
                        person_json
                        ORDER BY compl_pers_srno
                    ) AS person_array

                FROM
                (
                    SELECT
                        p.compl_pers_srno,

                        (
                            compl.jsonb_camel_keys(to_jsonb(p.*) - 'compl_acc_srno_migr' - 'person_cd_migr')
                            || jsonb_build_object(

                                'lang', ml_p_offcl_lang.look_up_value,
                                'fullName', NULLIF(TRIM(CONCAT_WS(' ', p.first_name, p.middle_name, p.last_name)), ''),
                                'ageType', ml_age_type.look_up_value,
                                'personType', ml_p_type.look_up_value,
                                'relationType', ml_rel_type.look_up_value,
                                'nationality', ml_p_nat.look_up_value,
                                'landDialect', ml_p_lang.look_up_value,
                                'category', ml_p_cat.look_up_value,
                                'gender', ml_p_gender.look_up_value,

                                'recordCreatedByFullName', NULLIF(TRIM(CONCAT_WS(' ', rcb_p.first_name, rcb_p.middle_name, rcb_p.last_name)), ''),
                                'recordCreatedByRank', rcb_p.rank_desc,
                                'recordCreatedByLoginId', rcb_p.login_id,

                                'recordUpdatedByFullName', NULLIF(TRIM(CONCAT_WS(' ', rub_p.first_name, rub_p.middle_name, rub_p.last_name)), ''),
                                'recordUpdatedByRank', rub_p.rank_desc,
                                'recordUpdatedByLoginId', rub_p.login_id,

                                -- PERSON ADDRESS
                                'addressGrid', (
                                    SELECT COALESCE(
                                        jsonb_agg(
                                            compl.jsonb_camel_keys(to_jsonb(addr.*) - 'compl_person_srno_migr' - 'compl_reg_num_migr')
                                            || jsonb_build_object(
                                                
                                                'lang', ml_addr_lang.look_up_value,
                                                'homeAddress', NULLIF(TRIM(CONCAT_WS(', ', addr.address_line_1, addr.address_line_2, addr.address_line_3)), ''),
                                                'permanentAddress', CASE WHEN addr.is_perm_addr_same = true THEN NULLIF(TRIM(CONCAT_WS(', ', addr.address_line_1, addr.address_line_2, addr.address_line_3, msdv.village_name, addr.tehsil, msd.sub_district, ml_addr_c.look_up_value, addr.pincode)), '') ELSE NULL END,
                                                'communicationAddress', CASE WHEN addr.is_comm_addr = true THEN NULLIF(TRIM(CONCAT_WS(', ', addr.address_line_1, addr.address_line_2, addr.address_line_3, msdv.village_name, addr.tehsil, msd.sub_district, ml_addr_c.look_up_value, addr.pincode)), '') ELSE NULL END,
                                                
                                                'addressType', ml_add_typ.look_up_value,
                                                'subDistrict', msd.sub_district,
                                                'village', msdv.village_name,
                                                'country', ml_addr_c.look_up_value,
                                                
                                                'state', st_addr.state,
                                                'stateCd', st_addr.state_cd,
                                                'lgDistrict', mld.lg_district_name,
                                                'ps', mps_addr.ps,
                                                'psCd', mps_addr.ps_cd,

                                                'recordCreatedByFullName', NULLIF(TRIM(CONCAT_WS(' ', rcb_addr.first_name, rcb_addr.middle_name, rcb_addr.last_name)), ''),
                                                'recordCreatedByRank', rcb_addr.rank_desc,
                                                'recordCreatedByLoginId', rcb_addr.login_id,

                                                'recordUpdatedByFullName', NULLIF(TRIM(CONCAT_WS(' ', rub_addr.first_name, rub_addr.middle_name, rub_addr.last_name)), ''),
                                                'recordUpdatedByRank', rub_addr.rank_desc,
                                                'recordUpdatedByLoginId', rub_addr.login_id
                                            )
                                            ORDER BY addr.compl_addr_srno
                                        ), '[]'::jsonb
                                    )
                                    FROM compl.t_complaint_person_addr addr
                                    LEFT JOIN mdm.m_lookup_masters ml_add_typ ON ml_add_typ.api_master_code='ADD_TYP' AND ml_add_typ.look_up_code::TEXT=addr.address_type_cd::TEXT AND ml_add_typ.lang_cd = addr.lang_cd
                                    LEFT JOIN mdm.m_sub_district msd ON msd.sub_district_cd=addr.sub_district_cd AND msd.lang_cd = addr.lang_cd
                                    LEFT JOIN mdm.m_subdist_villages msdv ON msdv.village_cd=addr.village_cd AND msdv.lang_cd = addr.lang_cd
                                    LEFT JOIN mdm.m_lookup_masters ml_addr_c ON ml_addr_c.api_master_code='NATIONALITY' AND ml_addr_c.look_up_code::TEXT=addr.country_cd::TEXT AND ml_addr_c.lang_cd = addr.lang_cd
                                    LEFT JOIN mdm.m_state st_addr ON st_addr.state_id=addr.state_id AND st_addr.lang_cd = addr.lang_cd
                                    LEFT JOIN mdm.m_lgd_district mld ON mld.lg_act_dist_cd=addr.lg_district_cd AND mld.lang_cd=addr.lang_cd
                                    LEFT JOIN mdm.m_police_station mps_addr ON mps_addr.ps_id=addr.ps_id AND mps_addr.lang_cd = addr.lang_cd
                                    LEFT JOIN users.t_police_staff_info rcb_addr ON rcb_addr.staff_id=addr.record_created_by
                                    LEFT JOIN users.t_police_staff_info rub_addr ON rub_addr.staff_id=addr.record_updated_by
                                    LEFT JOIN mdm.m_lookup_masters ml_addr_lang ON ml_addr_lang.api_master_code='OFFCL_LANG' AND ml_addr_lang.look_up_code::TEXT=addr.lang_cd::TEXT AND ml_addr_lang.lang_cd = addr.lang_cd
                                    WHERE addr.compl_pers_srno = p.compl_pers_srno
                                ),

                                -- PERSON NATIONAL ID
                                'idList', (
                                    SELECT COALESCE(
                                        jsonb_agg(
                                            compl.jsonb_camel_keys(to_jsonb(nat.*) - 'national_id_srno_migr' - 'person_cd_migr')
                                            || jsonb_build_object(
                                                'lang', ml_nat_lang.look_up_value,
                                                'nationalIdType', ml_nat_id_typ.look_up_value,

                                                'recordCreatedByFullName', NULLIF(TRIM(CONCAT_WS(' ', rcb_nat.first_name, rcb_nat.middle_name, rcb_nat.last_name)), ''),
                                                'recordCreatedByRank', rcb_nat.rank_desc,
                                                'recordCreatedByLoginId', rcb_nat.login_id,

                                                'recordUpdatedByFullName', NULLIF(TRIM(CONCAT_WS(' ', rub_nat.first_name, rub_nat.middle_name, rub_nat.last_name)), ''),
                                                'recordUpdatedByRank', rub_nat.rank_desc,
                                                'recordUpdatedByLoginId', rub_nat.login_id
                                            )
                                            ORDER BY nat.national_id_srno
                                        ), '[]'::jsonb
                                     )
                                    FROM compl.t_complaint_person_national_id nat
                                    LEFT JOIN mdm.m_lookup_masters ml_nat_id_typ ON ml_nat_id_typ.api_master_code IN ('NAT_ID_DOC_TYPE', 'NTNL_ID_DOC_TYP') AND ml_nat_id_typ.look_up_code::TEXT=nat.national_id_type_cd::TEXT AND ml_nat_id_typ.lang_cd = nat.lang_cd
                                    LEFT JOIN users.t_police_staff_info rcb_nat ON rcb_nat.staff_id=nat.record_created_by
                                    LEFT JOIN users.t_police_staff_info rub_nat ON rub_nat.staff_id=nat.record_updated_by
                                     LEFT JOIN mdm.m_lookup_masters ml_nat_lang ON ml_nat_lang.api_master_code='OFFCL_LANG' AND ml_nat_lang.look_up_code::TEXT=nat.lang_cd::TEXT AND ml_nat_lang.lang_cd = nat.lang_cd
                                    WHERE nat.compl_pers_srno = p.compl_pers_srno
                                )
                            )
                        ) AS person_json,

                        ml_p_type.look_up_value AS person_type_name

                    FROM compl.t_complaint_person_info p
                    
                    -- Person Type mapping as defined in YAML configuration
                    LEFT JOIN mdm.m_lookup_masters ml_p_type ON ml_p_type.api_master_code='MODULE_PERS_TYPES' AND ml_p_type.look_up_code::TEXT=p.person_type::TEXT AND ml_p_type.lang_cd = p.lang_cd
                    
                    LEFT JOIN mdm.m_lookup_masters ml_age_type ON ml_age_type.api_master_code='AGE_PANEL_TYPE' AND ml_age_type.look_up_code::TEXT=p.age_type_cd::TEXT AND ml_age_type.lang_cd = p.lang_cd
                    LEFT JOIN mdm.m_lookup_masters ml_rel_type ON ml_rel_type.api_master_code='RELATION_TYP' AND ml_rel_type.look_up_code::TEXT=p.relation_type_cd::TEXT AND ml_rel_type.lang_cd = p.lang_cd
                    LEFT JOIN mdm.m_lookup_masters ml_p_nat ON ml_p_nat.api_master_code='NATIONALITY' AND ml_p_nat.look_up_code::TEXT=p.nationality_cd::TEXT AND ml_p_nat.lang_cd = p.lang_cd
                    LEFT JOIN mdm.m_lookup_masters ml_p_lang ON ml_p_lang.api_master_code='LANG_DIALECTS' AND ml_p_lang.look_up_code::TEXT=p.lang_dialect_cd::TEXT AND ml_p_lang.lang_cd = p.lang_cd
                    LEFT JOIN mdm.m_lookup_masters ml_p_cat ON ml_p_cat.api_master_code='CATEGORY' AND ml_p_cat.look_up_code::TEXT=p.category_cd::TEXT AND ml_p_cat.lang_cd = p.lang_cd
                    LEFT JOIN mdm.m_lookup_masters ml_p_gender ON ml_p_gender.api_master_code='GENDER' AND ml_p_gender.look_up_code::TEXT=p.gender_cd::TEXT AND ml_p_gender.lang_cd = p.lang_cd
                    
                    LEFT JOIN users.t_police_staff_info rcb_p ON rcb_p.staff_id=p.record_created_by
                    LEFT JOIN users.t_police_staff_info rub_p ON rub_p.staff_id=p.record_updated_by
                    LEFT JOIN mdm.m_lookup_masters ml_p_offcl_lang ON ml_p_offcl_lang.api_master_code='OFFCL_LANG' AND ml_p_offcl_lang.look_up_code::TEXT=p.lang_cd::TEXT AND ml_p_offcl_lang.lang_cd = p.lang_cd

                    WHERE p.compl_reg_num = p_compl_reg_num
                ) person_data

                GROUP BY person_type_name
            ) grouped_persons
        ),
        '{}'::jsonb
    )
    || (
        SELECT CASE
            WHEN EXISTS (
                SELECT 1 
                FROM compl.t_missing_cattle_dtls mc 
                WHERE mc.compl_reg_num = r.compl_reg_num
            ) THEN (
                SELECT jsonb_build_object(
                    'ownerDetails', owner_array
                )
                FROM (
    SELECT COALESCE(
        jsonb_agg(
            compl.jsonb_camel_keys(
                to_jsonb(p.*)
                - 'compl_acc_srno_migr'
                - 'person_cd_migr'
            )
            || jsonb_build_object(
                'lang', ml_p_offcl_lang.look_up_value,
                'fullName', NULLIF(
                    TRIM(
                        CONCAT_WS(
                            ' ',
                            p.first_name,
                            p.middle_name,
                            p.last_name
                        )
                    ),
                    ''
                ),
                'ageType', ml_age_type.look_up_value,
                'personType', ml_p_type.look_up_value,
                'relationType', ml_rel_type.look_up_value,
                'nationality', ml_p_nat.look_up_value,
                'landDialect', ml_p_lang.look_up_value,
                'category', ml_p_cat.look_up_value,
                'gender', ml_p_gender.look_up_value,

                'recordCreatedByFullName',
                NULLIF(
                    TRIM(
                        CONCAT_WS(
                            ' ',
                            rcb_p.first_name,
                            rcb_p.middle_name,
                            rcb_p.last_name
                        )
                    ),
                    ''
                ),

                'recordCreatedByRank', rcb_p.rank_desc,
                'recordCreatedByLoginId', rcb_p.login_id,

                'recordUpdatedByFullName',
                NULLIF(
                    TRIM(
                        CONCAT_WS(
                            ' ',
                            rub_p.first_name,
                            rub_p.middle_name,
                            rub_p.last_name
                        )
                    ),
                    ''
                ),

                'recordUpdatedByRank', rub_p.rank_desc,
                'recordUpdatedByLoginId', rub_p.login_id

                -- your addressGrid here

                -- your idList here
            )
        ),
        '[]'::jsonb
    ) AS owner_array

    FROM compl.t_complaint_person_info p

    LEFT JOIN mdm.m_lookup_masters ml_p_type
        ON ml_p_type.api_master_code = 'MODULE_PERS_TYPES'
        AND ml_p_type.look_up_code::TEXT = p.person_type::TEXT
        AND ml_p_type.lang_cd = p.lang_cd

    LEFT JOIN mdm.m_lookup_masters ml_age_type
        ON ml_age_type.api_master_code = 'AGE_PANEL_TYPE'
        AND ml_age_type.look_up_code::TEXT = p.age_type_cd::TEXT
        AND ml_age_type.lang_cd = p.lang_cd

    LEFT JOIN mdm.m_lookup_masters ml_rel_type
        ON ml_rel_type.api_master_code = 'RELATION_TYP'
        AND ml_rel_type.look_up_code::TEXT = p.relation_type_cd::TEXT
        AND ml_rel_type.lang_cd = p.lang_cd

    LEFT JOIN mdm.m_lookup_masters ml_p_nat
        ON ml_p_nat.api_master_code = 'NATIONALITY'
        AND ml_p_nat.look_up_code::TEXT = p.nationality_cd::TEXT
        AND ml_p_nat.lang_cd = p.lang_cd

    LEFT JOIN mdm.m_lookup_masters ml_p_lang
        ON ml_p_lang.api_master_code = 'LANG_DIALECTS'
        AND ml_p_lang.look_up_code::TEXT = p.lang_dialect_cd::TEXT
        AND ml_p_lang.lang_cd = p.lang_cd

    LEFT JOIN mdm.m_lookup_masters ml_p_cat
        ON ml_p_cat.api_master_code = 'CATEGORY'
        AND ml_p_cat.look_up_code::TEXT = p.category_cd::TEXT
        AND ml_p_cat.lang_cd = p.lang_cd

    LEFT JOIN mdm.m_lookup_masters ml_p_gender
        ON ml_p_gender.api_master_code = 'GENDER'
        AND ml_p_gender.look_up_code::TEXT = p.gender_cd::TEXT
        AND ml_p_gender.lang_cd = p.lang_cd

    LEFT JOIN users.t_police_staff_info rcb_p
        ON rcb_p.staff_id = p.record_created_by

    LEFT JOIN users.t_police_staff_info rub_p
        ON rub_p.staff_id = p.record_updated_by

    LEFT JOIN mdm.m_lookup_masters ml_p_offcl_lang
        ON ml_p_offcl_lang.api_master_code = 'OFFCL_LANG'
        AND ml_p_offcl_lang.look_up_code::TEXT = p.lang_cd::TEXT
        AND ml_p_offcl_lang.lang_cd = p.lang_cd

    WHERE p.compl_reg_num = r.compl_reg_num
) sub
            )
            ELSE '{}'::jsonb
        END
    )

    INTO v_result

FROM compl.t_complaint_reg_info r

-- Standard lookup joins for root table 
LEFT JOIN users.t_police_staff_info rcb_r ON rcb_r.staff_id = r.record_created_by
LEFT JOIN users.t_police_staff_info rub_r ON rub_r.staff_id = r.record_updated_by
LEFT JOIN mdm.m_lookup_masters ml_compl_nature ON ml_compl_nature.api_master_code = 'COMPL_NATURE' AND ml_compl_nature.look_up_code::TEXT = r.complaint_nature_cd::TEXT AND ml_compl_nature.lang_cd = r.lang_cd
LEFT JOIN mdm.m_lookup_masters ml_info_source ON ml_info_source.api_master_code IN ('COMPL_INFO_SRC', 'INFO_SOURCE') AND ml_info_source.look_up_code::TEXT = r.information_source_cd::TEXT AND ml_info_source.lang_cd = r.lang_cd
LEFT JOIN mdm.m_lookup_masters ml_soc_med ON ml_soc_med.api_master_code IN ('SOCIAL_MEDIA_TYPE', 'SOCIAL_MEDIA_TYP') AND ml_soc_med.look_up_code::TEXT = r.info_src_soc_med_cd::TEXT AND ml_soc_med.lang_cd = r.lang_cd
LEFT JOIN users.t_police_staff_info cog_pol ON cog_pol.staff_id = r.cognz_police_cd
LEFT JOIN mdm.m_lookup_masters ml_court_type ON ml_court_type.api_master_code = 'COURT_TYP' AND ml_court_type.look_up_code::TEXT = r.src_court_type_cd::TEXT AND ml_court_type.lang_cd = r.lang_cd
LEFT JOIN mdm.m_lookup_masters ml_receipt_mode ON ml_receipt_mode.api_master_code IN ('RECIEPT_MODE', 'RECEPTION_MODE') AND ml_receipt_mode.look_up_code::TEXT = r.receipt_mode_cd::TEXT AND ml_receipt_mode.lang_cd = r.lang_cd
LEFT JOIN mdm.m_lookup_masters ml_compl_status ON ml_compl_status.api_master_code IN ('COMPL_NCR_STATUS', 'COMPLAINT_STATUS') AND ml_compl_status.look_up_code::TEXT = r.complaint_status_cd::TEXT AND ml_compl_status.lang_cd = r.lang_cd
LEFT JOIN mdm.m_police_station mps ON mps.ps_id = r.submit_ps_id AND mps.lang_cd = r.lang_cd
LEFT JOIN mdm.m_state st ON st.state_id = r.submit_state_id AND st.lang_cd = r.lang_cd
LEFT JOIN mdm.m_district di ON di.district_id = r.submit_district_id AND di.lang_cd = r.lang_cd
LEFT JOIN mdm.m_lookup_masters ml_lang ON ml_lang.api_master_code = 'OFFCL_LANG' AND ml_lang.look_up_code::TEXT = r.lang_cd::TEXT AND ml_lang.lang_cd = r.lang_cd

WHERE r.compl_reg_num = p_compl_reg_num;

-- The COALESCE ensures we safely return an empty object '{}' instead of a pure SQL NULL 
-- if the query yields no results.
RETURN COALESCE(v_result, '{}'::jsonb);

END;
$$;