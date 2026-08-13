#!/usr/bin/env python3
"""Build the Interro lookup CSV in the same shape as the non-Accused modules."""

from __future__ import annotations

import csv
import json
import re
from collections import defaultdict
from pathlib import Path
from posixpath import join as posix_join, normpath as posix_normpath
from xml.etree import ElementTree as ET
from zipfile import ZipFile


ROOT = Path(__file__).resolve().parent.parent
WORKBOOK = ROOT / "CCTNS_Modules_Search&View_Figma_26052026_V2 (1).xlsx"
OUTPUT = Path(__file__).resolve().parent / "interro_lookup.csv"
VALIDATION_OUTPUT = Path(__file__).resolve().parent / "interro_lookup_validation.csv"
MODULE_SHEET = "module_wise_json_postgres"
MASTER_SHEET = "m_lookup_masters"
COMPLAINT_TYPE = "interro"

MAIN_NS = "{http://schemas.openxmlformats.org/spreadsheetml/2006/main}"
REL_NS = "{http://schemas.openxmlformats.org/officeDocument/2006/relationships}"


def lookup_master(code: str) -> tuple[str, str]:
    return (f"mdm.m_lookup_masters.look_up_code : {code}", "look_up_value")


def lookup_parent(code: str) -> tuple[str, str]:
    return (f"mdm.m_lookup_masters.look_up_parentcode : {code}", "look_up_parentvalue")


STAFF = (
    "users.t_police_staff_info.staff_id",
    "first_name, middle_name, last_name, login_id, rank_desc",
)
STATE = ("mdm.m_state.state_id", "state, state_cd")
DISTRICT = ("mdm.m_district.district_id", "district, district_cd")
LG_DISTRICT = ("mdm.m_district.lg_district_cd", "lg_district_name")
POLICE_STATION = ("mdm.m_police_station.ps_id", "ps, ps_cd")
SUB_DISTRICT = ("mdm.m_sub_district.sub_district_cd", "sub_district")
VILLAGE = ("mdm.m_subdist_villages.village_cd", "village_name")
UNKNOWN = ("??", "??")


