-- VERSION 24: FINAL JUVENILE-BACKGROUND VALIDATION
--
-- This file does not modify the function. Run these read-only checks after
-- compiling the final apprehend.get_apprehend_memo_json(BIGINT).

-- ========================================================================
-- CHECK 1: one public function signature only
-- ========================================================================

SELECT
    procedure.oid::REGPROCEDURE::TEXT AS function_signature
FROM pg_proc procedure
JOIN pg_namespace function_schema
  ON function_schema.oid = procedure.pronamespace
WHERE function_schema.nspname = 'apprehend'
  AND procedure.proname = 'get_apprehend_memo_json'
ORDER BY procedure.oid::REGPROCEDURE::TEXT;

-- Expected required signature:
-- apprehend.get_apprehend_memo_json(bigint)

-- ========================================================================
-- CHECK 2: top-level juvenile array exists and contains reports 57/58
-- ========================================================================

SELECT
    jsonb_typeof(
        apprehend.get_apprehend_memo_json(
            3810100126001865
        ) -> 'juvenileBackgroundReports'
    ) AS background_type,

    jsonb_array_length(
        apprehend.get_apprehend_memo_json(
            3810100126001865
        ) -> 'juvenileBackgroundReports'
    ) AS background_count;

-- Expected: array, 2

-- ========================================================================
-- CHECK 3: compare all seven juvenile tables and both extra Java arrays
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

    -- Table 1 is the parent report itself. Existence is represented by this
    -- object in juvenileBackgroundReports.
    1 AS background_report_count,

    -- Table 2: t_jcl_background_files
    (
        SELECT COUNT(*)
        FROM apprehend.t_jcl_background_files source
        WHERE source.bg_report_srno =
              (function_reports.report_object ->> 'bgReportSrno')::BIGINT
    ) AS source_file_count,
    jsonb_array_length(
        function_reports.report_object -> 'fileList'
    ) AS json_file_count,

    -- Table 3: t_juv_family_dtls
    (
        SELECT COUNT(*)
        FROM apprehend.t_juv_family_dtls source
        WHERE source.bg_report_srno =
              (function_reports.report_object ->> 'bgReportSrno')::BIGINT
    ) AS source_family_count,
    jsonb_array_length(
        function_reports.report_object -> 'familyDtls'
    ) AS json_family_count,

    -- Table 4: t_juv_phy_abuse
    (
        SELECT COUNT(*)
        FROM apprehend.t_juv_phy_abuse source
        WHERE source.bg_report_srno =
              (function_reports.report_object ->> 'bgReportSrno')::BIGINT
    ) AS source_abuse_count,
    jsonb_array_length(
        function_reports.report_object -> 'phyAbuse'
    ) AS json_abuse_count,

    -- Table 5: t_juv_dress
    (
        SELECT COUNT(*)
        FROM apprehend.t_juv_dress source
        WHERE source.bg_report_srno =
              (function_reports.report_object ->> 'bgReportSrno')::BIGINT
    ) AS source_dress_count,
    jsonb_array_length(
        function_reports.report_object ->
            'physicalDescription' -> 'dressTypeList'
    ) AS json_dress_count,

    -- Table 6: t_juv_identity_marks
    (
        SELECT COUNT(*)
        FROM apprehend.t_juv_identity_marks source
        WHERE source.bg_report_srno =
              (function_reports.report_object ->> 'bgReportSrno')::BIGINT
    ) AS source_identity_count,
    jsonb_array_length(
        function_reports.report_object ->
            'physicalDescription' -> 'identityMarkList'
    ) AS json_identity_count,

    -- Table 7: t_juv_phy_feature
    (
        SELECT COUNT(*)
        FROM apprehend.t_juv_phy_feature source
        WHERE source.bg_report_srno =
              (function_reports.report_object ->> 'bgReportSrno')::BIGINT
    ) AS source_feature_count,
    jsonb_array_length(
        function_reports.report_object ->
            'physicalDescription' -> 'physicalFeatureList'
    ) AS json_feature_count,

    -- Extra Java response collections
    jsonb_array_length(
        function_reports.report_object -> 'actSectionList'
    ) AS act_section_count,
    jsonb_array_length(
        function_reports.report_object ->
            'accusedDetails' -> 'addressGrid'
    ) AS accused_address_count

