-- VERSION 15 COMPLETE PART 13 REPLACEMENT BLOCK
--
-- Delete the entire existing Version 14 juvenileBackgroundReports block and
-- paste this complete block in its place. Do not append it after Version 14.
--
-- This version maps the root fields exposed by JuvBackgroundReportDTO.
-- Child collections and master-data labels are intentionally deferred.
-- PostgreSQL permits at most 100 function arguments, so the key/value pairs
-- are split across several jsonb_build_object calls and merged with ||.

        ||

        jsonb_build_object(
            'juvenileBackgroundReports',
            COALESCE(
                (
                    SELECT jsonb_agg(
                        (
                        jsonb_build_object(
    -- ================================================================
    -- REPORT IDENTITY AND COMMON REQUEST/RESPONSE CONTEXT
    -- ================================================================
    'bgReportSrno', report.bg_report_srno::TEXT,
    'ciclGdNum', memo.cicl_gd_num,
    'firRegNum', report.fir_reg_num::TEXT,
    'apprehendSrno', report.apprehend_srno::TEXT,

    'langCd', report.lang_cd,
    'psId', report.ps_id::TEXT,

    -- These CommonParamsDTO values come from request headers/session in
    -- Java. They are not stored on the background-report record.
    'staffId', NULL,
    'loginId', NULL,
    'officeCd', NULL,
    'stateId', NULL,
    'districtId', NULL,
    'officeTypeCd', NULL,
    'rankCd', NULL,
    'officeLevelCd', NULL,
    'allowedRoleCd', NULL,
    'oicStaffId', NULL,
    'oicLoginId', NULL,
    'loginparams', NULL,
    'requestId', NULL,
    'authToken', NULL
                        )

                        ||

                        jsonb_build_object(

    -- ================================================================
    -- AGE, MARRIAGE AND HOUSEHOLD
    -- ================================================================
    'ageVers', report.age_vers,
    'ageSourceCd', report.age_source_cd,
    'isMarried', report.is_married,
    'spouseName', report.spouse_name,
    'spouseAge', report.spouse_age,
    'childrenDtls', report.children_dtls,
    'childAge', report.child_age,

    -- ================================================================
    -- HOME, EDUCATION, EMPLOYMENT AND SOCIAL ENVIRONMENT
    -- ================================================================
    'religionAttitude', report.religion_attitude,
    'presentLivingCond', report.present_living_cond,
    'homeDisciplineDtls', report.home_discipline_dtls,
    'otherFactors', report.other_factors,
    'employmentDtls', report.employment_dtls,
    'incomeUtilManner', report.income_util_manner,
    'workRecord', report.work_record,
    'eduQualCd', report.edu_qual_cd,
    'familyLeaveReason', report.family_leave_reason,
    'lastSchoolStud', report.last_school_stud,
    'vocationalTraining', report.vocational_training,
    'attTowardFriend', report.att_toward_friend,
    'attitudeOfFriend', report.attitude_of_friend,
    'attitudeOfTeacher', report.attitude_of_teacher,
    'neighbourObserv', report.neighbour_observ,

    -- The unusual spelling is intentional: it is the Java DTO name.
    'onbservAbtNeighbour', report.onbserv_abt_neighbour

                        )

                        ||

                        jsonb_build_object(

    -- ================================================================
    -- RISK, HISTORY, HEALTH, ASSESSMENT AND REHABILITATION
    -- ================================================================
    'isVictimOffence', report.is_victim_offence,
    'victimOffenceDtls', report.victim_offence_dtls,
    'isDrugPeddlerUsed', report.is_drug_peddler_used,
    'drugPeddlerUsedDtls', report.drug_peddler_used_dtls,
    'hasTendRunAway', report.has_tend_run_away,
    'runAwayDtls', report.run_away_dtls,
    'isEarlierApprehend', report.is_earlier_apprehend,
    'prevApprehendDtls', report.prev_apprehend_dtls,
    'prevCaseHistory', report.prev_case_history,
    'healthConditions', report.health_conditions,
    'mentalCondition', report.mental_condition,
    'anyOthrRemarks', report.any_othr_remarks,
    'emotionalFactors', report.emotional_factors,
    'intelligenceDtls', report.intelligence_dtls,
    'suggestiveProblems', report.suggestive_problems,
    'socioEcoStatus', report.socio_eco_status,
    'enquiryAnalysis', report.enquiry_analysis,
    'expertOpinion', report.expert_opinion,
    'rehabRecommend', report.rehab_recommend,
    'physicalCondCd', report.physical_cond_cd

                        )

                        ||

                        jsonb_build_object(

    -- ================================================================
    -- PHYSICAL SUMMARY
    -- Labels are populated inside physicalDescription in a later version.
    -- At the root, Java currently leaves the label-only values unset.
    -- ================================================================
    'bodyBuildTypeCd', report.build_type_cd,
    'bodyComplexionTypeCd', report.complexion_type_cd,
    'bodyBuildType', NULL,
    'bodyComplexionType', NULL,
    'otherPhysicalDetails', report.other_physical_desc,
    'heightFromCm', report.height_from,
    'heightToCm', report.height_to

                        )

                        ||

                        jsonb_build_object(

    -- ================================================================
    -- JUVENILE/JJB AND COURT INFORMATION
    -- ================================================================
    'isOffenceHeinous', report.is_offence_heinous,
    'isJuvDifferAble', report.is_juv_differ_able,
    'differAbleCd', report.differ_able_cd,

    -- JuvBackgroundReportDTO exposes courtEstblCd, but the Java entity does
    -- not map a source column for it in this report table.
    'courtEstblCd', NULL,
    'courtOrderNum', report.court_order_num,
    'otherDifferAble', report.differ_able_others,
    'othrAgeSrc', report.age_src_others,
    'jjbOrderDt', report.jjb_order_dt,
    'jjbName', report.jjb_name,

    -- These are conditionally enriched by Java only when courtEstblCd is
    -- available. With no mapped source, they remain null for now.
    'courtComplexCd', NULL,
    'courtComplexName', NULL,
    'establishmentName', NULL,
    'cisDistrictCd', NULL,
    'cisDistrictName', NULL,

    -- alleged_offence_dtls is already stored as JSON in the parent table.
    'allegedOffenceDtls', report.alleged_offence_dtls,

    -- ================================================================
    -- AUDIT VALUES EXPOSED BY THE JAVA DTO
    -- ================================================================
    'recordStatus', report.record_status,
    'recordCreatedOn', report.record_created_on,
                            'recordCreatedBy', report.record_created_by::TEXT
                        )
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
-- VERIFICATION 1: inspect the enriched report 57 only.
-- ========================================================================

SELECT report_object
FROM jsonb_array_elements(
    apprehend.get_apprehend_memo_json(
        3810100126001865
    ) -> 'juvenileBackgroundReports'
) AS report_object
WHERE report_object ->> 'bgReportSrno' = '57';

-- ========================================================================
-- VERIFICATION 2: confirm important source-to-JSON mappings.
-- ========================================================================

WITH function_report AS (
    SELECT report_object
    FROM jsonb_array_elements(
        apprehend.get_apprehend_memo_json(
            3810100126001865
        ) -> 'juvenileBackgroundReports'
    ) AS report_object
    WHERE report_object ->> 'bgReportSrno' = '57'
)
SELECT
    source.bg_report_srno::TEXT =
        function_report.report_object ->> 'bgReportSrno'
        AS bg_report_matches,

    source.apprehend_srno::TEXT =
        function_report.report_object ->> 'apprehendSrno'
        AS apprehend_matches,

    source.age_vers::TEXT =
        function_report.report_object ->> 'ageVers'
        AS age_matches,

    source.jjb_name IS NOT DISTINCT FROM
        (function_report.report_object ->> 'jjbName')
        AS jjb_name_matches,

    source.record_status =
        function_report.report_object ->> 'recordStatus'
        AS status_matches

FROM apprehend.t_juv_background_report source
CROSS JOIN function_report
WHERE source.bg_report_srno = 57;

-- ========================================================================
-- VERIFICATION 3: the array count must remain unchanged after expansion.
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