# Reused mappings are taken from UIFP, Apprehend, Arrest, and Proclaimed Offender.
# Interro-only mappings are selected from m_lookup_masters by master description/API code.
FIELD_MAP: dict[str, tuple[str, str]] = {
    "lang_cd": lookup_master("OFFCL_LANG"),
    "language_used": lookup_master("LANG_DIALECTS"),
    "addr_type_cd": lookup_master("ADD_TYP"),
    "addr_type": lookup_master("ADD_TYP"),
    "country_cd": lookup_master("NATIONALITY"),
    "sub_district_cd": SUB_DISTRICT,
    "village_cd": VILLAGE,
    "lg_district_cd": LG_DISTRICT,
    "ps_id": POLICE_STATION,
    "state_id": STATE,
    "method_type": lookup_master("MODUS_METHOD"),
    "bank_cd": lookup_master("BANKS"),
    "account_type_cd": lookup_master("ACCNT_TYP"),
    "character_type": lookup_master("CHARACTER_ASSUMED"),
    "conveyance_type": lookup_master("MV_TYP"),
    "conveyance_color": lookup_master("MV_COLOR"),
    "accused_status_cd": lookup_master("INTERRO_ACC_STATUS"),
    "acquit_reason_cd": lookup_master("ACQTL_RSN"),
    "dress_for_cd": lookup_master("PHY_DESC_TYP"),
    "dress_type_cd": lookup_master("PHYSCL_FEATURES"),
    "dress_subtype_cd": lookup_master("PHYSCL_FEATURES"),
    "habits_cd": lookup_master("PHYSCL_FEATURES"),
    "id_marks_type_cd": lookup_master("IDENTITY_MARKS"),
    "body_part_loc_cd": lookup_master("PHYSCL_FEATURES"),
    "tattoo_type_cd": lookup_master("IDENTITY_MARKS"),
    "edu_qual_cd": lookup_master("EDU_QUAL"),
    "gender_cd": lookup_master("GENDER"),
    "relation_type_cd": lookup_master("RELATION_TYP"),
    "relative_type_cd": lookup_master("RELATION_TYP"),
    "nationality_cd": lookup_master("NATIONALITY"),
    "national_id_type_cd": lookup_master("NTNL_ID_DOC_TYP"),
    "national_id_type": lookup_master("NTNL_ID_DOC_TYP"),
    "occupation_cd": lookup_master("OCCUPATION"),
    "religion_cd": lookup_master("RELIGION"),
    "living_status_cd": lookup_master("LIVING_STATUS"),
    "marital_status_cd": lookup_master("MARTL_STATUS"),
    "caste_cd": lookup_master("CASTE_TRIBE"),
    "category_cd": lookup_master("CATEGORY"),
    "income_group_cd": lookup_master("INCOME_GROUP"),
    "build_type_cd": lookup_master("PHY_FEAT_PCODE_BUILD"),
    "complexion_type_cd": lookup_master("PHY_FEAT_PCODE_COMPL"),
    "fir_status_cd": lookup_master("FIR_STATUS"),
    "crime_motive_type": lookup_master("CRM_MOTIVE"),
    "phy_feature_maj_cd": lookup_parent("PHYSCL_FEATURES"),
    "phy_feature_min_cd": lookup_master("PHYSCL_FEATURES"),
    "phy_feat_category_cd": lookup_master("PHY_DESC_TYP"),
    "prop_nature_cd": lookup_master("PROP_NATURE"),
    "property_type_cd": lookup_master("PROP_TYP"),
    "socialmedia_type": lookup_master("SOCIAL_MEDIA_TYP"),
    "accused_spl_feature": lookup_master("SPCL_FEATURE"),
    "file_type_cd": lookup_master("UPLOAD_FILE_TYP"),
    "file_subtype_cd": lookup_master("UPLOAD_FILE_SUB_TYP"),
    "interro_by_pis_cd": STAFF,
    "acc_police_cd": STAFF,
    "juv_age_proof": lookup_master("JUV_AGE_PROOF"),
    "offence_period_cd": lookup_master("OFFENCE_PERIOD"),
    "indulge_bef_offence_cd": lookup_master("BFR_OFFNC_ACT"),
    "offence_prepare_acts": lookup_master("CRM_PREPRATION"),
    "indulge_aft_offence_cd": lookup_master("AFTR_OFFNC_ACT"),
    "blood_group_cd": lookup_master("BLOOD_GROUP"),
    "record_created_by": STAFF,
    "record_updated_by": STAFF,
}


TABLE_FIELD_MAP: dict[tuple[str, str], tuple[str, str]] = {
    # In jail history this is release type, not a family relationship.
    ("t_interro_person_jail_details", "rel_type_cd"): lookup_master("JAIL_RELEASE_TYP"),
    ("t_interro_person_jail_details", "jail_stat_id"): STATE,
    ("t_interro_person_jail_details", "jail_dist_id"): DISTRICT,
    ("t_interro_person_jail_details", "jail_vill_cd"): VILLAGE,
    ("t_interro_person_jail_details", "jail_ps_id"): POLICE_STATION,
    ("t_interro_person_jail_details", "arrest_by_ps_id"): POLICE_STATION,
    ("t_interro_person_cases_confessed", "case_state_id"): STATE,
    ("t_interro_person_cases_confessed", "case_district_id"): DISTRICT,
    ("t_interro_person_cases_confessed", "case_ps_id"): POLICE_STATION,
    ("t_interro_person_convictions", "case_state_id"): STATE,
    ("t_interro_person_convictions", "case_district_id"): DISTRICT,
    ("t_interro_person_convictions", "case_ps_id"): POLICE_STATION,
    ("t_interro_person_jail_details", "arrest_state_id"): STATE,
    ("t_interro_person_jail_details", "arrest_district_id"): DISTRICT,
    ("t_interro_person_jail_details", "arrest_ps_id"): POLICE_STATION,
    ("t_interrogation_info", "arrest_state_id"): STATE,
    ("t_interrogation_info", "arrest_district_id"): DISTRICT,
    ("t_interrogation_info", "arrest_ps_id"): POLICE_STATION,
    # The two samples use code 7 for different agency names; no safe master can be assigned.
    ("t_interro_person_cases_confessed", "receiving_agency_cd"): UNKNOWN,
    ("t_interro_person_jail_details", "receive_agency_cd"): UNKNOWN,
    # No matching external/master-table convention was found in the non-Accused templates.
    ("t_interro_person_jail_details", "jail_city_cd"): UNKNOWN,
    ("t_interro_person_jail_details", "transf_jail_cd"): UNKNOWN,
    ("t_interro_person_property", "prop_disposed_by"): UNKNOWN,
}


