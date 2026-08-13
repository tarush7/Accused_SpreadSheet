# Interro module flow diagram

This first-pass ER flow was generated from `CCTNS_Modules_Search&View_Figma_26052026_V2 (1).xlsx`, sheet `module_wise_json_postgres`, filtered to `complaint_type = interro`.

- Source records: 24
- Workbook rows: 350 and 405–428
- Core parent: `t_interrogation_info`
- Main relationship: child tables carry `interro_srno`
- Secondary relationship: `t_interro_emp_rel_prop_addr` carries employer, relative, and property references

Files:

- `interro_flow_diagram.svg` — reviewable visual
- `interro_flow_diagram.png` — raster preview
- `interro_flow_diagram.mmd` — editable Mermaid ER source
- `interro_source_rows.csv` — traceability back to workbook rows and relationship keys
- `build_interro_flow.py` — reproducible extractor and renderer
- `embed_interro_flow_sheet.py` — reproducibly inserts the diagram in a workbook copy
- `CCTNS_Modules_Search&View_Figma_26052026_V2_with_interro_flow.xlsx` — workbook copy with the new `interro_flow_chart` sheet

Note: relationship directions are inferred from field names in the supplied JSON samples. Confirm them against the PostgreSQL foreign-key definitions before treating the diagram as a physical database ERD.
