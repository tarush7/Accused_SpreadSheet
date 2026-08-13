#!/usr/bin/env python3
"""Build a source-backed ER flow for the `interro` module without XLSX libraries."""

from __future__ import annotations

import csv
import html
import json
import re
from pathlib import Path
from xml.etree import ElementTree as ET
from zipfile import ZipFile


ROOT = Path(__file__).resolve().parent.parent
WORKBOOK = ROOT / "CCTNS_Modules_Search&View_Figma_26052026_V2 (1).xlsx"
OUT_DIR = Path(__file__).resolve().parent
SHEET_NAME = "module_wise_json_postgres"
COMPLAINT_TYPE = "interro"

MAIN_NS = "{http://schemas.openxmlformats.org/spreadsheetml/2006/main}"
REL_NS = "{http://schemas.openxmlformats.org/officeDocument/2006/relationships}"
PKG_REL_NS = "{http://schemas.openxmlformats.org/package/2006/relationships}"


TABLES = {
    "t_interrogation_info": {
        "pk": "interro_srno",
        "fields": ["interro_srno PK", "fir_reg_num FK", "accused_srno FK", "interro_dt", "interro_statement"],
    },
    "t_interro_person_info": {
        "pk": "person_code",
        "fields": ["person_code PK", "interro_srno FK", "name / DOB / gender", "identity & contact"],
    },
    "t_interro_person_addr": {
        "pk": "interro_addr_srno",
        "fields": ["interro_addr_srno PK", "interro_srno FK", "fir_reg_num FK", "address / location"],
    },
    "t_interro_person_nationality": {
        "pk": "national_id_srno",
        "fields": ["national_id_srno PK", "interro_srno FK", "nationality_cd", "national ID / passport"],
    },
    "t_interro_person_socialmedia": {
        "pk": "interro_soc_med_srno",
        "fields": ["interro_soc_med_srno PK", "interro_srno FK", "socialmedia_type", "ID / email / mobile"],
    },
    "t_interro_person_phy_feature": {
        "pk": "interro_phy_feat_srno",
        "fields": ["interro_phy_feat_srno PK", "interro_srno FK", "feature category", "major / minor feature"],
    },
    "t_interro_person_spl_feature": {
        "pk": "acc_feat_srno",
        "fields": ["acc_feat_srno PK", "interro_srno FK", "fir_reg_num FK", "special feature"],
    },
    "t_interro_person_id_marks": {
        "pk": "identiy_marks_srno",
        "fields": ["identiy_marks_srno PK", "interro_srno FK", "body part", "tattoo / mark details"],
    },
    "t_interro_person_dress": {
        "pk": "interro_dress_srno",
        "fields": ["interro_dress_srno PK", "interro_srno FK", "dress type / subtype", "other details"],
    },
    "t_interro_person_language": {
        "pk": "acc_lang_srno",
        "fields": ["acc_lang_srno PK", "interro_srno FK", "fir_reg_num FK", "language_used"],
    },
    "t_interro_person_method": {
        "pk": "acc_method_srno",
        "fields": ["acc_method_srno PK", "interro_srno FK", "fir_reg_num FK", "method_type"],
    },
    "t_interro_person_character": {
        "pk": "acc_charac_srno",
        "fields": ["acc_charac_srno PK", "interro_srno FK", "fir_reg_num FK", "character_type"],
    },
    "t_interro_person_habits": {
        "pk": "habits_srno",
        "fields": ["habits_srno PK", "interro_srno FK", "fir_reg_num FK", "habits_cd"],
    },
    "t_interro_person_motive": {
        "pk": "acc_motive_srno",
        "fields": ["acc_motive_srno PK", "interro_srno FK", "fir_reg_num FK", "crime_motive_type"],
    },
    "t_interro_person_cases_confessed": {
        "pk": "confess_case_srno",
        "fields": ["confess_case_srno PK", "interro_srno FK", "case FIR / year", "receiving agency"],
    },
    "t_interro_person_convictions": {
        "pk": "convict_case_srno",
        "fields": ["convict_case_srno PK", "interro_srno FK", "case FIR / year", "conviction details"],
    },
    "t_interro_person_jail_details": {
        "pk": "acc_jail_srno",
        "fields": ["acc_jail_srno PK", "interro_srno FK", "jail / arrest details", "sentence / parole"],
    },
    "t_interro_person_employer": {
        "pk": "employer_srno",
        "fields": ["employer_srno PK", "interro_srno FK", "employer / phone", "employment period"],
    },
    "t_interro_person_relatives": {
        "pk": "relative_srno",
        "fields": ["relative_srno PK", "interro_srno FK", "relative / alias", "criminal background"],
    },
    "t_interro_person_property": {
        "pk": "acc_prop_srno",
        "fields": ["acc_prop_srno PK", "interro_srno FK", "property type", "disposal details"],
    },
    "t_interro_person_bank_dtls": {
        "pk": "interro_bank_srno",
        "fields": ["interro_bank_srno PK", "interro_srno FK", "bank / account type", "account number"],
    },
    "t_interro_person_conveyance": {
        "pk": "interro_conv_srno",
        "fields": ["interro_conv_srno PK", "interro_srno FK", "type / color", "vehicle number"],
    },
    "t_interrogation_files": {
        "pk": "interro_file_srno",
        "fields": ["interro_file_srno PK", "interro_srno FK", "file type / name", "path / GUID"],
    },
    "t_interro_emp_rel_prop_addr": {
        "pk": "address_cd",
        "fields": ["address_cd PK", "employer_srno FK", "relative_srno FK", "acc_prop_srno FK", "address / location"],
    },
}