# Domain checks that go beyond merely finding the same numeric code in a broad master.
EXPECTED_PARENT_LITERAL: dict[tuple[str, str], str] = {
    ("t_interro_person_habits", "habits_cd"): "52",  # PHYSCL_FEATURES parent HABITS
}

EXPECTED_PARENT_FIELD: dict[tuple[str, str], str] = {
    ("t_interro_person_phy_feature", "phy_feature_min_cd"): "phy_feature_maj_cd",
    ("t_interro_person_dress", "dress_subtype_cd"): "dress_type_cd",
    ("t_interrogation_files", "file_subtype_cd"): "file_type_cd",
}


def cell_column(reference: str) -> str:
    return re.match(r"[A-Z]+", reference).group()


def load_xlsx() -> tuple[dict[str, list[dict[str, str]]], list[str]]:
    """Return sheet rows keyed by sheet name and workbook sheet order."""
    with ZipFile(WORKBOOK) as archive:
        shared_strings: list[str] = []
        if "xl/sharedStrings.xml" in archive.namelist():
            root = ET.fromstring(archive.read("xl/sharedStrings.xml"))
            shared_strings = [
                "".join(node.text or "" for node in item.iter(f"{MAIN_NS}t"))
                for item in root.findall(f"{MAIN_NS}si")
            ]

        workbook = ET.fromstring(archive.read("xl/workbook.xml"))
        rels = ET.fromstring(archive.read("xl/_rels/workbook.xml.rels"))
        targets = {rel.attrib["Id"]: rel.attrib["Target"] for rel in rels}
        sheet_paths: dict[str, str] = {}
        order: list[str] = []
        for sheet in workbook.findall(f".//{MAIN_NS}sheet"):
            name = sheet.attrib["name"]
            target = targets[sheet.attrib[f"{REL_NS}id"]]
            path = target.lstrip("/") if target.startswith("/") else posix_normpath(posix_join("xl", target))
            sheet_paths[name] = path
            order.append(name)

        required = {MODULE_SHEET, MASTER_SHEET}
        missing = required - set(sheet_paths)
        if missing:
            raise RuntimeError(f"Required sheet(s) missing: {sorted(missing)}")

        def value(cell: ET.Element) -> str:
            cell_type = cell.attrib.get("t")
            raw = cell.find(f"{MAIN_NS}v")
            if cell_type == "inlineStr":
                return "".join(node.text or "" for node in cell.iter(f"{MAIN_NS}t"))
            if raw is None or raw.text is None:
                return ""
            return shared_strings[int(raw.text)] if cell_type == "s" else raw.text

        result: dict[str, list[dict[str, str]]] = {}
        for name in required:
            root = ET.fromstring(archive.read(sheet_paths[name]))
            xml_rows = root.findall(f".//{MAIN_NS}row")
            headers = {
                cell_column(cell.attrib["r"]): value(cell)
                for cell in xml_rows[0].findall(f"{MAIN_NS}c")
                if value(cell)
            }
            rows: list[dict[str, str]] = []
            for xml_row in xml_rows[1:]:
                row: dict[str, str] = {}
                for cell in xml_row.findall(f"{MAIN_NS}c"):
                    column = cell_column(cell.attrib["r"])
                    if column in headers:
                        row[headers[column]] = value(cell)
                rows.append(row)
            result[name] = rows
        return result, order


def module_rows(sheet_rows: list[dict[str, str]]) -> list[dict[str, object]]:
    output: list[dict[str, object]] = []
    for row in sheet_rows:
        if row.get("complaint_type", "").strip().lower() != COMPLAINT_TYPE:
            continue
        output.append({
            "table_name": row["table_name"],
            "json_value": json.loads(row["json_value"]),
        })
    return output


