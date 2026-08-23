# Source and method notes

## Reporting job

- Question: reconstruct an accused person's CCTNS journey in chronological order and identify how to verify it in PostgreSQL.
- Audience: technical.
- Scope: adult FIR-linked accused journey; juvenile/apprehend and Kalandra paths are treated as parallel variants rather than forced into the main path.
- Time frame: lifecycle order, not a particular calendar period.
- Decision-useful output: a defensible stage map, key model, confidence labels, and runnable discovery queries.

## Controlling sources

1. `CCTNS_Modules_Search&View_Figma_26052026_V2 (1).xlsx`
   - Workbook sheets reviewed: `accused_tables`, `Accused_flow_chart_Module_wise`, `accused_Proclaimed_offender_flow`, `Arrest Flow Chart`, `Apprehend_Flow_chart`, and associated lookup sheets.
   - Establishes table families, root/detail relationships, and the workbook author's interpretation of key links.
   - The workbook file timestamp/content is newer than the pasted query result, but it is a design/reference artifact, not proof of production constraints.

2. `pasted-text.txt` from the user's `information_schema.columns` query.
   - Establishes 53 production-visible tables in 18 schemas that directly contain a column named `accused_vid`.
   - Does not establish populated rows, foreign-key enforcement, event dates, or chronological order.

3. Ministry of Home Affairs, `Matrix of mandatory Data Sets to be shared as per Law`.
   - Establishes the official ICJS/CCTNS exchange domains: FIR, arrest memo, accused details, investigation, property/seizure, bail/remand, charge-sheet/final report, court process, prison/release, and judgment-related information.
   - URL: https://www.mha.gov.in/sites/default/files/2022-09/CCTNSMHA_11012016%5B1%5D.pdf

4. Ministry of Home Affairs, Lok Sabha Unstarred Question No. 4954, answered 1 April 2025.
   - Confirms the high-level computerized process sequence includes complaints/FIRs, investigation, charge-sheet, court disposal, and appeals.
   - URL: https://www.mha.gov.in/MHA1/Par2017/pdfs/par2025-pdfs/LS01042025/4954.pdf

5. Ministry of Home Affairs CCTNS/ICJS page.
   - Establishes cross-pillar integration among Police/CCTNS, Courts, Prisons, Forensic Labs, and Prosecution and the one-data-once-entry objective.
   - URL: https://www.mha.gov.in/en/divisionofmha/women-safety-division/cctns

## Evidence grades

- `confirmed`: shown in the workbook flow/table sheets and present in the pasted production metadata result.
- `database_confirmed`: present in the pasted production metadata result, but its chronological role is inferred from the module/table name until date/status columns and sample rows are verified.
- `inferred`: process placement follows official CCTNS/ICJS domains and common case flow, not a declared table-level event sequence.

## Key interpretation

- `accused_vid` is treated as the cross-case person identifier.
- `accused_srno` plus `fir_reg_num` is treated as the accused's case/FIR participation identifier.
- Module-specific serial numbers such as `arr_surr_srno`, `remand_srno`, `bailer_srno`, `accused_disposal_srno`, and `accused_appeal_srno` identify repeatable events or detail parents.
- Audit timestamps such as `record_created_on` and `record_updated_on` are not automatically business event times.

## Chart map

- Segment: direct database footprint.
- Question: how many direct `accused_vid` tables fall in each interpreted lifecycle area?
- Type: horizontal categorical bar.
- Fields: `phase`, `table_count`, plus `phase_order`, `core_table_count`, and `conditional_table_count` retained for context.
- Takeaway: most direct references are profile/detail or conditional branches; table count must not be read as time spent or mandatory steps.
- Palette: single-root blue with neutral labels; no redundant legend.
- Delivery: native chart inside `report.html`.

## Robustness and omissions

- The report does not claim that every table is populated for every accused.
- The report does not use audit timestamps to order legal events.
- The report does not force warrants, proclaimed-offender action, bail, seizure, freezing, appeal, Kalandra, IMOT, or data-bank entries into the mandatory backbone.
- The report does not enumerate every indirect child table in the final table because the production FK graph has not yet been queried. Query Q8 is provided for that purpose.
- A final typed `UNION ALL` timeline is intentionally deferred until Q5-Q8 confirm date columns, data types, and declared relationships.
