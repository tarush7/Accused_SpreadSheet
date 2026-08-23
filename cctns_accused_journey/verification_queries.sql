/*
CCTNS accused journey verification pack (PostgreSQL)

Purpose
  1. Confirm the person and FIR-specific identifiers.
  2. Find which accused_vid tables actually contain rows for one person.
  3. Inspect business-date candidates without guessing their types or meanings.
  4. Discover indirect child tables that do not carry accused_vid themselves.

Safety
  - Read-only against application schemas.
  - Creates TEMP tables only in your database session.
  - Replace 119802 below with the accused_vid you want to trace.
*/

CREATE TEMP TABLE journey_param (
    accused_vid bigint NOT NULL
) ON COMMIT PRESERVE ROWS;

INSERT INTO journey_param (accused_vid) VALUES (119802);


/* Q1. Confirm the root accused rows and their FIR-specific identities. */
SELECT
    a.accused_vid,
    a.accused_srno,
    a.fir_reg_num,
    a.first_name,
    a.middle_name,
    a.last_name,
    a.accused_status_cd,
    a.arrest_surr_dt,
    a.arr_surr_srno,
    a.record_status,
    a.record_created_on,
    a.record_updated_on
FROM fir.t_fir_accused_info AS a
JOIN journey_param AS p ON p.accused_vid = a.accused_vid
ORDER BY a.fir_reg_num, a.accused_srno;


/* Q2. Reproduce and summarize every direct accused_vid table. */
SELECT
    c.table_schema,
    c.table_name,
    c.column_name,
    c.data_type,
    c.udt_name,
    c.is_nullable,
    c.ordinal_position
FROM information_schema.columns AS c
WHERE c.column_name = 'accused_vid'
  AND c.table_schema NOT IN ('pg_catalog', 'information_schema')
ORDER BY c.table_schema, c.table_name;


/* Q3. Find populated direct-link tables for the chosen accused_vid. */
CREATE TEMP TABLE accused_vid_hits (
    table_schema text NOT NULL,
    table_name text NOT NULL,
    row_count bigint NOT NULL
) ON COMMIT PRESERVE ROWS;

DO $block$
DECLARE
    r record;
    v_accused_vid bigint;
    v_count bigint;
BEGIN
    SELECT accused_vid INTO STRICT v_accused_vid FROM journey_param;

    FOR r IN
        SELECT c.table_schema, c.table_name
        FROM information_schema.columns AS c
        WHERE c.column_name = 'accused_vid'
          AND c.table_schema NOT IN ('pg_catalog', 'information_schema')
        ORDER BY c.table_schema, c.table_name
    LOOP
        EXECUTE format(
            'SELECT count(*) FROM %I.%I WHERE accused_vid = $1',
            r.table_schema,
            r.table_name
        )
        INTO v_count
        USING v_accused_vid;

        IF v_count > 0 THEN
            INSERT INTO accused_vid_hits(table_schema, table_name, row_count)
            VALUES (r.table_schema, r.table_name, v_count);
        END IF;
    END LOOP;
END
$block$;

SELECT *
FROM accused_vid_hits
ORDER BY table_schema, table_name;


/* Q4. Collect the matching rows as JSON without assuming a common schema. */
CREATE TEMP TABLE accused_vid_rows (
    table_schema text NOT NULL,
    table_name text NOT NULL,
    row_data jsonb NOT NULL
) ON COMMIT PRESERVE ROWS;

DO $block$
DECLARE
    r record;
    v_accused_vid bigint;
BEGIN
    SELECT accused_vid INTO STRICT v_accused_vid FROM journey_param;

    FOR r IN
        SELECT table_schema, table_name
        FROM accused_vid_hits
        ORDER BY table_schema, table_name
    LOOP
        EXECUTE format(
            'INSERT INTO accused_vid_rows(table_schema, table_name, row_data)
             SELECT %L, %L, to_jsonb(t)
             FROM %I.%I AS t
             WHERE t.accused_vid = $1',
            r.table_schema,
            r.table_name,
            r.table_schema,
            r.table_name
        )
        USING v_accused_vid;
    END LOOP;
END
$block$;

SELECT
    table_schema,
    table_name,
    row_data ->> 'fir_reg_num' AS fir_reg_num,
    row_data ->> 'accused_srno' AS accused_srno,
    row_data ->> 'arr_surr_srno' AS arr_surr_srno,
    row_data