def mapping_for(table_name: str, field: str) -> tuple[str, str]:
    return TABLE_FIELD_MAP.get((table_name, field), FIELD_MAP.get(field, ("", "")))


def json_fragment(field: str, value: object, trailing_comma: bool) -> str:
    fragment = f"{json.dumps(field, ensure_ascii=False)}:{json.dumps(value, ensure_ascii=False, separators=(',', ':'))}"
    return fragment + ("," if trailing_comma else "")


def write_lookup(rows: list[dict[str, object]]) -> None:
    with OUTPUT.open("w", encoding="utf-8-sig", newline="") as handle:
        writer = csv.writer(handle)
        writer.writerow(["Table Name", "Column Name", "Lookup_code", "Lookup Value", "Sample Data"])
        writer.writerow(["", "", "", "", "{"])
        for table_index, row in enumerate(rows):
            table_name = str(row["table_name"])
            payload: dict[str, object] = row["json_value"]
            writer.writerow([table_name, "", "", "", f'"{table_name}":['])
            writer.writerow(["", "", "", "", "{"])
            fields = list(payload.items())
            for field_index, (field, sample_value) in enumerate(fields):
                lookup_code, lookup_value = mapping_for(table_name, field)
                writer.writerow([
                    "",
                    field,
                    lookup_code,
                    lookup_value,
                    json_fragment(field, sample_value, field_index < len(fields) - 1),
                ])
            writer.writerow(["", "", "", "", "}"])
            writer.writerow(["", "", "", "", "]," if table_index < len(rows) - 1 else "]"])
        writer.writerow(["", "", "", "", "}"])


def normalize_code(value: object) -> str:
    if value is None:
        return ""
    if isinstance(value, bool):
        return str(value).lower()
    text = str(value).strip()
    try:
        number = float(text)
        return str(int(number)) if number.is_integer() else str(number)
    except ValueError:
        return text


def master_index(rows: list[dict[str, str]]) -> dict[str, list[dict[str, str]]]:
    result: dict[str, list[dict[str, str]]] = defaultdict(list)
    for row in rows:
        code = row.get("api_master_code", "").strip()
        if code and code != "NULL":
            result[code].append(row)
    return result


