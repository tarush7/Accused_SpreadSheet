-- VERSION 17 INSERTION BLOCK: JUVENILE FAMILY DETAILS
--
-- Inside the final jsonb_build_object(...) for each object in
-- juvenileBackgroundReports, paste this pair after fileList and before the
-- audit fields (recordStatus, recordCreatedOn, recordCreatedBy).

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

-- ========================================================================
-- VERIFICATION 1: inspect familyDtls for report 57.
-- Expected count from the source audit: 2.
-- ========================================================================

SELECT report_object -> 'familyDtls' AS family_details
FROM jsonb_array_elements(
    apprehend.get_apprehend_memo_json(
        3810100126001865
    ) -> 'juvenileBackgroundReports'
) AS report_object
WHERE report_object ->> 'bgReportSrno' = '57';

-- ========================================================================
-- VERIFICATION 2: compare source and function counts for reports 57/58.
-- Expected: report 57 = 2; report 58 = 1.
-- ========================================================================

WITH function_reports AS (
    SELECT report_object
    FROM jsonb_array_elements(
        apprehend.get_apprehend_memo_json(
            3810100126001865
        ) -> 'juvenileBackgroundReports'
    ) AS report_object
)
SELECT
    function_reports.report_object ->> 'bgReportSrno' AS bg_report_srno,

    (
        SELECT COUNT(*)
        FROM apprehend.t_juv_family_dtls source_family
        WHERE source_family.bg_report_srno =
              (function_reports.report_object ->> 'bgReportSrno')::BIGINT
    ) AS source_count,

    jsonb_array_length(
        function_reports.report_object -> 'familyDtls'
    ) AS function_count

FROM function_reports
ORDER BY
    (function_reports.report_object ->> 'bgReportSrno')::BIGINT;

-- ========================================================================
-- VERIFICATION 3: ensure lookup labels and DOB format are inspectable.
-- ========================================================================

SELECT
    family_object ->> 'id' AS id,
    family_object ->> 'relationTypeCd' AS relation_type_cd,
    family_object ->> 'relationType' AS relation_type,
    family_object ->> 'occupationCd' AS occupation_cd,
    family_object ->> 'occupation' AS occupation,
    family_object ->> 'dob' AS dob
FROM jsonb_array_elements(
    apprehend.get_apprehend_memo_json(
        3810100126001865
    ) -> 'juvenileBackgroundReports'
) AS report_object
CROSS JOIN LATERAL jsonb_array_elements(
    report_object -> 'familyDtls'
) AS family_object
WHERE report_object ->> 'bgReportSrno' = '57'
ORDER BY (family_object ->> 'id')::BIGINT;
