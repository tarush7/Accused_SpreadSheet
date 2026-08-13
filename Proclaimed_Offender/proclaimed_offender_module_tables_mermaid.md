# Proclaimed Offender Module — Order and Evidence

```mermaid
flowchart TB
    FIR(("<b>t_fir_registration</b><br/><br/>Parent FIR record<br/>Identified by fir_reg_num<br/><br/>Reference only"))
    ACC(("<b>t_fir_accused_info</b><br/><br/>Accused identity<br/>Reference only"))

    subgraph MODULE["Proclaimed Offender Tables"]
        direction LR

        ORDER["<b>t_accused_procl_offender</b><br/><br/>Stores the court order, order date,<br/>court details and proclamation actions<br/><br/><b>PK:</b> procl_offend_srno<br/><b>FK:</b> fir_reg_num / accused_srno / accused_vid"]

        FILES["<b>t_accused_procl_files</b><br/><br/>Stores proclamation documents,<br/>photographs, videos and other evidence<br/><br/><b>PK:</b> procl_file_srno<br/><b>FK:</b> procl_offend_srno"]

        ORDER -->|"parent-to-file link<br/>FK: procl_offend_srno"| FILES
    end

    FIR -.->|"parent FIR reference<br/>FK: fir_reg_num"| ORDER
    ACC -.->|"accused identity reference<br/>FK: accused_srno / accused_vid"| ORDER

    classDef reference fill:#ffffff,stroke:#303247,stroke-width:2px,color:#303247
    classDef main fill:#ffffff,stroke:#303247,stroke-width:2px,color:#303247
    classDef evidence fill:#ffffff,stroke:#303247,stroke-width:2px,color:#303247

    class FIR,ACC reference
    class ORDER main
    class FILES evidence
    style MODULE fill:#fafafa,stroke:#303247,stroke-width:2px
```

## How to read it

- Only the two rectangular tables inside the boundary belong to Proclaimed Offender ingestion.
- The circular tables are shared references owned by the FIR and Accused modules.
- Dotted lines mean lookup/reference only; they are not additional ingestion tables.
- The solid line is the module's main parent-to-evidence flow, ending at `t_accused_procl_files`.
- `t_fir_registration` supplies the parent FIR identified by `fir_reg_num`. It does not mean that FIR number and year are separate foreign keys in this flow.
- **PK** identifies a row within its own table; **FK** connects that row to its parent/reference table.
