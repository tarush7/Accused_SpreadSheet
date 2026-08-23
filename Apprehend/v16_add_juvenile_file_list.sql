-- VERSION 16 INSERTION BLOCK: JUVENILE BACKGROUND FILES
--
-- Inside the final jsonb_build_object(...) for each object in
-- juvenileBackgroundReports, find:
--
--     'allegedOffenceDtls', report.alleged_offence_dtls,
--
-- Paste the following fileList pair immediately after that line and before
-- the audit fields (recordStatus, recordCreatedOn, recordCreatedBy).

    'fileList',
        (
            SELECT COALESCE(
                jsonb_agg(
                    jsonb_build_object(
                        'fileUploadSrno',
                            background_file.file_upload_srno::TEXT,

                        'langCd',
                            background_file.lang_cd,

                        'bgReportSrno',
                            background_file.bg_report_srno::TEXT,

                        'fileSrno',
                            background_file.file_srno,

                        'fileTypeCd',
                            background_file.file_type_cd,

                        'fileSubtypeCd',
                            background_file.file_subtype_cd,

                        -- Java resolves file labels using the language of
                        -- the parent background report.
                        'fileType',
                            (
                                SELECT file_type_master.file_type
                                FROM mdm.m_upload_file_types file_type_master
                                WHERE file_type_master.lang_cd = report.lang_cd
                                  AND file_type_master.file_type_cd =
                                      background_file.file_type_cd
                                LIMIT 1
                            ),

                        'fileSubtype',
                            (
                                SELECT file_subtype_master.file_sub_type
                                FROM mdm.m_upload_file_subtypes
                                    file_subtype_master
                                WHERE file_subtype_master.lang_cd =
                                      report.lang_cd
                                  AND file_subtype_master.file_subtype_cd =
                                      background_file.file_subtype_cd
                                LIMIT 1
                            ),

                        'fileName',
                            background_file.file_name,

                        'filePath',
                            background_file.file_path,

                        'fileBelongsTo',
                            background_file.file_belongs_to,

                        'fileBelongsToSrno',
                            background_file.file_belongs_to_srno::TEXT,

                        'fileGuid',
                            background_file.file_guid,

                        'fileDesc',
                            background_file.file_desc,

                        'fileSize',
                            background_file.file_size,

                        -- These DTO values are not populated from this
                        -- entity by SocialBackgroundViewUseCaseImpl.
                        'contentType', NULL,
                        'moduleName', NULL,
                        'fileUploadedOn', NULL
                    )
                    ORDER BY background_file.file_upload_srno
                ),
                '[]'::JSONB
            )
            FROM apprehend.t_jcl_background_files background_file
            WHERE background_file.bg_report_srno = report.bg_report_srno
        ),

-- ========================================================================
-- VERIFICATION 1: inspect fileList for report 57.
-- Expected count from the source audit: 1.
-- ========================================================================

SELECT report_object -> 'fileList' AS file_list
FROM jsonb_array_elements(
    apprehend.get_apprehend_memo_json(
        3810100126001865
    ) -> 'juvenileBackgroundReports'
) AS report_object
WHERE report_object ->> 'bgReportSrno' = '57';

-- ========================================================================
-- VERIFICATION 2: report 57 must have one file; report 58 must have none.
-- ========================================================================

SELECT
    report_object ->> 'bgReportSrno' AS bg_report_srno,
    jsonb_array_length(report_object -> 'fileList') AS function_file_count
FROM jsonb_array_elements(
    apprehend.get_apprehend_memo_json(
        3810100126001865
    ) -> 'juvenileBackgroundReports'
) AS report_object
ORDER BY (report_object ->> 'bgReportSrno')::BIGINT;

-- ========================================================================
-- VERIFICATION 3: compare database and function counts for both reports.
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
        FROM apprehend.t_jcl_background_files source_file
        WHERE source_file.bg_report_srno =
              (function_reports.report_object ->> 'bgReportSrno')::BIGINT
    ) AS source_count,

    jsonb_array_length(
        function_reports.report_object -> 'fileList'
    ) AS function_count

FROM function_reports
ORDER BY
    (function_reports.report_object ->> 'bgReportSrno')::BIGINT;