CATEGORIES = [
    ("Identity & Contact", [
        "t_interro_person_info",
        "t_interro_person_addr",
        "t_interro_person_nationality",
        "t_interro_person_socialmedia",
    ]),
    ("Physical Description", [
        "t_interro_person_phy_feature",
        "t_interro_person_spl_feature",
        "t_interro_person_id_marks",
        "t_interro_person_dress",
    ]),
    ("Behaviour & Context", [
        "t_interro_person_language",
        "t_interro_person_method",
        "t_interro_person_character",
        "t_interro_person_habits",
        "t_interro_person_motive",
    ]),
    ("Criminal History", [
        "t_interro_person_cases_confessed",
        "t_interro_person_convictions",
        "t_interro_person_jail_details",
    ]),
    ("Employment & Network", [
        "t_interro_person_employer",
        "t_interro_person_relatives",
    ]),
    ("Assets & Evidence", [
        "t_interro_person_property",
        "t_interro_person_bank_dtls",
        "t_interro_person_conveyance",
        "t_interrogation_files",
    ]),
]


def read_sheet_rows() -> list[dict[str, object]]:
    """Read the requested XLSX sheet via its XML parts."""
    with ZipFile(WORKBOOK) as archive:
        workbook = ET.fromstring(archive.read("xl/workbook.xml"))
        relations = ET.fromstring(archive.read("xl/_rels/workbook.xml.rels"))
        target_by_id = {rel.attrib["Id"]: rel.attrib["Target"] for rel in relations}

        sheet_target = None
        for sheet in workbook.findall(f".//{MAIN_NS}sheet"):
            if sheet.attrib["name"] == SHEET_NAME:
                relation_id = sheet.attrib[f"{REL_NS}id"]
                target = target_by_id[relation_id].lstrip("/")
                sheet_target = target if target.startswith("xl/") else f"xl/{target}"
                break
        if not sheet_target:
            raise RuntimeError(f"Sheet not found: {SHEET_NAME}")

        shared_strings: list[str] = []
        if "xl/sharedStrings.xml" in archive.namelist():
            shared_root = ET.fromstring(archive.read("xl/sharedStrings.xml"))
            for item in shared_root.findall(f"{MAIN_NS}si"):
                shared_strings.append("".join(node.text or "" for node in item.iter(f"{MAIN_NS}t")))

        def cell_value(cell: ET.Element) -> str:
            value = cell.find(f"{MAIN_NS}v")
            cell_type = cell.attrib.get("t")
            if cell_type == "inlineStr":
                return "".join(node.text or "" for node in cell.iter(f"{MAIN_NS}t"))
            if value is None or value.text is None:
                return ""
            return shared_strings[int(value.text)] if cell_type == "s" else value.text

        sheet_root = ET.fromstring(archive.read(sheet_target))
        xml_rows = sheet_root.findall(f".//{MAIN_NS}row")
        header_cells = {
            re.match(r"[A-Z]+", cell.attrib["r"]).group(): cell_value(cell)
            for cell in xml_rows[0].findall(f"{MAIN_NS}c")
        }
        headers = {column: name for column, name in header_cells.items() if name}
        output: list[dict[str, object]] = []
        for xml_row in xml_rows[1:]:
            values = {
                headers[column]: cell_value(cell)
                for cell in xml_row.findall(f"{MAIN_NS}c")
                if (column := re.match(r"[A-Z]+", cell.attrib["r"]).group()) in headers
            }
            if str(values.get("complaint_type", "")).strip().lower() != COMPLAINT_TYPE:
                continue
            raw_json = str(values.get("json_value", ""))
            parsed_json = json.loads(raw_json)
            output.append({
                "workbook_row": int(xml_row.attrib["r"]),
                "complaint_type": values["complaint_type"],
                "table_name": values["table_name"],
                "json_value": parsed_json,
            })
        return output


