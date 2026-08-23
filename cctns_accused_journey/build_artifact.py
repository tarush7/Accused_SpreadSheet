#!/usr/bin/env python3
"""Build the canonical Data Analytics artifact for the CCTNS accused journey report."""

from __future__ import annotations

import csv
import json
from collections import Counter
from pathlib import Path


ROOT = Path(__file__).resolve().parent
PHASE_MAP = ROOT / "table_phase_map.csv"
OUTPUT = ROOT / "artifact.json"


def load_phase_map() -> list[dict[str, str]]:
    with PHASE_MAP.open(encoding="utf-8", newline="") as handle:
        return list(csv.DictReader(handle))


def markdown(block_id: str, body: str, source_id: str | None = None) -> dict:
    block = {"id": block_id, "type": "markdown", "body": body.strip()}
    if source_id:
        block["sourceId"] = source_id
    return block


def main() -> None:
    mapped_tables = load_phase_map()
    phase_counts = Counter(row["phase"] for row in mapped_tables)
    phase_order = {
        row["phase"]: int(row["phase_order"])
        for row in mapped_tables
    }
    phase_rows = [
        {
            "phase_order": phase_order[phase],
            "phase": phase,
            "table_count": phase_counts[phase],
            "population": "53 direct accused_vid tables",
            "measure_note": "Schema footprint, not event frequency or duration",
        }
        for phase in sorted(phase_counts, key=phase_order.get)
    ]
    sql_values = ",\n        ".join(
        "(" + str(int(row["phase_order"])) + ", "
        + "'" + row["phase"].replace("'", "''") + "', "
        + "'" + (row["table_schema"] + "." + row["table_name"]).replace("'", "''") + "')"
        for row in mapped_tables
    )
    phase_count_sql = (
        "WITH phase_map(phase_order, phase, qualified_table) AS (\n"
        "    VALUES\n        " + sql_values + "\n"
        ")\n"
        "SELECT phase_order, phase, count(DISTINCT qualified_table)::bigint AS table_count\n"
        "FROM phase_map\n"
        "GROUP BY phase_order, phase\n"
        "ORDER BY phase_order;"
    )

    journey_rows = [
        {
            "stage": 1,
            "journey_phase": "FIR and accused identity",
            "path_status": "Backbone start",
            "business_event": "An FIR exists and the person is added or identified as an accused.",
            "anchor_tables": "fir.t_fir_registration; fir.t_fir_accused_info",
            "date_to_verify": "FIR registration date; accused profile record_created_on is audit-only",
            "join_context": "fir_reg_num + accused_srno; accused_vid for the person",
            "confidence": "Confirmed anchor; FIR event date still to query",
        },
        {
            "stage": 2,
            "journey_phase": "Tracing and coercive branches",
            "path_status": "Optional, may occur before or after arrest",
            "business_event": "Notice, look-out circular, warrant, proclaimed-offender order, reward, externment, freezing order, or IMOT detail is recorded when applicable.",
            "anchor_tables": "notc_ordr.*, court.t_accused_warrants, fir.t_accused_procl_offender",
            "date_to_verify": "Issue/order/review date specific to each branch",
            "join_context": "accused_vid plus FIR/case keys; branch-specific serial number",
            "confidence": "Tables confirmed; relative order is case-dependent",
        },
        {
            "stage": 3,
            "journey_phase": "Arrest or surrender",
            "path_status": "Conditional but central when custody occurs",
            "business_event": "The arrest/surrender memo establishes the custody event and captures person-search, address, physical and related details.",
            "anchor_tables": "arrest.t_arrest_memo",
            "date_to_verify": "arrest_surrender_dt (business event); record_created_on (audit)",
            "join_context": "arr_surr_srno; fir_reg_num; accused_vid; accused_srno",
            "confidence": "Confirmed in workbook and lookup extract",
        },
        {
            "stage": 4,
            "journey_phase": "Investigation and custody evidence",
            "path_status": "Parallel and repeatable",
            "business_event": "Case progress, interrogation, fingerprints, seizure and property attachment are recorded as investigation develops.",
            "anchor_tables": "casediary.t_accused_case_progress; interro.t_interrogation_info; seizure.*",
            "date_to_verify": "interro_dt and table-specific case-progress/seizure/attachment dates",
            "join_context": "accused_vid and case keys; some children inherit a module parent key",
            "confidence": "Direct links confirmed; event order must come from dates",
        },
        {
            "stage": 5,
            "journey_phase": "Remand and bail",
            "path_status": "Conditional and repeatable; interleaves with investigation",
            "business_event": "Police/court remand, extensions, bail hearings, grant, release and cancellation/review can occur in cycles.",
            "anchor_tables": "court.t_accused_remand; bail.t_accused_bail_details; bail.t_acc_bail_cancel_review",
            "date_to_verify": "remand_req_frm_dt, remand_sanc_dt, remand_extn_till_dt, bail_grant_dt, bail_release_dt",
            "join_context": "accused_vid + accused_srno + fir_reg_num; arr_surr_srno when tied to arrest",
            "confidence": "Confirmed fields in workbook lookup sheets",
        },
        {
            "stage": 6,
            "journey_phase": "Police final report or charge-sheet",
            "path_status": "Backbone decision point",
            "business_event": "Investigation reaches a police outcome: charge-sheet/supplementary charge-sheet or another final-report outcome.",
            "anchor_tables": "finalreport.t_accused_chargesheet and detail tables",
            "date_to_verify": "Charge-sheet/final-report submission and acceptance dates",
            "join_context": "accused_vid + accused_srno + fir_reg_num; chargesheet_srno for children",
            "confidence": "Official domain and direct tables confirmed; date columns to query",
        },
        {
            "stage": 7,
            "journey_phase": "Court process",
            "path_status": "Conditional and repeatable",
            "business_event": "Charges are framed and hearings/process events, including warrants for non-appearance, may follow.",
            "anchor_tables": "court.t_charges_framed_acts; court.t_accused_warrants",
            "date_to_verify": "Charges-framed, hearing, warrant issue and review dates",
            "join_context": "accused_vid plus FIR/court-case identifiers",
            "confidence": "Official process confirmed; production event fields to query",
        },
        {
            "stage": 8,
            "journey_phase": "Judgment, disposal and custody outcome",
            "path_status": "Terminal for a court level; custody can continue",
            "business_event": "The accused receives a disposal outcome; conviction-type outcomes may add punishment, jail/sentence and release details.",
            "anchor_tables": "disposal.t_accused_disposal; t_accused_punishment; t_accused_jail_details",
            "date_to_verify": "Judgment/disposal date, sentence_from_dt, sentence_to_dt, jail_last_release_dt",
            "join_context": "accused_vid + accused_srno + fir_reg_num; accused_disposal_srno for children",
            "confidence": "Confirmed parent/detail structure; judgment date to query",
        },
        {
            "stage": 9,
            "journey_phase": "Appeal",
            "path_status": "Optional and repeatable across court levels",
            "business_event": "An appeal records the appellate result and whether punishment is retained or modified.",
            "anchor_tables": "appeal.t_accused_appeal",
            "date_to_verify": "Appeal filing, order and result dates",
            "join_context": "accused_vid + accused_srno + fir_reg_num; accused_appeal_srno",
            "confidence": "Confirmed table and result fields; event dates to query",
        },
        {
            "stage": 10,
            "journey_phase": "Post-case intelligence and registers",
            "path_status": "Derived/administrative, not a legal step",
            "business_event": "Dossiers, conviction data banks, information sheets and other registers consolidate or reuse the person's history.",
            "anchor_tables": "crmprof.*, dbank.*, other_reg.*, stranger.*",
            "date_to_verify": "Register effective/entry date; do not substitute audit timestamps for conviction date",
            "join_context": "accused_vid plus source-case identifiers where present",
            "confidence": "Direct tables confirmed; derivation rules to query",
        },
    ]

    key_rows = [
        {
            "identifier": "accused_vid",
            "grain": "Person across cases",
            "role": "Cross-module identity anchor",
            "verification": "Check count(distinct fir_reg_num) per accused_vid and duplicates/merges",
        },
        {
            "identifier": "fir_reg_num",
            "grain": "FIR/case registration",
            "role": "Case context",
            "verification": "Join to fir.t_fir_registration and confirm the true FIR date field",
        },
        {
            "identifier": "accused_srno",
            "grain": "One accused occurrence in one FIR",
            "role": "Case-specific accused identity",
            "verification": "Test uniqueness with fir_reg_num and compare fir_accused_srno aliases",
        },
        {
            "identifier": "arr_surr_srno",
            "grain": "Arrest/surrender event",
            "role": "Custody-event parent for arrest details",
            "verification": "Inspect FK declarations and re-arrest chains",
        },
        {
            "identifier": "Module serial numbers",
            "grain": "Repeatable event/detail parent",
            "role": "Examples: remand_srno, bailer_srno, accused_disposal_srno, accused_appeal_srno",
            "verification": "Use pg_constraint output; do not infer joins only from similar names",
        },
    ]

    def values_query(cte_name: str, columns: list[str], rows: list[dict]) -> str:
        def sql_literal(value: object) -> str:
            if isinstance(value, bool):
                return "TRUE" if value else "FALSE"
            if isinstance(value, (int, float)) and not isinstance(value, bool):
                return str(value)
            return "'" + str(value).replace("'", "''") + "'"

        values = ",\n        ".join(
            "(" + ", ".join(sql_literal(row[column]) for column in columns) + ")"
            for row in rows
        )
        return (
            f"WITH {cte_name}({', '.join(columns)}) AS (\n"
            f"    VALUES\n        {values}\n"
            ")\n"
            f"SELECT {', '.join(columns)} FROM {cte_name};"
        )

    journey_sql = values_query(
        "journey_stages",
        [
            "stage", "journey_phase", "path_status", "business_event",
            "anchor_tables", "date_to_verify", "join_context", "confidence",
        ],
        journey_rows,
    )
    key_sql = values_query(
        "key_model",
        ["identifier", "grain", "role", "verification"],
        key_rows,
    )

    sources = [
        {
            "id": "src_workbook",
            "label": "CCTNS_Modules_Search&View_Figma_26052026_V2 (1).xlsx — accused flow and table sheets",
        },
        {
            "id": "src_pasted",
            "label": "User-provided information_schema.columns result for column accused_vid",
        },
        {
            "id": "src_phase_map",
            "label": "Reviewed accused_vid table-to-phase classification",
            "path": "table_phase_map.csv",
            "query": {
                "description": "Counts each of the 53 direct accused_vid tables once in one interpreted lifecycle area.",
                "engine": "PostgreSQL",
                "language": "sql",
                "sql": phase_count_sql,
                "metric_definitions": [
                    "table_count = count of distinct schema-qualified tables assigned to the lifecycle area",
                    "Counts describe schema footprint, not event frequency, duration, or mandatory completion",
                ],
                "filters": ["column_name = accused_vid", "53 user-provided production-visible tables"],
            },
        },
        {
            "id": "src_mha_matrix",
            "label": "Ministry of Home Affairs — Matrix of mandatory Data Sets to be shared as per Law",
            "href": "https://www.mha.gov.in/sites/default/files/2022-09/CCTNSMHA_11012016%5B1%5D.pdf",
        },
        {
            "id": "src_mha_2025",
            "label": "Ministry of Home Affairs — Lok Sabha Unstarred Question No. 4954 (1 April 2025)",
            "href": "https://www.mha.gov.in/MHA1/Par2017/pdfs/par2025-pdfs/LS01042025/4954.pdf",
        },
        {
            "id": "src_mha_cctns",
            "label": "Ministry of Home Affairs — CCTNS/ICJS overview",
            "href": "https://www.mha.gov.in/en/divisionofmha/women-safety-division/cctns",
        },
        {
            "id": "src_journey_model",
            "label": "Reviewed CCTNS accused journey stage model",
            "path": "source_notes.md",
            "query": {
                "description": "Reproduces the ten-stage interpreted journey table documented in the report.",
                "engine": "PostgreSQL",
                "language": "sql",
                "sql": journey_sql,
                "tables_used": [
                    "fir.t_fir_accused_info", "arrest.t_arrest_memo",
                    "court.t_accused_remand", "bail.t_accused_bail_details",
                    "finalreport.t_accused_chargesheet", "disposal.t_accused_disposal",
                    "appeal.t_accused_appeal",
                ],
                "metric_definitions": [
                    "stage is an interpreted lifecycle order, not elapsed time or a mandatory funnel",
                ],
            },
        },
        {
            "id": "src_key_model",
            "label": "Reviewed CCTNS accused identifier grain model",
            "path": "source_notes.md",
            "query": {
                "description": "Reproduces the identifier-grain table documented in the report.",
                "engine": "PostgreSQL",
                "language": "sql",
                "sql": key_sql,
                "tables_used": ["fir.t_fir_accused_info", "arrest.t_arrest_memo"],
            },
        },
    ]

    blocks = [
        markdown("title", "# CCTNS Accused Journey: Chronology, Keys and Verification"),
        markdown(
            "technical-summary",
            """
## Technical summary

**The defensible backbone is:** FIR and accused identity → arrest/surrender when applicable → investigation with custody/remand/bail events → police final report or charge-sheet → court process → judgment/disposal and any sentence/jail outcome → appeal when filed.

**This is a state machine, not one compulsory row-by-row sequence.** Notices, warrants, proclaimed-offender action, seizure/freezing, IMOT, Kalandra, bail, compensation, appeal and post-case registers are conditional or parallel branches.

**Use two timelines.** `accused_vid` follows the person across cases; `fir_reg_num` plus `accused_srno` follows that person's journey inside one FIR. Event serial numbers then distinguish repeatable arrests, remands, bail actions, disposals and appeals.

**Current evidence is sufficient for a stage map, not yet for a dated person timeline.** The workbook and metadata result show where data can live; production dates, populated rows and declared foreign keys still need the supplied SQL checks.
""",
        ),
        markdown(
            "state-machine-finding",
            """
## Direct references are concentrated in profile and conditional branches

The 53 schema-qualified tables in the pasted result were each assigned once to an interpreted lifecycle area. The chart measures **schema footprint only**—it does not measure how often an event occurs, how long a phase lasts, or whether a phase is mandatory. Its main use is to prevent a common error: sorting schemas or table names and calling that a person's chronology.
""",
            "src_phase_map",
        ),
        {"id": "phase-coverage-chart-block", "type": "chart", "chartId": "phase-coverage-chart"},
        markdown(
            "journey-map-intro",
            """
## The chronological journey has a backbone with interleaved branches

Read the stages from top to bottom for the normal case direction. Optional, parallel or repeatable events can occur zero, one or many times and sometimes move relative to another stage.

1. **FIR and accused identity — backbone start.** Create the FIR-linked accused occurrence.
2. **Tracing and coercive action — optional.** Notices, look-out circulars, warrants, proclaimed-offender action, rewards, externment, freezing orders and IMOT details may appear before or after arrest.
3. **Arrest or surrender — conditional custody milestone.** The arrest/surrender memo is the preferred event anchor.
4. **Investigation and custody evidence — parallel and repeatable.** Case progress, interrogation, fingerprints, seizure and attachment develop over time.
5. **Remand and bail — conditional and repeatable.** Requests, sanctions, extensions, hearings, grant, release and cancellation can interleave with investigation.
6. **Police final report or charge-sheet — backbone decision point.** Investigation reaches a police outcome, including supplementary reports when applicable.
7. **Court process — conditional and repeatable.** Charges framed, hearings and non-appearance warrants follow the court case.
8. **Judgment, disposal and custody outcome — terminal for a court level.** Conviction-type outcomes can add punishment, jail and release details.
9. **Appeal — optional and repeatable by court level.** Filing, order and result events may modify the prior outcome.
10. **Post-case intelligence and registers — derived.** Dossiers and data banks summarize history; they are not new legal milestones.
""",
        ),
        markdown(
            "journey-verification-intro",
            """
### Technical field checklist

Start with these parent/event anchors and prefer their business timestamps over audit timestamps:

- FIR identity: `fir.t_fir_accused_info`; retain `fir_reg_num`, `accused_srno` and `accused_vid`.
- Arrest/surrender: `arrest.t_arrest_memo`; verify `arrest_surrender_dt` and retain `arr_surr_srno`.
- Investigation: `casediary.t_accused_case_progress`, `interro.t_interrogation_info`, and seizure/attachment parents; verify event-specific dates such as `interro_dt`.
- Remand/bail: `court.t_accused_remand` and `bail.t_accused_bail_details`; verify request, sanction, extension, hearing, grant and release dates.
- Police outcome: `finalreport.t_accused_chargesheet`; discover submission/acceptance timestamps and child serials.
- Court/disposal: `court.t_charges_framed_acts` and `disposal.t_accused_disposal`; verify charges-framed, judgment/disposal and sentence/release dates.
- Appeal: `appeal.t_accused_appeal`; discover filing, order and result dates plus `accused_appeal_srno`.
""",
        ),
        markdown(
            "key-model",
            """
## Person identity and case identity must remain separate

`accused_vid` is the most useful cross-module search key, but it is not enough to describe one legal journey because the same person can appear in multiple FIRs. The case-specific grain is normally `fir_reg_num` plus `accused_srno`; module serial numbers identify repeatable event parents and their indirect children. A final timeline should therefore partition by FIR and retain the person identifier rather than collapsing all rows into one undifferentiated sequence.

- `accused_vid`: person across cases.
- `fir_reg_num`: FIR/case registration context.
- `accused_srno`: one accused occurrence within an FIR.
- `arr_surr_srno`: one arrest or surrender event.
- Module serials such as remand, bailer, disposal and appeal numbers: repeatable event/detail parents.
""",
        ),
        markdown(
            "scope-definitions",
            """
## Scope and evidence definitions

- **Confirmed** means the table/link is present in both the workbook's accused flow/table material and the pasted database metadata.
- **Database-confirmed** means a direct `accused_vid` column is present in the pasted production metadata, while exact timing still needs columns and row values.
- **Backbone** means a normal case-direction milestone, not a guarantee that every case reaches it.
- **Conditional/parallel** means the event depends on facts, orders or procedural choices and can interleave with other phases.
- **Audit timestamp** means `record_created_on`, `record_updated_on` or similar system metadata; it must not be substituted for an arrest, remand, bail, judgment or appeal date.

The adult FIR-linked path controls this report. The workbook's `Apprehend` material is primarily juvenile-oriented, and Kalandra is a preventive/petty-case branch, so neither is forced into the adult FIR backbone.
""",
        ),
        markdown(
            "method",
            """
## Method: derive order from business events, not schemas

1. The workbook established root/detail structures such as `t_fir_accused_info`, `t_arrest_memo`, remand, bail, disposal and appeal parents.
2. The user's metadata result established every table with a direct `accused_vid` column.
3. Official Ministry of Home Affairs material established the high-level CCTNS/ICJS domains: FIR/accused, arrest, investigation, property/seizure, bail/remand, charge-sheet/final report, court, prison/release and judgment/appeal.
4. The table map classified every direct table into one lifecycle area and labeled branches explicitly.
5. The PostgreSQL pack discovers populated rows, business-date candidates, declared constraints and indirect descendants. Only after those results are reviewed should a typed `UNION ALL` event timeline be written.

This method deliberately treats table/schema placement as implementation structure and event dates/statuses as chronology evidence.
""",
        ),
        markdown(
            "limitations",
            """
## Limitations and robustness checks

**No production rows were queried here.** Presence of `accused_vid` proves a possible direct link, not that a particular accused has a row.

**Direct-column search is incomplete by design.** Acts, files, sureties, addresses and release details often inherit identity through a parent serial number and will only appear after the foreign-key graph is traversed.

**Some event tables also contain copied dates.** For example, an FIR profile may carry `arrest_surr_dt`; the arrest memo should normally control the arrest event after row-level reconciliation.

**Schema names are not time order.** Remand lives under `court` but belongs during investigation/custody; warrants can appear in more than one procedural period; conviction-history fields in a charge-sheet are not the current judgment.

**Foreign keys may be absent or unenforced.** The query pack checks declared constraints, but application-level relationships may still require sample-row reconciliation.
""",
        ),
        markdown(
            "next-steps",
            """
## Recommended next steps

1. In a fresh PostgreSQL session, open `verification_queries.sql`, replace the sample `119802` with one real `accused_vid`, and run Q1 through Q10 in order.
2. Return the Q6 identifier/date inventory, Q7 constraints, Q8 indirect-child graph and Q10 JSON bundle.
3. Reconcile any duplicate event dates, especially arrest dates copied into FIR, interrogation or report tables.
4. Build the final case-partitioned timeline with typed timestamps and a precedence rule for each event type.
5. Validate at least three patterns: a simple no-arrest/no-charge-sheet case, an arrest-remand-bail case, and a conviction-appeal case. Add a proclaimed-offender or repeat-arrest case as an edge test.
""",
        ),
        markdown(
            "further-questions",
            """
## Further questions

- Does `accused_vid` remain stable across migrated and merged person records?
- Is `accused_srno` globally unique, or only unique with `fir_reg_num`?
- Which table/date is authoritative when arrest or surrender dates disagree?
- What are the exact final-report and court-disposal parent tables and event timestamps in this deployment?
- Are appeal levels stored as repeated rows in `appeal.t_accused_appeal`, and what identifies the court level?
- Which indirect child tables lack declared foreign keys and must be joined by application convention?
""",
        ),
    ]

    artifact = {
        "surface": "report",
        "manifest": {
            "version": 1,
            "surface": "report",
            "title": "CCTNS Accused Journey: Chronology, Keys and Verification",
            "description": "A technical reconstruction of the FIR-linked accused journey, its conditional branches, identifiers and PostgreSQL verification plan.",
            "generatedAt": "2026-08-20T00:00:00+05:30",
            "blocks": blocks,
            "charts": [
                {
                    "id": "phase-coverage-chart",
                    "title": "Direct accused_vid tables by interpreted lifecycle area",
                    "description": "53 schema-qualified tables, each classified once; counts show schema footprint rather than event volume.",
                    "type": "bar",
                    "intent": "comparison",
                    "question": "How many direct accused_vid tables fall in each interpreted lifecycle area?",
                    "rationale": "A bar chart compares one count across the ten interpreted lifecycle areas.",
                    "dataset": "phase_counts",
                    "encodings": {
                        "x": {"field": "phase"},
                        "y": {"field": "table_count"},
                    },
                    "palette": {"kind": "sequential", "name": "blue"},
                    "labels": {"values": "all"},
                    "source": next(source for source in sources if source["id"] == "src_phase_map"),
                }
            ],
            "tables": [
                {
                    "id": "journey-table",
                    "title": "Canonical accused journey and branch status",
                    "description": "Lifecycle order for an FIR-linked accused; optional and repeatable rows may interleave.",
                    "dataset": "journey_stages",
                    "columns": [
                        {"field": "stage", "label": "Stage", "type": "number"},
                        {"field": "journey_phase", "label": "Journey phase", "type": "text"},
                        {"field": "path_status", "label": "Path status", "type": "text"},
                    ],
                    "defaultSort": {"field": "stage", "direction": "asc"},
                    "source": next(source for source in sources if source["id"] == "src_journey_model"),
                },
                {
                    "id": "journey-verification-table",
                    "title": "Technical anchors and event dates",
                    "description": "Implementation checklist for the same ten lifecycle stages.",
                    "dataset": "journey_stages",
                    "columns": [
                        {"field": "stage", "label": "Stage", "type": "number"},
                        {"field": "anchor_tables", "label": "Anchor tables", "type": "text"},
                        {"field": "date_to_verify", "label": "Date/time to verify", "type": "text"},
                    ],
                    "defaultSort": {"field": "stage", "direction": "asc"},
                    "source": next(source for source in sources if source["id"] == "src_journey_model"),
                },
                {
                    "id": "key-table",
                    "title": "Identifier grain and role",
                    "description": "Keep person, case and repeatable event identifiers at their proper grains.",
                    "dataset": "key_model",
                    "columns": [
                        {"field": "identifier", "label": "Identifier", "type": "text"},
                        {"field": "grain", "label": "Grain", "type": "text"},
                        {"field": "role", "label": "Role", "type": "text"},
                    ],
                    "defaultSort": {"field": "identifier", "direction": "asc"},
                    "source": next(source for source in sources if source["id"] == "src_key_model"),
                },
            ],
            "sources": sources,
        },
        "snapshot": {
            "version": 1,
            "status": "ready",
            "generatedAt": "2026-08-20T00:00:00+05:30",
            "datasets": {
                "phase_counts": phase_rows,
                "journey_stages": journey_rows,
                "key_model": key_rows,
                "table_phase_map": mapped_tables,
            },
        },
        "sources": sources,
        "package_info": {
            "audience": "technical",
            "report_mode": "html",
            "supporting_files": [
                "table_phase_map.csv",
                "verification_queries.sql",
                "source_notes.md",
            ],
        },
    }

    OUTPUT.write_text(json.dumps(artifact, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")
    print(f"Wrote {OUTPUT} with {len(mapped_tables)} mapped tables and {len(journey_rows)} stages")


if __name__ == "__main__":
    main()
