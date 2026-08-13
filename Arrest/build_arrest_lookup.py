#!/usr/bin/env python3
"""Build the Arrest lookup sheet and validate mapped sample codes.

The source workbook does not expose PostgreSQL FK metadata, so mappings are
reconciled from the module lookup sheets.  Shared Arrest/Apprehend fields use
the same master only when the column meaning and name agree.  Unsupported
code fields are emitted as ``??`` instead of being guessed.
"""

from __future__ import annotations

import csv
import json
import re
from collections import defaultdict
from pathlib import Path
from xml.etree import ElementTree as ET
from zipfile import ZipFile


ROOT = Path(__file__).resolve().parents[1]
WORKBOOK = ROOT / "CCTNS_Modules_Search&View_Figma_26052026_V2.xlsx"
LOOKUP_OUTPUT = Path(__file__).with_name("arrest_lookup.csv")
AUDIT_OUTPUT = Path(__file__).with_name("arrest_lookup_validation.csv")

MAIN_NS = "{http://schemas.openxmlformats.org/spreadsheetml/2006/main}"
REL_ID = "{http://schemas.openxmlformats.org/officeDocument/2006/relationships}id"

STAFF_SOURCE = "users.t_police_staff_info.staff_id"
STAFF_VALUE = "first_name, middle_name, last_name, login_id, rank_desc"


def master(api_code: str) -> tuple[str, str]:
    return (f"mdm.m_lookup_masters.look_up_code : {api_code}", "look_up_value")


def parent_master(api_code: str) -> tuple[str, str]:
    return (
        f"mdm.m_lookup_masters.look_up_parentcode : {api_code}",
        "look_up_parentvalue",
    )


# Reconciled against Apprehend_LookUp, property_lookup, Accused lookup sheets,
# and the exact Arrest mappings retained in repository history.
COMMON_MAPPINGS: dict[str, tuple[str, str]] = {
    "lang_cd": master("OFFCL_LANG"),
    "record_created_by": (STAFF_SOURCE, STAFF_VALUE),
    "record_updated_by": (STAFF_SOURCE, STAFF_VALUE),
    "act_cd": ("mdm.m_act.act_cd", "act_long"),
    "section_cd": ("mdm.m_section.section_code", "section, section_desc"),
    "address_type_cd": master("ADD_TYP"),
    "sub_district_cd": ("mdm.m_sub_district.sub_district_cd", "sub_district"),
    "village_cd": ("mdm.m_subdist_villages.village_cd", "village_name"),
    "country_cd": master("NATIONALITY"),
    "lg_district_cd": ("mdm.m_district.lg_district_cd", "lg_district_name"),
    "ps_id": ("mdm.m_police_station.ps_id", "ps, ps_cd"),
    "state_id": ("mdm.m_state.state_id", "state, state_cd"),
    "bank_cd": master("BANKS"),
    "account_type_cd": master("ACCNT_TYP"),
    "file_type_cd": master("UPLOAD_FILE_TYP"),
    "file_subtype_cd": master("UPLOAD_FILE_SUB_TYP"),
    "id_marks_type_cd": master("IDENTITY_MARKS"),
    "body_part_loc_cd": master("PHYSCL_FEATURES"),
    "tattoo_type_cd": master("IDENTITY_MARKS"),
    "nationality_id_type_cd": master("NTNL_ID_DOC_TYP"),
    "phy_feat_category_cd": master("PHY_DESC_TYP"),
    "phy_feature_maj_cd": parent_master("PHYSCL_FEATURES"),
    "phy_feature_min_cd": master("PHYSCL_FEATURES"),
    "relation_type_cd": master("RELATION_TYP"),
    "nationality_cd": master("NATIONALITY"),
    "occupation_cd": master("OCCUPATION"),
    "gender_cd": master("GENDER"),
    "marital_status_cd": master("MARTL_STATUS"),
    "age_type_cd": master("AGE_PANEL_TYPE"),
    "witn_evid_tender_cd": master("EVIDENCE_TENDERED"),
    "witn_category_cd": master("CATEGORY"),
    "dress_for_cd": master("PHY_DESC_TYP"),
    "dress_type_cd": master("PHYSCL_FEATURES"),
    "dress_subtype_cd": master("PHYSCL_FEATURES"),
    "quantity_unit_cd": master("MESURING_UNITS"),
}