FROM function_reports
ORDER BY
    (function_reports.report_object ->> 'bgReportSrno')::BIGINT;

-- Expected:
-- report 57: files 1, family 2, abuse 1, dress 1, identity 2, features 2,
--            acts 4, accused addresses 2
-- report 58: files 0, family 1, abuse 0, dress 0, identity 0, features 0,
--            acts 4, accused addresses 2

-- ========================================================================
-- CHECK 4: every collection has JSON array type, including empty lists
-- ========================================================================

SELECT
    report_object ->> 'bgReportSrno' AS bg_report_srno,
    jsonb_typeof(report_object -> 'fileList') AS file_list_type,
    jsonb_typeof(report_object -> 'familyDtls') AS family_type,
    jsonb_typeof(report_object -> 'phyAbuse') AS abuse_type,
    jsonb_typeof(
        report_object -> 'physicalDescription' -> 'dressTypeList'
    ) AS dress_type,
    jsonb_typeof(
        report_object -> 'physicalDescription' -> 'identityMarkList'
    ) AS identity_type,
    jsonb_typeof(
        report_object -> 'physicalDescription' -> 'physicalFeatureList'
    ) AS feature_type,
    jsonb_typeof(report_object -> 'actSectionList') AS act_type,
    jsonb_typeof(
        report_object -> 'accusedDetails' -> 'addressGrid'
    ) AS address_type
FROM jsonb_array_elements(
    apprehend.get_apprehend_memo_json(
        3810100126001865
    ) -> 'juvenileBackgroundReports'
) AS report_object
ORDER BY (report_object ->> 'bgReportSrno')::BIGINT;

-- Every returned type above must be: array

-- ========================================================================
-- CHECK 5: Java date and Long serialization
-- ========================================================================

SELECT
    report_object ->> 'bgReportSrno' AS bg_report_srno,
    jsonb_typeof(report_object -> 'bgReportSrno') AS bg_id_json_type,
    report_object ->> 'jjbOrderDt' AS jjb_order_date,
    report_object -> 'accusedDetails' ->> 'dob' AS accused_dob,
    jsonb_typeof(
        report_object -> 'accusedDetails' -> 'apprehendSrno'
    ) AS apprehend_id_json_type
FROM jsonb_array_elements(
    apprehend.get_apprehend_memo_json(
        3810100126001865
    ) -> 'juvenileBackgroundReports'
) AS report_object
ORDER BY (report_object ->> 'bgReportSrno')::BIGINT;

-- IDs should have JSON type string.
-- Dates should be yyyy-MM-dd without T00:00:00.

-- ========================================================================
-- CHECK 6: missing parent memo and orphan background reports
-- ========================================================================

SELECT
    apprehend.get_apprehend_memo_json(-1) IS NULL
        AS missing_memo_returns_null,

    apprehend.get_apprehend_memo_json(
        3810100126000073
    ) IS NULL AS orphan_background_parent_returns_null;

-- Expected: true, true

-- ========================================================================
-- CHECK 7: FIR branch parity for the enriched FIR fixture
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
        FROM fir.t_alter_act_section source
        WHERE source.fir_reg_num =
              (function_reports.report_object ->> 'firRegNum')::BIGINT
          AND source.record_status = 'C'
    ) AS source_count,
    jsonb_array_length(
        function_reports.report_object -> 'actSectionList'
    ) AS function_count
FROM function_reports
ORDER BY
    (function_reports.report_object ->> 'bgReportSrno')::BIGINT;
