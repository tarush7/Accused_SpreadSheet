#!/usr/bin/env python3
"""Add the generated flow image as a new sheet while preserving the source XLSX."""

from __future__ import annotations

import re
from pathlib import Path
from zipfile import ZIP_DEFLATED, ZipFile, ZipInfo


ROOT = Path(__file__).resolve().parent.parent
SOURCE = ROOT / "CCTNS_Modules_Search&View_Figma_26052026_V2 (1).xlsx"
IMAGE = Path(__file__).resolve().parent / "interro_flow_diagram.png"
OUTPUT = Path(__file__).resolve().parent / "CCTNS_Modules_Search&View_Figma_26052026_V2_with_interro_flow.xlsx"
SHEET_NAME = "interro_flow_chart"


def insert_before_closing(xml: str, closing_tag: str, fragment: str) -> str:
    if closing_tag not in xml:
        raise RuntimeError(f"Closing tag not found: {closing_tag}")
    return xml.replace(closing_tag, fragment + closing_tag, 1)


def update_app_properties(xml: str) -> str:
    """Keep the optional Office sheet-count/title properties consistent."""
    worksheet_count_pattern = re.compile(
        r"(<vt:lpstr>Worksheets</vt:lpstr></vt:variant><vt:variant><vt:i4>)(\d+)(</vt:i4>)"
    )
    xml, count_replacements = worksheet_count_pattern.subn(
        lambda match: match.group(1) + str(int(match.group(2)) + 1) + match.group(3),
        xml,
        count=1,
    )
    titles_pattern = re.compile(r'(<TitlesOfParts><vt:vector[^>]*size=")(\d+)("[^>]*>)')
    xml, size_replacements = titles_pattern.subn(
        lambda match: match.group(1) + str(int(match.group(2)) + 1) + match.group(3),
        xml,
        count=1,
    )
    marker = "</vt:vector></TitlesOfParts>"
    if count_replacements and size_replacements and marker in xml:
        xml = xml.replace(marker, f"<vt:lpstr>{SHEET_NAME}</vt:lpstr>{marker}", 1)
    return xml


