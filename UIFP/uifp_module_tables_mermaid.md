# UIFP Module Table Diagram

Source workbook: `CCTNS_Modules_Search&View_Figma_26052026_V2 (1).xlsx`

Filter used: `module_wise_json_postgres.complaint_type = uifp`

The `MODULES` sheet lists this module as `UIFP(unidentified Person)`.

## Flow Chart With PK/FK

```mermaid
flowchart LR
    ROOT["t_unidentified_person<br/><b>PK:</b> unident_reg_num<br/><b>Other keys:</b> reg_gd_num, linked_fir_num"]

    ENQ["t_unidentified_person_enquiry<br/><b>PK:</b> uifp_eo_srno<br/><b>FK:</b> unident_reg_num<br/><b>FK:</b> person_cd"]
    PERSON["t_uifp_persons<br/><b>PK:</b> person_cd<br/><b>FK:</b> unident_reg_num<br/><b>FK:</b> uifp_eo_srno"]

    DRESS["t_uifp_person_dress<br/><b>PK:</b> uifp_dress_srno<br/><b>FK:</b> unident_reg_num"]
    FILES["t_unidentified_person_files<br/><b>PK:</b> file_upload_srno<br/><b>FK:</b> unident_reg_num"]
    IDMARKS["t_unidentified_person_id_marks<br/><b>PK:</b> uifp_id_marks_srno<br/><b>FK:</b> unident_reg_num"]
    PHY["t_unidentified_person_phy_feature<br/><b>PK:</b> unid_phy_feat_srno<br/><b>FK:</b> unident_reg_num"]

    ADDR["t_uifp_persons_address<br/><b>PK:</b> addr_srno<br/><b>FK:</b> person_cd"]
    NATIONAL["t_unidentified_national_id<br/><b>PK:</b> national_id_srno<br/><b>FK:</b> person_cd"]

    ROOT -->|"PK unident_reg_num = FK unident_reg_num"| ENQ
    ROOT -->|"PK unident_reg_num = FK unident_reg_num"| PERSON
    ROOT -->|"PK unident_reg_num = FK unident_reg_num"| DRESS
    ROOT -->|"PK unident_reg_num = FK unident_reg_num"| FILES
    ROOT -->|"PK unident_reg_num = FK unident_reg_num"| IDMARKS
    ROOT -->|"PK unident_reg_num = FK unident_reg_num"| PHY

    ENQ -->|"PK uifp_eo_srno = FK uifp_eo_srno"| PERSON
    PERSON -->|"PK person_cd = FK person_cd"| ADDR
    PERSON -->|"PK person_cd = FK person_cd"| NATIONAL
    PERSON -.->|"PK person_cd = FK person_cd when identified"| ENQ

    classDef root fill:#eef6ff,stroke:#2563eb,stroke-width:2px,color:#111827
    classDef caseChild fill:#f8fafc,stroke:#64748b,color:#111827
    classDef personChild fill:#f0fdf4,stroke:#16a34a,color:#111827
    classDef enquiry fill:#fff7ed,stroke:#ea580c,color:#111827

    class ROOT root
    class ENQ enquiry
    class DRESS,FILES,IDMARKS,PHY caseChild
    class PERSON,ADDR,NATIONAL personChild
```

## ER Diagram