def validate(rows: list[dict[str, object]]) -> None:
    observed = {str(row["table_name"]) for row in rows}
    expected = set(TABLES)
    if observed != expected:
        raise RuntimeError(
            "Interro table mismatch. "
            f"Missing: {sorted(expected - observed)}; unexpected: {sorted(observed - expected)}"
        )
    for row in rows:
        table_name = str(row["table_name"])
        keys = set(row["json_value"])
        if TABLES[table_name]["pk"] not in keys:
            raise RuntimeError(f"Expected primary key absent from {table_name}: {TABLES[table_name]['pk']}")
        if table_name not in {"t_interrogation_info", "t_interro_emp_rel_prop_addr"} and "interro_srno" not in keys:
            raise RuntimeError(f"Expected interro_srno absent from {table_name}")


def write_source_csv(rows: list[dict[str, object]]) -> None:
    output_path = OUT_DIR / "interro_source_rows.csv"
    with output_path.open("w", encoding="utf-8", newline="") as handle:
        writer = csv.writer(handle)
        writer.writerow(["workbook_row", "complaint_type", "table_name", "json_key_count", "relationship_keys"])
        for row in sorted(rows, key=lambda item: int(item["workbook_row"])):
            keys = list(row["json_value"])
            relationship_keys = [
                key for key in keys
                if key in {"interro_srno", "fir_reg_num", "accused_srno", "person_code", "employer_srno", "relative_srno", "acc_prop_srno", "address_cd"}
            ]
            writer.writerow([
                row["workbook_row"], row["complaint_type"], row["table_name"], len(keys),
                ", ".join(relationship_keys),
            ])


def esc(value: object) -> str:
    return html.escape(str(value), quote=True)


def svg_text(x: float, y: float, text: str, size: int, weight: int = 400,
             fill: str = "#1f2937", anchor: str = "start", family: str = "Inter, Arial, sans-serif") -> str:
    return (
        f'<text x="{x}" y="{y}" font-family="{family}" font-size="{size}" '
        f'font-weight="{weight}" fill="{fill}" text-anchor="{anchor}">{esc(text)}</text>'
    )


def table_box(x: int, y: int, table_name: str, width: int = 380) -> tuple[str, dict[str, float]]:
    fields = TABLES[table_name]["fields"]
    height = 60 + 22 * len(fields)
    parts = [
        f'<g id="{esc(table_name)}">',
        f'<rect x="{x}" y="{y}" width="{width}" height="{height}" rx="12" fill="#ffffff" stroke="#8b7cf6" stroke-width="2"/>',
        f'<path d="M {x+12} {y} H {x+width-12} Q {x+width} {y} {x+width} {y+12} V {y+43} H {x} V {y+12} Q {x} {y} {x+12} {y}" fill="#ede9fe"/>',
        f'<line x1="{x}" y1="{y+43}" x2="{x+width}" y2="{y+43}" stroke="#8b7cf6" stroke-width="2"/>',
        svg_text(x + width / 2, y + 28, table_name, 15, 700, "#312e81", "middle"),
    ]
    for index, field in enumerate(fields):
        color = "#7c3aed" if field.endswith(" PK") else "#2563eb" if " FK" in field else "#475569"
        parts.append(svg_text(x + 15, y + 67 + 22 * index, field, 13, 600 if " PK" in field or " FK" in field else 400, color))
    parts.append("</g>")
    anchors = {"left": x, "right": x + width, "top": y, "bottom": y + height, "center_x": x + width / 2, "center_y": y + height / 2}
    return "\n".join(parts), anchors


