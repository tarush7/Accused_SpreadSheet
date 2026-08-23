-- VERSION 19 INSERTION BLOCK: PHYSICAL DESCRIPTION + DRESS LIST
--
-- Inside the final jsonb_build_object(...) for each object in
-- juvenileBackgroundReports, paste this pair after phyAbuse and before the
-- audit fields (recordStatus, recordCreatedOn, recordCreatedBy).

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
                '[]'::JSONB,

            -- Java @JsonProperty renames physicalFeaturesList to this key.
            'physicalFeatureList',
                '[]'::JSONB
        ),

-- ========================================================================
-- VERIFICATION 1: inspect physicalDescription for report 57.
-- ========================================================================

SELECT report_object -> 'physicalDescription' AS physical_description
FROM jsonb_array_elements(
    apprehend.get_apprehend_memo_json(
        3810100126001865
    ) -> 'juvenileBackgroundReports'
) AS report_object
WHERE report_object ->> 'bgReportSrno' = '57';

-- ========================================================================
-- VERIFICATION 2: compare dress counts for reports 57 and 58.
-- Expected: report 57 = 1; report 58 = 0.
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
        FROM apprehend.t_juv_dress source_dress
        WHERE source_dress.bg_report_srno =
              (function_reports.report_object ->> 'bgReportSrno')::BIGINT
    ) AS source_count,

    jsonb_array_length(
        function_reports.report_object ->
            'physicalDescription' -> 'dressTypeList'
    ) AS function_count

FROM function_reports
ORDER BY
    (function_reports.report_object ->> 'bgReportSrno')::BIGINT;

-- ========================================================================
-- VERIFICATION 3: inspect master labels and the dress object.
-- ========================================================================

SELECT
    report_object -> 'physicalDescription' ->> 'bodyBuildType'
        AS body_build_type,
    report_object -> 'physicalDescription' ->> 'bodyComplexionType'
        AS body_complexion_type,
    dress_object
FROM jsonb_array_elements(
    apprehend.get_apprehend_memo_json(
        3810100126001865
    ) -> 'juvenileBackgroundReports'
) AS report_object
CROSS JOIN LATERAL jsonb_array_elements(
    report_object -> 'physicalDescription' -> 'dressTypeList'
) AS dress_object
WHERE report_object ->> 'bgReportSrno' = '57'
ORDER BY (dress_object ->> 'id')::BIGINT;