```mermaid
erDiagram
    t_unidentified_person ||--o{ t_unidentified_person_enquiry : "unident_reg_num"
    t_unidentified_person ||--o{ t_uifp_persons : "unident_reg_num"
    t_unidentified_person ||--o{ t_uifp_person_dress : "unident_reg_num"
    t_unidentified_person ||--o{ t_unidentified_person_files : "unident_reg_num"
    t_unidentified_person ||--o{ t_unidentified_person_id_marks : "unident_reg_num"
    t_unidentified_person ||--o{ t_unidentified_person_phy_feature : "unident_reg_num"

    t_unidentified_person_enquiry ||--o{ t_uifp_persons : "uifp_eo_srno"
    t_uifp_persons ||--o{ t_uifp_persons_address : "person_cd"
    t_uifp_persons ||--o{ t_unidentified_national_id : "person_cd"

    t_unidentified_person {
        bigint unident_reg_num PK
        int lang_cd
        int reg_year
        int unidentified_srno
        string reg_gd_num
        datetime reg_gd_dt
        bigint linked_fir_num
        datetime information_recv_dt
        int information_src_cd
        int information_mode_cd
        datetime found_dt
        string found_plc
        int found_condition_cd
        int case_status_cd
        int state_id
        int district_id
        int ps_id
        int pers_gender_cd
        int complexion_type_cd
        int build_type_cd
    }

    t_unidentified_person_enquiry {
        bigint uifp_eo_srno PK
        int lang_cd
        bigint unident_reg_num FK
        int assign_eo_cd
        datetime assign_dt
        int reassign_eo_cd
        datetime reassign_dt
        datetime enq_start_dt
        datetime enq_report_dt
        string enq_rep_status
        int enq_apprvr_cd
        boolean is_uifp_identified
        int action_taken_cd
        bigint person_cd FK
    }

    t_uifp_persons {
        bigint person_cd PK
        int lang_cd
        int person_type_cd
        bigint unident_reg_num FK
        bigint uifp_eo_srno FK
        string first_name
        string middle_name
        string last_name
        int nationality_cd
        int rel_type_cd
        string rel_name
        int gender_cd
        int age_yrs
        int age_mnths
        int age_type_cd
        json alias
    }

    t_uifp_persons_address {
        bigint addr_srno PK
        bigint person_cd FK
        int lang_cd
        int address_type_cd
        string address_line_1
        string address_line_2
        string address_line_3
        bigint sub_district_cd
        bigint village_cd
        int country_cd
        int pincode
        int lg_district_cd
        int ps_id
        int state_id
    }

    t_unidentified_national_id {
        bigint national_id_srno PK
        bigint person_cd FK
        int lang_cd
        int national_id_type_cd
        string national_id_num
        datetime passport_issue_dt
        string passport_issue_plc
    }

    t_uifp_person_dress {
        bigint uifp_dress_srno PK
        int lang_cd
        bigint unident_reg_num FK
        int dress_for_cd
        int dress_type_cd
        string dress_type
        int dress_subtype_cd
        string dress_subtype
        string othr_dress_dtls
    }

    t_unidentified_person_files {
        bigint file_upload_srno PK
        int lang_cd
        bigint unident_reg_num FK
        int file_srno
        int file_type_cd
        int file_subtype_cd
        string file_name
        string file_path
        string file_guid
        bigint file_belongs_to_srno
    }

    t_unidentified_person_id_marks {
        bigint uifp_id_marks_srno PK
        int lang_cd
        bigint unident_reg_num FK
        int id_marks_type_cd
        int body_part_loc_cd
        int tattoo_type_cd
        string tattoo_mark_desc
    }

    t_unidentified_person_phy_feature {
        bigint unid_phy_feat_srno PK
        int lang_cd
        bigint unident_reg_num FK
        string phy_feat_category
        int phy_feature_maj_cd
        int phy_feature_min_cd
        int phy_feat_category_cd
    }
```

## Tables Detected

| Table | Columns | Main Role |
|---|---:|---|
| `t_unidentified_person` | 71 | Root UIFP registration/case table |
| `t_unidentified_person_enquiry` | 44 | Enquiry assignment/report/closure details |
| `t_uifp_persons` | 39 | Identified/associated person details |
| `t_uifp_persons_address` | 28 | Address details for `t_uifp_persons` |
| `t_unidentified_national_id` | 15 | National ID details for `t_uifp_persons` |
| `t_uifp_person_dress` | 18 | Dress/apparel details linked to the UIFP registration |
| `t_unidentified_person_files` | 21 | Uploaded files linked to the UIFP registration |
| `t_unidentified_person_id_marks` | 15 | Identification marks linked to the UIFP registration |
| `t_unidentified_person_phy_feature` | 17 | Physical features linked to the UIFP registration |

## Relationship Notes

- `t_unidentified_person` is the root table by `unident_reg_num`.
- Most supporting tables carry `unident_reg_num`, so they attach directly to the root UIFP case.
- `t_uifp_persons` also carries `uifp_eo_srno`, so it can be tied to an enquiry row.
- `t_uifp_persons_address` and `t_unidentified_national_id` attach through `person_cd`.
- These relationships are inferred from shared key column names in the JSON sheet; the workbook does not expose database FK constraints directly.