FROM accused_vid_rows
ORDER BY table_schema, table_name,
         row_data ->> 'fir_reg_num', row_data ->> 'accused_srno';


/*
Q5. Extract date/time-looking fields from the matching rows.

event_value is intentionally left as text. Casting every field to timestamp is
unsafe until the actual data types and formats have been reviewed.
*/
SELECT
    r.table_schema,
    r.table_name,
    r.row_data ->> 'fir_reg_num' AS fir_reg_num,
    r.row_data ->> 'accused_srno' AS accused_srno,
    e.key AS event_field,
    e.value AS event_value,
    CASE
        WHEN e.key ~* '^record_(created|updated|sync)_on$'
          OR e.key ~* '^(created|updated)_(at|on)$'
        THEN 'technical_audit_timestamp'
        ELSE 'business_event_candidate'
    END AS timestamp_role
FROM accused_vid_rows AS r
CROSS JOIN LATERAL jsonb_each_text(r.row_data) AS e(key, value)
WHERE e.value IS NOT NULL
  AND e.value <> ''
  AND (
      e.key ~* '(^|_)(dt|date|time|timestamp|on)$'
      OR e.key IN (
          'arrest_surrender_dt', 'interro_dt', 'bail_grant_dt',
          'bail_release_dt', 'remand_sanc_dt', 'court_order_dt',
          'sentence_from_dt', 'sentence_to_dt', 'jail_last_release_dt'
      )
  )
ORDER BY event_value, r.table_schema, r.table_name, e.key;


/* Q6. Inventory identifiers, dates, status fields, and audit timestamps. */
WITH relevant_tables AS (
    SELECT DISTINCT table_schema, table_name
    FROM information_schema.columns
    WHERE column_name IN (
        'accused_vid', 'accused_srno', 'fir_accused_srno',
        'fir_reg_num', 'arr_surr_srno'
    )
      AND table_schema NOT IN ('pg_catalog', 'information_schema')
)
SELECT
    c.table_schema,
    c.table_name,
    c.ordinal_position,
    c.column_name,
    c.data_type,
    c.udt_name,
    c.is_nullable
FROM information_schema.columns AS c
JOIN relevant_tables AS r
  ON r.table_schema = c.table_schema
 AND r.table_name = c.table_name
WHERE c.column_name IN (
        'accused_vid', 'accused_srno', 'fir_accused_srno',
        'fir_reg_num', 'arr_surr_srno'
      )
   OR c.column_name ~* '(^|_)(dt|date|time|timestamp|on)$'
   OR c.column_name ~* '(status|result|disposal|punish|sentence|release|grant|surrender|arrest|remand)'
ORDER BY c.table_schema, c.table_name, c.ordinal_position;


/* Q7. Inspect declared primary keys, unique keys, and foreign keys. */
WITH target_relations AS (
    SELECT DISTINCT (quote_ident(table_schema) || '.' || quote_ident(table_name))::regclass AS relid
    FROM information_schema.columns
    WHERE column_name = 'accused_vid'
      AND table_schema NOT IN ('pg_catalog', 'information_schema')
)
SELECT
    child_ns.nspname AS child_schema,
    child.relname AS child_table,
    con.conname AS constraint_name,
    CASE con.contype
        WHEN 'p' THEN 'PRIMARY KEY'
        WHEN 'u' THEN 'UNIQUE'
        WHEN 'f' THEN 'FOREIGN KEY'
        ELSE con.contype::text
    END AS constraint_type,
    pg_get_constraintdef(con.oid, true) AS definition,
    parent_ns.nspname AS parent_schema,
    parent.relname AS parent_table
FROM pg_constraint AS con
JOIN pg_class AS child ON child.oid = con.conrelid
JOIN pg_namespace AS child_ns ON child_ns.oid = child.relnamespace
LEFT JOIN pg_class AS parent ON parent.oid = con.confrelid
LEFT JOIN pg_namespace AS parent_ns ON parent_ns.oid = parent.relnamespace
WHERE con.conrelid IN (SELECT relid FROM target_relations)
   OR con.confrelid IN (SELECT relid FROM target_relations)
ORDER BY child_ns.nspname, child.relname, constraint_type, con.conname;