TABLE_COLUMN_MAPPINGS: dict[tuple[str, str], tuple[str, str]] = {
    ("t_arrest_memo", "district_id"): (
        "mdm.m_district.district_id",
        "district, district_cd",
    ),
    ("t_arrest_memo", "arrest_type_cd"): master("ARR_SURR_TYPE"),
    ("t_arrest_memo", "othr_rel_type_cd"): master("RELATION_TYP"),
    ("t_arrest_memo", "religion_cd"): master("RELIGION"),
    ("t_arrest_memo", "national_id_type_cd"): master("NTNL_ID_DOC_TYP"),
    ("t_arrest_memo", "category_cd"): master("CATEGORY"),
    ("t_arrest_memo", "edu_qual_cd"): master("EDU_QUAL"),
    ("t_arrest_memo", "living_status_cd"): master("LIVING_STATUS"),
    ("t_arrest_memo", "age_proof_type_cd"): master("AGE_DETERM"),
    ("t_arrest_memo", "lang_dialect_cd"): master("LANG_DIALECTS"),
    ("t_arrest_memo", "language_dialect_cd"): master("LANG_DIALECTS"),
    ("t_arrest_memo", "blood_group_cd"): master("BLOOD_GROUP"),
    ("t_arrest_memo", "income_group_cd"): master("INCOME_GROUP"),
    ("t_arrest_memo", "arr_from_district_cd"): (
        "mdm.m_district.district_cd",
        "district, district_id",
    ),
    ("t_arrest_memo", "arrest_beat_cd"): ("mdm.m_ps_beat.beat_cd", "beat_name"),
    ("t_arrest_memo", "major_place_type_cd"): master("MAJOR_PLACE_OCCURANCE"),
    ("t_arrest_memo", "minor_place_type_cd"): master("PLACE_TYP"),
    ("t_arrest_memo", "dysp_login_id"): (STAFF_SOURCE, STAFF_VALUE),
    ("t_arrest_memo", "arrest_action_taken_cd"): master("ARRST_ACTN"),
    ("t_arrest_memo", "arrest_status_cd"): master("ARRST_STATUS"),
    ("t_arrest_memo", "io_cd"): (STAFF_SOURCE, STAFF_VALUE),
    ("t_arrest_memo", "intimate_rel_type_cd"): master("RELATION_TYP"),
    ("t_arrest_memo", "intimate_mode_cd"): master("INFO_MODE"),
    ("t_arrest_memo", "evidence_type_cd"): master("EVIDENCE_TYP"),
    ("t_arrest_memo", "id_type_cd"): master("IDENTITY_TYP"),
    ("t_arrest_memo", "build_type_cd"): master("PHY_FEAT_PCODE_BUILD"),
    ("t_arrest_memo", "complexion_type_cd"): master("PHY_FEAT_PCODE_COMPL"),
    ("t_arrest_memo", "arr_from_state_id"): (
        "mdm.m_state.state_id",
        "state, state_cd",
    ),
    ("t_arrest_memo", "arr_from_district_id"): (
        "mdm.m_district.district_id",
        "district, district_cd",
    ),
    ("t_arrest_memo", "arr_from_ps_id"): (
        "mdm.m_police_station.ps_id",
        "ps, ps_cd",
    ),
}


# These fields remain unsupported in the available lookup sheets.  Keeping an
# explicit unknown is safer than assigning a master merely because the sample
# code happens to exist in several unrelated master categories.
UNRESOLVED_FIELDS = {
    ("t_arrest_memo", "arrested_by_pis_cd"),
    ("t_arrest_memo", "arrested_police_cd"),
    ("t_arrest_memo", "surrend_in_court_cd"),
    ("t_arrest_memo", "criminal_gang_cd"),
    ("t_arrest_memo", "physical_cond_cd"),
    ("t_arrest_memo", "court_estbl_cd"),
}


def column_index(cell_reference: str) -> int:
    result = 0
    for char in cell_reference:
        if not char.isalpha():
            break
        result = result * 26 + ord(char.upper()) - 64
    return result - 1


def shared_strings(archive: ZipFile) -> list[str]:
    root = ET.fromstring(archive.read("xl/sharedStrings.xml"))
    return [
        "".join(node.text or "" for node in item.iter(MAIN_NS + "t"))
        for item in root.findall(MAIN_NS + "si")
    ]


def sheet_targets(archive: ZipFile) -> dict[str, str]:
    workbook = ET.fromstring(archive.read("xl/workbook.xml"))
    relationships = ET.fromstring(archive.read("xl/_rels/workbook.xml.rels"))
    rels = {node.attrib["Id"]: node.attrib["Target"] for node in relationships}
    return {
        sheet.attrib["name"]: "xl/" + rels[sheet.attrib[REL_ID]]
        for sheet in workbook.find(MAIN_NS + "sheets")
    }


