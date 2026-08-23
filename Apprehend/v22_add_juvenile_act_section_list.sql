-- VERSION 22 INSERTION BLOCK: JUVENILE BACKGROUND ACT/SECTION LIST
--
-- Inside the final jsonb_build_object(...) for each object in
-- juvenileBackgroundReports, paste this pair after physicalDescription and
-- before the audit fields (recordStatus, recordCreatedOn, recordCreatedBy).
--
-- Java rule preserved:
--   1. report.fir_reg_num is present -> FIR sections
--   2. otherwise use memo.cicl_gd_num -> CICL/GD sections
--   3. neither key is present -> []

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

-- ========================================================================
-- VERIFICATION 1: inspect GD act sections for reports 57 and 58.
-- Both reports use memo.cicl_gd_num because firRegNum is null.
-- ========================================================================

SELECT
    report_object ->> 'bgReportSrno' AS bg_report_srno,
    report_object ->> 'firRegNum' AS fir_reg_num,
    jsonb_array_length(report_object -> 'actSectionList') AS act_count,
    report_object -> 'actSectionList' AS act_section_list
FROM jsonb_array_elements(
    apprehend.get_apprehend_memo_json(
        3810100126001865
    ) -> 'juvenileBackgroundReports'
) AS report_object
ORDER BY (report_object ->> 'bgReportSrno')::BIGINT;

-- ========================================================================
-- VERIFICATION 2: GD helper count must match every GD report's count.
-- ========================================================================

WITH helper_count AS (
    SELECT COUNT(*) AS row_count
    FROM apprehend.get_gd_act_section_list(
        '381010012007202617540017'
    )
),
function_reports AS (
    SELECT report_object
    FROM jsonb_array_elements(
        apprehend.get_apprehend_memo_json(
            3810100126001865
        ) -> 'juvenileBackgroundReports'
    ) AS report_object
)
SELECT
    function_reports.report_object ->> 'bgReportSrno' AS bg_report_srno,
    helper_count.row_count AS helper_count,
    jsonb_array_length(
        function_reports.report_object -> 'actSectionList'
    ) AS function_count
FROM function_reports
CROSS JOIN helper_count
ORDER BY
    (function_reports.report_object ->> 'bgReportSrno')::BIGINT;

-- ========================================================================
-- VERIFICATION 3: FIR fixture.
-- Reports 49, 50, 51 and 53 use FIR 38101001250090 if the parent memo exists.
-- ========================================================================

WITH function_reports AS (
    SELECT report_object
    FROM jsonb_array_elements(
        apprehend.get_apprehend_memo_json(
            38101001250019
        ) -> 'juvenileBackgroundReports'
    ) AS report_object
)
SELECT
    function_reports.report_object ->> 'bgReportSrno' AS bg_report_srno,
    function_reports.report_object ->> 'firRegNum' AS fir_reg_num,

    (
        SELECT COUNT(*)
        FROM fir.t_alter_act_section source_section
        WHERE source_section.fir_reg_num =
              (function_reports.report_object ->> 'firRegNum')::BIGINT
          AND source_section.record_status = 'C'
    ) AS source_count,

    jsonb_array_length(
        function_reports.report_object -> 'actSectionList'
    ) AS function_count

FROM function_reports
ORDER BY
    (function_reports.report_object ->> 'bgReportSrno')::BIGINT;
