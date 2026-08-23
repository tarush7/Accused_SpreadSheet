-- VERSION 23 INSERTION BLOCK: ACCUSED DETAILS
--
-- Inside the final jsonb_build_object(...) for each object in
-- juvenileBackgroundReports, paste this pair after actSectionList and before
-- the audit fields (recordStatus, recordCreatedOn, recordCreatedBy).
--
-- This uses the same two helpers called by SocialBackgroundViewRepositoryImpl:
--   apprehend.get_accused_details_apprehend(apprehendSrno)
--   apprehend.get_accused_address_apprehend(apprehendSrno)

    'accusedDetails',
        (
            SELECT jsonb_build_object(
                'apprehendSrno',
                    accused_details.apprehendsrno::TEXT,

                -- These DTO fields are not returned by the Java helper.
                'accusedSrno', NULL,
                'accusedVid', NULL,

                'firstName',
                    accused_details.firstname,

                'middleName',
                    accused_details.middlename,

                'lastName',
                    accused_details.lastname,

                'firstNameEng',
                    accused_details.firstnameeng,

                'middleNameEng',
                    accused_details.middlenameeng,

                'lastNameEng',
                    accused_details.lastnameeng,

                'relationTypeCd',
                    accused_details.relationtypecd,

                'relativeName',
                    accused_details.relativename,

                'relativeNameEng',
                    accused_details.relativenameeng,

                'relMobileNum',
                    accused_details.relmobilenum::TEXT,

                'genderCd',
                    accused_details.gendercd,

                'ageTypeCd',
                    accused_details.agetypecd,

                'ageYrs',
                    accused_details.ageyrs,

                -- AccusedDetailsDTO uses ageMonths, not ageMnths.
                'ageMonths',
                    accused_details.agemonths,

                'yob',
                    accused_details.yob,

                -- Java formats this LocalDateTime as yyyy-MM-dd.
                'dob',
                    accused_details.dob::DATE,

                'ageFromYrs',
                    accused_details.agefromyrs,

                'ageToYrs',
                    accused_details.agetoyrs,

                -- Present in the DTO/projection but absent from the actual
                -- helper result, so Java leaves them null.
                'nationalityCd', NULL,
                'categoryCd', NULL,
                'religionCd', NULL,
                'occupationCd', NULL,
                'injuryDetails', NULL,

                'addressGrid',
                    (
                        SELECT COALESCE(
                            jsonb_agg(
                                jsonb_build_object(
                                    'id',
                                        accused_address.id::TEXT,

                                    'addressTypeCd',
                                        accused_address.addresstypecd,

                                    'addressLine1',
                                        accused_address.addressline1,

                                    'addressLine2',
                                        accused_address.addressline2,

                                    'addressLine3',
                                        accused_address.addressline3,

                                    'subDistrictCd',
                                        accused_address.subdistrictcd::TEXT,

                                    'villageCd',
                                        accused_address.villagecd::TEXT,

                                    'village',
                                        accused_address.village,

                                    'tehsil',
                                        accused_address.tehsil,

                                    'countryCd',
                                        accused_address.countrycd,

                                    'psId',
                                        accused_address.psid::TEXT,

                                    'stateId',
                                        accused_address.stateid::TEXT,

                                    'lgDistrictCd',
                                        accused_address.lgdistrictcd,

                                    'pincode',
                                        accused_address.pincode,

                                    'isCommAddr',
                                        accused_address.iscommaddr,

                                    'outsideIndiaAddr',
                                        accused_address.outsideindiaaddr,

                                    'addressEng',
                                        accused_address.addresseng,

                                    'isPermAddrSame',
                                        accused_address.ispermaddrsame,

                                    'subDistrict',
                                        accused_address.subdistrict,

                                    'country',
                                        accused_address.country,

                                    'state',
                                        accused_address.state,

                                    'district',
                                        accused_address.district,

                                    'ps',
                                        accused_address.ps
                                )
                                ORDER BY accused_address.id
                            ),
                            '[]'::JSONB
                        )
                        FROM apprehend.get_accused_address_apprehend(
                            report.apprehend_srno
                        ) accused_address
                    ),

                -- SocialBackgroundViewRepositoryImpl does not populate IDs.
                'idList', NULL
            )

            -- The anchor row ensures Java's empty AccusedDetailsDomain shape
            -- is still returned even when the personal-details helper has no
            -- row. In that case, its scalar fields are JSON null.
            FROM (VALUES (1)) AS anchor(dummy)
            LEFT JOIN LATERAL (
                SELECT details_row.*
                FROM apprehend.get_accused_details_apprehend(
                    report.apprehend_srno
                ) details_row
                LIMIT 1
            ) accused_details ON TRUE
        ),

-- ========================================================================
-- VERIFICATION 1: inspect accusedDetails for report 57.
-- ========================================================================

SELECT report_object -> 'accusedDetails' AS accused_details
FROM jsonb_array_elements(
    apprehend.get_apprehend_memo_json(
        3810100126001865
    ) -> 'juvenileBackgroundReports'
) AS report_object
WHERE report_object ->> 'bgReportSrno' = '57';

-- ========================================================================
-- VERIFICATION 2: both reports share the same apprehend_srno and therefore
-- must each have two addresses.
-- ========================================================================

SELECT
    report_object ->> 'bgReportSrno' AS bg_report_srno,
    jsonb_array_length(
        report_object -> 'accusedDetails' -> 'addressGrid'
    ) AS function_address_count
FROM jsonb_array_elements(
    apprehend.get_apprehend_memo_json(
        3810100126001865
    ) -> 'juvenileBackgroundReports'
) AS report_object
ORDER BY (report_object ->> 'bgReportSrno')::BIGINT;

-- ========================================================================
-- VERIFICATION 3: compare source helper and function address counts.
-- ========================================================================

WITH helper_count AS (
    SELECT COUNT(*) AS row_count
    FROM apprehend.get_accused_address_apprehend(
        3810100126001865
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
        function_reports.report_object ->
            'accusedDetails' -> 'addressGrid'
    ) AS function_count
FROM function_reports
CROSS JOIN helper_count
ORDER BY
    (function_reports.report_object ->> 'bgReportSrno')::BIGINT;

-- ========================================================================
-- VERIFICATION 4: verify date format and intentional null fields.
-- ========================================================================

SELECT
    report_object ->> 'bgReportSrno' AS bg_report_srno,
    report_object -> 'accusedDetails' ->> 'firstName' AS first_name,
    report_object -> 'accusedDetails' ->> 'dob' AS dob,
    jsonb_typeof(
        report_object -> 'accusedDetails' -> 'idList'
    ) AS id_list_type,
    report_object -> 'accusedDetails' -> 'accusedVid' AS accused_vid
FROM jsonb_array_elements(
    apprehend.get_apprehend_memo_json(
        3810100126001865
    ) -> 'juvenileBackgroundReports'
) AS report_object
ORDER BY (report_object ->> 'bgReportSrno')::BIGINT;
