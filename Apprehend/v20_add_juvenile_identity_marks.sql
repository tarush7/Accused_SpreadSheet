-- VERSION 20 REPLACEMENT BLOCK: JUVENILE IDENTITY MARKS
--
-- Inside physicalDescription, replace exactly:
--
--     'identityMarkList',
--         '[]'::JSONB,
--
-- with the block below. Keep the following physicalFeatureList placeholder.

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

-- ========================================================================
-- VERIFICATION 1: inspect identityMarkList for report 57.
-- Expected source count: 2.
-- ========================================================================

SELECT
    report_object -> 'physicalDescription' -> 'identityMarkList'
        AS identity_mark_list
FROM jsonb_array_elements(
    apprehend.get_apprehend_memo_json(
        3810100126001865
    ) -> 'juvenileBackgroundReports'
) AS report_object
WHERE report_object ->> 'bgReportSrno' = '57';

-- ========================================================================
-- VERIFICATION 2: compare source and function counts for reports 57/58.
-- Expected: report 57 = 2; report 58 = 0.
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
        FROM apprehend.t_juv_identity_marks source_mark
        WHERE source_mark.bg_report_srno =
              (function_reports.report_object ->> 'bgReportSrno')::BIGINT
    ) AS source_count,

    jsonb_array_length(
        function_reports.report_object ->
            'physicalDescription' -> 'identityMarkList'
    ) AS function_count

FROM function_reports
ORDER BY
    (function_reports.report_object ->> 'bgReportSrno')::BIGINT;

-- ========================================================================
-- VERIFICATION 3: inspect both Java-enriched labels.
-- ========================================================================

SELECT
    identity_object ->> 'id' AS id,
    identity_object ->> 'idMarksTypeCd' AS id_marks_type_cd,
    identity_object ->> 'idMarksType' AS id_marks_type,
    identity_object ->> 'bodyPartLocCd' AS body_part_loc_cd,
    identity_object ->> 'bodyPartLoc' AS body_part_loc,
    identity_object ->> 'tattooMarkDesc' AS tattoo_mark_desc
FROM jsonb_array_elements(
    apprehend.get_apprehend_memo_json(
        3810100126001865
    ) -> 'juvenileBackgroundReports'
) AS report_object
CROSS JOIN LATERAL jsonb_array_elements(
    report_object -> 'physicalDescription' -> 'identityMarkList'
) AS identity_object
WHERE report_object ->> 'bgReportSrno' = '57'
ORDER BY (identity_object ->> 'id')::BIGINT;