def iter_rows(archive: ZipFile, path: str, strings: list[str]):
    with archive.open(path) as source:
        for _, row in ET.iterparse(source, events=("end",)):
            if row.tag != MAIN_NS + "row":
                continue
            cells: dict[int, str] = {}
            for cell in row.findall(MAIN_NS + "c"):
                value_node = cell.find(MAIN_NS + "v")
                cell_type = cell.attrib.get("t")
                if cell_type == "s" and value_node is not None:
                    value = strings[int(value_node.text)]
                elif cell_type == "inlineStr":
                    value = "".join(
                        node.text or "" for node in cell.iter(MAIN_NS + "t")
                    )
                elif value_node is not None:
                    value = value_node.text or ""
                else:
                    value = ""
                cells[column_index(cell.attrib.get("r", ""))] = value
            yield [cells.get(index, "") for index in range(max(cells, default=-1) + 1)]
            row.clear()


def load_arrest_tables() -> dict[str, dict[str, object]]:
    with ZipFile(WORKBOOK) as archive:
        strings = shared_strings(archive)
        target = sheet_targets(archive)["module_wise_json_postgres"]
        tables: dict[str, dict[str, object]] = {}
        for row in iter_rows(archive, target, strings):
            if len(row) >= 3 and row[0].strip().lower() == "arrest":
                tables[row[1]] = json.loads(row[2])
        return tables


def mapping_for(table: str, column: str) -> tuple[str, str]:
    if (table, column) in UNRESOLVED_FIELDS:
        return ("??", "??")
    if (table, column) in TABLE_COLUMN_MAPPINGS:
        return TABLE_COLUMN_MAPPINGS[(table, column)]
    if column in COMMON_MAPPINGS:
        return COMMON_MAPPINGS[column]
    if column.endswith("_cd"):
        return ("??", "??")
    return ("", "")


def json_fragment(column: str, value: object, final: bool) -> str:
    suffix = "" if final else ","
    return (
        json.dumps(column, ensure_ascii=False)
        + ":"
        + json.dumps(value, ensure_ascii=False, separators=(",", ":"))
        + suffix
    )


def write_lookup(tables: dict[str, dict[str, object]]) -> None:
    with LOOKUP_OUTPUT.open("w", encoding="utf-8-sig", newline="") as output:
        writer = csv.writer(output)
        writer.writerow(
            ["Table Name", "Column Name", "Lookup_code", "Lookup Value", "Sample Data"]
        )
        writer.writerow(["", "", "", "", "{"])
        table_items = list(tables.items())
        for table_index, (table, fields) in enumerate(table_items):
            writer.writerow([table, "", "", "", f'{json.dumps(table)}:['])
            writer.writerow(["", "", "", "", "{"])
            field_items = list(fields.items())
            for field_index, (column, value) in enumerate(field_items):
                lookup_code, lookup_value = mapping_for(table, column)
                writer.writerow(
                    [
                        "",
                        column,
                        lookup_code,
                        lookup_value,
                        json_fragment(column, value, field_index == len(field_items) - 1),
                    ]
                )
            writer.writerow(["", "", "", "", "}"])
            closing = "]" if table_index == len(table_items) - 1 else "],"
            writer.writerow(["", "", "", "", closing])
        writer.writerow(["", "", "", "", "}"])


def normalize_code(value: object) -> str:
    if value is None:
        return ""
    if isinstance(value, bool):
        return str(value).lower()
    if isinstance(value, float) and value.is_integer():
        return str(int(value))
    text = str(value).strip()
    # Excel stores many integer master codes as numeric strings such as
    # ``99.0`` while the JSON sample contains the integer ``99``.
    try:
        number = float(text)
    except ValueError:
        return text
    return str(int(number)) if number.is_integer() else str(number)


def load_master_index():
    codes: dict[tuple[str, str], list[tuple[str, str]]] = defaultdict(list)
    parents: dict[tuple[str, str], list[tuple[str, str]]] = defaultdict(list)
    categories: set[str] = set()
    with ZipFile(WORKBOOK) as archive:
        strings = shared_strings(archive)
        target = sheet_targets(archive)["m_lookup_masters"]
        for row_number, row in enumerate(iter_rows(archive, target, strings), start=1):
            if row_number == 1 or len(row) <= 12:
                continue
            api_code = row[12].strip()
            if not api_code or api_code == "NULL":
                continue
            categories.add(api_code)
            lang = normalize_code(row[7])
            code = normalize_code(row[6])
            value = row[8]
            parent_code = normalize_code(row[9])
            parent_value = row[10]
            if code and code != "NULL":
                codes[(api_code, code)].append((lang, value))
            if parent_code and parent_code != "NULL":
                parents[(api_code, parent_code)].append((lang, parent_value))
    return categories, codes, parents


