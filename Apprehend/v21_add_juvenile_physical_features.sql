-- VERSION 21 REPLACEMENT BLOCK: JUVENILE PHYSICAL FEATURES
--
-- Inside physicalDescription, replace exactly:
--
--     'physicalFeatureList',
--         '[]'::JSONB
--
-- with the block below.

            'physicalFeatureList',
                (
                    SELECT COALESCE(
                        jsonb_agg(
                            jsonb_build_object(
                                'id',
                                    physical_feature.juv_phy_feat_srno::TEXT,

                                'juvenileVid',
                                    physical_feature.juvenile_vid,

                                'langCd',
                                    physical_feature.lang_cd,

                                'bgReportSrno',
                                    physical_feature.bg_report_srno::TEXT,

                                -- Java globally serializes Long IDs as text.
                                'juvenileSrno',
                                    physical_feature.juvenile_srno::TEXT,

                                'phyFeatCategoryCd',
                                    physical_feature.phy_feat_category_cd,

                                'phyFeatureMajCd',
                                    physical_feature.phy_feature_maj_cd,

                                'phyFeatureMinCd',
                                    physical_feature.phy_feature_min_cd,

                                -- Java keeps the category and major labels
                                -- stored on the child entity.
                                'phyFeatCategory',
                                    physical_feature.phy_feat_category,

                                'phyFeatureMaj',
                                    physical_feature.phy_feature_major,

                                -- Java overwrites the stored minor label by
                                -- looking up phyFeatureMinCd through
                                -- Constants.PHY_FEAT = PHYSCL_FEATURES.
                                'phyFeatureMin',
                                    CASE
                                        WHEN physical_feature.phy_feature_min_cd
                                             IS NOT NULL
                                        THEN (
                                            SELECT lookup_master.look_up_value
                                            FROM mdm.m_lookup_masters
                                                lookup_master
                                            WHERE lookup_master.api_master_code =
                                                  'PHYSCL_FEATURES'
                                              AND lookup_master.look_up_code::TEXT =
                                                  physical_feature.phy_feature_min_cd::TEXT
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
                            ORDER BY physical_feature.juv_phy_feat_srno
                        ),
                        '[]'::JSONB
                    )
                    FROM apprehend.t_juv_phy_feature physical_feature
                    WHERE physical_feature.bg_report_srno =
                          report.bg_report_srno
                )

-- ========================================================================
-- VERIFICATION 1: inspect physicalFeatureList for report 57.
-- Expected source count: 2.
-- ========================================================================

SELECT
    report_object -> 'physicalDescription' -> 'physicalFeatureList'
        AS physical_feature_list
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
        FROM apprehend.t_juv_phy_feature source_feature
        WHERE source_feature.bg_report_srno =
              (function_reports.report_object ->> 'bgReportSrno')::BIGINT
    ) AS source_count,

    jsonb_array_length(
        function_reports.report_object ->
            'physicalDescription' -> 'physicalFeatureList'
    ) AS function_count

FROM function_reports
ORDER BY
    (function_reports.report_object ->> 'bgReportSrno')::BIGINT;

-- ========================================================================
-- VERIFICATION 3: inspect stored category/major and enriched minor labels.
-- ========================================================================

SELECT
    feature_object ->> 'id' AS id,
    feature_object ->> 'phyFeatCategoryCd' AS category_cd,
    feature_object ->> 'phyFeatCategory' AS category,
    feature_object ->> 'phyFeatureMajCd' AS major_cd,
    feature_object ->> 'phyFeatureMaj' AS major,
    feature_object ->> 'phyFeatureMinCd' AS minor_cd,
    feature_object ->> 'phyFeatureMin' AS minor
FROM jsonb_array_elements(
    apprehend.get_apprehend_memo_json(
        3810100126001865
    ) -> 'juvenileBackgroundReports'
) AS report_object
CROSS JOIN LATERAL jsonb_array_elements(
    report_object -> 'physicalDescription' -> 'physicalFeatureList'
) AS feature_object
WHERE report_object ->> 'bgReportSrno' = '57'
ORDER BY (feature_object ->> 'id')::BIGINT;