def build() -> None:
    if not IMAGE.exists():
        raise RuntimeError(f"Diagram image not found: {IMAGE}")

    with ZipFile(SOURCE, "r") as source_archive:
        names = set(source_archive.namelist())
        workbook_xml = source_archive.read("xl/workbook.xml").decode("utf-8")
        workbook_rels = source_archive.read("xl/_rels/workbook.xml.rels").decode("utf-8")
        content_types = source_archive.read("[Content_Types].xml").decode("utf-8")

        if re.search(rf'<sheet\b[^>]*\bname="{re.escape(SHEET_NAME)}"', workbook_xml):
            raise RuntimeError(f"Sheet already exists: {SHEET_NAME}")

        sheet_numbers = [int(value) for value in re.findall(r"xl/worksheets/sheet(\d+)\.xml", "\n".join(names))]
        drawing_numbers = [int(value) for value in re.findall(r"xl/drawings/drawing(\d+)\.xml", "\n".join(names))]
        image_numbers = [int(value) for value in re.findall(r"xl/media/image(\d+)\.[A-Za-z0-9]+", "\n".join(names))]
        sheet_part = max(sheet_numbers) + 1
        drawing_part = max(drawing_numbers) + 1
        image_part = max(image_numbers) + 1
        sheet_id = max(int(value) for value in re.findall(r'sheetId="(\d+)"', workbook_xml)) + 1
        workbook_rel_id = max(int(value) for value in re.findall(r'Id="rId(\d+)"', workbook_rels)) + 1

        workbook_xml = insert_before_closing(
            workbook_xml,
            "</sheets>",
            f'<sheet name="{SHEET_NAME}" sheetId="{sheet_id}" r:id="rId{workbook_rel_id}"/>',
        )
        workbook_rels = insert_before_closing(
            workbook_rels,
            "</Relationships>",
            f'<Relationship Id="rId{workbook_rel_id}" '
            'Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet" '
            f'Target="worksheets/sheet{sheet_part}.xml"/>',
        )
        content_types = insert_before_closing(
            content_types,
            "</Types>",
            f'<Override PartName="/xl/worksheets/sheet{sheet_part}.xml" '
            'ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml"/>'
            f'<Override PartName="/xl/drawings/drawing{drawing_part}.xml" '
            'ContentType="application/vnd.openxmlformats-officedocument.drawing+xml"/>',
        )

        sheet_xml = f'''<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<worksheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main" xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships">
  <sheetPr><pageSetUpPr fitToPage="1"/></sheetPr>
  <dimension ref="A1"/>
  <sheetViews><sheetView showGridLines="0" zoomScale="25" zoomScaleNormal="25" workbookViewId="0"><selection activeCell="A1" sqref="A1"/></sheetView></sheetViews>
  <sheetFormatPr defaultRowHeight="15"/>
  <sheetData><row r="1"><c r="A1" t="inlineStr"><is><t>Interro module table relationship flow</t></is></c></row></sheetData>
  <pageMargins left="0.25" right="0.25" top="0.25" bottom="0.25" header="0" footer="0"/>
  <pageSetup orientation="landscape" fitToWidth="1" fitToHeight="1"/>
  <drawing r:id="rId1"/>
</worksheet>'''

        sheet_rels = f'''<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/drawing" Target="../drawings/drawing{drawing_part}.xml"/>
</Relationships>'''

        # Native image size: 2624 x 1600 px. OOXML uses 9,525 EMU per 96-DPI pixel.
        width_emu = 2624 * 9525
        height_emu = 1600 * 9525
        drawing_xml = f'''<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<xdr:wsDr xmlns:xdr="http://schemas.openxmlformats.org/drawingml/2006/spreadsheetDrawing" xmlns:a="http://schemas.openxmlformats.org/drawingml/2006/main" xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships">
  <xdr:oneCellAnchor>
    <xdr:from><xdr:col>0</xdr:col><xdr:colOff>0</xdr:colOff><xdr:row>0</xdr:row><xdr:rowOff>0</xdr:rowOff></xdr:from>
    <xdr:ext cx="{width_emu}" cy="{height_emu}"/>
    <xdr:pic>
      <xdr:nvPicPr><xdr:cNvPr id="1" name="Interro Module Flow Diagram" descr="ER flow generated from complaint_type interro"/><xdr:cNvPicPr/></xdr:nvPicPr>
      <xdr:blipFill><a:blip r:embed="rId1"/><a:stretch><a:fillRect/></a:stretch></xdr:blipFill>
      <xdr:spPr><a:xfrm><a:off x="0" y="0"/><a:ext cx="{width_emu}" cy="{height_emu}"/></a:xfrm><a:prstGeom prst="rect"><a:avLst/></a:prstGeom></xdr:spPr>
    </xdr:pic>
    <xdr:clientData/>
  </xdr:oneCellAnchor>
</xdr:wsDr>'''

        drawing_rels = f'''<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/image" Target="../media/image{image_part}.png"/>
</Relationships>'''

        replacements: dict[str, bytes] = {
            "xl/workbook.xml": workbook_xml.encode("utf-8"),
            "xl/_rels/workbook.xml.rels": workbook_rels.encode("utf-8"),
            "[Content_Types].xml": content_types.encode("utf-8"),
        }
        if "docProps/app.xml" in names:
            replacements["docProps/app.xml"] = update_app_properties(
                source_archive.read("docProps/app.xml").decode("utf-8")
            ).encode("utf-8")

        with ZipFile(OUTPUT, "w") as output_archive:
            for item in source_archive.infolist():
                output_archive.writestr(item, replacements.get(item.filename, source_archive.read(item.filename)))
            output_archive.writestr(f"xl/worksheets/sheet{sheet_part}.xml", sheet_xml, compress_type=ZIP_DEFLATED)
            output_archive.writestr(f"xl/worksheets/_rels/sheet{sheet_part}.xml.rels", sheet_rels, compress_type=ZIP_DEFLATED)
            output_archive.writestr(f"xl/drawings/drawing{drawing_part}.xml", drawing_xml, compress_type=ZIP_DEFLATED)
            output_archive.writestr(f"xl/drawings/_rels/drawing{drawing_part}.xml.rels", drawing_rels, compress_type=ZIP_DEFLATED)
            output_archive.writestr(f"xl/media/image{image_part}.png", IMAGE.read_bytes(), compress_type=ZIP_DEFLATED)

    print(f"Created workbook copy: {OUTPUT.name}")
    print(f"Added sheet {SHEET_NAME!r} as sheet{sheet_part}.xml with drawing{drawing_part}.xml and image{image_part}.png")


if __name__ == "__main__":
    build()