def api_code_from_mapping(lookup_code: str) -> str:
    match = re.search(r":\s*([A-Za-z0-9_]+)\s*$", lookup_code)
    return match.group(1) if match else ""


def values_to_check(sample: object) -> list[str]:
    if isinstance(sample, list):
        return [normalize_code(value) for value in sample if value is not None]
    code = normalize_code(sample)
    return [code] if code else []


def preferred_values(rows: list[tuple[str, str]]) -> list[str]:
    english = [value for lang, value in rows if lang == "99" and value]
    source = english or [value for _, value in rows if value]
    return list(dict.fromkeys(source))


def write_audit(tables: dict[str, dict[str, object]]) -> None:
    categories, codes, parents = load_master_index()
    with AUDIT_OUTPUT.open("w", encoding="utf-8-sig", newline="") as output:
        writer = csv.writer(output)
        writer.writerow(
            [
                "table_name",
                "column_name",
                "sample_value",
                "lookup_code",
                "lookup_value",
                "api_master_code",
                "resolved_sample_value",
                "status",
                "evidence_note",
            ]
        )
        for table, fields in tables.items():
            for column, sample in fields.items():
                lookup_code, lookup_value = mapping_for(table, column)
                if not (lookup_code or column.endswith("_cd")):
                    continue
                api_code = api_code_from_mapping(lookup_code)
                resolved: list[str] = []
                note = ""
                if lookup_code == "??":
                    status = "unknown_lookup"
                    note = "No supported mapping in the compared lookup sheets."
                elif "m_lookup_masters" not in lookup_code:
                    status = "external_lookup"
                    note = "Resolved through a dedicated master/reference table."
                elif api_code not in categories:
                    status = "master_category_missing"
                    note = "The proposed API master code is absent from m_lookup_masters."
                else:
                    samples = values_to_check(sample)
                    if not samples:
                        status = "master_category_verified_null_sample"
                        note = "Master category exists; the JSON sample is null or empty."
                    else:
                        index = parents if "look_up_parentcode" in lookup_code else codes
                        missing = []
                        for code in samples:
                            rows = index.get((api_code, code), [])
                            if rows:
                                resolved.extend(preferred_values(rows))
                            else:
                                missing.append(code)
                        if missing:
                            status = "master_category_verified_sample_missing"
                            note = (
                                "Master category exists, but sample code(s) "
                                + ", ".join(missing)
                                + " are absent."
                            )
                        else:
                            status = "m_lookup_masters_value_verified"
                            note = "Sample code exists under the mapped master category."

                # The workbook carries denormalized dress labels, allowing two
                # direct consistency checks in addition to master-code presence.
                if table == "t_arrest_dress" and column == "dress_for_cd":
                    label = str(fields.get("dress_for", ""))
                    if resolved and label and label.casefold() not in {
                        value.casefold() for value in resolved
                    }:
                        note += f" JSON dress_for={label!r} conflicts with the resolved value."
                if table == "t_arrest_dress" and column == "dress_type_cd":
                    label = str(fields.get("dress_type", ""))
                    if resolved and label and label.casefold() not in {
                        value.casefold() for value in resolved
                    }:
                        note += f" JSON dress_type={label!r} conflicts with the resolved value."

                writer.writerow(
                    [
                        table,
                        column,
                        json.dumps(sample, ensure_ascii=False, separators=(",", ":")),
                        lookup_code,
                        lookup_value,
                        api_code,
                        " | ".join(dict.fromkeys(resolved)),
                        status,
                        note,
                    ]
                )


def main() -> None:
    tables = load_arrest_tables()
    if len(tables) != 15:
        raise RuntimeError(f"Expected 15 Arrest tables, found {len(tables)}")
    write_lookup(tables)
    write_audit(tables)
    print(
        f"Created {LOOKUP_OUTPUT.name} and {AUDIT_OUTPUT.name} "
        f"from {len(tables)} tables / {sum(len(fields) for fields in tables.values())} fields."
    )


if __name__ == "__main__":
    main()