def write_validation(rows: list[dict[str, object]], master_rows: list[dict[str, str]]) -> None:
    masters = master_index(master_rows)
    configured_groups = {
        match.group(1)
        for lookup_code, _ in [*FIELD_MAP.values(), *TABLE_FIELD_MAP.values()]
        if (match := re.search(r":\s*([A-Z0-9_]+)$", lookup_code))
    }
    missing_groups = configured_groups - set(masters)
    if missing_groups:
        raise RuntimeError(f"Configured m_lookup_masters group(s) not found: {sorted(missing_groups)}")

    with VALIDATION_OUTPUT.open("w", encoding="utf-8-sig", newline="") as handle:
        writer = csv.writer(handle)
        writer.writerow([
            "Table Name", "Column Name", "Sample Value", "Lookup_code", "Lookup Value",
            "Validation Status", "Resolved Sample Value", "Notes",
        ])
        for row in rows:
            table_name = str(row["table_name"])
            for field, sample_value in row["json_value"].items():
                lookup_code, lookup_value = mapping_for(table_name, field)
                if not lookup_code:
                    continue

                status = ""
                resolved = ""
                notes = ""
                if lookup_code == "??":
                    status = "unresolved"
                    if field in {"receiving_agency_cd", "receive_agency_cd"}:
                        notes = "Sample code 7 is paired with conflicting agency names; no safe master assignment."
                    else:
                        notes = "No matching convention in the non-Accused lookup templates or m_lookup_masters."
                elif "m_lookup_masters" not in lookup_code:
                    status = "external_master_not_validated"
                    notes = "Mapped from the established non-Accused module lookup convention."
                elif sample_value is None:
                    status = "blank_sample"
                    notes = "Mapping retained, but the supplied sample value is null."
                else:
                    group = re.search(r":\s*([A-Z0-9_]+)$", lookup_code).group(1)
                    source_field = "look_up_parentcode" if "look_up_parentcode" in lookup_code else "look_up_code"
                    label_field = "look_up_parentvalue" if source_field == "look_up_parentcode" else "look_up_value"
                    target = normalize_code(sample_value)
                    candidates = [
                        master for master in masters[group]
                        if normalize_code(master.get(source_field)) == target
                    ]
                    if candidates:
                        preferred = next((master for master in candidates if normalize_code(master.get("lang_cd")) == "99"), candidates[0])
                        resolved = preferred.get(label_field, "")
                        expected_parent = EXPECTED_PARENT_LITERAL.get((table_name, field))
                        parent_field = EXPECTED_PARENT_FIELD.get((table_name, field))
                        if parent_field:
                            expected_parent = normalize_code(row["json_value"].get(parent_field))
                        if expected_parent and source_field == "look_up_code":
                            domain_candidates = [
                                master for master in candidates
                                if normalize_code(master.get("look_up_parentcode")) == expected_parent
                            ]
                            if domain_candidates:
                                preferred = next(
                                    (master for master in domain_candidates if normalize_code(master.get("lang_cd")) == "99"),
                                    domain_candidates[0],
                                )
                                resolved = preferred.get(label_field, "")
                                status = "matched"
                            else:
                                status = "master_domain_mismatch"
                                notes = f"Code exists, but not under expected parent code {expected_parent}."
                        else:
                            status = "matched"
                    elif target in {"0", "-1"}:
                        status = "sentinel_not_in_master"
                        notes = "Sample appears to use a sentinel/default value not present in the master."
                    else:
                        status = "sample_not_in_master"
                        notes = "Mapping is semantically supported, but the supplied sample code is absent from this master group."

                writer.writerow([
                    table_name,
                    field,
                    json.dumps(sample_value, ensure_ascii=False, separators=(",", ":")),
                    lookup_code,
                    lookup_value,
                    status,
                    resolved,
                    notes,
                ])


def validate_output(rows: list[dict[str, object]]) -> None:
    with OUTPUT.open(encoding="utf-8-sig", newline="") as handle:
        output_rows = list(csv.reader(handle))
    if output_rows[0] != ["Table Name", "Column Name", "Lookup_code", "Lookup Value", "Sample Data"]:
        raise RuntimeError("Lookup CSV header does not match the non-Accused five-column format.")

    expected_pairs = {
        (str(row["table_name"]), field)
        for row in rows
        for field in row["json_value"]
    }
    actual_pairs: set[tuple[str, str]] = set()
    current_table = ""
    for output_row in output_rows[1:]:
        output_row += [""] * (5 - len(output_row))
        if output_row[0]:
            current_table = output_row[0]
        if output_row[1]:
            actual_pairs.add((current_table, output_row[1]))
    if expected_pairs != actual_pairs:
        raise RuntimeError(
            f"Lookup field coverage mismatch. Missing={sorted(expected_pairs-actual_pairs)}; "
            f"extra={sorted(actual_pairs-expected_pairs)}"
        )


def main() -> None:
    sheets, _ = load_xlsx()
    rows = module_rows(sheets[MODULE_SHEET])
    if len(rows) != 24:
        raise RuntimeError(f"Expected 24 interro tables, found {len(rows)}")
    write_lookup(rows)
    write_validation(rows, sheets[MASTER_SHEET])
    validate_output(rows)

    field_count = sum(len(row["json_value"]) for row in rows)
    mapped_count = sum(
        1
        for row in rows
        for field in row["json_value"]
        if mapping_for(str(row["table_name"]), field)[0]
    )
    unresolved_count = sum(
        1
        for row in rows
        for field in row["json_value"]
        if mapping_for(str(row["table_name"]), field)[0] == "??"
    )
    print(
        f"Built {OUTPUT.name}: {len(rows)} tables, {field_count} fields, "
        f"{mapped_count} lookup/reference mappings, {unresolved_count} unresolved mappings."
    )
    print(f"Validation details: {VALIDATION_OUTPUT.name}")


if __name__ == "__main__":
    main()
