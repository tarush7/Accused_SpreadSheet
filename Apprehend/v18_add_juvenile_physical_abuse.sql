-- VERSION 18 INSERTION BLOCK: JUVENILE PHYSICAL-ABUSE DETAILS
--
-- Inside the final jsonb_build_object(...) for each object in
-- juvenileBackgroundReports, paste this pair after familyDtls and before the
-- audit fields (recordStatus, recordCreatedOn, recordCreatedBy).

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

-- ========================================================================
-- VERIFICATION 1: inspect phyAbuse for report 57.
-- Expected source count: 1.
-- ========================================================================

SELECT report_object -> 'phyAbuse' AS physical_abuse
FROM jsonb_array_elements(
    apprehend.get_apprehend_memo_json(
        3810100126001865
    ) -> 'juvenileBackgroundReports'
) AS report_object
WHERE report_object ->> 'bgReportSrno' = '57';

-- ========================================================================
-- VERIFICATION 2: compare source and function counts for reports 57/58.
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
        FROM apprehend.t_juv_phy_abuse source_abuse
        WHERE source_abuse.bg_report_srno =
              (function_reports.report_object ->> 'bgReportSrno')::BIGINT
    ) AS source_count,

    jsonb_array_length(
        function_reports.report_object -> 'phyAbuse'
    ) AS function_count

FROM function_reports
ORDER BY
    (function_reports.report_object ->> 'bgReportSrno')::BIGINT;

-- ========================================================================
-- VERIFICATION 3: verify Java-parity fields and JSON types for report 57.
-- ========================================================================

SELECT
    abuse_object ->> 'id' AS id,
    abuse_object -> 'juvAbuseSrno' AS juv_abuse_srno,
    abuse_object -> 'juvenileVid' AS juvenile_vid,
    abuse_object ->> 'juvenileSrno' AS juvenile_srno,
    abuse_object -> 'abuseTypeCd' AS abuse_type_cd,
    abuse_object -> 'abuseType' AS abuse_type,
    abuse_object ->> 'abuseRemarks' AS abuse_remarks
FROM jsonb_array_elements(
    apprehend.get_apprehend_memo_json(
        3810100126001865
    ) -> 'juvenileBackgroundReports'
) AS report_object
CROSS JOIN LATERAL jsonb_array_elements(
    report_object -> 'phyAbuse'
) AS abuse_object
WHERE report_object ->> 'bgReportSrno' = '57'
ORDER BY (abuse_object ->> 'id')::BIGINT;
