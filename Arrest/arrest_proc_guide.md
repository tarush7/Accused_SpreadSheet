# Arrest JSON Function — Study Guide

Companion SQL: `Arrest/arrest_proc.sql`

## What We Are Building

The Arrest data is normalized: one arrest is spread across 15 tables. The application does not want to query all 15 tables separately, so `arrest.get_arrest_json(arr_surr_srno)` acts like a packer:

```text
One Arrest number
        ↓
t_arrest_memo (root)
        ↓
Direct detail tables + two nested branches
        ↓
One complete JSON object
```

Example call:

```sql
SELECT jsonb_pretty(
    arrest.get_arrest_json(3810100426000007)
);
```

## Function Versus Procedure

The file is called `arrest_proc.sql`, but the database object should be a **function**.

| Function | Procedure |
|---|---|
| Returns a value | Usually performs an operation |
| Called with `SELECT` | Called with `CALL` |
| Good for returning JSON | Good for workflows such as posting, approving or closing a case |

Our requirement is “give me the complete Arrest JSON for this number,” so a function is the right object:

```sql
SELECT arrest.get_arrest_json(3810100426000007);
```

A procedure would be more appropriate for an action such as:

```sql
CALL arrest.approve_arrest(3810100426000007, 4370);
```

## Why `arr_surr_srno` Is the Input

`arr_surr_srno` is the primary key of `t_arrest_memo`. It also appears in all 12 direct child tables. That makes it the safest value for finding one exact arrest/surrender event.

`fir_reg_num` is not enough because one FIR can have multiple accused people and multiple arrest events. `accused_vid` identifies an accused profile, but the same accused can potentially have more than one arrest/surrender event.

## The Table Relationships

| Output section | Table | Relationship |
|---|---|---|
| Root fields | `t_arrest_memo` | `arr_surr_srno` is the primary key |
| `actSection` | `t_arrest_act_section` | `arr_surr_srno` |
| `addresses` | `t_arrest_addresses` | `arr_surr_srno` |
| `bankDetails` | `t_arrest_bank_dtls` | `arr_surr_srno` |
| `dressDetails` | `t_arrest_dress` | `arr_surr_srno` |
| `fileUploads` | `t_arrest_files` | `arr_surr_srno` |
| `identityMarks` | `t_arrest_identity_marks` | `arr_surr_srno` |
| `intimationAddresses` | `t_arrest_intimate_addr` | `arr_surr_srno` |
| `medicalExams` | `t_arrest_med_exam` | `arr_surr_srno` |
| `nationalIds` | `t_arrest_national_id` | `arr_surr_srno` |
| `physicalFeatures` | `t_arrest_phy_feature` | `arr_surr_srno` |
| `witnesses` | `t_arrest_witness` | `arr_surr_srno` |
| Witness `addressGrid` | `t_arrest_witness_addr` | `arr_witns_srno` |
| `personSearches` | `t_person_search_property` | `arr_surr_srno` |
| Search `items` | `t_person_search_items` | `prop_srno` |

The last two are second-level relationships:

```text
t_arrest_memo
    └── t_arrest_witness
            └── t_arrest_witness_addr

t_arrest_memo
    └── t_person_search_property
            └── t_person_search_items
```

That is why witness addresses belong inside each witness object, and search items belong inside each person-search object.

## How the Function Works

### 1. Convert database names to application names

PostgreSQL columns use snake case:

```text
arr_surr_srno
record_created_by
```

The application JSON uses camel case:

```text
arrSurrSrno
recordCreatedBy
```

`arrest.jsonb_camel_keys()` performs that conversion.

### 2. Preserve raw fields

This expression keeps every column from the table:

```sql
arrest.jsonb_camel_keys(to_jsonb(memo.*))
```

This is useful because adding lookup labels does not remove the original codes.

### 3. Add readable values

The database may store:

```json
{ "arrestTypeCd": 2 }
```

The function adds:

```json
{
  "arrestTypeCd": 2,
  "arrestTypeCdValue": "Surrender"
}
```

`arrest.lookup_value()` searches `mdm.m_lookup_masters` using three values:

1. API master category, such as `ARR_SURR_TYPE`
2. Stored code, such as `2`
3. Row language, such as `99`

The category is essential. Code `2` can mean different things in different masters.

### 4. Resolve dedicated master tables

Not every lookup lives in `m_lookup_masters`:

- Staff comes from `users.t_police_staff_info`.
- State comes from `mdm.m_state`.
- District comes from `mdm.m_district`.
- Police station comes from `mdm.m_police_station`.
- Beat comes from `mdm.m_ps_beat`.
- Act comes from `mdm.m_act`.
- Section comes from `mdm.m_section`.

That is why the code uses a dedicated table instead of forcing every code through `m_lookup_masters`.

For audit fields, the raw IDs remain available as `recordCreatedBy` and
`recordUpdatedBy`. The function joins each ID to
`users.t_police_staff_info` and adds scalar fields in the same style as the
NCR function:

```json
{
  "recordCreatedBy": 12345,
  "recordCreatedByFullName": "Amit Kumar Singh",
  "recordCreatedByRankDesc": "Sub-Inspector",
  "recordCreatedByLoginId": "amit.singh",
  "recordUpdatedBy": 4881,
  "recordUpdatedByFullName": "Tashi Dorjay",
  "recordUpdatedByRankDesc": "Inspector",
  "recordUpdatedByLoginId": "tashi.dorjay"
}
```