def write_svg() -> None:
    canvas_w, canvas_h = 2624, 1600
    group_xs = [36, 464, 892, 1320, 1748, 2176]
    group_y, group_w = 342, 412
    header_h, table_y, table_gap = 48, 405, 16
    table_w = 380
    table_anchors: dict[str, dict[str, float]] = {}
    chunks = [
        f'<svg xmlns="http://www.w3.org/2000/svg" width="{canvas_w}" height="{canvas_h}" viewBox="0 0 {canvas_w} {canvas_h}">',
        "<defs>",
        '<filter id="shadow" x="-10%" y="-10%" width="120%" height="130%"><feDropShadow dx="0" dy="4" stdDeviation="7" flood-color="#0f172a" flood-opacity="0.09"/></filter>',
        '<marker id="arrow" markerWidth="8" markerHeight="8" refX="7" refY="4" orient="auto"><path d="M0,0 L8,4 L0,8 Z" fill="#64748b"/></marker>',
        '<marker id="arrow-orange" markerWidth="8" markerHeight="8" refX="7" refY="4" orient="auto"><path d="M0,0 L8,4 L0,8 Z" fill="#d97706"/></marker>',
        "</defs>",
        '<rect width="2624" height="1600" fill="#f8fafc"/>',
        svg_text(40, 42, "Interro Module — Table Relationship Flow", 28, 750, "#0f172a"),
        svg_text(40, 69, "Source: module_wise_json_postgres · complaint_type = interro · 24 tables", 15, 400, "#64748b"),
    ]

    # Hub table.
    hub_x, hub_y, hub_w, hub_h = 914, 92, 796, 172
    chunks.extend([
        f'<g filter="url(#shadow)"><rect x="{hub_x}" y="{hub_y}" width="{hub_w}" height="{hub_h}" rx="18" fill="#ffffff" stroke="#5b4bdb" stroke-width="3"/>',
        f'<rect x="{hub_x}" y="{hub_y}" width="{hub_w}" height="50" rx="18" fill="#5b4bdb"/>',
        f'<rect x="{hub_x}" y="{hub_y+32}" width="{hub_w}" height="18" fill="#5b4bdb"/>',
        svg_text(hub_x + hub_w / 2, hub_y + 33, "t_interrogation_info  •  CORE / PARENT", 19, 750, "#ffffff", "middle"),
        svg_text(hub_x + 25, hub_y + 78, "interro_srno  PK", 15, 700, "#7c3aed"),
        svg_text(hub_x + 25, hub_y + 105, "fir_reg_num  FK", 15, 700, "#2563eb"),
        svg_text(hub_x + 25, hub_y + 132, "accused_srno  FK", 15, 700, "#2563eb"),
        svg_text(hub_x + 285, hub_y + 78, "interro_dt", 15, 500, "#475569"),
        svg_text(hub_x + 285, hub_y + 105, "interro_statement", 15, 500, "#475569"),
        svg_text(hub_x + 285, hub_y + 132, "flags: accused · juvenile · medical · disclosures", 15, 500, "#475569"),
        "</g>",
    ])

    # Main one-to-many relationship bus.
    group_centres = [x + group_w / 2 for x in group_xs]
    bus_y = 308
    chunks.extend([
        f'<path d="M {hub_x + hub_w/2} {hub_y + hub_h} V {bus_y} H {group_centres[-1]} M {hub_x + hub_w/2} {bus_y} H {group_centres[0]}" fill="none" stroke="#5b4bdb" stroke-width="3"/>',
        f'<rect x="{hub_x + hub_w/2 - 74}" y="{bus_y-15}" width="148" height="30" rx="15" fill="#ede9fe" stroke="#8b7cf6"/>',
        svg_text(hub_x + hub_w/2, bus_y + 5, "1 : N  interro_srno", 12, 700, "#5b4bdb", "middle"),
    ])

    for index, (category_name, table_names) in enumerate(CATEGORIES):
        x = group_xs[index]
        max_table_h = max(60 + 22 * len(TABLES[name]["fields"]) for name in table_names)
        group_h = (table_y - group_y) + len(table_names) * max_table_h + (len(table_names) - 1) * table_gap + 18
        chunks.extend([
            f'<path d="M {group_centres[index]} {bus_y} V {group_y}" fill="none" stroke="#5b4bdb" stroke-width="2.5"/>',
            f'<rect x="{x}" y="{group_y}" width="{group_w}" height="{group_h}" rx="18" fill="#f5f3ff" stroke="#c4b5fd" stroke-width="1.5"/>',
            f'<rect x="{x}" y="{group_y}" width="{group_w}" height="{header_h}" rx="18" fill="#ddd6fe"/>',
            f'<rect x="{x}" y="{group_y+30}" width="{group_w}" height="18" fill="#ddd6fe"/>',
            svg_text(x + group_w / 2, group_y + 30, category_name, 16, 750, "#3730a3", "middle"),
        ])
        trunk_x = x + 8
        current_y = table_y
        table_centres = []
        for table_name in table_names:
            box, anchors = table_box(x + 16, current_y, table_name, table_w)
            table_anchors[table_name] = anchors
            table_centres.append(anchors["center_y"])
            chunks.append(box)
            current_y += max_table_h + table_gap
        chunks.append(f'<path d="M {group_centres[index]} {group_y+header_h} H {trunk_x} V {table_centres[-1]}" fill="none" stroke="#7c83a3" stroke-width="1.5"/>')
        for table_name in table_names:
            a = table_anchors[table_name]
            chunks.append(f'<path d="M {trunk_x} {a["center_y"]} H {a["left"]}" fill="none" stroke="#7c83a3" stroke-width="1.5" marker-end="url(#arrow)"/>')

    # Shared address child with three alternate parent references.
    shared_name = "t_interro_emp_rel_prop_addr"
    shared_x, shared_y, shared_w = 1854, 1200, 720
    shared_h = 60 + 22 * len(TABLES[shared_name]["fields"])
    chunks.extend([
        f'<rect x="{shared_x}" y="{shared_y}" width="{shared_w}" height="{shared_h}" rx="14" fill="#fffbeb" stroke="#f59e0b" stroke-width="2.5" filter="url(#shadow)"/>',
        f'<path d="M {shared_x+14} {shared_y} H {shared_x+shared_w-14} Q {shared_x+shared_w} {shared_y} {shared_x+shared_w} {shared_y+14} V {shared_y+46} H {shared_x} V {shared_y+14} Q {shared_x} {shared_y} {shared_x+14} {shared_y}" fill="#fef3c7"/>',
        f'<line x1="{shared_x}" y1="{shared_y+46}" x2="{shared_x+shared_w}" y2="{shared_y+46}" stroke="#f59e0b" stroke-width="2"/>',
        svg_text(shared_x + shared_w / 2, shared_y + 30, shared_name + "  •  SHARED ADDRESS", 16, 750, "#92400e", "middle"),
    ])
    for i, field in enumerate(TABLES[shared_name]["fields"]):
        color = "#7c3aed" if field.endswith(" PK") else "#b45309" if " FK" in field else "#475569"
        chunks.append(svg_text(shared_x + 18 + (i % 3) * 225, shared_y + 75 + (i // 3) * 29, field, 14, 650 if " PK" in field or " FK" in field else 400, color))

    for parent_name, start_side, route_x, target_x in [
        ("t_interro_person_employer", "left", 1734, shared_x + 180),
        ("t_interro_person_relatives", "right", 2166, shared_x + 360),
        ("t_interro_person_property", "right", 2592, shared_x + 540),
    ]:
        parent = table_anchors[parent_name]
        start_x = parent[start_side]
        chunks.append(
            f'<path d="M {start_x} {parent["center_y"]} H {route_x} V {shared_y-28} H {target_x} V {shared_y}" '
            'fill="none" stroke="#d97706" stroke-width="2" stroke-dasharray="7 5" marker-end="url(#arrow-orange)"/>'
        )
    chunks.append(svg_text(shared_x + shared_w / 2, shared_y - 40, "Address belongs to employer OR relative OR property", 13, 700, "#b45309", "middle"))

    # Legend and source notes.
    chunks.extend([
        '<rect x="40" y="1437" width="1710" height="112" rx="14" fill="#ffffff" stroke="#cbd5e1"/>',
        svg_text(62, 1468, "Reading the flow", 15, 750, "#0f172a"),
        svg_text(62, 1496, "PK", 13, 750, "#7c3aed"),
        svg_text(92, 1496, "primary key", 13, 400, "#475569"),
        svg_text(225, 1496, "FK", 13, 750, "#2563eb"),
        svg_text(255, 1496, "foreign key", 13, 400, "#475569"),
        svg_text(406, 1496, "Solid branch", 13, 750, "#5b4bdb"),
        svg_text(500, 1496, "each section joins to the parent through interro_srno", 13, 400, "#475569"),
        svg_text(62, 1526, "Dashed amber branch", 13, 750, "#b45309"),
        svg_text(235, 1526, "shared address relationship through employer_srno, relative_srno, or acc_prop_srno", 13, 400, "#475569"),
        svg_text(1790, 1473, "Scope check", 15, 750, "#0f172a"),
        svg_text(1790, 1503, "24 / 24 interro tables included", 14, 650, "#166534"),
        svg_text(1790, 1530, "Rows 350 and 405–428 in source sheet", 13, 400, "#475569"),
        svg_text(40, 1581, "First-pass logical ER flow inferred from key names in the supplied JSON samples; database constraints were not provided.", 12, 400, "#64748b"),
        "</svg>",
    ])
    (OUT_DIR / "interro_flow_diagram.svg").write_text("\n".join(chunks), encoding="utf-8")


def write_png() -> None:
    """Render a review PNG with Pillow using the same logical layout as the SVG."""
    from PIL import Image, ImageDraw, ImageFont

    canvas_w, canvas_h = 2624, 1600
    image = Image.new("RGB", (canvas_w, canvas_h), "#f8fafc")
    draw = ImageDraw.Draw(image)
    regular_path = "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf"
    bold_path = "/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf"

    def font(size: int, bold: bool = False):
        return ImageFont.truetype(bold_path if bold else regular_path, size)

    def label(x: float, y: float, value: str, size: int, color: str = "#1f2937",
              bold: bool = False, anchor: str = "la") -> None:
        draw.text((x, y), value, font=font(size, bold), fill=color, anchor=anchor)

    def arrowhead(x: float, y: float, color: str) -> None:
        draw.polygon([(x, y), (x - 9, y - 5), (x - 9, y + 5)], fill=color)

    def dashed_line(points: list[tuple[float, float]], color: str, width: int = 2) -> None:
        for start, end in zip(points, points[1:]):
            x1, y1 = start
            x2, y2 = end
            length = abs(x2 - x1) + abs(y2 - y1)
            if not length:
                continue
            dash, gap, cursor = 10, 7, 0
            while cursor < length:
                stop = min(cursor + dash, length)
                if x1 == x2:
                    direction = 1 if y2 >= y1 else -1
                    draw.line([(x1, y1 + direction * cursor), (x1, y1 + direction * stop)], fill=color, width=width)
                else:
                    direction = 1 if x2 >= x1 else -1
                    draw.line([(x1 + direction * cursor, y1), (x1 + direction * stop, y1)], fill=color, width=width)
                cursor += dash + gap

    label(40, 44, "Interro Module — Table Relationship Flow", 28, "#0f172a", True, "ls")
    label(40, 72, "Source: module_wise_json_postgres · complaint_type = interro · 24 tables", 15, "#64748b", False, "ls")

    hub_x, hub_y, hub_w, hub_h = 914, 92, 796, 172
    draw.rounded_rectangle((hub_x, hub_y, hub_x + hub_w, hub_y + hub_h), radius=18, fill="#ffffff", outline="#5b4bdb", width=3)
    draw.rounded_rectangle((hub_x, hub_y, hub_x + hub_w, hub_y + 50), radius=18, fill="#5b4bdb")
    draw.rectangle((hub_x, hub_y + 31, hub_x + hub_w, hub_y + 50), fill="#5b4bdb")
    label(hub_x + hub_w / 2, hub_y + 31, "t_interrogation_info  •  CORE / PARENT", 19, "#ffffff", True, "mm")
    label(hub_x + 25, hub_y + 78, "interro_srno  PK", 15, "#7c3aed", True)
    label(hub_x + 25, hub_y + 105, "fir_reg_num  FK", 15, "#2563eb", True)
    label(hub_x + 25, hub_y + 132, "accused_srno  FK", 15, "#2563eb", True)
    label(hub_x + 285, hub_y + 78, "interro_dt", 15, "#475569")
    label(hub_x + 285, hub_y + 105, "interro_statement", 15, "#475569")
    label(hub_x + 285, hub_y + 132, "flags: accused · juvenile · medical · disclosures", 15, "#475569")

    group_xs = [36, 464, 892, 1320, 1748, 2176]
    group_y, group_w = 342, 412
    group_centres = [x + group_w / 2 for x in group_xs]
    bus_y = 308
    hub_centre = hub_x + hub_w / 2
    draw.line([(hub_centre, hub_y + hub_h), (hub_centre, bus_y)], fill="#5b4bdb", width=3)
    draw.line([(group_centres[0], bus_y), (group_centres[-1], bus_y)], fill="#5b4bdb", width=3)
    draw.rounded_rectangle((hub_centre - 74, bus_y - 15, hub_centre + 74, bus_y + 15), radius=15, fill="#ede9fe", outline="#8b7cf6", width=1)
    label(hub_centre, bus_y, "1 : N  interro_srno", 12, "#5b4bdb", True, "mm")

    table_anchors: dict[str, dict[str, float]] = {}
    table_y, table_gap, table_w = 405, 16, 380

    def draw_table(x: int, y: int, table_name: str) -> dict[str, float]:
        fields = TABLES[table_name]["fields"]
        height = 60 + 22 * len(fields)
        draw.rounded_rectangle((x, y, x + table_w, y + height), radius=12, fill="#ffffff", outline="#8b7cf6", width=2)
        draw.rounded_rectangle((x, y, x + table_w, y + 43), radius=12, fill="#ede9fe")
        draw.rectangle((x, y + 31, x + table_w, y + 43), fill="#ede9fe")
        draw.line((x, y + 43, x + table_w, y + 43), fill="#8b7cf6", width=2)
        label(x + table_w / 2, y + 23, table_name, 14, "#312e81", True, "mm")
        for idx, field_value in enumerate(fields):
            color = "#7c3aed" if field_value.endswith(" PK") else "#2563eb" if " FK" in field_value else "#475569"
            label(x + 15, y + 67 + 22 * idx, field_value, 13, color, " PK" in field_value or " FK" in field_value)
        return {"left": x, "right": x + table_w, "top": y, "bottom": y + height, "center_x": x + table_w / 2, "center_y": y + height / 2}

    for index, (category_name, table_names) in enumerate(CATEGORIES):
        x = group_xs[index]
        max_table_h = max(60 + 22 * len(TABLES[name]["fields"]) for name in table_names)
        group_h = (table_y - group_y) + len(table_names) * max_table_h + (len(table_names) - 1) * table_gap + 18
        draw.line([(group_centres[index], bus_y), (group_centres[index], group_y)], fill="#5b4bdb", width=3)
        draw.rounded_rectangle((x, group_y, x + group_w, group_y + group_h), radius=18, fill="#f5f3ff", outline="#c4b5fd", width=2)
        draw.rounded_rectangle((x, group_y, x + group_w, group_y + 48), radius=18, fill="#ddd6fe")
        draw.rectangle((x, group_y + 30, x + group_w, group_y + 48), fill="#ddd6fe")
        label(x + group_w / 2, group_y + 26, category_name, 16, "#3730a3", True, "mm")

        trunk_x = x + 8
        current_y = table_y
        pending = []
        for table_name in table_names:
            height = 60 + 22 * len(TABLES[table_name]["fields"])
            pending.append((table_name, current_y, current_y + height / 2))
            current_y += max_table_h + table_gap
        draw.line([(group_centres[index], group_y + 48), (trunk_x, group_y + 48), (trunk_x, pending[-1][2])], fill="#7c83a3", width=2)
        for table_name, y, centre_y in pending:
            draw.line([(trunk_x, centre_y), (x + 16, centre_y)], fill="#7c83a3", width=2)
            arrowhead(x + 16, centre_y, "#64748b")
            table_anchors[table_name] = draw_table(x + 16, y, table_name)

    shared_name = "t_interro_emp_rel_prop_addr"
    shared_x, shared_y, shared_w = 1854, 1200, 720
    shared_h = 60 + 22 * len(TABLES[shared_name]["fields"])
    for parent_name, start_side, route_x, target_x in [
        ("t_interro_person_employer", "left", 1734, shared_x + 180),
        ("t_interro_person_relatives", "right", 2166, shared_x + 360),
        ("t_interro_person_property", "right", 2592, shared_x + 540),
    ]:
        parent = table_anchors[parent_name]
        dashed_line([(parent[start_side], parent["center_y"]), (route_x, parent["center_y"]), (route_x, shared_y - 28), (target_x, shared_y - 28), (target_x, shared_y)], "#d97706", 2)
        draw.polygon([(target_x, shared_y), (target_x - 5, shared_y - 9), (target_x + 5, shared_y - 9)], fill="#d97706")
    label(shared_x + shared_w / 2, shared_y - 40, "Address belongs to employer OR relative OR property", 13, "#b45309", True, "mm")
    draw.rounded_rectangle((shared_x, shared_y, shared_x + shared_w, shared_y + shared_h), radius=14, fill="#fffbeb", outline="#f59e0b", width=3)
    draw.rounded_rectangle((shared_x, shared_y, shared_x + shared_w, shared_y + 46), radius=14, fill="#fef3c7")
    draw.rectangle((shared_x, shared_y + 31, shared_x + shared_w, shared_y + 46), fill="#fef3c7")
    draw.line((shared_x, shared_y + 46, shared_x + shared_w, shared_y + 46), fill="#f59e0b", width=2)
    label(shared_x + shared_w / 2, shared_y + 25, shared_name + "  •  SHARED ADDRESS", 16, "#92400e", True, "mm")
    for idx, field_value in enumerate(TABLES[shared_name]["fields"]):
        color = "#7c3aed" if field_value.endswith(" PK") else "#b45309" if " FK" in field_value else "#475569"
        label(shared_x + 18 + (idx % 3) * 225, shared_y + 75 + (idx // 3) * 29, field_value, 14, color, " PK" in field_value or " FK" in field_value)

    draw.rounded_rectangle((40, 1437, 1750, 1549), radius=14, fill="#ffffff", outline="#cbd5e1", width=1)
    label(62, 1468, "Reading the flow", 15, "#0f172a", True)
    label(62, 1496, "PK", 13, "#7c3aed", True)
    label(92, 1496, "primary key", 13, "#475569")
    label(225, 1496, "FK", 13, "#2563eb", True)
    label(255, 1496, "foreign key", 13, "#475569")
    label(406, 1496, "Solid branch", 13, "#5b4bdb", True)
    label(500, 1496, "each section joins to the parent through interro_srno", 13, "#475569")
    label(62, 1526, "Dashed amber branch", 13, "#b45309", True)
    label(235, 1526, "shared address relationship through employer_srno, relative_srno, or acc_prop_srno", 13, "#475569")
    label(1790, 1473, "Scope check", 15, "#0f172a", True)
    label(1790, 1503, "24 / 24 interro tables included", 14, "#166534", True)
    label(1790, 1530, "Rows 350 and 405–428 in source sheet", 13, "#475569")
    label(40, 1581, "First-pass logical ER flow inferred from key names in the supplied JSON samples; database constraints were not provided.", 12, "#64748b")
    image.save(OUT_DIR / "interro_flow_diagram.png", format="PNG", optimize=True)


def write_mermaid() -> None:
    direct_children = [name for _, names in CATEGORIES for name in names]
    lines = [
        "erDiagram",
        "    t_interrogation_info {",
        "        bigint interro_srno PK",
        "        bigint fir_reg_num FK",
        "        bigint accused_srno FK",
        "        date interro_dt",
        "    }",
    ]
    for table_name in direct_children:
        lines.extend([
            f"    {table_name} {{",
            f"        bigint {TABLES[table_name]['pk']} PK",
            "        bigint interro_srno FK",
            "    }",
            f"    t_interrogation_info ||--o{{ {table_name} : interro_srno",
        ])
    lines.extend([
        "    t_interro_emp_rel_prop_addr {",
        "        bigint address_cd PK",
        "        bigint employer_srno FK",
        "        bigint relative_srno FK",
        "        bigint acc_prop_srno FK",
        "    }",
        "    t_interro_person_employer ||--o{ t_interro_emp_rel_prop_addr : employer_srno",
        "    t_interro_person_relatives ||--o{ t_interro_emp_rel_prop_addr : relative_srno",
        "    t_interro_person_property ||--o{ t_interro_emp_rel_prop_addr : acc_prop_srno",
    ])
    (OUT_DIR / "interro_flow_diagram.mmd").write_text("\n".join(lines) + "\n", encoding="utf-8")


def write_readme(rows: list[dict[str, object]]) -> None:
    row_numbers = [int(row["workbook_row"]) for row in rows]
    content = f"""# Interro module flow diagram

This first-pass ER flow was generated from `{WORKBOOK.name}`, sheet `{SHEET_NAME}`, filtered to `complaint_type = {COMPLAINT_TYPE}`.

- Source records: {len(rows)}
- Workbook rows: {min(row_numbers)} and {min(n for n in row_numbers if n > min(row_numbers))}–{max(row_numbers)}
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
"""
    (OUT_DIR / "README.md").write_text(content, encoding="utf-8")


def main() -> None:
    rows = read_sheet_rows()
    validate(rows)
    write_source_csv(rows)
    write_svg()
    write_png()
    write_mermaid()
    write_readme(rows)
    print(f"Built interro flow from {len(rows)} source rows across {len(TABLES)} tables.")


if __name__ == "__main__":
    main()
