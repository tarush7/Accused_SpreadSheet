-- VERSION 14 PATCH
--
-- Add this block to apprehend.get_apprehend_memo_json(BIGINT)
-- immediately before the existing CommonParams block that starts with:
--
--     || jsonb_build_object(
--            'staffId', NULL,
--
-- This first juvenile-background version deliberately includes only the
-- parent report fields. The six child-table collections will be added and
-- verified separately in later versions.

        ||

        -- ================================================================
        -- PART 13: JUVENILE SOCIAL-BACKGROUND REPORTS
        --
        -- One apprehension can have more than one bg_report_srno, so this
        -- property must be a JSON array rather than one JSON object.
        -- ================================================================
        jsonb_build_object(
            'juvenileBackgroundReports',
            COALESCE(
                (
                    SELECT jsonb_agg(
                        jsonb_build_object(
                            'bgReportSrno',
                                report.bg_report_srno::TEXT,

                            'apprehendSrno',
                                report.apprehend_srno::TEXT,

                            'firRegNum',
                                report.fir_reg_num::TEXT,

                            'recordStatus',
                                report.record_status,

                            'recordCreatedOn',
                                report.record_created_on,

                            'recordCreatedBy',
                                report.record_created_by::TEXT
                        )
                        ORDER BY report.bg_report_srno
                    )
                    FROM apprehend.t_juv_background_report report
                    WHERE report.apprehend_srno = memo.apprehend_srno
                ),
                '[]'::JSONB
            )
        )

-- ========================================================================
-- VERIFICATION 1: inspect the two juvenile reports for the enriched memo.
-- Expected bgReportSrno values: "57" and "58".
-- ========================================================================

SELECT apprehend.get_apprehend_memo_json(
    3810100126001865
) -> 'juvenileBackgroundReports' AS juvenile_background_reports;

-- ========================================================================
-- VERIFICATION 2: source count must equal function count.
-- ========================================================================

SELECT
    (
        SELECT COUNT(*)
        FROM apprehend.t_juv_background_report report
        WHERE report.apprehend_srno = 3810100126001865
    ) AS source_count,

    jsonb_array_length(
        apprehend.get_apprehend_memo_json(
            3810100126001865
        ) -> 'juvenileBackgroundReports'
    ) AS function_count;

-- ========================================================================
-- VERIFICATION 3: confirm it is always a JSON array when the memo exists.
-- ========================================================================

SELECT jsonb_typeof(
    apprehend.get_apprehend_memo_json(
        3810100126001865
    ) -> 'juvenileBackgroundReports'
) AS juvenile_background_type;