These are separate JSON fields, not nested staff objects. The same six added
fields are produced for the root and every child row that carries the audit
IDs.

### 5. Turn child rows into arrays

One arrest can have multiple files, addresses or witnesses. `jsonb_agg()` collects those rows into arrays:

```json
{
  "fileUploads": [
    { "arrFileSrno": 1 },
    { "arrFileSrno": 2 }
  ]
}
```

### 6. Return empty arrays consistently

This pattern:

```sql
COALESCE(jsonb_agg(...), '[]'::jsonb)
```

means that “no files” becomes:

```json
{ "fileUploads": [] }
```

instead of:

```json
{ "fileUploads": null }
```

Stable output makes frontend code simpler.

### 7. Return an empty object for an unknown Arrest number

If the requested `arr_surr_srno` does not exist, the function returns:

```json
{}
```

It does not return SQL `NULL`.

## Why Some Labels Can Still Be Null

The lookup audit found several codes that are absent from their mapped master category. For example, the raw code remains in the JSON, but its added `...Value` field may be null.

This does not delete or hide the data:

```json
{
  "nationalityCd": 1234,
  "nationalityCdValue": null
}
```

That output tells us, honestly, that code `1234` could not be translated using the current `NATIONALITY` master.

Six fields do not have a supported lookup mapping yet, so the function preserves their raw values without inventing labels:

- `arrested_by_pis_cd`
- `arrested_police_cd`
- `surrend_in_court_cd`
- `criminal_gang_cd`
- `physical_cond_cd`
- `court_estbl_cd`

## Important Assumptions to Confirm

Before calling this production-ready, confirm:

1. The actual database schema is named `arrest`.
2. The UI agrees with output names such as `bankDetails`, `witnesses` and `personSearches`.
3. `mdm.common_get_address_master_values(...)` exists with the same signature used by the NCR and Complaint functions.
4. Confirm whether `dysp_login_id` stores a staff ID or login name. The draft supports either form, but the data owner should identify the canonical one.
5. `edu_qual_cd` may contain multiple codes. Its exact PostgreSQL type must be confirmed before returning a resolved list.
6. The six unknown lookup fields need confirmation from the database/API owner. In particular, the updated Arrest lookup does not establish that `arrested_by_pis_cd` references `users.t_police_staff_info.staff_id`; therefore the function does not use it to derive an officer name, rank or login ID. The raw `arrestedByPisCd` and free-text `arrestedByOthers` values are still returned.

## How to Test It

### Basic result

```sql
SELECT jsonb_pretty(
    arrest.get_arrest_json(3810100426000007)
);
```

### Confirm the root key

```sql
SELECT arrest.get_arrest_json(3810100426000007)->>'arrSurrSrno';
```

### Count witnesses

```sql
SELECT jsonb_array_length(
    arrest.get_arrest_json(3810100426000007)->'witnesses'
);
```

### Check nested witness addresses

```sql
SELECT witness->'addressGrid'
FROM jsonb_array_elements(
    arrest.get_arrest_json(3810100426000007)->'witnesses'
) witness;
```

### Check person-search items

```sql
SELECT person_search->'items'
FROM jsonb_array_elements(
    arrest.get_arrest_json(3810100426000007)->'personSearches'
) person_search;
```

### Check a missing number

```sql
SELECT arrest.get_arrest_json(-1);
```

Expected result:

```json
{}
```

### Check performance

```sql
EXPLAIN (ANALYZE, BUFFERS)
SELECT arrest.get_arrest_json(3810100426000007);
```

The important child-link columns should be indexed:

- `arr_surr_srno` on every direct child table
- `arr_witns_srno` on `t_arrest_witness_addr`
- `prop_srno` on `t_person_search_items`

## Questions You Should Be Ready to Answer

### Why did you use a function rather than a procedure?

Because the requirement is to return one JSON value. PostgreSQL functions can be called through `SELECT`; procedures are better suited to actions and transaction-oriented workflows.

### Why is `t_arrest_memo` the root?

It contains the unique arrest/surrender event identified by `arr_surr_srno`, and the direct child tables carry that same key.

### Why not join all child tables in one large query?

Doing that creates row multiplication. For example, three files and two witnesses could produce six combined rows. Correlated aggregations build each child array independently and avoid duplicate data.

### Why are lookup joins language-aware?

The same code can have different display text for different languages. Matching `lang_cd` returns the label in the record's language.

### Why keep both the code and its display value?

The code is the stable database identifier. The display value is meant for humans and can vary by language.

### Why use `LEFT JOIN LATERAL` for addresses?

The shared address helper needs values from the current address row. `LATERAL` allows the helper to receive those values without removing the address when some master data is missing.

### What happens when a lookup code is invalid?

The raw code remains in the JSON, while the added display field is null. This exposes the data-quality problem without inventing a value.

### Why did you avoid `SECURITY DEFINER`?

The function only needs to read data and should normally use the caller's permissions. `SECURITY DEFINER` runs with the function owner's permissions and should only be added after a deliberate security review.

## One-Sentence Explanation

“The Arrest function takes one `arr_surr_srno`, reads the root arrest record, aggregates all related child records into stable JSON arrays, resolves verified codes to language-specific display values, and preserves unresolved raw codes for transparency.”