/*
Q8. Find indirect child tables up to three FK hops away.

This is essential because many workbook tables (files, acts, sureties, addresses,
release rows) do not carry accused_vid; they inherit identity through their parent.
*/
WITH RECURSIVE
roots AS (
    SELECT DISTINCT
        cls.oid AS root_oid,
        ns.nspname AS root_schema,
        cls.relname AS root_table
    FROM information_schema.columns AS col
    JOIN pg_namespace AS ns ON ns.nspname = col.table_schema
    JOIN pg_class AS cls
      ON cls.relnamespace = ns.oid
     AND cls.relname = col.table_name
    WHERE col.column_name = 'accused_vid'
      AND col.table_schema NOT IN ('pg_catalog', 'information_schema')
),
fk_edges AS (
    SELECT
        con.conrelid AS child_oid,
        con.confrelid AS parent_oid,
        con.conname,
        pg_get_constraintdef(con.oid, true) AS definition
    FROM pg_constraint AS con
    WHERE con.contype = 'f'
),
walk AS (
    SELECT
        r.root_oid,
        r.root_schema,
        r.root_table,
        r.root_oid AS current_oid,
        0 AS depth,
        ARRAY[r.root_oid]::oid[] AS visited,
        NULL::text AS via_constraint,
        NULL::text AS fk_definition
    FROM roots AS r

    UNION ALL

    SELECT
        w.root_oid,
        w.root_schema,
        w.root_table,
        e.child_oid,
        w.depth + 1,
        w.visited || e.child_oid,
        e.conname,
        e.definition
    FROM walk AS w
    JOIN fk_edges AS e ON e.parent_oid = w.current_oid
    WHERE w.depth < 3
      AND NOT e.child_oid = ANY(w.visited)
)
SELECT DISTINCT
    w.depth,
    w.root_schema,
    w.root_table,
    child_ns.nspname AS child_schema,
    child.relname AS child_table,
    w.via_constraint,
    w.fk_definition
FROM walk AS w
JOIN pg_class AS child ON child.oid = w.current_oid
JOIN pg_namespace AS child_ns ON child_ns.oid = child.relnamespace
WHERE w.depth > 0
ORDER BY w.root_schema, w.root_table, w.depth, child_schema, child_table;


/* Q9. Check whether accused_vid is one-to-one with a person across FIRs. */
SELECT
    accused_vid,
    count(*) AS accused_rows,
    count(DISTINCT fir_reg_num) AS fir_count,
    count(DISTINCT accused_srno) AS accused_srno_count,
    min(record_created_on) AS first_profile_created_on,
    max(record_updated_on) AS last_profile_updated_on
FROM fir.t_fir_accused_info
WHERE accused_vid = (SELECT accused_vid FROM journey_param)
GROUP BY accused_vid;


/*
Q10. Return a compact result bundle to share back for the final typed timeline.
Run Q1-Q9 first in the same session.
*/
SELECT jsonb_build_object(
    'accused_vid', (SELECT accused_vid FROM journey_param),
    'root_rows', (
        SELECT coalesce(jsonb_agg(to_jsonb(x)), '[]'::jsonb)
        FROM (
            SELECT accused_vid, accused_srno, fir_reg_num,
                   accused_status_cd, arrest_surr_dt, arr_surr_srno,
                   record_status, record_created_on, record_updated_on
            FROM fir.t_fir_accused_info
            WHERE accused_vid = (SELECT accused_vid FROM journey_param)
            ORDER BY fir_reg_num, accused_srno
        ) AS x
    ),
    'populated_tables', (
        SELECT coalesce(jsonb_agg(to_jsonb(h) ORDER BY h.table_schema, h.table_name), '[]'::jsonb)
        FROM accused_vid_hits AS h
    ),
    'date_candidates', (
        SELECT coalesce(jsonb_agg(to_jsonb(d) ORDER BY d.event_value, d.table_schema, d.table_name), '[]'::jsonb)
        FROM (
            SELECT
                r.table_schema,
                r.table_name,
                r.row_data ->> 'fir_reg_num' AS fir_reg_num,
                r.row_data ->> 'accused_srno' AS accused_srno,
                e.key AS event_field,
                e.value AS event_value
            FROM accused_vid_rows AS r
            CROSS JOIN LATERAL jsonb_each_text(r.row_data) AS e(key, value)
            WHERE e.value IS NOT NULL
              AND e.value <> ''
              AND (
                  e.key ~* '(^|_)(dt|date|time|timestamp|on)$'
                  OR e.key IN ('arrest_surrender_dt', 'interro_dt')
              )
        ) AS d
    )
) AS accused_journey_verification_bundle;

